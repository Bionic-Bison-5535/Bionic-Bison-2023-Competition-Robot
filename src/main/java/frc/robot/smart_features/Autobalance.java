package frc.robot.smart_features;

import frc.robot.systems.Arm;
import frc.robot.systems.Weswerve;
import frc.robot.systems.Navx;

public class Autobalance {

    private Arm arm;
    private Weswerve swerveCtrl;
    private Navx navx;

    public Autobalance(Arm armAccess, Weswerve swerveAccess, Navx navxAccess) {
        arm = armAccess;
        swerveCtrl = swerveAccess;
        navx = navxAccess;
    }

}
