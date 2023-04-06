package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.systems.Weswerve;
import frc.robot.systems.Navx;
import frc.robot.systems.Controls;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;
import frc.robot.systems.Lights;
import frc.robot.smart_features.GetObject;
import frc.robot.smart_features.Score;
import frc.robot.smart_features.Autonomous;

public class Robot extends TimedRobot {

  	private final Weswerve swerveCtrl = new Weswerve(30, 31, 32, 33, 20, 21, 22, 23, 10, 11, 12, 13, 70, 100, 148, 358);
	private final Navx navx = new Navx();
	private final Controls primary = new Controls(0, 0.1);
	private final Controls secondary = new Controls(1, 0.1);
	private final Arm arm = new Arm(50, 51);
	private final Intake claw = new Intake(53, arm);
	private final Lights colors = new Lights(17);
	private final GetObject collector = new GetObject(1, swerveCtrl, arm, claw);
	private final Score score = new Score(2, arm, claw);
	private final Autonomous auto = new Autonomous(swerveCtrl, arm, claw, navx, collector, score, colors);

	private boolean smart = true;
	private boolean finalMode = false;
	private boolean conscious = false;
	private double dir = 0;
	private double rotation = 0;
	private double vert = 0;
	private double hor = 0;
	private double dir_accuracy = 1.7;
	private double initialAngle = 180;
	private boolean armEnabled = true;
	private double pwr2 = 0.15;
	private double time = 135;
	private int now = 0;
	/* now = ID of currently running dynamic periodic
	 * 0 = Driving
	 * 1 = Getting Object
	 * 2 = Preparing to Score
	 * 3 = Scoring Mode (With Input)
	*/
	private String selection1;
	private final SendableChooser<String> m_chooser = new SendableChooser<>();
	private double newAngle;
	private int secondary_pov;

	private void dashInit() {
		SmartDashboard.putNumber("Pos 0 a", arm.pos_0_a);
		SmartDashboard.putNumber("Pos 0 b", arm.pos_0_b);
		SmartDashboard.putNumber("Pos 1 a", arm.pos_1_a);
		SmartDashboard.putNumber("Pos 1 b", arm.pos_1_b);
		SmartDashboard.putNumber("Pos 2 a", arm.pos_2_a);
		SmartDashboard.putNumber("Pos 2 b", arm.pos_2_b);
		SmartDashboard.putNumber("Pos 3 a", arm.pos_3_a);
		SmartDashboard.putNumber("Pos 3 b", arm.pos_3_b);
		SmartDashboard.putNumber("Robot Speed", swerveCtrl.default_speed);
		SmartDashboard.putNumber("Robot Steering", swerveCtrl.steeringAmplifier);
		SmartDashboard.putNumber("A offset", swerveCtrl.A_offset);
		SmartDashboard.putNumber("B offset", swerveCtrl.B_offset);
		SmartDashboard.putNumber("C offset", swerveCtrl.C_offset);
		SmartDashboard.putNumber("D offset", swerveCtrl.D_offset);
		SmartDashboard.putNumber("Secondary Adjust", pwr2);
		SmartDashboard.putNumber("Cube Closeness for Pickup", collector.cubeWidthForPickUp);
		SmartDashboard.putNumber("Arm Pos", arm.mostRecentPos);
	}

	private void dash() {
		SmartDashboard.putBoolean("Smart Mode", smart);
		SmartDashboard.putBoolean("Final Mode!", finalMode);
		SmartDashboard.putNumber("Yaw", navx.yaw());
		SmartDashboard.putNumber("Balance", navx.balance());
		SmartDashboard.putNumber("Raw Balance", navx.rawBalance());
		SmartDashboard.putNumber("Dynamic Periodic", now);
		SmartDashboard.putNumber("Points Earned", score.points);
		SmartDashboard.putNumber("Cubes", score.cubes);
		SmartDashboard.putNumber("Auto Stage", (double)auto.stage);
		SmartDashboard.putNumber("Remaining Time", time);
		arm.pos_0_a = SmartDashboard.getNumber("Pos 0 a", arm.pos_0_a);
		arm.pos_0_b = SmartDashboard.getNumber("Pos 0 b", arm.pos_0_b);
		arm.pos_1_a = SmartDashboard.getNumber("Pos 1 a", arm.pos_1_a);
		arm.pos_1_b = SmartDashboard.getNumber("Pos 1 b", arm.pos_1_b);
		arm.pos_2_a = SmartDashboard.getNumber("Pos 2 a", arm.pos_2_a);
		arm.pos_2_b = SmartDashboard.getNumber("Pos 2 b", arm.pos_2_b);
		arm.pos_3_a = SmartDashboard.getNumber("Pos 3 a", arm.pos_3_a);
		arm.pos_3_b = SmartDashboard.getNumber("Pos 3 b", arm.pos_3_b);
		swerveCtrl.default_speed = SmartDashboard.getNumber("Robot Speed", swerveCtrl.default_speed);
		swerveCtrl.steeringAmplifier = SmartDashboard.getNumber("Robot Steering", swerveCtrl.steeringAmplifier);
		swerveCtrl.A_offset = SmartDashboard.getNumber("A offset", swerveCtrl.A_offset);
		swerveCtrl.B_offset = SmartDashboard.getNumber("B offset", swerveCtrl.B_offset);
		swerveCtrl.C_offset = SmartDashboard.getNumber("C offset", swerveCtrl.C_offset);
		swerveCtrl.D_offset = SmartDashboard.getNumber("D offset", swerveCtrl.D_offset);
		pwr2 = SmartDashboard.getNumber("Secondary Adjust", pwr2);
		collector.cubeWidthForPickUp = SmartDashboard.getNumber("Cube Closeness for Pickup", collector.cubeWidthForPickUp);
	}

	double cubed(double inputNumber) {
		return inputNumber * inputNumber * inputNumber;
	}

	private void resetAll() {
		navx.fullReset();
		now = 0;
		dir = 0;
		rotation = 0;
		arm.reset();
	}

	private void getTimeFromFMS() {
		time = DriverStation.getMatchTime();
		if (time == -1) { // FMS not connected
			time = 135;
		}
	}


	@Override
	public void robotInit() {
		dashInit();
		resetAll();
		m_chooser.setDefaultOption("YES", "CHARGE");
    	m_chooser.addOption("NO", "DON'T CHARGE");
		m_chooser.addOption("o/", "USELESS");
    	SmartDashboard.putData("CHARGE UP", m_chooser);
	}


	@Override
	public void robotPeriodic() {
		dash();
	}


	@Override
	public void autonomousInit() {
		score.april.setPip();
		resetAll();
		colors.setBrightness(0.5);
		colors.checkAlliance();
		colors.allianceColor();
		auto.start();
		time = 15;
		selection1 = m_chooser.getSelected();
	}


	@Override
	public void autonomousPeriodic() {
		getTimeFromFMS();
		if (time < 0.5) {
			swerveCtrl.lock();
			if (selection1 != "USELESS") {
				claw.fire();
			}
		} else {
			if (selection1 == "CHARGE") {
				auto.chargeAuto();
			} else if (selection1 == "DON'T CHARGE") {
				auto.noChargeAuto();
			} else {
				auto.snailAuto();
			}
		}
	}


	@Override
	public void teleopInit() {

		if (arm.mostRecentPos == 0) {
			colors.setBrightness(0.1);
		} else {
			colors.setBrightness(1);
		}
		colors.checkAlliance();
		colors.allianceColor();
		arm.pos(3);
		collector.cubeCam.setPip();
		time = 135;
		now = 0;
		dir = navx.yaw();

		if (auto.stage != 0) {
			auto.finish();
			if (auto.balanced() && selection1 == "CHARGE") {
				score.points += 12;
			} else if (selection1 == "CHARGE") {
				score.points += 8;
			}
		}

	}


	@Override
	public void teleopPeriodic() {

		getTimeFromFMS();

		if (arm.mostRecentPos == 0) {
			colors.orange();
		} else if (arm.mostRecentPos != 3) {
			colors.turquoise();
		} else if (time < 30 || finalMode) {
			colors.white();
		} else {
			colors.allianceColor();
		}

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
		if (secondary.B.getAsBoolean() || primary.LEFT_STICK.getAsBoolean()) {
			finalMode = false;
		} else if (secondary.A.getAsBoolean() || primary.RIGHT_STICK.getAsBoolean() || (time < 20 && Math.abs(navx.rawBalance()) > 10)) {
			finalMode = true;
		}
		if (Math.abs(secondary.stick(4)) > 0.2) {
			navx.correctYaw(0.5 * secondary.stick(4));
		}
		secondary_pov = secondary.pov();
		if (secondary_pov != -1) {
			if (secondary_pov == 0 || secondary_pov == 45 || secondary_pov == 315) { // Up
				armEnabled = true;
			} else if (secondary_pov >= 135 && secondary_pov <= 225) { // Down
				armEnabled = false;
			}
			if (secondary_pov >= 45 && secondary_pov <= 135) { // Right
				conscious = true;
			} else if (secondary_pov >= 225 && secondary_pov <= 315) { // Left
				conscious = false;
			}
		}
		if (secondary.BACK.getAsBoolean()) {
			smart = false;
		} else if (secondary.START.getAsBoolean()) {
			smart = true;
		}

		if (now == 0) {

			if (secondary.stick(2) > 0.1) {
				arm.pos(0);
			} else if (secondary.LEFT.getAsBoolean()) {
				arm.pos(3);
			} else if (secondary.stick(3) > 0.1) {
				arm.pos(1);
			} else if (secondary.stick(3) > 0.97 || secondary.RIGHT.getAsBoolean()) {
				arm.pos(2);
			}

			if (finalMode) {                         // Restrictive Final Mode Functionality:

				swerveCtrl.speed = swerveCtrl.default_speed * 0.35;

			} else {                                 // Restrictive Non-Final Mode Functionality:

				swerveCtrl.speed = swerveCtrl.default_speed * 1.5;
				if (primary.X.getAsBoolean()) {
					now = 1;
				}

			}

			if (primary.stick(2) > 0.1) {            // Other Primary Controller Code:
				claw.variableIntake(0.3*primary.stick(2));
			} else if (primary.stick(3) > 0.1) {
				score.drop(0);
			} else {
				claw.stop();
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
				rotation = cubed(primary.stick(4));
			}

			if (primary.B.getAsBoolean()) {          // Actual Drive:
				swerveCtrl.lock();
			} else if (smart) {
				swerveCtrl.swerve(cubed(-primary.stickWithRamp(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stickWithRamp(0))+(pwr2*secondary.stick(0)), rotation, navx.coterminalYaw()+initialAngle);
			} else {
				swerveCtrl.swerve(cubed(-primary.stickWithRamp(1))+(pwr2*(-secondary.stick(1))), cubed(primary.stickWithRamp(0))+(pwr2*secondary.stick(0)), rotation, 0);
			}

		} else if (now == 1) {
			if (collector.getGamePiece()) {
				now = 0;
				collector.stage = 0;
			}
		}

		// Static Periodics:
		swerveCtrl.update();
		if (armEnabled) {
			arm.update();
		}

	}


	@Override
	public void testInit() {
		claw.intakeMotor.setEnc(0);
		needsReset = true;
	}


	@Override
	public void testPeriodic() {
		colors.yellow();
		arm.beta.set(0.07);
	}

	@Override
	public void disabledInit() {
		colors.green();
	}

}
