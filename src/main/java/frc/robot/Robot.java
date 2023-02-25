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
import frc.robot.smart_features.GetObject;
import frc.robot.smart_features.Score;

public class Robot extends TimedRobot {

  	private final Weswerve swerveCtrl = new Weswerve(30, 31, 32, 33, 20, 21, 22, 23, 10, 11, 12, 13, 70, 100, 148, 358);
	private final Navx navx = new Navx();
	private final Controls primary = new Controls(0, 0.1);
	private final Controls secondary = new Controls(1, 0.2);
	private final Arm arm = new Arm(50, 51, 0, 0);
	private final Intake claw = new Intake(55, 30);
	private final Peg peg = new Peg(0, 1, 0, 0.7);
	private final GetObject collector = new GetObject(2, 1, swerveCtrl, arm, claw);
	private final Score score = new Score(0, swerveCtrl, arm, claw, navx);
	
	Timer timer;

	public boolean headless = true;
	public double front = 0;
	public double dir = 0;
	public double dir_accuracy = 1;
	public double rotation = 0;
	public boolean resist = true;
	public boolean rawMode = false;
	public boolean finalMode = false;
	public double pwr2 = 0.15;
	private int getting;
	private double newAngle;
	private boolean resetClaw;
	public int now = 0;
	/* now = ID of currently running dynamic periodic
	 * 0 = Driving
	 * 1 = Getting Object
	 * 2 = Preparing to Score
	 * 3 = Scoring Mode (With Input)
	*/

	public void dashInit() {
		SmartDashboard.putNumber("Pos 0 x", arm.pos_0_x);
		SmartDashboard.putNumber("Pos 0 y", arm.pos_0_y);
		SmartDashboard.putNumber("Pos 1 x", arm.pos_1_x);
		SmartDashboard.putNumber("Pos 1 y", arm.pos_1_y);
		SmartDashboard.putNumber("Pos 2 x", arm.pos_2_x);
		SmartDashboard.putNumber("Pos 2 y", arm.pos_2_y);
		SmartDashboard.putNumber("Pos 3 x", arm.pos_3_x);
		SmartDashboard.putNumber("Pos 3 y", arm.pos_3_y);
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
		SmartDashboard.putBoolean("Arm Reached Position", arm.there());
		SmartDashboard.putBoolean("Robot In Motion", navx.accel());
		SmartDashboard.putNumber("Points Earned", score.points);
		SmartDashboard.putNumber("Cubes", score.cubes);
		SmartDashboard.putNumber("Cones", score.cones);
		SmartDashboard.putBoolean("Peg Out", peg.actuated);
		arm.pos_0_x = SmartDashboard.getNumber("Pos 0 x", arm.pos_0_x);
		arm.pos_0_y = SmartDashboard.getNumber("Pos 0 y", arm.pos_0_y);
		arm.pos_1_x = SmartDashboard.getNumber("Pos 1 x", arm.pos_1_x);
		arm.pos_1_y = SmartDashboard.getNumber("Pos 1 y", arm.pos_1_y);
		arm.pos_2_x = SmartDashboard.getNumber("Pos 2 x", arm.pos_2_x);
		arm.pos_2_y = SmartDashboard.getNumber("Pos 2 y", arm.pos_2_y);
		arm.pos_3_x = SmartDashboard.getNumber("Pos 3 x", arm.pos_3_x);
		arm.pos_3_y = SmartDashboard.getNumber("Pos 3 y", arm.pos_3_y);
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
		dir = 0;
	}


	@Override
	public void autonomousPeriodic() {
		swerveCtrl.update();
		arm.update();
		claw.update();
	}


	@Override
	public void teleopInit() {
		now = 0;
	}


	@Override
	public void teleopPeriodic() {

		if (secondary.BACK.getAsBoolean()) {          // Secondary Controller Input:
			rawMode = true;
		}
		if (secondary.START.getAsBoolean()) {
			rawMode = false;
		}
		if (secondary.LEFT.getAsBoolean()) {
			finalMode = true;
		}
		if (secondary.RIGHT.getAsBoolean()) {
			finalMode = false;
		}
		if (secondary.A.getAsBoolean()) {
			resist = true;
		}
		if (secondary.B.getAsBoolean()) {
			resist = false;
		}
		if (secondary.X.getAsBoolean()) {
			navx.fullReset();
			while (secondary.X.getAsBoolean()) {}
			navx.zeroYaw();
			dir = 0;
		}
		if (secondary.Y.getAsBoolean()) {
			now = 0;
			collector.stage = 0;
			score.stage = 0;
		}
		if (secondary.stick(3) > 0.9) {
			peg.in();
		}
		if (secondary.stick(2) > 0.9) {
			peg.out();
		}
		navx.correctYaw(0.2*secondary.stick(4));


		if (now == 0) {

			if (rawMode) {                   // RAW MODE:

				swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), primary.stick(4), 0);
				arm.setRaw(primary.stick(3)-primary.stick(2), -primary.stick(5));
				if (primary.LEFT.getAsBoolean()) {
					claw.close();
				}
				if (primary.RIGHT.getAsBoolean()) {
					claw.open();
				}
				if (primary.A.getAsBoolean()) {
					rawMode = false;
				}
				if (primary.X.getAsBoolean()) {
					peg.out();
				}
				if (primary.Y.getAsBoolean()) {
					peg.in();
				}
			
			} else {                         // NORMAL MODE:
				
				if (finalMode) {                 // Restrictive Final Mode Functionality:
					swerveCtrl.speed = swerveCtrl.default_speed * 0.3;
					if (primary.LEFT_STICK.getAsBoolean()) {
						peg.out();
					}
				
				} else {                         // Restrictive Non-Final Mode Functionality:
					swerveCtrl.speed = swerveCtrl.default_speed;
					if (primary.X.getAsBoolean()) {
						getting = 0;
						collector.stage = 0;
						now = 1;
					}
					if (primary.Y.getAsBoolean()) {
						getting = 1;
						collector.stage = 0;
						now = 1;
					}
					if (primary.A.getAsBoolean()) {
						score.stage = 0;
						now = 2;
					}
					if (peg.actuated) {
						peg.in();
					}
				}

				if (primary.stick(2) > 0.9) {        // General Primary Controller Button Actions:
					headless = false;
				}
				if (primary.stick(3) > 0.9) {
					headless = true;
				}
				if (primary.A.getAsBoolean()) {            
					navx.zeroYaw();
					dir = 0;
				}
				if (primary.RIGHT.getAsBoolean()) {
					score.drop(2, false, true);
				}
				if (primary.LEFT.getAsBoolean()) {
					if (primary.stick(0) >= 0) {
						dir -= 180;
					} else {
						dir += 180;
					}
					while (primary.LEFT.getAsBoolean()) {}
				}
				if (primary.pov() != -1) {
					newAngle = (double)primary.pov();
					newAngle += 180;
					while (newAngle > dir+180) { newAngle -= 360; }
					while (newAngle < dir-180) { newAngle += 360; }
					dir = newAngle;
				}
				if (resist) {
					dir += 4 * primary.stick(4);
					if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
						rotation = -0.01*(navx.yaw()-dir);
						if (rotation > 0) {
							rotation += 0.1;
						} else {
							rotation -= 0.1;
						}
					} else {
						rotation = 0;
					}
				} else {
					rotation = primary.stick(4);
				}
				
				if (headless) {                  // Actual Drive:
					swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), rotation, navx.coterminalYaw()+180);
				} else {
					swerveCtrl.swerve(cubed(-primary.stick(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stick(0))+(pwr2*secondary.stick(0)), rotation, 0);
				}
				
			}
		}
		if (now == 1) {
			action(collector.getGamePiece(getting), 0);
		}
		if (now == 2) {
			action(score.prepare(getting), 3);
		}
		if (now == 3) {
			if (primary.stick(5) == 0) { arm.pos(1); }
			if (primary.stick(5) > 0) { arm.pos(0); }
			if (primary.stick(5) < 0) { arm.pos(2); }
			if (primary.RIGHT.getAsBoolean()) {
				score.drop(2, false, true);
				arm.pos(3);
				now = 0;
			}
		}
		
		// Static Periodics:
		swerveCtrl.update();
		arm.update();
		claw.update();
	}
	
	
	@Override
	public void testInit() {
		peg.in();
		resetClaw = true;
	}


	@Override
	public void testPeriodic() {
		if (resetClaw) {
			if (claw.zeroIntake()) {
				resetClaw = false;
			} else {
				Timer.delay(0.5);
			}
		} else {
			if (swerveCtrl.resetMotors()) {
				swerveCtrl.tone();
			}
		}
	}

}
