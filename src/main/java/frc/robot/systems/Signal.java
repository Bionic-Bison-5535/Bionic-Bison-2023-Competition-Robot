package frc.robot.systems;

import edu.wpi.first.wpilibj.PWM;

public class Signal {

    private PWM cubeLED, coneLED;

    public Signal(int cubeLED_Port, int coneLED_port) {
        peg1 = new PWM(peg1PWMport);
        peg2 = new PWM(peg2PWMPort);
        setCubeLED(false);
        setConeLED(false);
    }

    public void setCubeLED(boolean on) {
        if (on) {
            cubeLED.setSpeed(1);
        } else {
            cubeLED.setSpeed(-1);
        }
    }

    public void setConeLED(boolean on) {
        if (on) {
            coneLED.setSpeed(1);
        } else {
            coneLED.setSpeed(-1);
        }
    }

}
