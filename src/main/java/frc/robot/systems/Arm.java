package frc.robot.systems;

public class Arm {

    public Motor alpha, beta, theta;

    public double pos_0_a = -141488; // Position 0 - Collect or Score in Row 1
    public double pos_0_b = -17251;
    public double pos_0_c = 102960;
    public double pos_1_a = -66438; // Position 1 -  Score in Row 2
    public double pos_1_b = 54271;
    public double pos_1_c = 134730;
    public double pos_2_a = -412271; // Position 2 - Score in Row 3 (Most extended and most dangerous)
    public double pos_2_b = 140848;
    public double pos_2_c = 180789;
    public double pos_3_a = 0; // Position 3 - Holding Mode
    public double pos_3_b = 10000;
    public double pos_3_c = -7000;

    public int mostRecentPos = 3;

    public Arm(int alpha_canID, int beta_canID, int theta_canID) {
        alpha = new Motor(alpha_canID, true, false, 1);
        beta = new Motor(beta_canID, true, false, 1);
        theta = new Motor(theta_canID, true, false, 0.9);
        alpha.setEnc(0);
        beta.setEnc(0);
        theta.setEnc(0);
    }

    public void go(double alpha_encValue, double beta_encValue, double theta_encValue) {
        alpha.goTo(alpha_encValue);
        beta.goTo(beta_encValue);
        theta.goTo(theta_encValue);
    }

    public void pos(int positionNumber) {
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
        mostRecentPos = positionNumber;
    }

    public boolean all_there() {
        return (alpha.there() && beta.there() && theta.there());
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
