package frc.robot.systems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

public class Motor {

    private CANSparkMax maxMotor;
    private TalonSRX talonMotor;
    private RelativeEncoder canEncoder;
    public usingTalon = false;

    public Motor(int canID, boolean isTalon, boolean invert) {
        usingTalon = isTalon;
        if (usingTalon) {
            talonMotor = new TalonSRX(canID);
            talonMotor.setInverted(invert);
        } else {
            maxMotor = new CANSparkMax(canID, MotorType.kBrushless);
            maxMotor.setInverted(!invert);
            canEncoder = maxMotor.getEncoder();
        }
    }

    public void set(double power) {
        if (usingTalon) {
            talonMotor.set(ControlMode.PercentOutput, power);
        } else {
            maxMotor.set(power);
        }
    }

    public double getEnc() {
        if (usingTalon) {
            return talonMotor.getSelectedSensorPosition();
        } else {
            return canEncoder.getPosition();
        }
    }

    public double getRotations() {
        return (getEnc() / ticksPerRotation());
    }

    public void setEnc(double newEncValue) {
        if (usingTalon) {
            talonMotor.setSelectedSensorPosition(newEncValue);
        } else {
            canEncoder.setPosition(newEncValue);
        }
    }

    public double ticksPerRotation() {
        if (usingTalon) {
            return 4096;
        } else {
            return 43;
        }
    }

}
