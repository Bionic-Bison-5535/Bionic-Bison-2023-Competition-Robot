package frc.robot.smart_features;

import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;
import frc.robot.systems.Navx;

public class Score {

    public Limelight tape;
    private Weswerve swerveCtrl;
    private Arm arm;
    private Navx navx;
    public int stage = 0; // 0 = Ready, 1 = Aligning Horizontally, 2 = Pressing Up Against Grid, 3 = Adjusting Arm Position, 4 = Done, 5 = Error / Cancel,

    public int cones = 0;
    public int cubes = 0;
    public int points = 0;

    public Score(int reflectiveTapePipeline, Weswerve swerveAccess, Arm armAccess, Navx navxAccess) {
        tape = new Limelight(reflectiveTapePipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
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
                    swerveCtrl.swerve(-0.05, -tape.X()/50, 0, 0);
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
            swerveCtrl.swerve(0.3, 0, 0, 0);
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

}
