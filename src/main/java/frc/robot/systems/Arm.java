package frc.robot.systems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class Arm {

    private TalonSRX expansion, upDown;
    public double expansionPos, upDownPos;
    private double expansionMin, expansionMax, upDownMin, upDownMax;

    public Arm(int expansion_canID, int upDown_canID, double expansionStart, double upDownStart) {
        expansion = new TalonSRX(expansion_canID);
        upDown = new TalonSRX(upDown_canID);
        expansion.setSelectedSensorPosition(expansionStart);
        upDown.setSelectedSensorPosition(upDownStart);
        expansion.configOpenloopRamp(0);
        upDown.configOpenloopRamp(0);
        expansion.setInverted(true);
        upDown.setInverted(false);
    }

    public void setExpansion(double Rotations) {
        expansionPos = 4096*Rotations;
        if (expansionPos < expansionMin) { expansionPos = expansionMin; }
        if (expansionPos > expansionMax) { expansionPos = expansionMax; }
    }
    
    public void changeExpansion(double newRotations) {
        expansionPos += 4096*newRotations;
        if (expansionPos < expansionMin) { expansionPos = expansionMin; }
        if (expansionPos > expansionMax) { expansionPos = expansionMax; }
    }

    public void setUpDown(double Rotations) {
        upDownPos = 4096*Rotations;
        if (upDownPos < upDownMin) { upDownPos = upDownMin; }
        if (upDownPos > upDownMax) { upDownPos = upDownMax; }
    }
    
    public void changeUpDown(double newRotations) {
        upDownPos += 4096*newRotations;
        if (upDownPos < upDownMin) { upDownPos = upDownMin; }
        if (upDownPos > upDownMax) { upDownPos = upDownMax; }
    }

    public void update() {
        expansion.set(ControlMode.PercentOutput, (expansionPos-expansion.getSelectedSensorPosition())/4096);
        upDown.set(ControlMode.PercentOutput, (upDownPos-upDown.getSelectedSensorPosition())/4096);
    }

}
