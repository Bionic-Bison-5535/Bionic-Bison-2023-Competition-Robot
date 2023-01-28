package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.systems.*;

public class Robot extends TimedRobot {

  private final Weswerve swerveCtrl = new Weswerve(30, 31, 32, 33, 20, 21, 22, 23, 10, 11, 12, 13, 70, 100, 148, -2);
	private final Controls input = new Controls(0);
	private final Navx navx = new Navx();
	
	Timer timer;

	public boolean headless = true;
	public double front = 0;
	public double dir = 0;
	public double dir_accuracy = 1;
	public double rotation = 0;

	public double deadband(double num_input, double db) {
		if (Math.abs(num_input) < db) {
			return 0;
		} else {
			return num_input;
		}
	}

	public void dash() {
		swerveCtrl.A_offset = SmartDashboard.getNumber("A Offset", swerveCtrl.A_offset);
		swerveCtrl.B_offset = SmartDashboard.getNumber("B Offset", swerveCtrl.B_offset);
		swerveCtrl.C_offset = SmartDashboard.getNumber("C Offset", swerveCtrl.C_offset);
		swerveCtrl.D_offset = SmartDashboard.getNumber("D Offset", swerveCtrl.D_offset);
		swerveCtrl.default_speed = SmartDashboard.getNumber("Speed MAX", swerveCtrl.default_speed);
		swerveCtrl.steeringAmplifier = SmartDashboard.getNumber("Steer MAX", swerveCtrl.steeringAmplifier);
		if (headless) {
			SmartDashboard.putNumber("FRONT", front);
		} else {
			front = SmartDashboard.getNumber("FRONT", front);
		}
		SmartDashboard.putNumber("theta", swerveCtrl.theta);
		SmartDashboard.putNumber("x", swerveCtrl.x);
		SmartDashboard.putNumber("y", swerveCtrl.y);
		SmartDashboard.putNumber("t", swerveCtrl.speed);
		SmartDashboard.putNumber("A Proportion", swerveCtrl.a);
		SmartDashboard.putNumber("B Proportion", swerveCtrl.b);
		SmartDashboard.putNumber("C Proportion", swerveCtrl.c);
		SmartDashboard.putNumber("D Proportion", swerveCtrl.d);
		SmartDashboard.putNumber("A Angle", swerveCtrl.angle0);
		SmartDashboard.putNumber("B Angle", swerveCtrl.angle1);
		SmartDashboard.putNumber("C Angle", swerveCtrl.angle2);
		SmartDashboard.putNumber("D Angle", swerveCtrl.angle3);
		SmartDashboard.putBoolean("A Negation", swerveCtrl.negation0);
		SmartDashboard.putBoolean("B Negation", swerveCtrl.negation1);
		SmartDashboard.putBoolean("C Negation", swerveCtrl.negation2);
		SmartDashboard.putBoolean("D Negation", swerveCtrl.negation3);
		SmartDashboard.putBoolean("Moving Enabled", swerveCtrl.move);
		SmartDashboard.putBoolean("Headless", headless);
		SmartDashboard.putNumber("yaw", navx.yaw());
		SmartDashboard.putNumber("balance", navx.balance());
	}

	public void dashInit() {
		SmartDashboard.putNumber("A Offset", swerveCtrl.A_offset);
		SmartDashboard.putNumber("B Offset", swerveCtrl.B_offset);
		SmartDashboard.putNumber("C Offset", swerveCtrl.C_offset);
		SmartDashboard.putNumber("D Offset", swerveCtrl.D_offset);
		SmartDashboard.putNumber("Speed MAX", swerveCtrl.default_speed);
		SmartDashboard.putNumber("Steer MAX", swerveCtrl.steeringAmplifier);
		SmartDashboard.putNumber("FRONT", front);
		SmartDashboard.putNumber("Pipeline", 0);
	}

	@Override
	public void robotInit() {
		dashInit();
	}


	@Override
	public void robotPeriodic() {}


	@Override
	public void autonomousInit() {
    swerveCtrl.setAngles(0, 0, 0, 0);
  }


	@Override
	public void autonomousPeriodic() {
    swerveCtrl.update();
		dash();
  }


	@Override
	public void testInit() {}


	@Override
	public void testPeriodic() {}


	@Override
	public void teleopInit() {
		navx.zeroYaw();
		dir = 0;
	}


	@Override
	public void teleopPeriodic() {
    /*
		dir += 2.7 * (input.in.getRawAxis(4));
		if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
			rotation = -0.04*(navx.yaw()-dir);
		}
		if (Math.abs(navx.yaw()-dir) < dir_accuracy) {
			rotation = 0;
		}
    */
		SmartDashboard.putNumber("dir", dir);
		if (headless) {
			if (input.BACK.get()) { headless = false; front = 0; navx.zeroYaw(); dir = 0; }
			if (input.LEFT.get()) { navx.zeroYaw(); rotation = 0; dir = 0; }
			front = navx.coterminalYaw();
		} else {
			if (input.START.get()) { headless = true; navx.zeroYaw(); dir = 0; rotation = 0; }
			if (input.in.getPOV() != -1) {
				front = -input.in.getPOV();
				SmartDashboard.putNumber("FRONT", front);
				swerveCtrl.setAngles(front, front, front, front);
			}
		}
    rotation = input.in.getRawAxis(4);
		if (input.RIGHT.get()) {
			swerveCtrl.speed = 0.057;
			if (navx.balance() < -1.2) {
				swerveCtrl.swerve(0, 1-0.05*navx.balance(), 0, 0);
			}
			if (navx.balance() > 1.2) {
				swerveCtrl.swerve(0, -1-0.05*navx.balance(), 0, 0);
			}
			if (navx.balance() >= -1.2 && navx.balance() <= 1.2) {
				swerveCtrl.swerve(0, 0, 0, 0);
			}
		} else {
			swerveCtrl.speed = swerveCtrl.default_speed + 0.8*(1-swerveCtrl.default_speed)*input.in.getRawAxis(3);
			swerveCtrl.swerve(deadband(input.in.getRawAxis(0), 0.1), -deadband(input.in.getRawAxis(1), 0.1), rotation, front);
		}
    swerveCtrl.update();
		dash();
	}


	@Override
	public void disabledInit() {
		swerveCtrl.setVelocities(0, 0, 0, 0);
	}


	@Override
	public void disabledPeriodic() {}
}
