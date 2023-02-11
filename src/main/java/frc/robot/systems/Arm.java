package frc.robot.systems;

import java.lang.Math;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

public class Arm {

    public CANSparkMax expansion, upDown;
    public RelativeEncoder expansionEncoder, upDownEncoder;

    public double expansionPos, upDownPos;
    public boolean there;
    public double expansionMin = 0;
    public double expansionMax = 12.33*16;
    public double upDownMin = -12.33*8;
    public double upDownMax = 12.33*8;

    public double rotationsPerAngle30 = 18;
    public double retractedExpansionLength = 28;
    public double circumferenceOfExpansionWheel = 7.8125;

    public double pos_0_x = 50; // Collect or Score in Row 1
    public double pos_0_y = 50;
    public double pos_1_x = 50; // Score in Row 2
    public double pos_1_y = 50;
    public double pos_2_x = 50; // Score in Row 3
    public double pos_2_y = 50;
    public double pos_3_x = 50; // Holding Mode
    public double pos_3_y = 50;

    public Arm(int expansion_canID, int upDown_canID, double expansionStart, double upDownStart) {
        expansion = new CANSparkMax(expansion_canID, MotorType.kBrushless);
        upDown = new CANSparkMax(upDown_canID, MotorType.kBrushless);
        expansionPos = expansionStart;
        upDownPos = upDownStart;
        expansionEncoder = expansion.getEncoder();
        upDownEncoder = upDown.getEncoder();
        expansionEncoder.setPosition(expansionStart);
        upDownEncoder.setPosition(upDownStart);
        expansion.setInverted(true);
        upDown.setInverted(false);
    }

    public void setExpansion(double Rotations) {
        expansionPos = 12.33*Rotations;
        if (expansionPos < expansionMin) { expansionPos = expansionMin; }
        if (expansionPos > expansionMax) { expansionPos = expansionMax; }
    }
    
    public void changeExpansion(double newRotations) {
        expansionPos += 12.33*newRotations;
        if (expansionPos < expansionMin) { expansionPos = expansionMin; }
        if (expansionPos > expansionMax) { expansionPos = expansionMax; }
    }

    public void setUpDown(double Rotations) {
        upDownPos = 12.33*Rotations;
        if (upDownPos < upDownMin) { upDownPos = upDownMin; }
        if (upDownPos > upDownMax) { upDownPos = upDownMax; }
    }
    
    public void changeUpDown(double newRotations) {
        upDownPos += 12.33*newRotations;
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
        if (Math.abs((expansionPos-expansionEncoder.getPosition())/(12.33)) > 0.4) {
            expansion.set((expansionPos-expansionEncoder.getPosition())/(12.33));
        } else {
            expansion.set(0);
        }
        if (Math.abs((upDownPos-upDownEncoder.getPosition())/(12.33)) > 0.4) {
            upDown.set((upDownPos-upDownEncoder.getPosition())/(12.33));
        } else {
            upDown.set(0);
        }
        if ((Math.abs((expansionPos-expansionEncoder.getPosition())/(12.33)) > 0.4) && (Math.abs((upDownPos-upDown.getEncoder().getPosition())/(12.33)) > 0.4)) {
            there = false;
        } else {
            there = true;
        }
    }

}
