package frc.robot.systems;

import java.lang.Math;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class Arm {

    public TalonSRX expansion, upDown;

    public double expansionPos, upDownPos;
    public boolean there;
    public double expansionMin = 0;
    public double expansionMax = 4096*16;
    public double upDownMin = -4096*8;
    public double upDownMax = 4096*8;

    public double rotationsPerAngle30 = 10;
    public double retractedExpansionLength = 30;
    public double circumferenceOfExpansionWheel = 10;

    public double pos_0_x = 50; // Collect or Score in Row 1
    public double pos_0_y = 50;
    public double pos_1_x = 50; // Score in Row 2
    public double pos_1_y = 50;
    public double pos_2_x = 50; // Score in Row 3
    public double pos_2_y = 50;
    public double pos_3_x = 50; // Holding Mode
    public double pos_3_y = 50;

    public Arm(int expansion_canID, int upDown_canID, double expansionStart, double upDownStart) {
        expansion = new TalonSRX(expansion_canID);
        upDown = new TalonSRX(upDown_canID);
        expansionPos = expansionStart;
        upDownPos = upDownStart;
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

    public void setAngle(double angle) {
        setUpDown((rotationsPerAngle30/30)*angle);
    }

    public void setLength(double length) {
        setExpansion((length-retractedExpansionLength)/circumferenceOfExpansionWheel);
    }

    public void trigIt(double x, double y) {
        setLength(Math.sqrt((x*x)+(y*y)));
        setAngle((180/Math.PI)*Math.atan(y/x));
    }

    public void pos(int positionNumber) {
        if (positionNumber == 0) {
            trigIt(pos_0_x, pos_0_y);
        }
        if (positionNumber == 1) {
            trigIt(pos_1_x, pos_1_y);
        }
        if (positionNumber == 2) {
            trigIt(pos_2_x, pos_2_y);
        }
        if (positionNumber == 3) {
            trigIt(pos_3_x, pos_3_y);
        }
    }

    public void update() {
        expansion.set(ControlMode.PercentOutput, (expansionPos-expansion.getSelectedSensorPosition())/4096);
        upDown.set(ControlMode.PercentOutput, (upDownPos-upDown.getSelectedSensorPosition())/4096);
        if (Math.abs((expansionPos-expansion.getSelectedSensorPosition())/4096) > 0.4) {
            there = false;
        } else {
            there = true;
        }
    }

}
