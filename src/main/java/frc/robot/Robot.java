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
import frc.robot.smart_features.Autonomous;

public class Robot extends TimedRobot {

  	private final Weswerve swerveCtrl = new Weswerve(30, 31, 32, 33, 20, 21, 22, 23, 10, 11, 12, 13, 70, 100, 148, 358);
	private final Navx navx = new Navx();
	private final Controls primary = new Controls(0, 0.1);
	private final Controls secondary = new Controls(1, 0.1);
	private final Arm arm = new Arm(50, 51, 53);
	private final Intake claw = new Intake(55);
	private final Peg peg = new Peg(8, 9, -0.64, 0.15);
	private final Signal toHuman = new Signal(4, 5);
	private final GetObject collector = new GetObject(2, 1, swerveCtrl, arm, claw);
	private final Score score = new Score(0, swerveCtrl, arm, claw, navx);
	private final Autonomous auto = new Autonomous(swerveCtrl, arm, claw, navx, collector, score);

	public boolean smart = true;
    public boolean finalMode = false;
    public double dir = 0;
    public double rotation = 0;
    public double dir_accuracy = 1.7;
    public double initialAngle = 180;
    public boolean armEnabled = true;
    public double pwr2 = 0.15;
    public int now = 0;
    /* now = ID of currently running dynamic periodic
	 * 0 = Driving
	 * 1 = Getting Object
	 * 2 = Preparing to Score
	 * 3 = Scoring Mode (With Input)
	 * 4 = Scoring in top row (part 1)
	 * 5 = Scoring in top row (part 2)
	*/

	private int getting = 0;
	private double newAngle;
	private boolean needsReset;
	private int secondary_pov;

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
		SmartDashboard.putNumber("Arm Pos", arm.mostRecentPos);
		SmartDashboard.putNumber("Auto Counts", (double)auto.counts);
		SmartDashboard.putNumber("Auto Stage", (double)auto.stage);
	}

	public void dash() {
		SmartDashboard.putNumber("Alpha", arm.alpha.getEnc());
		SmartDashboard.putNumber("Beta", arm.beta.getEnc());
		SmartDashboard.putNumber("Theta", arm.theta.getEnc());
		SmartDashboard.putNumber("Yaw", navx.yaw());
		SmartDashboard.putNumber("Balance", navx.balance());
		SmartDashboard.putNumber("Raw Balance", navx.rawBalance());
		SmartDashboard.putBoolean("Smart Mode", smart);
		SmartDashboard.putNumber("Mode", now);
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

	public double cubed(double inputNumber) {
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
		navx.fullReset();
		arm.reset();
		claw.reset();
		now = 0;
		dir = 0;
		rotation = 0;
	}


	@Override
	public void robotPeriodic() {
		dash();
	}


	@Override
	public void autonomousInit() {
		navx.fullReset();
		now = 0;
		dir = 0;
		rotation = 0;
		arm.reset();
		claw.reset();
		Timer.delay(1);
		auto.start();
	}


	@Override
	public void autonomousPeriodic() {
		auto.update();
	}


	@Override
	public void teleopInit() {
		auto.finish();
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
			dir = 0;
			rotation = 0;
		}
		if (secondary.RIGHT.getAsBoolean()) {
			finalMode = false;
			peg.in();
		} else if (secondary.LEFT.getAsBoolean()) {
			finalMode = true;
		}
		if (secondary.B.getAsBoolean()) {
			smart = false;
		} else if (secondary.START.getAsBoolean() || secondary.A.getAsBoolean()) {
			smart = true;
		}
		if (secondary.stick(2) > 0.2 || secondary.stick(3) > 0.2) {
			navx.correctYaw(secondary.stick(3)-secondary.stick(2));
		}
		secondary_pov = secondary.pov();
		if (secondary_pov == -1) {
			toHuman.off();
		} else {
			if (secondary_pov == 0 || secondary_pov == 45 || secondary_pov == 315) {
				toHuman.cone();
			} else if (secondary_pov == 180 || secondary_pov == 135 || secondary_pov == 225) {
				toHuman.cube();
			}
			if (secondary_pov == 90 || secondary_pov == 45 || secondary_pov == 135) {
				peg.out();
			} else if (secondary_pov == 270 || secondary_pov == 225 || secondary_pov == 315) {
				peg.in();
			}
		}
		if (secondary.BACK.getAsBoolean()) {
			armEnabled = false;
		} else if (secondary.START.getAsBoolean()) {
			armEnabled = true;
		}

		if (now == 0) {

			if (secondary.stick(5) > 0.4) {
				arm.pos(0);
			} else if (secondary.stick(5) > -0.1) {
				arm.pos(3);
			} else if (secondary.stick(5) > -0.97) {
				arm.pos(1);
			} else {
				arm.pos(2);
			}

			if (finalMode) {                         // Restrictive Final Mode Functionality:

				swerveCtrl.speed = swerveCtrl.default_speed * 0.35;
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
				}/* else if (primary.A.getAsBoolean()) {
					score.stage = 0;
					now = 2;
				} else if (primary.B.getAsBoolean()) {
					score.stage = 0;
					now = 4;
				}*/
				else if (primary.A.getAsBoolean()) {
					claw.changePos(1);
				} else if (primary.B.getAsBoolean()) {
					claw.changePos(-1);
				}

			}

			if (primary.stick(2) > 0.4) {            // Other Primary Controller Code:
				if (collector.cubeCam.area() > collector.coneCam.area()) {
					claw.close(0);
					getting = 0;
				} else {
					claw.close(1);
					getting = 1;
				}
			} else if (primary.stick(3) > 0.4) {
				score.drop(getting, false, true);
			} else if (primary.BACK.getAsBoolean()) {
				claw.close(0);
			} else if (primary.START.getAsBoolean()) {
				claw.close(1);
			}
			if (primary.RIGHT.getAsBoolean()) {
				dir = 0;
			} else if (primary.LEFT.getAsBoolean()) {
				dir = 180;
			} else if (primary.pov() != -1) {
				newAngle = (double)primary.pov();
				newAngle += 180;
				while (newAngle > dir+180) { newAngle -= 360; }
				while (newAngle < dir-180) { newAngle += 360; }
				dir = newAngle;
			}
			if (smart) {
				dir += 4 * cubed(primary.stick(4));
				if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
					rotation = -0.02*(navx.yaw()-dir);
				} else {
					rotation = 0;
				}
			} else {
				rotation = primary.stick(4);
			}

			if (smart) {                             // Actual Drive:
				swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), rotation, navx.coterminalYaw()+initialAngle);
			} else {
				swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), rotation, 0);
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
				score.drop(getting, false, true);
				arm.pos(3);
				now = 0;
			}
		} else if (now == 4) {
			action(collector.getGamePiece(getting), 5);
		} else if (now == 5) {
			arm.pos(2);
			if (arm.all_there()) {
				score.drop(getting, false, true);
				arm.pos(3);
				now = 0;
			}
		}

		// Static Periodics:
		swerveCtrl.update();
		if (armEnabled) {
			arm.update();
			claw.update();
		}
		peg.update();

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
				Timer.delay(1);
				swerveCtrl.swerve(0, 0, 0, 0);
				needsReset = false;
			}
		} else {
			arm.pos((int)(SmartDashboard.getNumber("Arm Pos", arm.mostRecentPos)));
			arm.update();
		}
	}

}
