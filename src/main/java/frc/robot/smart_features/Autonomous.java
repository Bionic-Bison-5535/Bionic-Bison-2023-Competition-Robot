package frc.robot.smart_features;

import edu.wpi.first.wpilibj.DriverStation;
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
    
    private double rotation = 0;
    private double dir_accuracy = 1.2;
    private double startTime = 15;
    private double time = 15;
    public boolean chargeUp = true;
    public int getting = 0;

    public int stage = 0;
    /* Stage
     * 0 - Ready
     * 1 - Moving arm up
     * 2 - Releasing cube
     * 3 - Driving back and lowering arm
     * 4 - Driving forward to charge station
     * 5 - Balancing
    */

    public Autonomous(Weswerve swerveAccess, Arm armAccess, Intake intakeAccess, Navx navxAccess, GetObject collectorAccess, Score scoreAccess) {
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
        navx = navxAccess;
        collector = collectorAccess;
        score = scoreAccess;
    }

    private void nextStage(boolean transitionIf, double timeout) {
        if (transitionIf || startTime - 5 > time) {
            startTime = DriverStation.getMatchTime();
            stage++;
        }
    }

    public void autonomousAction(double verticalThrust, double horizontalThrust, double direction, double clawSpeed, int armPos) {
        if (Math.abs(navx.yaw()-direction) > dir_accuracy) {
            rotation = -0.02*(navx.yaw()-direction);
        } else {
            rotation = 0;
        }
        swerveCtrl.swerve(verticalThrust, horizontalThrust, rotation, 0);
        claw.intakeMotor.set(clawSpeed);
        arm.pos(armPos);
    }

    public void start() {
        stage = 1;
    }

    public void finish() {
        stage = 0;
    }

    public void update() {
        if (stage == 0) {
            autonomousAction(0, 0, 0, 0, 3);
        } else if (stage == 1) {
            autonomousAction(0, 0, 0, 0, 2);
            nextStage(arm.all_there(), 3);
        } else if (stage == 2) {
            autonomousAction(0, 0, 0, 1, 2);
            nextStage(false, 1);
        } else if (stage == 3) {
            autonomousAction(-0.4, 0, 0, 1, 3);
            nextStage(false, 5);
        } else if (stage == 4) {
            autonomousAction(0.4, 0, 0, 0, 3);
            nextStage(false, 2);
        } else if (stage == 5) {
            charge();
        }

        swerveCtrl.update();
        arm.update();
    }

    public void charge() {
        if (navx.rawBalance() < 3 && navx.rawBalance() > -3) {
            swerveCtrl.lock();
        } else if (navx.rawBalance() > 0) {
            swerveCtrl.swerve(-0.1, 0, 0, 0);
        } else {
            swerveCtrl.swerve(0.1, 0, 0, 0);
        }
    }

}
