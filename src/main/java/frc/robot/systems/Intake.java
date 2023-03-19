package frc.robot.systems;

import frc.robot.systems.Arm;

public class Intake {

    public Motor intakeMotor;
    private Arm arm;

    public Intake(int intake_canID, Arm armAccess) {
        intakeMotor = new Motor(intake_canID, true, true, 1);
        arm = armAccess;
    }

    public void fire() {
        if (arm.mostRecentPos == 1) {
            intakeMotor.set(0.5);
        } else {
            intakeMotor.set(1);
        }
    }

    public void take() {
        intakeMotor.set(-0.2);
    }

    public void variableIntake(double intakeSpeed) {
        intakeMotor.set(-1*intakeSpeed);
    }

    public void stop() {
        intakeMotor.set(0);
    }

}
