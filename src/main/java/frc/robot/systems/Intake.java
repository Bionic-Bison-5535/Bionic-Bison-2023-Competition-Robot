package frc.robot.systems;

import java.lang.Math;

public class Intake {

    public Motor intakeMotor;

    public double presetOpen = 24;
    public double presetClosedCube = 0;
    public double presetClosedCone = -37;
    public boolean open = false;
    public int gamePiece = 0;

    public Intake(int intake_canID) {
        intakeMotor = new Motor(intake_canID, false, true, 1);
    }

    public double pos() {
        if (open) {
            return presetOpen;
        } else {
            if (gamePiece == 0) {
                return presetClosedCube;
            }
            if (gamePiece == 1) {
                return presetClosedCone;
            }
            return presetOpen;
        }
    }

    public void open() {
        open = true;
    }

    public void close(int cube0_or_cone1) {
        open = false;
        gamePiece = cube0_or_cone1;
    }

    public void reset() {
        intakeMotor.setEnc(0);
    }

    public void update() {
        if ((Math.abs((pos()-intakeMotor.getEnc())/(20)) > 0.05)) {
            intakeMotor.set((pos()-intakeMotor.getEnc())/(20));
        } else {
            intakeMotor.set(0);
        }
    }

    public boolean closed() {
        if (gamePiece == 1) {
            return (intakeMotor.getEnc()-5 < presetClosedCone);
        } else {
            return (intakeMotor.getEnc()-5 < presetClosedCube);
        }
    }

}
