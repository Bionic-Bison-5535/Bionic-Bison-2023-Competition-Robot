package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.systems.Weswerve;
import frc.robot.systems.Navx;
import frc.robot.systems.Controls;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;
import frc.robot.systems.Peg;
import frc.robot.systems.Signal;
import frc.robot.smart_features.GetObject;
import frc.robot.smart_features.Score;

public class Robot extends TimedRobot {

  	private final Weswerve swerveCtrl = new Weswerve(30, 31, 32, 33, 20, 21, 22, 23, 10, 11, 12, 13, 70, 100, 148, 358);
	private final Navx navx = new Navx();
	private final Controls primary = new Controls(0, 0.1);
	private final Controls secondary = new Controls(1, 0.1);
	private final Arm arm = new Arm(50, 51, 53);
	private final Intake claw = new Intake(55);
	private final Peg peg = new Peg(2, 3, -1, 1);
	private final Signal toHuman = new Signal(4, 5);
	private final GetObject collector = new GetObject(2, 1, swerveCtrl, arm, claw);
	private final Score score = new Score(0, swerveCtrl, arm, claw, navx);

	public boolean headless = true;
	public double dir = 0;
	public double dir_accuracy = 1;
	public double rotation = 0;
	public boolean resist = true;
	public boolean rawMode = false;
	public boolean finalMode = false;
	public double pwr2 = 0.15;
	private int getting;
	private double newAngle;
	private boolean needsReset;
	public int now = 0;
	/* now = ID of currently running dynamic periodic
	 * 0 = Driving
	 * 1 = Getting Object
	 * 2 = Preparing to Score
	 * 3 = Scoring Mode (With Input)
	 * 4 = Scoring top row
	*/

	Timer timer;

	public void dashInit() {
		SmartDashboard.putNumber("Pos 0 a", arm.pos_0_a);
		SmartDashboard.putNumber("Pos 0 b", arm.pos_0_b);
		SmartDashboard.putNumber("Pos 0 c", arm.pos_0_c);
		SmartDashboard.putNumber("Pos 1 a", arm.pos_1_a);
		SmartDashboard.putNumber("Pos 1 b", arm.pos_1_b);
		SmartDashboard.putNumber("Pos 1 c", arm.pos_1_c);
		SmartDashboard.putNumber("Pos 2 a", arm.pos_2_a);
		SmartDashboard.putNumber("Pos 2 b", arm.pos_2_b);
		SmartDashboard.putNumber("Pos 2 c", arm.pos_2_c);
		SmartDashboard.putNumber("Pos 3 a", arm.pos_3_a);
		SmartDashboard.putNumber("Pos 3 b", arm.pos_3_b);
		SmartDashboard.putNumber("Pos 3 c", arm.pos_3_c);
		SmartDashboard.putNumber("Robot Speed", swerveCtrl.default_speed);
		SmartDashboard.putNumber("Robot Steering Sharpness", swerveCtrl.steeringAmplifier);
		SmartDashboard.putNumber("A offset", swerveCtrl.A_offset);
		SmartDashboard.putNumber("B offset", swerveCtrl.B_offset);
		SmartDashboard.putNumber("C offset", swerveCtrl.C_offset);
		SmartDashboard.putNumber("D offset", swerveCtrl.D_offset);
		SmartDashboard.putNumber("Secondary Adjustment Strength", pwr2);
		SmartDashboard.putNumber("Cube Closeness for Pickup", collector.cubeWidthForPickUp);
		SmartDashboard.putNumber("Cone Closeness for Pickup", collector.coneWidthForPickUp);
	}

	public void dash() {

		SmartDashboard.putNumber("Alpha", arm.alpha.getEnc());
		SmartDashboard.putNumber("Beta", arm.beta.getEnc());
		SmartDashboard.putNumber("Theta", arm.theta.getEnc());

		SmartDashboard.putBoolean("Headless", headless);
		SmartDashboard.putNumber("Yaw", navx.yaw());
		SmartDashboard.putNumber("Balance", navx.balance());
		SmartDashboard.putBoolean("Resistance Enabled", resist);
		SmartDashboard.putNumber("Mode", now);
		SmartDashboard.putBoolean("Smart Features Enabled", !rawMode);
		SmartDashboard.putBoolean("Final Mode!", finalMode);
		SmartDashboard.putNumber("Alpha Encoder Value", arm.alpha.getEnc());
		SmartDashboard.putNumber("Beta Encoder Value", arm.beta.getEnc());
		SmartDashboard.putNumber("Intake Encoder Value", claw.intakeMotor.getEnc());
		SmartDashboard.putBoolean("Arm Reached Position", arm.all_there());
		SmartDashboard.putBoolean("Robot In Motion", navx.accel());
		SmartDashboard.putNumber("Points Earned", score.points);
		SmartDashboard.putNumber("Cubes", score.cubes);
		SmartDashboard.putNumber("Cones", score.cones);
		SmartDashboard.putBoolean("Peg Out", peg.actuated);
		arm.pos_0_a = SmartDashboard.getNumber("Pos 0 a", arm.pos_0_a);
		arm.pos_0_b = SmartDashboard.getNumber("Pos 0 b", arm.pos_0_b);
		arm.pos_0_c = SmartDashboard.getNumber("Pos 0 c", arm.pos_0_c);
		arm.pos_1_a = SmartDashboard.getNumber("Pos 1 a", arm.pos_1_a);
		arm.pos_1_b = SmartDashboard.getNumber("Pos 1 b", arm.pos_1_b);
		arm.pos_1_c = SmartDashboard.getNumber("Pos 1 c", arm.pos_1_c);
		arm.pos_2_a = SmartDashboard.getNumber("Pos 2 a", arm.pos_2_a);
		arm.pos_2_b = SmartDashboard.getNumber("Pos 2 b", arm.pos_2_b);
		arm.pos_2_c = SmartDashboard.getNumber("Pos 2 c", arm.pos_2_c);
		arm.pos_3_a = SmartDashboard.getNumber("Pos 3 a", arm.pos_3_a);
		arm.pos_3_b = SmartDashboard.getNumber("Pos 3 b", arm.pos_3_b);
		arm.pos_3_c = SmartDashboard.getNumber("Pos 3 c", arm.pos_3_c);
		swerveCtrl.default_speed = SmartDashboard.getNumber("Robot Speed", swerveCtrl.default_speed);
		swerveCtrl.steeringAmplifier = SmartDashboard.getNumber("Robot Steering Sharpness", swerveCtrl.steeringAmplifier);
		swerveCtrl.A_offset = SmartDashboard.getNumber("A offset", swerveCtrl.A_offset);
		swerveCtrl.B_offset = SmartDashboard.getNumber("B offset", swerveCtrl.B_offset);
		swerveCtrl.C_offset = SmartDashboard.getNumber("C offset", swerveCtrl.C_offset);
		swerveCtrl.D_offset = SmartDashboard.getNumber("D offset", swerveCtrl.D_offset);
		pwr2 = SmartDashboard.getNumber("Secondary Adjustment Strength", pwr2);
		collector.cubeWidthForPickUp = SmartDashboard.getNumber("Cube Closeness for Pickup", collector.cubeWidthForPickUp);
		collector.coneWidthForPickUp = SmartDashboard.getNumber("Cone Closeness for Pickup", collector.coneWidthForPickUp);
	}

	double cubed(double inputNumber) {
		return inputNumber * inputNumber * inputNumber;
	}

	void action(boolean dynamicPeriodicFunction, int defaultNowTo) {
		if (dynamicPeriodicFunction == true) {
			now = defaultNowTo;
		}
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
	public void autonomousInit() {
		now = 0;
		dir = 0;
		rotation = 0;
		resist = true;
		rawMode = false;
		finalMode = false;
		arm.reset();
		claw.reset();
	}


	@Override
	public void autonomousPeriodic() {}


	@Override
	public void teleopInit() {
		now = 0;
	}


	@Override
	public void teleopPeriodic() {

		if (secondary.Y.getAsBoolean()) {            // Secondary Controller Input:
			now = 0;
			collector.stage = 0;
			score.stage = 0;
		}
		if (secondary.X.getAsBoolean()) {
			navx.fullReset();
			while (secondary.X.getAsBoolean()) {}
			navx.zeroYaw();
			dir = 0;
		}
		if (secondary.BACK.getAsBoolean()) {
			rawMode = true;
		} else if (secondary.START.getAsBoolean()) {
			rawMode = false;
		}
		if (secondary.RIGHT.getAsBoolean()) {
			finalMode = false;
		} else if (secondary.LEFT.getAsBoolean()) {
			finalMode = true;
		}
		if (secondary.B.getAsBoolean()) {
			resist = false;
		} else if (secondary.A.getAsBoolean()) {
			resist = true;
		}
		if (secondary.stick(3) > 0.9) {
			peg.in();
		} else if (secondary.stick(2) > 0.9) {
			peg.out();
		}
    	if (Math.abs(secondary.stick(4)) > 0.15 && Math.abs(secondary.stick(5)) < Math.abs(secondary.stick(4))) {
			navx.correctYaw(secondary.stick(4));
			toHuman.off();
		} else {
			if (secondary.stick(5) < -0.1) {
				toHuman.cone();
			} else if (secondary.stick(5) > 0.1) {
				toHuman.cube();
			}
		}

		if (now == 0) {

			if (rawMode) {                           // RAW MODE:

				swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), primary.stick(4), 0);
				if (primary.A.getAsBoolean()) {
					peg.out();
				} else if (primary.B.getAsBoolean()) {
					peg.in();
				}
				if (primary.RIGHT.getAsBoolean()) {
					claw.open();
				} else if (primary.X.getAsBoolean()) {
					claw.close(0);
				} else if (primary.Y.getAsBoolean()) {
					claw.close(1);
				}
				if (primary.LEFT.getAsBoolean()) {
					rawMode = false;
				}
				if (primary.stick(5) > 0.4) {
					arm.pos(0);
				} else if (primary.stick(5) > -0.12) {
					arm.pos(3);
				} else if (primary.stick(5) > -0.97) {
					arm.pos(1);
				} else {
					arm.pos(2);
				}

			} else {                                 // NORMAL MODE:

				if (finalMode) {                         // Restrictive Final Mode Functionality:
					swerveCtrl.speed = swerveCtrl.default_speed * 0.3;
					if (primary.LEFT_STICK.getAsBoolean()) {
						peg.out();
					}
				
				} else {                                 // Restrictive Non-Final Mode Functionality:
					swerveCtrl.speed = swerveCtrl.default_speed;
					if (primary.X.getAsBoolean()) {
						getting = 0;
						collector.stage = 0;
						now = 1;
					} else if (primary.Y.getAsBoolean()) {
						getting = 1;
						collector.stage = 0;
						now = 1;
					} else if (primary.A.getAsBoolean()) {
						score.stage = 0;
						now = 2;
					} else if (primary.B.getAsBoolean()) {
						score.stage = 0;
						now = 4;
					}
					if (peg.actuated) {
						peg.in();
					}
				}

				if (primary.stick(2) > 0.9) {            // Other Primary Controller Code:
					headless = false;
				} else if (primary.stick(3) > 0.9) {
					headless = true;
				}
				if (primary.RIGHT.getAsBoolean()) {
					score.drop(2, false, true);
					arm.pos(3);
				} else if (primary.LEFT.getAsBoolean()) {
					if (primary.stick(0) >= 0) {
						dir -= 180;
					} else {
						dir += 180;
					}
					while (primary.LEFT.getAsBoolean()) {}
				} else if (primary.pov() != -1) {
					newAngle = (double)primary.pov();
					newAngle += 180;
					while (newAngle > dir+180) { newAngle -= 360; }
					while (newAngle < dir-180) { newAngle += 360; }
					dir = newAngle;
				}
				if (resist) {
					dir += cubed(5 * primary.stick(4));
					if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
						rotation = -0.05*(navx.yaw()-dir);
					} else {
						rotation = 0;
					}
				} else {
					rotation = cubed(primary.stick(4));
				}
				if (Math.abs(primary.stick(4)) < 0.1) {
					if (primary.stick(5) > 0.4) {
						arm.pos(0);
					} else if (primary.stick(5) > -0.12) {
						arm.pos(3);
					} else if (primary.stick(5) > -0.97) {
						arm.pos(1);
					} else {
						arm.pos(2);
					}
				}

				if (headless) {                          // Actual Drive:
					swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), rotation, navx.coterminalYaw()+180);
				} else {
					swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), rotation, 0);
				}

			}
		} else if (now == 1) {
			action(collector.getGamePiece(getting), 0);
		} else if (now == 2) {
			action(score.prepare(getting), 3);
		} else if (now == 3) {
			if (primary.stick(5) == 0) { arm.pos(1); }
			if (primary.stick(5) > 0) { arm.pos(0); }
			if (primary.stick(5) < 0) { arm.pos(2); }
			if (primary.RIGHT.getAsBoolean()) {
				score.drop(2, false, true);
				arm.pos(3);
				now = 0;
			}
		} else if (now == 4) {
			// This function not programmed yet, redirect to now = 0:
			now = 0;
		}

		// Static Periodics:
		swerveCtrl.update();
		arm.update();
		claw.update();

	}


	@Override
	public void testInit() {
		peg.in();
		claw.intakeMotor.setEnc(0);
		needsReset = true;
	}


	@Override
	public void testPeriodic() {
		if (needsReset) {
			if (swerveCtrl.resetMotors()) {
				swerveCtrl.tone();
				Timer.delay(2.5);
				swerveCtrl.swerve(0, 0, 0, 0);
				needsReset = false;
				SmartDashboard.putNumber("Arm Pos", arm.mostRecentPos);
			}
		} else {
			if (0 <= SmartDashboard.getNumber("Arm Pos", arm.mostRecentPos) && SmartDashboard.getNumber("Arm Pos", arm.mostRecentPos) <= 3) {
				arm.pos((int)(SmartDashboard.getNumber("Arm Pos", arm.mostRecentPos)));
				arm.update();
			}
		}
	}

}
