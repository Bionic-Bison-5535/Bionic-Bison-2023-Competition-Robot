package frc.robot.systems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {
    
    private NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
	public int pipeline = 0;

	public Limelight(int initPipline) {
		if (initPipline >= 0 && initPipline < 10) {
			pipeline = initPipline;
		}
		table.getEntry("pipeline").setNumber(pipeline);
	}

	public double X() {
		return table.getEntry("tx").getDouble(0);
	}

    public double Y() {
		return table.getEntry("ty").getDouble(0);
	}

    public double area() {
		return table.getEntry("ta").getDouble(0);
	}

    public void setPipeline(int pip) {
		if (pip != pipeline && pip >= 0 && pip < 10) {
			pipeline = pip;
			table.getEntry("pipeline").setNumber(pipeline);
		}
    }

	public boolean inRange(double value, double setting, double errorRange) {
		return ((value >= setting - errorRange) && (value <= setting + errorRange));
	}

}
