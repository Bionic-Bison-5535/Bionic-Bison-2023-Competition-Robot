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
    private double lastScored = -1;
    public int stage = 0; // 0 = Ready, 1 = Aligning Horizontally, 2 = Pressing Up Against Grid, 3 = Adjusting Arm Position, 4 = Done + Arm Up, 5 = Error / Cancel,

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

    public boolean align() {
        if (april.valid()) {
            if (april.inRange(april.width(), cubeWidthForPickUp, 30) && april.inRange(april.X(), 0, 7)) {
                stage = 2;
                swerveCtrl.swerve(0, 0, 0, 0);
                return true;
            } else {
                swerveCtrl.swerve((cubeWidthForPickUp-april.width())/90, april.X()/50, 0, 0);
                return false;
            }
        } else {
            swerveCtrl.swerve(0, 0, 0, 0);
            return true;
        }
    }

    public boolean getGamePiece() { // Returns true if done, otherwise must be run periodically
        counts += 1;
        if (stage == 0) {
            counts = 0;
            stage = 1;
        }
        if (stage == 1) {
            arm.pos(4);
            if (arm.all_there()) {
                stage++;
            }
        }
        if (stage == 2) {
            arm.pos(0);
            if (arm.all_there()) {
                stage++;
                counts = 0;
            }
        }
        if (stage == 3) {
            claw.take();
            if (alignToCube() || counts > 170) {
                stage++;
                counts = 0;
            }
        }
        if (stage == 4) {
            claw.take();
            swerveCtrl.swerve(0.2, 0, 0, 0);
            if (counts > 30) {
                stage++;
            }
        }
        if (stage == 5) {
            swerveCtrl.swerve(0, 0, 0, 0);
            claw.stop();
            arm.pos(4);
            if (arm.all_there()) {
                stage++;
            }
        }
        if (stage == 6) {
            swerveCtrl.swerve(0, 0, 0, 0);
            claw.stop();
            arm.pos(3);
            stage = 0;
            counts = 0;
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
