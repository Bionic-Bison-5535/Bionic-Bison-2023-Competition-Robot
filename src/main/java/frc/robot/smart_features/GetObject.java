package frc.robot.smart_features;

import frc.robot.systems.Weswerve;
import frc.robot.systems.Limelight;

public class GetObject {

    public Limelight cubeCam, coneCam;
    private Weswerve swerveCtrl;
    private Arm arm;
    public int stage = 0; // 0 = Ready, 1 = Aligning, 2 = Arm Going Down, 3 = Intake, 4 = Done, 5 = Error,

    public cubeAreaToPickUp = 15;
    public coneAreaToPickUp = 15;

    public GetObject(int cubePipeline, int conePipeline, Weswerve swerveAccess, Arm armAccess) {
        cubeCam = new Limelight(cubePipeline);
        coneCam = new Limelight(conePipeline);
        swerveCtrl = swerveAccess;
        arm = armAccess;
    }

    public int alignToCube() {
        if (stage == 0) {
            stage = 1;
        }
        if (stage == 1) {
            if (cubeCam.valid()) {
                if (cubeCam.inRange(cubeCam.area(), cubeAreaToPickUp, 0.5) && cubeCam.inRange(cubeCam.X(), 0, 5)) {
                    stage = 2;
                    swerveCtrl.swerve(0, 0, 0, 0);
                } else {
                    swerveCtrl.swerve((cubeAreaToPickUp-cubeCam.area())/7, -cubeCam.X()/50, 0, 0);
                }
            } else {
                stage = 5;
                swerveCtrl.swerve(0, 0, 0, 0);
            }
        }
        return stage;
    }

    public int alignToCone() {
        if (stage == 0) {
            stage = 1;
        }
        if (stage == 1) {
            if (coneCam.valid()) {
                if (coneCam.inRange(coneCam.area(), coneAreaToPickUp, 0.5) && coneCam.inRange(coneCam.X(), 0, 5)) {
                    stage = 2;
                    swerveCtrl.swerve(0, 0, 0, 0);
                } else {
                    swerveCtrl.swerve((coneAreaToPickUp-coneCam.area())/7, -coneCam.X()/50, 0, 0);
                }
            } else {
                stage = 5;
                swerveCtrl.swerve(0, 0, 0, 0);
            }
        }
        return stage;
    }

}
