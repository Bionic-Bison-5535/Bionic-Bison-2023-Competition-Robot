package frc.robot.systems;

public class Intake {

    public Motor intakeMotor;

    public Intake(int intake_canID) {
        intakeMotor = new Motor(intake_canID, true, true, 1);
    }

    public void fire() {
        intakeMotor.set(1);
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
