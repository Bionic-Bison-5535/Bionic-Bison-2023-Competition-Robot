package frc.robot.smart_features;

import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;
import frc.robot.systems.Navx;

public class Score {

    public Limelight tape;
    private Weswerve swerveCtrl;
    private Arm arm;
    private Intake claw;
    private Navx navx;
    public int stage = 0; // 0 = Ready, 1 = Aligning Horizontally, 2 = Pressing Up Against Grid, 3 = Adjusting Arm Position, 4 = Done + Arm Up, 5 = Error / Cancel,

    public int cones = 0;
    public int cubes = 0;
    public int points = 0;

    public Score(int reflectiveTapePipeline, Weswerve swerveAccess, Arm armAccess, Intake intakeAccess, Navx navxAccess) {
        tape = new Limelight(reflectiveTapePipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
        navx = navxAccess;
    }

    public void align(int cube0_or_cone1) {
        if (stage == 0) {
            stage = 1;
        }
        if (stage == 1) {
            if (tape.valid()) {
                if (tape.inRange(tape.X(), 0, 3)) {
                    stage = 2;
                    swerveCtrl.swerve(0, 0, 0, 0);
                } else {
                    swerveCtrl.swerve(-0.05, tape.X()/40, 0, 0);
                }
            } else {
                stage = 5;
                swerveCtrl.swerve(0, 0, 0, 0);
            }
        }
    }
    
    public boolean prepare(int cube0_or_cone1) { // Returns true if done, otherwise must be run periodically
        if (stage < 2) {
            align(cube0_or_cone1);
        }
        if (stage == 2) {
            arm.pos(1);
            swerveCtrl.swerve(0.5, 0, 0, 0);
            if (!navx.accel()) {
                stage = 3;
            }
        }
        if (stage == 3) {
            arm.pos(1);
            swerveCtrl.swerve(0, 0, 0, 0);
            if (arm.there) {
                stage = 4;
            }
        }
        if (stage < 4) {
            return false;
        } else {
            stage = 0;
            return true;
        }
    }

    public void drop(int cube0_or_cone1, boolean auto, boolean tele) {
        // claw open
        if (cube0_or_cone1 == 0) {
            cubes += 1;
        }
        if (cube0_or_cone1 == 1) {
            cones += 1;
        }
        if (cube0_or_cone1 == 0 || cube0_or_cone1 == 1) {
            if (arm.mostRecentPos == 2) {
                if (auto) { points += 6; }
                if (tele) { points += 5; }
            }
            if (arm.mostRecentPos == 1) {
                if (auto) { points += 4; }
                if (tele) { points += 3; }
            }
            if (arm.mostRecentPos == 0) {
                if (auto) { points += 3; }
                if (tele) { points += 2; }
            }
        }
    }

}
