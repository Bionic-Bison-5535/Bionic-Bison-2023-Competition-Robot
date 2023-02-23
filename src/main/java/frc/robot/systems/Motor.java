package frc.robot.systems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

public class Motor {

    private CANSparkMax maxMotor;
    private TalonSRX talonMotor;
    public usingTalon = false;

    public Motor(int canID, boolean isTalon) {
        if (isTalon) {
            talonMotor = new TalonSRX(canID);
        } else {
            maxMotor = new CANSparkMax(canID, MotorType.kBrushless);
        }
    }

    public void set(double power) {
        if (isTalon) {
            talonMotor.set(ControlMode.PercentOutput, power);
        } else {
            maxMotor.set(power);
        }
    }

}
