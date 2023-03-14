package frc.robot.systems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class Controls {

    public Joystick in;
    public JoystickButton A, B, X, Y, LEFT, RIGHT, BACK, START, LEFT_STICK, RIGHT_STICK;
    public double inputStickDeadband;

    private double[] rr0 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double[] rr1 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double add0 = 0;
    private double add1 = 0;

    public Controls(int inputNumber, double setInputStickDeadband) {
        in = new Joystick(inputNumber);
        A = new JoystickButton(in, 1);
        B = new JoystickButton(in, 2);
        X = new JoystickButton(in, 3);
        Y = new JoystickButton(in, 4);
        LEFT = new JoystickButton(in, 5);
        RIGHT = new JoystickButton(in, 6);
        BACK = new JoystickButton(in, 7);
        START = new JoystickButton(in, 8);
        LEFT_STICK = new JoystickButton(in, 9);
        RIGHT_STICK = new JoystickButton(in, 10);
        inputStickDeadband = setInputStickDeadband;
    }

    private double deadband(double num_input, double db) {
		if (Math.abs(num_input) < db) {
			return 0;
		} else {
			return num_input;
		}
	}

    public double stick(int axisNumber) {
        return deadband(in.getRawAxis(axisNumber), inputStickDeadband);
    }

    public double stickWithRamp(int axisNumber) {
        if (axisNumber == 0) {
            return shift0();
        } else if (axisNumber == 1) {
            return shift1();
        } else {
            return stick(axisNumber);
        }
    }

    public int pov() {
        return in.getPOV();
    }

    public boolean active() {
        return (stick(0) != 0 || stick(1) != 0);
    }

    public double shift0() {
        add0 = 0;
        for (int i = 0; i < rr0.length - 1; i++) {
            rr0[i] = rr0[i+1];
            add0 += rr0[i];
        }
        rr0[rr0.length - 1] = stick(0);
        add0 += stick(1);
        return add0/rr0.length;
    }

    public double shift1() {
        add1 = 0;
        for (int i = 0; i < rr1.length - 1; i++) {
            rr1[i] = rr1[i+1];
            add1 += rr1[i];
        }
        rr1[rr1.length - 1] = stick(1);
        add1 += stick(1);
        return add1/rr1.length;
    }

}
