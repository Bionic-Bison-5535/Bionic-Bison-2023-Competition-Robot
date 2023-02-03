package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.systems.*;

public class Robot extends TimedRobot {

  	private final Weswerve swerveCtrl = new Weswerve(30, 31, 32, 33, 20, 21, 22, 23, 10, 11, 12, 13, 70, 100, 148, 358);
	private final Controls primary = new Controls(0, 0.05);
	private final Controls secondary = new Controls(0, 0.05);
	private final Navx navx = new Navx();
	
	Timer timer;

	public boolean headless = true;
	public double front = 0;
	public double dir = 0;
	public double dir_accuracy = 1;
	public double rotation = 0;
	public double resist = true;
	
	public void dashInit() {
		SmartDashboard.putNumber("A Offset", swerveCtrl.A_offset);
		SmartDashboard.putNumber("B Offset", swerveCtrl.B_offset);
		SmartDashboard.putNumber("C Offset", swerveCtrl.C_offset);
		SmartDashboard.putNumber("D Offset", swerveCtrl.D_offset);
		SmartDashboard.putNumber("Speed MAX", swerveCtrl.default_speed);
		SmartDashboard.putNumber("Steer MAX", swerveCtrl.steeringAmplifier);
		SmartDashboard.putNumber("FRONT", front);
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
		SmartDashboard.putNumber("A Angle", swerveCtrl.frontLeft.getAbsolutePosition());
		SmartDashboard.putNumber("B Angle", swerveCtrl.frontRight.getAbsolutePosition());
		SmartDashboard.putNumber("C Angle", swerveCtrl.backRight.getAbsolutePosition());
		SmartDashboard.putNumber("D Angle", swerveCtrl.backLeft.getAbsolutePosition());
		SmartDashboard.putBoolean("A Negation", swerveCtrl.negation0);
		SmartDashboard.putBoolean("B Negation", swerveCtrl.negation1);
		SmartDashboard.putBoolean("C Negation", swerveCtrl.negation2);
		SmartDashboard.putBoolean("D Negation", swerveCtrl.negation3);
		SmartDashboard.putBoolean("Moving Enabled", swerveCtrl.move);
		SmartDashboard.putBoolean("Headless", headless);
		SmartDashboard.putNumber("yaw", navx.yaw());
		SmartDashboard.putNumber("balance", navx.balance());
		SmartDashboard.putNumber("dir", dir);
	}
	

	@Override
	public void robotInit() {
		dashInit();
	}


	@Override
	public void robotPeriodic() {
		dash();
	}


	@Override
	public void autonomousInit() {}


	@Override
	public void autonomousPeriodic() {
		swerveCtrl.update();
	}


	@Override
	public void teleopInit() {
		navx.zeroYaw();
	}


	@Override
	public void teleopPeriodic() {
		
		if (resist) {
			dir += 2.7 * (primary.stick(4));
			if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
				rotation = -0.04*(navx.yaw()-dir);
			} else {
				rotation = 0;
			}
		} else {
			rotation = primary.stick(4);
		}
		
		
		if (headless) {
			front = navx.coterminalYaw();
			if (primary.BACK.get()) { headless = false; front = 0; navx.zeroYaw(); dir = 0; }
			if (primary.LEFT.get()) { navx.zeroYaw(); rotation = 0; dir = 0; }
		} else {
			if (primary.START.get()) { headless = true; navx.zeroYaw(); dir = 0; rotation = 0; }
			if (primary.in.getPOV() != -1) {
				front = -primary.in.getPOV();
				SmartDashboard.putNumber("FRONT", front);
				swerveCtrl.setAngles(front, front, front, front);
			}
		}
    		
		if (primary.RIGHT.get()) { // Autobalance
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
			swerveCtrl.speed = swerveCtrl.default_speed + 0.8*(1-swerveCtrl.default_speed)*primary.stick(3);
			swerveCtrl.swerve(-primary.stick(1), primary.stick(0), rotation, front);
		}
		
    	swerveCtrl.update();
	}


	@Override
	public void disabledInit() {
		swerveCtrl.setVelocities(0, 0, 0, 0);
	}


	@Override
	public void disabledPeriodic() {}
	
	
	@Override
	public void testInit() {
		swerveCtrl.setAngles(0, 0, 0, 0);
	}


	@Override
	public void testPeriodic() {}

}
