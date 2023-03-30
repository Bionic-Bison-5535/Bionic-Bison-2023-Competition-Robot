package frc.robot.systems;

import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdleConfiguration;
import com.ctre.phoenix.led.CANdle.LEDStripType;

public class Lights {

    public CANdle leds;

    public Lights(int canID) {
        leds = new CANdle(canID);
        CANdleConfiguration ledConfig = new CANdleConfiguration();
        ledConfig.stripType = LEDStripType.RGB;
        ledConfig.brightnessScalar = 1;
        leds.configAllSettings(ledConfig);
    }
    
    public void setBrightness(double brightnessLevel) {
        CANdleConfiguration ledConfig = new CANdleConfiguration();
        ledConfig.stripType = LEDStripType.RGB;
        ledConfig.brightnessScalar = brightnessLevel;
        leds.configAllSettings(ledConfig);
    }

    public void blue() {
        leds.setLEDs(0, 0, 255);
    }

    public void red() {
        leds.setLEDs(255, 0, 0);
    }

    public void green() {
        leds.setLEDs(0, 255, 0);
    }

    public void cyan() {
        leds.setLEDs(0, 255, 255);
    }

    public void magenta() {
        leds.setLEDs(255, 0, 255);
    }

    public void yellow() {
        leds.setLEDs(255, 255, 0);
    }

    public void white() {
        leds.setLEDs(255, 255, 255);
    }

    public void off() {
        leds.setLEDs(0, 0, 0);
    }

    public void black() {
        leds.setLEDs(0, 0, 0);
    }

}
