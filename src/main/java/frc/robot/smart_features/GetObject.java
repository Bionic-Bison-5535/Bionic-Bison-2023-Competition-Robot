package frc.robot.smart_features;

import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;

public class GetObject {

    public Limelight cubeCam, coneCam;
    private Weswerve swerveCtrl;
    private Arm arm;
    private Intake claw;
    public int stage = 0; // 0 = Ready, 1 = Aligning, 2 = Arm Going Down, 3 = Intake, 4 = Done + Arm Up, 5 = Error / Cancel,

    public double cubeWidthForPickUp = 135;
    public double coneWidthForPickUp = 135;

    public GetObject(int cubePipeline, int conePipeline, Weswerve swerveAccess, Arm armAccess, Intake intakeAccess) {
        cubeCam = new Limelight(cubePipeline);
        coneCam = new Limelight(conePipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
        claw = intakeAccess;
    }

    public void alignToCube() {
        if (stage == 0) {
            stage = 1;
        }
        if (stage == 1) {
            if (cubeCam.valid()) {
                if (cubeCam.inRange(cubeCam.width(), cubeWidthForPickUp, 40) && cubeCam.inRange(cubeCam.X(), 0, 5)) {
                    stage = 2;
                    swerveCtrl.swerve(0, 0, 0, 0);
                } else {
                    swerveCtrl.swerve((cubeWidthForPickUp-cubeCam.width())/100, cubeCam.X()/50, 0, 0);
                }
            } else {
                stage = 5;
                swerveCtrl.swerve(0, 0, 0, 0);
            }
        }
    }

    public void alignToCone() {
        if (stage == 0) {
            stage = 1;
        }
        if (stage == 1) {
            if (coneCam.valid()) {
                if (coneCam.inRange(coneCam.width(), coneWidthForPickUp, 40) && coneCam.inRange(coneCam.X(), 0, 5)) {
                    stage = 2;
                    swerveCtrl.swerve(0, 0, 0, 0);
                } else {
                    swerveCtrl.swerve((coneWidthForPickUp-coneCam.width())/100, coneCam.X()/50, 0, 0);
                }
            } else {
                stage = 5;
                swerveCtrl.swerve(0, 0, 0, 0);
            }
        }
    }

    public boolean getGamePiece(int cube0_or_cone1) { // Returns true if done, otherwise must be run periodically
        if (stage < 2) {
            if (cube0_or_cone1 == 0) {
                alignToCube();
            } else {
                alignToCone();
            }
        }
        if (stage == 2) {
            claw.open();
            arm.pos(0);
            if (arm.there) {
                stage = 3;
            }
        }
        if (stage == 3) {
            claw.close();
            if (claw.pressure()) {
                stage = 4;
            }
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

}
