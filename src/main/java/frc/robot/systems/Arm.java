package frc.robot.systems;

import java.lang.Math;

public class Arm {

    public Motor alpha, beta;

    public double q1 = 48;
    public double q2 = 48;

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
        alpha = new Motor(alpha_canID, true, false);
        beta = new Motor(beta_canID, true, false);
        alpha.setEnc(alpha_start);
        beta.setEnc(beta_start);
    }

    public void setRaw(double alpha_speed, double beta_speed) {
        alpha.set(alpha_speed);
        beta.set(beta_speed);
    }

    public void trigIt(double set_x, double set_y) {
        setAlpha(180*Math.floor(set_x/(q1+q2)) + Math.atan(set_y/set_x)*(180/Math.PI) + Math.acos(((Math.sqrt(set_x*set_x+set_y*set_y)*(set_x*set_x+set_y*set_y+q1*q1-q2*q2))/(2*q1*(set_x*set_x+set_y*set_y)))*(Math.PI/180)));
        setBeta(Math.acos(((q1*q1+q2*q2-set_x*set_x-set_y*set_y)/(2*q1*q2))*(Math.PI/180)) - 180);
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

    public double getAlpha() {
        return alpha.getEnc()/-2347.14;
    }

    public double getBeta() {
        return 90-((beta.getEnc()-230000)/-5600);
    }

    public void setAlpha(double newSetAngle) {
        alpha.goTo(newSetAngle*-2347.14);
    }

    public void setBeta(double newSetAngle) {
        beta.goTo(-5600*(newSetAngle-90)+230000);
    }

    public boolean there() {
        return (alpha.there() && beta.there());
    }

    public void update() {
        alpha.update();
        beta.update();
    }

}
