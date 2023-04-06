package frc.robot.smart_features;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;

public class GetObject {

    public Limelight cubeCam;
    private Weswerve swerveCtrl;
    private Arm arm;
    private Intake claw;
    private double time = 0;
    private double startTime = 0;
    public int stage = 0; // 0 = Ready, 1 = Arm Out, 2 = Arm Down, 3 = Alignment, 4 = Go Forward, 5 = Arm Up, 6 = Arm In,

    public double cubeWidthForPickUp = 170;

    public GetObject(int cubePipeline, Weswerve swerveAccess, Arm armAccess, Intake intakeAccess) {
        cubeCam = new Limelight(cubePipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
    }

    private void nextStage(boolean transitionIf) {
        if (transitionIf) {
            startTime = DriverStation.getMatchTime();
            stage++;
        }
    }

    private void staticPeriodic() {
        time = DriverStation.getMatchTime();
    }

    public boolean alignToCube() {
        if (cubeCam.valid()) {
            if (cubeCam.inRange(cubeCam.X(), 0, 5)) {
                swerveCtrl.swerve(0, 0, 0, 0);
                return true;
            } else {
                swerveCtrl.swerve(0, -cubeCam.X()/70, 0, 0);
                return false;
            }
        } else {
            swerveCtrl.swerve(0, -0.05, 0, 0);
            return false;
        }
    }

    public boolean getGamePiece() { // Returns true if done, otherwise must be run periodically
        staticPeriodic();
        if (stage <= 0) {
            nextStage(true);
        }
        if (stage == 1) {
            if (arm.mostRecentPos == 3 || arm.mostRecentPos == 4) {
                arm.pos(4);
                nextStage(arm.all_there());
            } else {
                nextStage(true);
            }
        }
        if (stage == 2) {
            arm.pos(0);
            nextStage(arm.all_there());
        }
        if (stage == 3) {
            arm.pos(0);
            claw.take();
            nextStage(alignToCube());
        }
        if (stage == 4) {
            swerveCtrl.swerve(0.2, 0, 0, 0);
            nextStage(cubeCam.area() > 25 || startTime - 2 > time);
        }
        if (stage == 5) {
            claw.stop();
            swerveCtrl.swerve(0, 0, 0, 0);
            arm.pos(4);
            nextStage(arm.all_there());
        }
        if (stage >= 6) {
            swerveCtrl.swerve(0, 0, 0, 0);
            claw.stop();
            arm.pos(3);
            stage = 0;
            return true;
        } else {
            return false;
        }
    }

    public double getAlignment(double xPos) {
        if (cubeCam.valid()) {
            return (cubeCam.X()-xPos)/35;
        }
        return 0;
    }

    public boolean in() {
        return (cubeCam.area() > 24 && cubeCam.inRange(cubeCam.X(), 0, 10)) || (cubeCam.area() > 17 && Math.abs(cubeCam.X()) > 12);
    }

}
