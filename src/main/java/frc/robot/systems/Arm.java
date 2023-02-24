package frc.robot.systems;

import java.lang.Math;
import frc.robot.systems.Motor;

public class Arm {

    public Motor alpha, beta;

    public double pos_0_x = 50; // Collect or Score in Row 1
    public double pos_0_y = 30;
    public double pos_1_x = 50; // Score in Row 2
    public double pos_1_y = 30;
    public double pos_2_x = 50; // Score in Row 3
    public double pos_2_y = 30;
    public double pos_3_x = 50; // Holding Mode
    public double pos_3_y = 30;

    public int mostRecentPos = 3;

    public Arm(int alpha_canID, int beta_canID, double alpha_start, double beta_start) {
        alpha = new Motor(alpha_canID, false, false);
        beta = new Motor(beta_canID, false, true);
        alpha.setEnc(alpha_start);
        beta.setEnc(beta_start);
    }

    public setRaw(double alpha_speed, double beta_speed) {
        alpha.set(alpha_speed);
        beta.set(beta_speed);
    }

    public void trigIt(double x, double y) {
        // NOT DONE
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
        mostRecentPos = positionNumber;
    }

    public void update() {
        alpha.update();
        beta.update();
    }

}
