package frc.robot.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Arm {

    private TalonSRX expansion, upDown;

    public Arm(int expansion_canID, int upDown_canID) {
        expansion = new TalonSRX(expansion_canID);
        upDown = new TalonSRX(upDown_canID);
    }

    public void rawExpansion(double Vel) {
        expansion.set(ControlMode.PercentOutput, Vel);
    }

    public void rawUpDown(double Vel) {
        upDown.set(ControlMode.PercentOutput, Vel);
    }

    

}
