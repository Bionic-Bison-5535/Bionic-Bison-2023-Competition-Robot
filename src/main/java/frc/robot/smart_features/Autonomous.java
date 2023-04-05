package frc.robot.smart_features;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.systems.Weswerve;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;
import frc.robot.systems.Navx;
import frc.robot.systems.Lights;

public class Autonomous {

    private Weswerve swerveCtrl;
    private Arm arm;
    private Intake claw;
    private Navx navx;
    private GetObject collector;
    private Score score;
    private Lights colors;
    
    private double rotation = 0;
    private double dir_accuracy = 1.2;
    private double startTime = 15;
    private double time = 15;
    public int stage = 0;

    public Autonomous(Weswerve swerveAccess, Arm armAccess, Intake intakeAccess, Navx navxAccess, GetObject collectorAccess, Score scoreAccess, Lights colorAccess) {
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
        navx = navxAccess;
        collector = collectorAccess;
        score = scoreAccess;
        colors = colorAccess;
    }

    private void nextStage(boolean transitionIf, double timeout) {
        if (transitionIf || startTime - timeout > time) {
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

    public void noChargeAuto() {
        time = DriverStation.getMatchTime();
        if (stage == 0) {
            autonomousAction(0, 0, 0, 0, 3);
        } else if (stage == 1) {
            autonomousAction(0, 0, 0, 0, 2);
            nextStage(arm.all_there(), 3);
        } else if (stage == 2) {
            autonomousAction(0, 0, 0, 1, 2);
            nextStage(false, 1);
        } else if (stage == 3) {
            colors.orange();
            autonomousAction(-0.7, score.getAlignment(-10), 0, 1, 3);
            nextStage(false, 1.7);
        } else if (stage == 4) {
            collector.cubeCam.setPip();
            autonomousAction(0, 0, 180, 0, 3);
            nextStage(false, 1.5);
        } else if (stage == 5) {
            colors.yellow();
            nextStage(collector.getGamePiece(), 4);
        } else if (stage == 6) {
            colors.turquoise();
            autonomousAction(0, 0, 180, 0, 3);
            nextStage(false, 1.2);
        } else if (stage == 7) {
            autonomousAction(0, 0, 0, 0, 3);
            nextStage(false, 1.7);
        } else if (stage == 8) {
            colors.blue();
            autonomousAction(0.7, score.getAlignment(-7), 0, 0, 1);
            nextStage(score.closeEnough(), 3);
        } else if (stage >= 9) {
            colors.white();
            autonomousAction(0, 0, 0, 0, 1);
        }

        swerveCtrl.update();
        arm.update();
    }

    public void chargeAuto() {
        time = DriverStation.getMatchTime();
        if (stage == 0) {
            autonomousAction(0, 0, 0, 0, 3);
        } else if (stage == 1) {
            autonomousAction(0, 0, 0, 0, 2);
            nextStage(arm.all_there(), 3);
        } else if (stage == 2) {
            autonomousAction(0, 0, 0, 1, 2);
            nextStage(false, 1);
        } else if (stage == 3) {
            colors.orange();
            autonomousAction(-0.5, 0, 0, 1, 3);
            nextStage(false, 2.6);
        } else if (stage == 4) {
            colors.yellow();
            autonomousAction(0.4, 0, 0, 0, 3);
            nextStage(false, 1.87);
        } else if (stage >= 5) {
            charge();
        }
        
        swerveCtrl.update();
        arm.update();
    }
    
    public void snailAuto() {
        time = DriverStation.getMatchTime();
        if (stage == 0) {
            autonomousAction(0, 0, 0, 0, 3);
        } else if (stage == 1) {
            autonomousAction(0, 0, 0, 0, 2);
            nextStage(arm.all_there(), 3);
        } else if (stage == 2) {
            autonomousAction(0, 0, 0, 1, 2);
            nextStage(false, 1);
        } else if (stage == 3) {
            autonomousAction(-0.5, 0, 0, 1, 3);
            nextStage(false, 1.7);
        } else if (stage >= 4) {
            collector.cubeCam.setPip();
            autonomousAction(0, 0, 180, 0, 3);
        }

        swerveCtrl.update();
        arm.update();
    }

    public void charge() {
        if (balanced()) {
            swerveCtrl.lock();
            colors.turquoise();
        } else if (navx.rawBalance() > 0) {
            swerveCtrl.swerve(-0.1, 0, 0, 0);
            colors.red();
        } else {
            swerveCtrl.swerve(0.1, 0, 0, 0);
            colors.red();
        }
    }

    public boolean balanced() {
        if (navx.rawBalance() < 3 && navx.rawBalance() > -3) {
            return true;
        }
        return false;
    }

}
