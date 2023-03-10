package frc.robot.systems;

import java.lang.Math;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

public class Motor {

    private CANSparkMax maxMotor;
    private TalonSRX talonMotor;
    private RelativeEncoder canEncoder;
    public double goToPos = 0;
    public boolean usingTalon;
    public double db = 0.17;
    public boolean posMode = false;
    public double maxSpeed = 1;

    public Motor(int canID, boolean isTalon, boolean invert, double maximum_speed) {
        usingTalon = isTalon;
        if (usingTalon) {
            talonMotor = new TalonSRX(canID);
            talonMotor.configOpenloopRamp(0);
            talonMotor.setInverted(invert);
        } else {
            maxMotor = new CANSparkMax(canID, MotorType.kBrushless);
            maxMotor.setInverted(!invert);
            canEncoder = maxMotor.getEncoder();
        }
        maxSpeed = maximum_speed;
    }

    public void stop() {
        if (usingTalon) {
            talonMotor.set(ControlMode.PercentOutput, 0);
        } else {
            maxMotor.set(0);
        }
        if (posMode) {
            setEnc(getEnc());
        }
    }

    public void set(double power) {
        if (usingTalon) {
            talonMotor.set(ControlMode.PercentOutput, power);
        } else {
            maxMotor.set(power);
        }
        posMode = false;
    }

    public void goTo(double newEncValueToGoTo) {
        goToPos = newEncValueToGoTo;
        posMode = true;
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
        goToPos = newEncValue;
    }

    public double ticksPerRotation() {
        if (usingTalon) {
            return 2048;
        } else {
            return 43;
        }
    }

    public boolean there() {
        if (posMode && Math.abs(getEnc()-goToPos) > db * ticksPerRotation()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean almost() {
        if (Math.abs(0.27*((goToPos/ticksPerRotation())-getRotations())) >= 1) {
            return false;
        } else {
            return true;
        }
    }

    public void update() {
        if (posMode) {
            if (!there()) {
                if (0.27*((goToPos/ticksPerRotation())-getRotations()) <= maxSpeed && 0.27*((goToPos/ticksPerRotation())-getRotations()) >= -maxSpeed) {
                    set(0.27*((goToPos/ticksPerRotation())-getRotations()));
                } else if (0.27*((goToPos/ticksPerRotation())-getRotations()) > 0) {
                    set(maxSpeed);
                } else {
                    set(-maxSpeed);
                }
            } else {
                set(0);
            }
            posMode = true;
        }
    }

}
