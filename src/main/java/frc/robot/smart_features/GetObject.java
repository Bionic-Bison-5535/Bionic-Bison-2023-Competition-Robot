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
    public int stage = 0; // 0 = Ready, 1 = Arm Out, 2 = Arm Down, 3 = Alignment, 4 = Arm Up, 5 = Arm In,

    public double cubeWidthForPickUp = 150;

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

    private double positive(double inputValue) {
        if (inputValue < 0) {
            return 0;
        }
        return inputValue;
    }

    public boolean alignToCube() {
        if (cubeCam.valid()) {
            if (cubeCam.area() > 30) {
                swerveCtrl.swerve(0, 0, 0, 0);
                return true;
            } else {
                swerveCtrl.swerve(positive(cubeWidthForPickUp-cubeCam.width())/85, cubeCam.X()/40, 0, 0);
                return false;
            }
        } else {
            swerveCtrl.swerve(0, 0, 0, 0);
            return true;
        }
    }

    public boolean getGamePiece() { // Returns true if done, otherwise must be run periodically
        staticPeriodic();
        if (stage <= 0) {
            nextStage(true);
        }
        if (stage == 1) {
            arm.pos(4);
            nextStage(arm.all_there());
        }
        if (stage == 2) {
            arm.pos(0);
            nextStage(arm.all_there());
        }
        if (stage == 3) {
            claw.take();
            nextStage(alignToCube() || startTime - 5 > time);
        }
        if (stage == 4) {
            claw.stop();
            swerveCtrl.swerve(0, 0, 0, 0);
            arm.pos(4);
            nextStage(arm.all_there());
        }
        if (stage >= 5) {
            swerveCtrl.swerve(0, 0, 0, 0);
            claw.stop();
            arm.pos(3);
            stage = 0;
            return true;
        } else {
            return false;
        }
    }

}
