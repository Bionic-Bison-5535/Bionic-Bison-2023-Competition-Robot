package frc.robot.smart_features;

import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;

public class GetObject {

    public Limelight cubeCam;
    private Weswerve swerveCtrl;
    private Arm arm;
    private Intake claw;
    private int counts = 0;
    public int stage = 0; // 0 = Ready, 1 = Arm Out, 2 = Arm Down, 3 = Alignment, 4 = Drive Forward, 5 = Arm Up, 6 = Arm In,

    public double cubeWidthForPickUp = 150;

    public GetObject(int cubePipeline, Weswerve swerveAccess, Arm armAccess, Intake intakeAccess) {
        cubeCam = new Limelight(cubePipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
    }

    public boolean alignToCube() {
        if (cubeCam.valid()) {
            if (cubeCam.inRange(cubeCam.width(), cubeWidthForPickUp, 30) && cubeCam.inRange(cubeCam.X(), 0, 7)) {
                stage = 2;
                swerveCtrl.swerve(0, 0, 0, 0);
                return true;
            } else {
                swerveCtrl.swerve((cubeWidthForPickUp-cubeCam.width())/90, cubeCam.X()/50, 0, 0);
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
            if (alignToCube() || counts > 50) {
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

}
