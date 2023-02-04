package frc.robot.systems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class Controls {

    public Joystick in;
    public JoystickButton A, B, X, Y, LEFT, RIGHT, BACK, START, LEFT_STICK, RIGHT_STICK;
    public double inputStickDeadband;

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

}
