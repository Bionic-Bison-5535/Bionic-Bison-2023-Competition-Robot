package frc.robot.systems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {
    
    private NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
	private int pipeline = 0;
	private double invalidArea = 0.04;
	private double pipeline_change_delay = 0.4;

	public Limelight(int Pipline) {
		if (Pipline >= 0 && Pipline < 10) {
			pipeline = Pipline;
		}
	}

	public void setPip() {
		if (table.getEntry("pipeline").getInteger(pipeline) != pipeline) {
			table.getEntry("pipeline").setNumber(pipeline);
			Timer.delay(pipeline_change_delay);
		}
	}

	public int getPip() {
		return (int)table.getEntry("pipeline").getInteger(10);
	}

	public double X() {
		setPip();
		return table.getEntry("tx").getDouble(0);
	}

    public double Y() {
		setPip();
		return table.getEntry("ty").getDouble(0);
	}

    public double area() {
		setPip();
		return table.getEntry("ta").getDouble(0);
	}

	public double width() {
		setPip();
		return table.getEntry("thor").getDouble(0);
	}

	public double height() {
		setPip();
		return table.getEntry("tvert").getDouble(0);
	}

	public boolean valid() {
		setPip();
		if (table.getEntry("ta").getDouble(0) > invalidArea) {
			return true;
		} else {
			return false;
		}
	}

	public boolean inRange(double value, double setting, double errorRange) {
		return ((value >= setting - errorRange) && (value <= setting + errorRange));
	}

}
