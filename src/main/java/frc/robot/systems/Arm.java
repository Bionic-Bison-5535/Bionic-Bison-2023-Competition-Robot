package frc.robot.systems;

public class Arm {

    public Motor alpha, beta, theta;

    public double pos_0_a = 0; // Position 0 - Collect or Score in Row 1
    public double pos_0_b = -430000;
    public double pos_0_c = 153000;
    public double pos_1_a = 0; // Position 1 -  Score in Row 2
    public double pos_1_b = 0;
    public double pos_1_c = 170000;
    public double pos_2_a = -370000; // Position 2 - Score in Row 3 (Most extended and most dangerous)
    public double pos_2_b = 400000;
    public double pos_2_c = 190000;
    public double pos_3_a = 0; // Position 3 - Holding Mode
    public double pos_3_b = 0;
    public double pos_3_c = 0;

    public int previousPos = 3;
    public int mostRecentPos = 3;

    public Arm(int alpha_canID, int beta_canID, int theta_canID) {
        alpha = new Motor(alpha_canID, true, false, 1);
        beta = new Motor(beta_canID, true, false, 1);
        theta = new Motor(theta_canID, true, false, 1);
        alpha.setEnc(0);
        beta.setEnc(0);
        theta.setEnc(0);
    }

    public void go(double alpha_encValue, double beta_encValue, double theta_encValue) {
        alpha.goTo(alpha_encValue);
        beta.goTo(beta_encValue);
        theta.goTo(theta_encValue);
    }

    private int virtualPos(int actualPos) {
        if (actualPos == 3) {
            return -1;
        } else {
            return actualPos;
        }
    }

    public void pos(int positionNumber) {
        previousPos = mostRecentPos;
        mostRecentPos = positionNumber;
        if (positionNumber == 3 && previousPos == 0) {
            alpha.maxSpeed = 0.5;
            beta.maxSpeed = 1;
            theta.maxSpeed = 0.12;
        } else if (virtualPos(positionNumber) > virtualPos(previousPos)) {
            alpha.maxSpeed = 0.5;
            beta.maxSpeed = 0.7;
            theta.maxSpeed = 0.7;
        } else if (virtualPos(positionNumber) < virtualPos(previousPos)) {
            alpha.maxSpeed = 1;
            beta.maxSpeed = 0.5;
            theta.maxSpeed = 0.4;
        }
        if (positionNumber == 0) {
            go(pos_0_a, pos_0_b, pos_0_c);
        }
        if (positionNumber == 1) {
            go(pos_1_a, pos_1_b, pos_1_c);
        }
        if (positionNumber == 2) {
            go(pos_2_a, pos_2_b, pos_2_c);
        }
        if (positionNumber == 3) {
            go(pos_3_a, pos_3_b, pos_3_c);
        }
    }

    public boolean all_there() {
        return (alpha.almost() && beta.almost() && theta.almost());
    }

    public void reset() {
        alpha.setEnc(0);
        beta.setEnc(0);
        theta.setEnc(0);
    }

    public void update() {
        alpha.update();
        beta.update();
        theta.update();
    }

}
