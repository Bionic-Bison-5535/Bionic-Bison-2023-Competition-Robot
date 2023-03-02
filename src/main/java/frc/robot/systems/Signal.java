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

}
