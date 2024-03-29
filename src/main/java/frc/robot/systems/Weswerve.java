/*  WESWERVE for Trapezoid Swerve Robot with CANCoder Angle Detection and Either Talon motors or CANSparkMax motor controllers.
	Program written by Wesley McGinn {wesleymcginn1@gmail.com} for Team 5535, but open for use by anyone.
	Version 7.0
*/

package frc.robot.systems;

import java.lang.Math;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.ctre.phoenix.sensors.CANCoder;

public class Weswerve {

	private boolean usingTalons = true; // Set to false to use CANSparkMaxs, set to true to use TalonSRXs.

	public TalonSRX frontLeftSteer, frontRightSteer, backRightSteer, backLeftSteer, frontLeftDrive, frontRightDrive, backRightDrive, backLeftDrive;
	public CANSparkMax frontLeftSteer_sm, frontRightSteer_sm, backRightSteer_sm, backLeftSteer_sm, frontLeftDrive_sm, frontRightDrive_sm, backRightDrive_sm, backLeftDrive_sm;
	public final CANCoder frontLeft, frontRight, backRight, backLeft;
	public double angle0, angle1, angle2, angle3;
	public boolean negation0, negation1, negation2, negation3;
	private double newAngle;
	private boolean negation;
	public boolean SmartAngle = true;
	public double theta = 0;
	public double default_speed = 1;
	public double speed = default_speed;
	public double steeringAmplifier = 0.5;
	private final double wheelAngleErrorRange = 1.2;
	private final double dist1 = 0.6164031008;
	private final double dist2 = 0.5270387597;
	public double x;
	public double y;
	public double a, b, c, d;
	public boolean move = true;
	public double A_offset, B_offset, C_offset, D_offset;

	public Weswerve(int front_left_steer_canID, int front_right_steer_canID, int back_right_steer_canID, int back_left_steer_canID, int front_left_drive_canID, int front_right_drive_canID, int back_right_drive_canID, int back_left_drive_canID, int front_left_canCoder_canID, int front_right_canCoder_canID, int back_right_canCoder_canID, int back_left_canCoder_canID, int front_left_angle_offset, int front_right_angle_offset, int back_right_angle_offset, int back_left_angle_offset) {
		if (usingTalons) {
			// Steering Motors:
			frontLeftSteer = new TalonSRX(front_left_steer_canID);
			frontRightSteer = new TalonSRX(front_right_steer_canID);
			backRightSteer = new TalonSRX(back_right_steer_canID);
			backLeftSteer = new TalonSRX(back_left_steer_canID);
			// Driving Motors:
			frontLeftDrive = new TalonSRX(front_left_drive_canID);
			frontRightDrive = new TalonSRX(front_right_drive_canID);
			backRightDrive = new TalonSRX(back_right_drive_canID);
			backLeftDrive = new TalonSRX(back_left_drive_canID);
			// Motor Configuration:
			frontLeftSteer.configOpenloopRamp(0);
			frontRightSteer.configOpenloopRamp(0);
			backRightSteer.configOpenloopRamp(0);
			backLeftSteer.configOpenloopRamp(0);
			frontLeftDrive.configOpenloopRamp(0);
			frontRightDrive.configOpenloopRamp(0);
			backRightDrive.configOpenloopRamp(0);
			backLeftDrive.configOpenloopRamp(0);
			frontLeftSteer.setInverted(false);
			frontRightSteer.setInverted(false);
			backRightSteer.setInverted(false);
			backLeftSteer.setInverted(false);
			frontLeftDrive.setInverted(false);
			frontRightDrive.setInverted(false);
			backRightDrive.setInverted(false);
			backLeftDrive.setInverted(false);
		} else {
			// Steering Motors:
			frontLeftSteer_sm = new CANSparkMax(front_left_steer_canID, MotorType.kBrushless);
			frontRightSteer_sm = new CANSparkMax(front_right_steer_canID, MotorType.kBrushless);
			backRightSteer_sm = new CANSparkMax(back_right_steer_canID, MotorType.kBrushless);
			backLeftSteer_sm = new CANSparkMax(back_left_steer_canID, MotorType.kBrushless);
			// Driving Motors:
			frontLeftDrive_sm = new CANSparkMax(front_left_drive_canID, MotorType.kBrushless);
			frontRightDrive_sm = new CANSparkMax(front_right_drive_canID, MotorType.kBrushless);
			backRightDrive_sm = new CANSparkMax(back_right_drive_canID, MotorType.kBrushless);
			backLeftDrive_sm = new CANSparkMax(back_left_drive_canID, MotorType.kBrushless);
			// Motor Configuration:
			frontLeftSteer_sm.setInverted(false);
			frontRightSteer_sm.setInverted(false);
			backRightSteer_sm.setInverted(false);
			backLeftSteer_sm.setInverted(false);
			frontLeftDrive_sm.setInverted(false);
			frontRightDrive_sm.setInverted(false);
			backRightDrive_sm.setInverted(false);
			backLeftDrive_sm.setInverted(false);
		}
		// Rotation Sensors:
		frontLeft = new CANCoder(front_left_canCoder_canID);
		frontRight = new CANCoder(front_right_canCoder_canID);
		backRight = new CANCoder(back_right_canCoder_canID);
		backLeft = new CANCoder(back_left_canCoder_canID);
		// Wheel Angle Offsets:
		A_offset = front_left_angle_offset;
		B_offset = front_right_angle_offset;
		C_offset = back_right_angle_offset;
		D_offset = back_left_angle_offset;
	}

	public void setAngles(double Angle0, double Angle1, double Angle2, double Angle3) { // Sets angles of all wheels
		angle0 = Angle0;
		angle1 = Angle1;
		angle2 = Angle2;
		angle3 = Angle3;
	}

	public void setVelocities(double V0, double V1, double V2, double V3) { // Sets velocities of all wheels
		if (move) {
			if (usingTalons) {
				if (negation0) { frontLeftDrive.set(ControlMode.PercentOutput, -V0); } else { frontLeftDrive.set(ControlMode.PercentOutput, V0); }
				if (negation1) { frontRightDrive.set(ControlMode.PercentOutput, -V1); } else { frontRightDrive.set(ControlMode.PercentOutput, V1); }
				if (negation2) { backRightDrive.set(ControlMode.PercentOutput, -V2); } else { backRightDrive.set(ControlMode.PercentOutput, V2); }
				if (negation3) { backLeftDrive.set(ControlMode.PercentOutput, -V3); } else { backLeftDrive.set(ControlMode.PercentOutput, V3); }
			} else {
				if (negation0) { frontLeftDrive_sm.set(V0); } else { frontLeftDrive_sm.set(-V0); }
				if (negation1) { frontRightDrive_sm.set(V1); } else { frontRightDrive_sm.set(-V1); }
				if (negation2) { backRightDrive_sm.set(V2); } else { backRightDrive_sm.set(-V2); }
				if (negation3) { backLeftDrive_sm.set(V3); } else { backLeftDrive_sm.set(-V3); }
			}
		} else {
			if (usingTalons) {
				frontLeftDrive.set(ControlMode.PercentOutput, 0);
				frontRightDrive.set(ControlMode.PercentOutput, 0);
				backRightDrive.set(ControlMode.PercentOutput, 0);
				backLeftDrive.set(ControlMode.PercentOutput, 0);
			} else {
				frontLeftDrive_sm.set(0);
				frontRightDrive_sm.set(0);
				backRightDrive_sm.set(0);
				backLeftDrive_sm.set(0);
			}
		}
	}

	public boolean motorToAngle(TalonSRX Output, CANCoder Input, double angle, boolean smartAngle) { // Called periodically by update() function - adjusts wheel-rotating motor speeds to get to desired angle - if smart angle enabled, returns whether or not the wheel should have a negated velocity (true=negate) - if smart angle disabled, returns whether or not the wheel has reached te desired angle
		newAngle = angle;
		negation = false;
		if (smartAngle) {
			while (newAngle > Input.getPosition() + 180) { newAngle -= 360; }
			while (newAngle < Input.getPosition() - 180) { newAngle += 360; }
			if (newAngle < Input.getPosition() - 90) { newAngle += 180; negation = true; }
			if (newAngle > Input.getPosition() + 90) { newAngle -= 180; negation = true; }
		}
		if (move) {
			if (Math.round(Input.getPosition() - wheelAngleErrorRange) > newAngle) {
				Output.set(ControlMode.PercentOutput, -0.007 * (Input.getPosition() - newAngle) - 0.05);
			} else if (Math.round(Input.getPosition() + wheelAngleErrorRange) < newAngle) {
				Output.set(ControlMode.PercentOutput, -0.007 * (Input.getPosition() - newAngle) + 0.05);
			} else {
				Output.set(ControlMode.PercentOutput, 0);
				if (!smartAngle) { negation = true; }
			}
		} else {
			Output.set(ControlMode.PercentOutput, 0);
		}
		return negation;
	}

	public boolean motorToAngle_sm(CANSparkMax Output, CANCoder Input, double angle, boolean smartAngle) { // Called periodically by update() function - adjusts wheel-rotating motor speeds to get to desired angle - if smart angle enabled, returns whether or not the wheel should have a negated velocity (true=negate) - if smart angle disabled, returns whether or not the wheel has reached te desired angle
		newAngle = angle;
		negation = false;
		if (smartAngle) {
			while (newAngle > Input.getPosition() + 180) { newAngle -= 360; }
			while (newAngle < Input.getPosition() - 180) { newAngle += 360; }
			if (newAngle < Input.getPosition() - 90) { newAngle += 180; negation = true; }
			if (newAngle > Input.getPosition() + 90) { newAngle -= 180; negation = true; }
		}
		if (move) {
			if (Math.round(Input.getPosition() - wheelAngleErrorRange) > newAngle) {
				Output.set(0.007 * (Input.getPosition() - newAngle) + 0.05);
			} else if (Math.round(Input.getPosition() + wheelAngleErrorRange) < newAngle) {
				Output.set(0.007 * (Input.getPosition() - newAngle) - 0.05);
			} else {
				Output.set(0);
				if (!smartAngle) { negation = true; }
			}
		} else {
			Output.set(0);
		}
		return negation;
	}

	public double i_tan(double opp, double adj) { // Special inverse tangent - returns 0:360
		if (opp == 0 && adj == 0) {
			return 0;
		} else if (adj <= 0) {
			return (Math.atan(opp/adj) * (180 / Math.PI) + 180);
		} else {
			return (Math.atan(opp/adj) * (180 / Math.PI));
		}
	}

	public double sine(double sine_input) { // Sine function for degrees
		return Math.sin(sine_input * (Math.PI / 180));
	}

	public double cosine(double cosine_input) { // Cosine function for degrees
		return Math.cos(cosine_input * (Math.PI / 180));
	}

	public boolean resetMotors() { // Resets motors to their original position while ignoring coterminal values - returns true if motors are ready - must be called periodically to work - update() may not run at the same time
		setAngles(0, 0, 0, 0);
		setVelocities(0, 0, 0, 0);
		negation0 = false;
		negation1 = false;
		negation2 = false;
		negation3 = false;
		if (usingTalons) {
			return motorToAngle(frontLeftSteer, frontLeft, A_offset + 225, false) && motorToAngle(frontRightSteer, frontRight, B_offset + 135, false) && motorToAngle(backRightSteer, backRight, C_offset + 225, false) && motorToAngle(backLeftSteer, backLeft, D_offset + 135, false);
		} else {
			return motorToAngle_sm(frontLeftSteer_sm, frontLeft, A_offset + 225, false) && motorToAngle_sm(frontRightSteer_sm, frontRight, B_offset + 135, false) && motorToAngle_sm(backRightSteer_sm, backRight, C_offset + 225, false) && motorToAngle_sm(backLeftSteer_sm, backLeft, D_offset + 135, false);
		}
	}

	public void tone() { // Plays chord from driving motors (C5, E5, G5, C6)
		if (usingTalons) {
			frontLeftDrive.set(ControlMode.MusicTone, 523.25);
			frontRightDrive.set(ControlMode.MusicTone, 659.25);
			backRightDrive.set(ControlMode.MusicTone, 783.99);
			backLeftDrive.set(ControlMode.MusicTone, 1046.5);
		}
	}

	public void lock() {
		setAngles(45, 135, 45, 135);
		setVelocities(0, 0, 0, 0);
	}

	public void swerve(double verticalInput, double horizontalInput, double rotationalInput, double frontAngle) { // Main function - Does all swerve math
		if (horizontalInput == 0 && verticalInput == 0 && rotationalInput == 0) {
			setVelocities(0, 0, 0, 0);
		} else {
			theta = i_tan(-horizontalInput, verticalInput) + frontAngle;
			y = Math.sqrt(horizontalInput * horizontalInput + verticalInput * verticalInput);
			x = (speed * y) / (steeringAmplifier * rotationalInput);
			if (rotationalInput == 0) {
				setAngles(theta, theta, theta, theta);
				a = 1;
				b = 1;
				c = 1;
				d = 1;
				x = 0;
				setVelocities(a*y*speed, -b*y*speed, -c*y*speed, -d*y*speed);
			} else if (Math.abs(x) < 0.4) {
				setAngles(-54.20917723, 54.20917723, -71.56750291, 71.56750291);
				setVelocities(rotationalInput * speed, rotationalInput * speed, rotationalInput * speed, -rotationalInput * speed);
			} else {
				if (x < 0) {
					angle0 = -i_tan((dist1 * sine(-theta + 108.4324971)), (2*x - dist1 * cosine(-theta + 108.4324971))) + theta + 180;
					angle1 = -i_tan((dist1 * sine(-theta + 71.56750291)), (2*x - dist1 * cosine(-theta + 71.56750291))) + theta + 180;
					angle2 = -i_tan((dist2 * sine(-theta - 54.20917723)), (2*x - dist2 * cosine(-theta - 54.20917723))) + theta + 180;
					angle3 = -i_tan((dist2 * sine(-theta - 125.7908228)), (2*x - dist2 * cosine(-theta - 125.7908228))) + theta + 180;
				} else {
					angle0 = -i_tan((dist1 * sine(-theta + 108.4324971)), (2*x - dist1 * cosine(-theta + 108.4324971))) + theta;
					angle1 = -i_tan((dist1 * sine(-theta + 71.56750291)), (2*x - dist1 * cosine(-theta + 71.56750291))) + theta;
					angle2 = -i_tan((dist2 * sine(-theta - 54.20917723)), (2*x - dist2 * cosine(-theta - 54.20917723))) + theta;
					angle3 = -i_tan((dist2 * sine(-theta - 125.7908228)), (2*x - dist2 * cosine(-theta - 125.7908228))) + theta;
				}
				a = Math.abs(Math.sqrt((x*x) - (x * dist1 * cosine(-theta + 108.4324971)) + 0.5) / x);
				b = Math.abs(Math.sqrt((x*x) - (x * dist1 * cosine(-theta + 71.56750291)) + 0.5) / x);
				c = Math.abs(Math.sqrt((x*x) - (x * dist2 * cosine(-theta - 54.20917723)) + 0.5) / x);
				d = Math.abs(Math.sqrt((x*x) - (x * dist2 * cosine(-theta - 125.7908228)) + 0.5) / x);
				setVelocities(a*y*speed, -b*y*speed, -c*y*speed, -d*y*speed);
			}
		}
	}

	public void update() { // This function must be called periodically while operating the robot
		if (usingTalons) {
			negation0 = motorToAngle(frontLeftSteer, frontLeft, angle0 + A_offset + 225, SmartAngle);
			negation1 = motorToAngle(frontRightSteer, frontRight, angle1 + B_offset + 135, SmartAngle);
			negation2 = motorToAngle(backRightSteer, backRight, angle2 + C_offset + 225, SmartAngle);
			negation3 = motorToAngle(backLeftSteer, backLeft, angle3 + D_offset + 135, SmartAngle);
		} else {
			negation0 = motorToAngle_sm(frontLeftSteer_sm, frontLeft, angle0 + A_offset + 225, SmartAngle);
			negation1 = motorToAngle_sm(frontRightSteer_sm, frontRight, angle1 + B_offset + 135, SmartAngle);
			negation2 = motorToAngle_sm(backRightSteer_sm, backRight, angle2 + C_offset + 225, SmartAngle);
			negation3 = motorToAngle_sm(backLeftSteer_sm, backLeft, angle3 + D_offset + 135, SmartAngle);
		}
	}

}
