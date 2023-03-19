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
            claw.stop();
        } else if (stage == 1) {
            arm.pos(1);
            if (arm.all_there()) {
                stage = 3;
                counts = 0;
            }
            claw.stop();
        } else if (stage == 2) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(0.3, 0, rotation, 0);
            if (counts > 35) {
                stage += 1;
                counts = 0;
            }
            claw.stop();
        } else if (stage == 3) {
            swerveCtrl.swerve(0, 0, 0, 0);
            if (counts > 20) {
                stage += 1;
                counts = 0;
                arm.pos(3);
            }
            claw.fire();
        } else if (stage == 4) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(-0.4, 0, rotation, 0);
            if (counts > 170) {
                if (chargeUp) {
                    stage = 5;
                    swerveCtrl.swerve(0, 0, 0, 0);
                    Timer.delay(1);
                } else {
                    stage = 0;
                }
                counts = 0;
                arm.pos(3);
            }
            claw.stop();
        } else if (stage == 5) {
            dir = 0;
            if (Math.abs(navx.yaw()-dir) > dir_accuracy) {
                rotation = -0.02*(navx.yaw()-dir);
            } else {
                rotation = 0;
            }
            swerveCtrl.swerve(0.3, 0, rotation, 0);
            if (counts > 190) {
                stage += 1;
                counts = 0;
            }
            claw.stop();
        } else if (stage == 6) {
            charge();
            claw.stop();
        }

        swerveCtrl.update();
        arm.update();
    }

    public void charge() {
        if (navx.rawBalance() < 3 && navx.rawBalance() > -3) {
            swerveCtrl.lock();
        } else if (navx.rawBalance() > 0) {
            swerveCtrl.swerve(-0.15, 0, 0, 0);
        } else {
            swerveCtrl.swerve(0.15, 0, 0, 0);
        }
    }

}
