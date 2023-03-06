package frc.robot.systems;

import edu.wpi.first.wpilibj.PWM;

public class Signal {

    private PWM cubeLED, coneLED;

    public Signal(int cubeLED_Port, int coneLED_Port) {
        cubeLED = new PWM(cubeLED_Port);
        coneLED = new PWM(coneLED_Port);
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

    public void cube() {
        setCubeLED(true);
        setConeLED(false);
    }

    public void cone() {
        setCubeLED(false);
        setConeLED(true);
    }

    public void off() {
        setCubeLED(false);
        setConeLED(false);
    }

}
