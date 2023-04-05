package frc.robot.smart_features;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.systems.Limelight;
import frc.robot.systems.Arm;
import frc.robot.systems.Intake;

public class Score {

    public Limelight april;
    private Arm arm;
    private Intake claw;
    private boolean tele;
    private double time = 120;
    private double lastScored = 135;

    public int cones = 0;
    public int cubes = 0;
    public int points = 0;

    public Score(int aprilTagPipeline, Arm armAccess, Intake intakeAccess) {
        april = new Limelight(aprilTagPipeline);
        arm = armAccess;
        claw = intakeAccess;
    }

    public double getAlignment(double xPos) {
        if (april.valid()) {
            return (april.X()-xPos)/50;
        }
        return 0;
    }

    public boolean closeEnough() {
        return (april.area() > 1.3);
    }

    public void drop(int cube0_or_cone1) {
        claw.fire();
        time = DriverStation.getMatchTime();
        if (time + 2 < lastScored || time - 2 > lastScored) {
            lastScored = time;
            tele = DriverStation.isTeleop();
            if (cube0_or_cone1 == 0) {
                cubes += 1;
            } else if (cube0_or_cone1 == 1) {
                cones += 1;
            }
            if (arm.mostRecentPos == 2) {
                if (tele) { points += 5; } else { points += 6; }
            } else if (arm.mostRecentPos == 1) {
                if (tele) { points += 3; } else { points += 4; }
            } else {
                if (tele) { points += 2; } else { points += 3; }
            }
        }
    }

}
