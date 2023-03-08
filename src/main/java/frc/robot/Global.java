package frc.robot;

public class Global {

    public boolean smart = true;
    public boolean finalMode = false;
    public double dir = 0;
    public double rotation = 0;
    public double dir_accuracy = 1.7;
    public double initialAngle = 180;
    public boolean armEnabled = true;
    public double pwr2 = 0.15;
    public int now = 0;
    /* now = ID of currently running dynamic periodic
	 * 0 = Driving
	 * 1 = Getting Object
	 * 2 = Preparing to Score
	 * 3 = Scoring Mode (With Input)
	 * 4 = Scoring in top row (part 1)
	 * 5 = Scoring in top row (part 2)
	*/

    public double cubed(double inputNumber) {
		return inputNumber * inputNumber * inputNumber;
	}

}
