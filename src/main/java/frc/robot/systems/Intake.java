package frc.robot.systems;

import java.lang.Math;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

public class Intake {

    public CANSparkMax intakeMotor;
    public RelativeEncoder intakeEncoder;

    public double presetOpen = 50;
    public double presetClosed = 30;
    public boolean open;

    private double previousEncoderValue = 1000000;
    public double pressureThreshold = 0.3;

    public Intake(int intake_canID, double intakeStart) {
        intakeMotor = new CANSparkMax(intake_canID, MotorType.kBrushless);
        intakeEncoder = intakeMotor.getEncoder();
        intakeEncoder.setPosition(intakeStart);
        intakeMotor.setInverted(true);
    }

    public boolean zeroIntake() {
        if (intakeEncoder.getPosition() < previousEncoderValue) {
            previousEncoderValue = intakeEncoder.getPosition();
            intakeMotor.set(-0.1);
            return false;
        } else {
            intakeMotor.set(0);
            previousEncoderValue = 1000000;
            intakeEncoder.setPosition(0);
            return true;
        }
    }

    public double pos() {
        if (open) {
            return presetOpen;
        } else {
            return presetClosed;
        }
    }

    public void open() {
        open = true;
    }

    public void close() {
        open = false;
    }

    public boolean pressure() {
        return (intakeEncoder.getPosition()+pressureThreshold > previousEncoderValue && intakeEncoder.getPosition()-pressureThreshold < previousEncoderValue);
    }

    public void update() {
        if ((Math.abs((pos()-intakeEncoder.getPosition())/(12.33)) > 0.05) && !pressure()) {
            intakeMotor.set((pos()-intakeEncoder.getPosition())/(12.33));
        } else {
            if (pressure()) {
                intakeMotor.set(-0.1); // Hold object tightly
            } else {
                intakeMotor.set(0);
            }
        }
        previousEncoderValue = intakeEncoder.getPosition();
    }

}
