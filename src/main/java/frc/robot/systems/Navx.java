package frc.robot.systems;

import java.lang.Math;
import edu.wpi.first.wpilibj.I2C;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Timer;

public class Navx {
    
    private AHRS NavX = new AHRS(I2C.Port.kMXP);

    private double yaw_Offset = 0;
    private double balance_Offset1, balance_Offset2;

    public Navx() {
        NavX.reset();
        Timer.delay(1);
        balance_Offset1 = NavX.getRoll();
        balance_Offset2 = NavX.getPitch();
    }

    public double yaw() {
        return (NavX.getAngle()-yaw_Offset);
    }

    public double coterminalYaw() {
        return yaw() % 360;
    }

    public double balance() {
        return Math.sqrt(Math.pow(NavX.getRoll()-balance_Offset1, 2) + Math.pow(NavX.getPitch()-balance_Offset2, 2));
    }

    public double rawBalance() {
        return (NavX.getRoll()-balance_Offset1);
    }

    public boolean accel() {
        return (Math.sqrt(Math.pow(NavX.getVelocityX(),2)+Math.pow(NavX.getVelocityX(),2)) >= 0.01);
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