package frc.robot.systems;

public class Arm {

    public Motor alpha, beta;

    public double pos_0_a = 97.7; // Position 0 - Collect or Score in Row 1
    public double pos_0_b = 70000;
    public double pos_1_a = 0; // Position 1 -  Score in Row 2
    public double pos_1_b = 85000;
    public double pos_2_a = -46; // Position 2 - Score in Row 3
    public double pos_2_b = 114000;
    public double pos_3_a = 0; // Position 3 - Holding Mode
    public double pos_3_b = 5500;

    public int previousPos = 3;
    public int mostRecentPos = 3;

    private int special = 0;

    public Arm(int alpha_canID, int beta_canID) {
        alpha = new Motor(alpha_canID, false, false, 1);
        beta = new Motor(beta_canID, true, false, 0.7);
        alpha.setEnc(0);
        beta.setEnc(0);
    }

    public void go(double alpha_encValue, double beta_encValue) {
        if (beta.getEnc() < beta_encValue) {
            beta.maxSpeed = 0.72;
        } else {
            beta.maxSpeed = 0.45;
        }
        alpha.goTo(alpha_encValue);
        beta.goTo(beta_encValue);
        alpha.db = 0.1;
    }

    public void pos(int positionNumber) {
        if (previousPos == 3 && positionNumber == 0) {
            special = -1;
        } else if (previousPos == 0 && positionNumber == 3) {
            special = 1;
        } else {
            special = 0;
        }
        if (special == 0) {
            if (positionNumber == 0) {
                go(pos_0_a, pos_0_b);
            } else if (positionNumber == 1) {
                go(pos_1_a, pos_1_b);
            } else if (positionNumber == 2) {
                go(pos_2_a, pos_2_b);
            } else if (positionNumber == 3) {
                go(pos_3_a, pos_3_b);
            } else if (positionNumber == 4) {
                go(pos_3_a, pos_0_b);
            }
            previousPos = mostRecentPos;
            mostRecentPos = positionNumber;
        } else if (special == -1) {
            go(pos_3_a, pos_0_b);
            if (beta.almost()) {
                go(pos_0_a, pos_0_b);
                if (alpha.almost()) {
                    special = 0;
                    previousPos = 0;
                    mostRecentPos = 0;
                }
            }
        } else if (special == 1) {
            go(pos_3_a, pos_0_b);
            if (alpha.almost()) {
                go(pos_3_a, pos_3_b);
                if (alpha.almost()) {
                    special = 0;
                    previousPos = 3;
                    mostRecentPos = 3;
                }
            }
        }
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
