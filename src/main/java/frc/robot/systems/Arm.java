package frc.robot.systems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

public class Arm {

    private CANSparkMax expansion, upDown;
    private RelativeEncoder expansionCoder, upDownCoder;

    public Arm(int expansion_canID, int upDown_canID) {
        expansion = new CANSparkMax(expansion_canID, MotorType.kBrushless);
        upDown = new CANSparkMax(upDown_canID, MotorType.kBrushless);
        expansionCoder = expansion.getEncoder();
        upDownCoder = upDown.getEncoder();
    }

}
