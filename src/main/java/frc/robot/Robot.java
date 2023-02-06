package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.systems.*;
import frc.robot.smart_features.*;

public class Robot extends TimedRobot {

  	private final Weswerve swerveCtrl = new Weswerve(30, 31, 32, 33, 20, 21, 22, 23, 10, 11, 12, 13, 70, 100, 148, 358);
	private final Arm arm = new Arm(50, 51, 0, 0);
	private final Navx navx = new Navx();
	private final Controls primary = new Controls(0, 0.05);
	private final Controls secondary = new Controls(1, 0.4);
	private final GetObject collector = new GetObject(2, 1, swerveCtrl, arm);
	
	Timer timer;

	public boolean headless = true;
	public double front = 0;
	public double dir = 0;
	public double dir_accuracy = 1;
	public double rotation = 0;
	public boolean resist = true;
	public boolean rawMode = false;
	public boolean finalMode = false;
	private int selection;
	private boolean selectionNegated;
	private double computedAngle;
	private int getting;
	private double newAngle;
	public int now = 0;
	/* 0 = Driving
	 * 1 = Adjusting Wheel
	 * 2 = Getting Object
	 * 3 = Preparing to Score / Aligning & Extending
	*/
	
	public void dashInit() {}

	public void dash() {}
	

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
		arm.update();
	}


	@Override
	public void teleopInit() {
		navx.zeroYaw();
		dir = 0;
	}


	@Override
	public void teleopPeriodic() {

		// Secondary Controller Input:
		if (secondary.BACK.get()) {
			rawMode = true;
		}
		if (secondary.START.get()) {
			rawMode = false;
		}
		if (secondary.LEFT.get()) {
			finalMode = true;
		}
		if (secondary.RIGHT.get()) {
			finalMode = false;
		}
		if (secondary.A.get()) {
			resist = true;
		}
		if (secondary.B.get()) {
			resist = false;
		}
		if (secondary.pov() != -1) {
			now = 1;
			selection = secondary.pov();
			swerveCtrl.setAngles(0, 0, 0, 0);
			swerveCtrl.setVelocities(0, 0, 0, 0);
		}
		if (secondary.Y.get()) {
			now = 0;
		}

		// Dynamic Periodics:
		if (now == 0) {
			if (rawMode) {
				swerveCtrl.swerve(-primary.stick(1), primary.stick(0), primary.stick(3)-primary.stick(2), 0);
				arm.changeExpansion(primary.stick(4));
				arm.changeUpDown(primary.stick(5));
			} else {
				if (finalMode) {
					swerveCtrl.speed = swerveCtrl.default_speed * 0.47;
					if (primary.B.get()) {
						if (navx.balance() < -2 && arm.there) {
							arm.changeExpansion(2);
						}
						if (navx.balance() > 2 && arm.there) {
							arm.changeExpansion(-2);
						}
					}
				} else {
					swerveCtrl.speed = swerveCtrl.default_speed;
					if (primary.X.get()) {
						getting = 0;
						now = 2;
					}
					if (primary.Y.get()) {
						getting = 1;
						now = 2;
					}
				}
				if (primary.A.get()) {
					navx.zeroYaw();
					dir = 0;
				}
				if (primary.LEFT.get()) {
					if (primary.stick(0) >= 0) {
						dir -= 180;
					} else {
						dir += 180;
					}
				}
				if (primary.pov() != -1) {
					newAngle = (double)primary.pov();
					while (newAngle > dir+180) { newAngle -= 360; }
					while (newAngle < dir-180) { newAngle += 360; }
					dir = newAngle;
				}
				if (resist) {
					dir += 3 * (primary.stick(3)-primary.stick(2));
					if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
						rotation = -0.04*(navx.yaw()-dir);
					} else {
						rotation = 0;
					}
				} else {
					rotation = (primary.stick(3)-primary.stick(2));
				}
				if (primary.BACK.get()) {
					headless = false;
				}
				if (primary.START.get()) {
					headless = true;
				}
				if (headless) {
					swerveCtrl.swerve(-primary.stick(1), primary.stick(0), rotation, navx.coterminalYaw());
				} else {
					swerveCtrl.swerve(-primary.stick(1), primary.stick(0), rotation, 0);
				}
			}
		}
		if (now == 1) {
			if (secondary.X.get()) {
				selectionNegated = !selectionNegated;
				if (selectionNegated) {
					if (selection == 0) { swerveCtrl.setVelocities(-0.4, 0, 0, 0); }
					if (selection == 90) { swerveCtrl.setVelocities(0, -0.4, 0, 0); }
					if (selection == 180) { swerveCtrl.setVelocities(0, 0, -0.4, 0); }
					if (selection == 270) { swerveCtrl.setVelocities(0, 0, 0, -0.4); }
				} else {
					if (selection == 0) { swerveCtrl.setVelocities(0.4, 0, 0, 0); }
					if (selection == 90) { swerveCtrl.setVelocities(0, 0.4, 0, 0); }
					if (selection == 180) { swerveCtrl.setVelocities(0, 0, 0.4, 0); }
					if (selection == 270) { swerveCtrl.setVelocities(0, 0, 0, 0.4); }
				}
				while (secondary.X.get()) {}
				swerveCtrl.setVelocities(0, 0, 0, 0);
			}
			computedAngle = swerveCtrl.i_tan(secondary.stick(0), secondary.stick(1));
			if (selectionNegated) { computedAngle += 180; }
			if (selection == 0) { swerveCtrl.A_offset = computedAngle; }
			if (selection == 90) { swerveCtrl.B_offset = computedAngle; }
			if (selection == 180) { swerveCtrl.C_offset = computedAngle; }
			if (selection == 270) { swerveCtrl.D_offset = computedAngle; }
		}
		if (now == 2) {
			if (collector.getGamePiece(getting)) {
				now = 0;
			}
		}
		if (now == 4) {}

		// Static Periodics:
		swerveCtrl.update();
		arm.update();
	}
	
	
	@Override
	public void testInit() {}


	@Override
	public void testPeriodic() {
		swerveCtrl.resetMotors();
	}

}
