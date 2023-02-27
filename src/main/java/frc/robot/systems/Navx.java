package frc.robot.systems;

import java.lang.Math;
import edu.wpi.first.wpilibj.I2C;
import com.kauailabs.navx.frc.AHRS;

public class Navx {
    
    private AHRS NavX = new AHRS(I2C.Port.kMXP);

    private double yaw_Offset = 0;
    private double balance_Offset;
    private double xVel, yVel;

    public Navx() {
        balance_Offset = NavX.getRoll();
    }

    public double yaw() {
        return (NavX.getAngle()-yaw_Offset);
    }

    public double coterminalYaw() {
        return yaw() % 360;
    }

    public double balance() {
        return NavX.getRoll()-balance_Offset;
    }

    public double botVel() {
        return Math.sqrt(Math.pow(NavX.getVelocityX(), 2) + Math.pow(NavX.getVelocityY(), 2));
    }

    public void zeroYaw() {
        yaw_Offset = NavX.getAngle();
    }

    public void correctYaw(double correction) {
        yaw_Offset += correction;
    }

    public void fullReset() {
        NavX.reset();
    }

}