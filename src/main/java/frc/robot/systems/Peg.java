package frc.robot.systems;

import edu.wpi.first.wpilibj.PWM;

public class Peg {

    private PWM peg1, peg2;
    public double in_pos, out_pos;
    public boolean actuated = false;

    public Peg(int peg1PWMport, int peg2PWMPort, double inPosition, double outPosition) {
        peg1 = new PWM(peg1PWMport);
        peg2 = new PWM(peg2PWMPort);
        in_pos = inPosition;
        out_pos = outPosition;
        in();
    }

    public void in() {
        peg1.setSpeed(in_pos);
        peg2.setSpeed(in_pos);
        actuated = false;
    }

    public void out() {
        peg1.setSpeed(out_pos);
        peg2.setSpeed(in_pos);
        actuated = true;
    }

    public void pos(double position) {
        peg1.setSpeed(position);
        peg2.setSpeed(position);
        if (position < 0) {
            actuated = false;
        } else {
            actuated = true;
        }
    }

}
