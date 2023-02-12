package frc.robot.systems;

import java.lang.Math;
import edu.wpi.first.wpilibj.I2C;
import com.kauailabs.navx.frc.AHRS;

public class Navx {
    
    private AHRS NavX = new AHRS(I2C.Port.kMXP);

    private double yaw_Offset = 0;
    private double balance_Offset;

    public Navx() {
        balance_Offset = NavX.getRoll();
    }

    public double yaw() {
        return Math.round(100*(NavX.getAngle()-yaw_Offset))/100;
    }

    public double coterminalYaw() {
        return yaw() % 360;
    }

    public double balance() {
        return NavX.getRoll()-balance_Offset;
    }

    public boolean accel() {
        return (NavX.getVelocityX() != 0);
    }

    public void zeroYaw() {
        yaw_Offset = Math.round(100*NavX.getAngle())/100;
    }

    public void fullReset() {
        NavX.reset();
    }

}