package frc.robot.smart_features;

import frc.robot.systems.Weswerve;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;
import frc.robot.systems.Navx;

public class Autonomous {

    private Weswerve swerveCtrl;
    private Arm arm;
    private Intake claw;
    private Navx navx;
    private GetObject collector;
    private Score score;

    private double dir = 0;
    private double rotation = 0;
    private double dir_accuracy = 1.7;

    public int stage = 0;
    /* Stage
     * 0 - Ready
     * 1 - Scoring first game piece
     * 2 - Going to game piece
     * 3 - Rotating
     * 4 - Getting game piece
     * 5 - Rotating
     * 6 - Going back to grid
     * 7 - Scoring
     * 8 - Going to charge station
     * 9 - Balancing
    */

    public boolean chargeUp = false;
    private int getting = 0;

    public Autonomous(Weswerve swerveAccess, Arm armAccess, Intake intakeAccess, Navx navxAccess, GetObject collectorAccess, Score scoreAccess) {
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
        navx = navxAccess;
        collector = collectorAccess;
        score = scoreAccess;
    }

    public void start() {
        stage = 1;
    }

    public void finish() {
        stage = 0;
    }

    public void update() {
        if (stage == 0) {
            swerveCtrl.swerve(0, 0, 0, 0);
            arm.pos(3);
        } else if (stage == 1) {
            arm.pos(2);
            if (arm.all_there()) {
                stage = 2;
                score.drop(0, true, false);
                arm.pos(3);
            }
        } else if (stage == 2) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(-0.5, 0, rotation, 0);
        } else if (stage == 3) {
            dir = 180;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
                if (collector.cubeCam.area() > collector.coneCam.area()) {
					getting = 0;
				} else {
					getting = 1;
				}
                stage = 4;
            }
            swerveCtrl.swerve(0, 0, rotation, 0);
        } else if (stage == 4) {
            if (collector.getGamePiece(getting)) {
                stage = 5;
            }
        } else if (stage == 5) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
                stage = 6;
            }
            swerveCtrl.swerve(0, 0, rotation, 0);
        } else if (stage == 6) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(0.5, 0, rotation, 0);
        } else if (stage == 7) {
            arm.pos(3);
            if (arm.all_there()) {
                stage = 8;
                score.drop(getting, true, false);
            }
        } else if (stage == 8) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(-0.5, -0.1, rotation, 0);
        } else if (stage == 9) {
            charge();
        }

        swerveCtrl.update();
        arm.update();
        claw.update();
    }

    public void charge() {
        if (navx.rawBalance() < 1.2 && navx.rawBalance() > -1.2) {
            swerveCtrl.swerve(0, 0, 0, 0);
        } else if (navx.rawBalance() >= 1.2) {
            swerveCtrl.swerve(-0.1, 0, 0, 0);
        } else {
            swerveCtrl.swerve(0.1, 0, 0, 0);
        }
    }

}
