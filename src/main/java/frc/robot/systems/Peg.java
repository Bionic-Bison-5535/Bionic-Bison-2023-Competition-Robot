package frc.robot.systems;

import edu.wpi.first.wpilibj.PWM;

public class Peg {

    public PWM peg1;
    public double in_pos, out_pos;
    public boolean actuated = false;

    public Peg(int peg1PWMport, double inPosition, double outPosition) {
        peg1 = new PWM(peg1PWMport);
        in_pos = inPosition;
        out_pos = outPosition;
        in();
    }

    public void in() {
        peg1.setSpeed(in_pos);
        actuated = false;
    }

    public void out() {
        peg1.setSpeed(out_pos);
        actuated = true;
    }

    public void pos(double position) {
        peg1.setSpeed(position);
        if (position < 0) {
            actuated = false;
        } else {
            actuated = true;
        }
    }

}
