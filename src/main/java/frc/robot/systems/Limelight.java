package frc.robot.systems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {
    
    private NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
	private int pipeline = 0;

	public Limelight(int Pipline) {
		if (Pipline >= 0 && Pipline < 10) {
			pipeline = Pipline;
		}
	}

	public double X() {
		table.getEntry("pipeline").setNumber(pipeline);
		return table.getEntry("tx").getDouble(0);
	}

    public double Y() {
		table.getEntry("pipeline").setNumber(pipeline);
		return table.getEntry("ty").getDouble(0);
	}

    public double area() {
		table.getEntry("pipeline").setNumber(pipeline);
		return table.getEntry("ta").getDouble(0);
	}

	public boolean inRange(double value, double setting, double errorRange) {
		return ((value >= setting - errorRange) && (value <= setting + errorRange));
	}

}
