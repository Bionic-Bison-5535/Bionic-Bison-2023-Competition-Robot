package frc.robot.systems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Arm {

    private TalonSRX expansion, upDown;
    public double expansionPosInit, upDownPosInit, expansionPos, upDownPos;

    public Arm(int expansion_canID, int upDown_canID, double expansionStart, double upDownStart) {
        expansion = new TalonSRX(expansion_canID);
        upDown = new TalonSRX(upDown_canID);
        expansionPosInit = expansionStart;
        upDownPosInit = upDownStart;
    }

    public void setExpansion(double newPos) {
        expansion.set(ControlMode.Position, newPos-expansionPosInit);
        expansionPos = newPos;
    }

    public void setUpDown(double newPos) {
        upDown.set(ControlMode.Position, newPos-upDownPosInit);
        upDownPos = newPos;
    }

    public void changeExpansion(double changeBy) {
        expansionPos += changeBy;
        expansion.set(ControlMode.Position, expansionPos-expansionPosInit);
    }

    public void changeUpDown(double changeBy) {
        upDownPos += changeBy;
        upDown.set(ControlMode.Position, upDownPos-upDownPosInit);
    }

}
