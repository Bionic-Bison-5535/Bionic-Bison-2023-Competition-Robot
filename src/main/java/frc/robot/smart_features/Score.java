package frc.robot.smart_features;

import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;

public class Score {

    public Limelight tape;
    private Weswerve swerveCtrl;
    private Arm arm;
    public int stage = 0; // 0 = Ready, 1 = Aligning Horizontally, 2 = Pressing Up Against Grid, 3 = Adjusting Arm Angle, 4 = Extending Arm, 5 = Error / Cancel,

    public int cones = 0;
    public int cubes = 0;
    public int points = 0;

    public Score(int reflectiveTapePipeline, Weswerve swerveAccess, Arm armAccess) {
        tape = new Limelight(reflectiveTapePipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
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
                    swerveCtrl.swerve(-0.05, -tape.X()/50, 0, 0);
                }
            } else {
                stage = 5;
                swerveCtrl.swerve(0, 0, 0, 0);
            }
        }
    }
    /* Function not done:
    public boolean prepare(int cube0_or_cone1) { // Returns true if done, otherwise must be run periodically
        if (stage < 2) {
            if (cube0_or_cone1 == 0) {
                align();
            } else {
                align();
            }
        }
        if (stage == 2) {
            arm.pos(0);
            if (arm.there) {
                stage = 3;
            }
        }
        if (stage == 3) {
            //grab
            // if (grabber squeezing tight) {
                stage = 4;
            // }
        }
        if (stage == 4) {
            arm.pos(3);
        }
        if (stage < 4) {
            return false;
        } else {
            stage = 0;
            return true;
        }
    }
    */
}
