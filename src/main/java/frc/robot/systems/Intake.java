package frc.robot.systems;

import java.lang.Math;

public class Intake {

    public Motor intakeMotor;

    public double presetOpen = 100;
    public double presetClosed = 20;
    public boolean open = false;

    private double previousEncoderValue = 1000000;
    public double pressureThreshold = 0.3;

    public Intake(int intake_canID, double intakeStart) {
        intakeMotor = new Motor(intake_canID, false, true);
        intakeMotor.setEnc(intakeStart);
    }

    public boolean zeroIntake() {
        if (intakeMotor.getEnc() < previousEncoderValue) {
            previousEncoderValue = intakeMotor.getEnc();
            intakeMotor.set(-0.14);
            return false;
        } else {
            intakeMotor.set(0);
            previousEncoderValue = 1000000;
            intakeMotor.setEnc(0);
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
        return (intakeMotor.getEnc()+pressureThreshold > previousEncoderValue && intakeMotor.getEnc()-pressureThreshold < previousEncoderValue);
    }

    public void update() {
        if ((Math.abs((pos()-intakeMotor.getEnc())/(43)) > 0.05)) {
            if (((pos()-intakeMotor.getEnc())/(43) < 0 && !pressure()) || (pos()-intakeMotor.getEnc())/(43) >= 0) {
                intakeMotor.set((pos()-intakeMotor.getEnc())/(43));
            }
        } else {
            intakeMotor.set(0);
        }
        previousEncoderValue = intakeMotor.getEnc();
    }

}
