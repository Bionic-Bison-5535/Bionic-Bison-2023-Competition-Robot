package frc.robot.systems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class Controls {

    public Joystick in;
    public JoystickButton A, B, X, Y, LEFT, RIGHT, BACK, START, LEFT_STICK, RIGHT_STICK;
    public double inputStickDeadband;

    public double q0, q1, q2, q3, q4, q5, q6, q7, q8, q9;
    public double w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;

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
        resetQ();
        resetW();
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
            return shiftQ();
        } else if (axisNumber == 1) {
            return shiftW();
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

    public void resetQ() {
        q0 = 0;
        q1 = 0;
        q2 = 0;
        q3 = 0;
        q4 = 0;
        q5 = 0;
        q6 = 0;
        q7 = 0;
        q8 = 0;
        q9 = 0;
    }

    public void resetW() {
        w0 = 0;
        w1 = 0;
        w2 = 0;
        w3 = 0;
        w4 = 0;
        w5 = 0;
        w6 = 0;
        w7 = 0;
        w8 = 0;
        w9 = 0;
    }

    public double shiftQ() {
        q9 = q8;
        q8 = q7;
        q7 = q6;
        q6 = q5;
        q5 = q4;
        q4 = q3;
        q3 = q2;
        q2 = q1;
        q1 = q0;
        q0 = stick(0);
        return (q0 + q1 + q2 + q3 + q4 + q5 + q6 + q7 + q8 + q9)/10;
    }

    public double shiftW() {
        w9 = w8;
        w8 = w7;
        w7 = w6;
        w6 = w5;
        w5 = w4;
        w4 = w3;
        w3 = w2;
        w2 = w1;
        w1 = w0;
        w0 = stick(1);
        return (w0 + w1 + w2 + w3 + w4 + w5 + w6 + w7 + w8 + w9)/10;
    }

}
