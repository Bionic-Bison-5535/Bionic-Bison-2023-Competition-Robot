package frc.robot.smart_features;

import edu.wpi.first.wpilibj.Timer;
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
    public int counts = 0;
    public boolean chargeUp = true;
    public int getting = 0;

    public int stage = 0;
    /* Stage
     * 0 - Ready
     * 1 - Moving arm up
     * 2 - Driving Forward
     * 3 - Releasing cube
     * 4 - Driving back and lowering arm
     * 5 - Driving forward to charge station
     * 6 - Balancing
    */

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
        counts = 0;
    }

    public void finish() {
        stage = 0;
    }

    public void update() {
        counts += 1;
        if (stage == 0) {
            swerveCtrl.swerve(0, 0, 0, 0);
            arm.pos(3);
        } else if (stage == 1) {
            arm.pos(2);
            if (arm.all_there()) {
                stage += 1;
                counts = 0;
            }
        } else if (stage == 2) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(0.15, 0, rotation, 0);
            if (counts > 150) {
                stage += 1;
                counts = 0;
            }
        } else if (stage == 3) {
            swerveCtrl.swerve(0, 0, 0, 0);
            score.drop(0, true, false);
            if (counts > 27) {
                stage += 1;
                counts = 0;
                arm.pos(3);
            }
        } else if (stage == 4) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(-0.4, 0, rotation, 0);
            if (counts > 200) {
                if (chargeUp) {
                    stage = 5;
                } else {
                    stage = 0;
                }
                counts = 0;
                arm.pos(3);
                claw.close(0);
            }
        } else if (stage == 5) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(0.5, 0, rotation, 0);
            if (counts > 77) {
                stage += 1;
                counts = 0;
            }
        } else if (stage == 6) {
            charge();
        }

        swerveCtrl.update();
        arm.update();
        claw.update();
    }

    public void charge() {
        if (navx.rawBalance() < 3 && navx.rawBalance() > -3) {
            swerveCtrl.swerve(0, 0, 0, 0);
        } else if (navx.rawBalance() > 0) {
            swerveCtrl.swerve(-0.1, 0, 0, 0);
        } else {
            swerveCtrl.swerve(0.1, 0, 0, 0);
        }
    }

}
