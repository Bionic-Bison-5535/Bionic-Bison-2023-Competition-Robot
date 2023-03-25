package frc.robot.smart_features;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;
import frc.robot.systems.Navx;

public class Score {

    public Limelight april;
    private Weswerve swerveCtrl;
    private Arm arm;
    private Intake claw;
    private Navx navx;
    private boolean auto, tele;
    private double time = 120;
    private double lastScored = 200;
    public double setHeight = 15;
    public int stage = 0; // 0 = Ready, 1 = Aligning Horizontally, 2 = Pressing Up Against Grid, 3 = Done + Arm Up

    public int cones = 0;
    public int cubes = 0;
    public int points = 0;

    public Score(int aprilTagPipeline, Weswerve swerveAccess, Arm armAccess, Intake intakeAccess, Navx navxAccess) {
        april = new Limelight(aprilTagPipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
        navx = navxAccess;
    }

    private void nextStage(boolean transitionIf) {
        if (transitionIf) {
            stage++;
        }
    }

    public boolean alignHorizontal() {
        if (april.valid()) {
            if (april.inRange(april.X(), 0, 8)) {
                swerveCtrl.lock();
                return true;
            } else {
                swerveCtrl.swerve(0, -april.X()/50, -0.02*navx.yaw(), 0);
                return false;
            }
        } else {
            swerveCtrl.lock();
            return true;
        }
    }

    public double getAlignment() {
        if (april.valid()) {
            return (april.X()+10)/50;
        }
        return 0;
    }

    public boolean alignVertical() {
        if (april.valid()) {
            swerveCtrl.swerve(0.15, 0, -0.02*navx.yaw(), 0);
            return false;
        } else {
            swerveCtrl.lock();
            return true;
        }
    }

    public boolean run() { // Returns true if done, otherwise must be run periodically
        if (stage == 0) {
            stage = 1;
        }
        if (stage == 1) {
            nextStage(alignHorizontal());
        }
        if (stage == 2) {
            nextStage(alignVertical());
        }
        if (stage >= 3) {
            swerveCtrl.lock();
            stage = 0;
            return true;
        } else {
            return false;
        }
    }

    public void drop(int cube0_or_cone1) {
        claw.fire();
        time = DriverStation.getMatchTime();
        if (time + 3 < lastScored || time - 3 > lastScored) {
            lastScored = time;
            auto = DriverStation.isAutonomousEnabled();
            tele = DriverStation.isTeleopEnabled();
            if (cube0_or_cone1 == 0) {
                cubes += 1;
            } else if (cube0_or_cone1 == 1) {
                cones += 1;
            }
            if (cube0_or_cone1 == 0 || cube0_or_cone1 == 1) {
                if (arm.mostRecentPos == 2) {
                    if (auto) { points += 6; }
                    if (tele) { points += 5; }
                } else if (arm.mostRecentPos == 1) {
                    if (auto) { points += 4; }
                    if (tele) { points += 3; }
                } else {
                    if (auto) { points += 3; }
                    if (tele) { points += 2; }
                }
            }
        }
    }

}
