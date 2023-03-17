package frc.robot.systems;

public class Arm {

    public Motor alpha, beta;

    public double pos_0_a = 62; // Position 0 - Collect or Score in Row 1
    public double pos_0_b = 60000;
    public double pos_1_a = 0; // Position 1 -  Score in Row 2
    public double pos_1_b = 80000;
    public double pos_2_a = -52; // Position 2 - Score in Row 3 (Most extended and most dangerous)
    public double pos_2_b = 114000;
    public double pos_3_a = 0; // Position 3 - Holding Mode
    public double pos_3_b = 4000;

    public int previousPos = 3;
    public int mostRecentPos = 3;

    public Arm(int alpha_canID, int beta_canID) {
        alpha = new Motor(alpha_canID, false, false, 1);
        beta = new Motor(beta_canID, true, false, 0.5);
        alpha.db = 0.1;
        beta.db = 0.07;
        alpha.setEnc(0);
        beta.setEnc(0);
    }

    public void go(double alpha_encValue, double beta_encValue) {
        alpha.goTo(alpha_encValue);
        beta.goTo(beta_encValue);
    }

    public void pos(int positionNumber) {
        if (positionNumber == 0) {
            go(pos_0_a, pos_0_b);
        }
        if (positionNumber == 1) {
            go(pos_1_a, pos_1_b);
        }
        if (positionNumber == 2) {
            go(pos_2_a, pos_2_b);
        }
        if (positionNumber == 3) {
            go(pos_3_a, pos_3_b);
        }
        if (positionNumber == 4) {
            go(pos_3_a, pos_0_b);
        }
        previousPos = mostRecentPos;
        mostRecentPos = positionNumber;
    }

    public boolean all_there() {
        return (alpha.almost() && beta.almost());
    }

    public void reset() {
        alpha.setEnc(0);
        beta.setEnc(0);
    }

    public void update() {
        alpha.update();
        beta.update();
    }

}
