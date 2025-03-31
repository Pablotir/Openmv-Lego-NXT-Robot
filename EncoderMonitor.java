import lejos.nxt.*;
import lejos.nxt.addon.tetrix.*;
import lejos.util.Delay;

public class EncoderMonitor {
    public static void main(String[] args) {
        TetrixControllerFactory factory = new TetrixControllerFactory(SensorPort.S1);
        TetrixMotorController controller = factory.newMotorController();
        TetrixEncoderMotor motor = controller.getEncoderMotor(TetrixMotorController.MOTOR_1);
        
        // Do not reset encoder here—observe the natural increments.
        LCD.clear();
        LCD.drawString("Driving at low power", 0, 0);
        
        // Set a low constant power.
        motor.setPower(30);
        motor.forward();
        
        while (!Button.ESCAPE.isDown()) {
            LCD.clear();
            LCD.drawString("Raw: " + motor.getTachoCount(), 0, 1);
            Delay.msDelay(200);
        }
        
        motor.stop();
        LCD.clear();
        LCD.drawString("Stopped", 0, 0);
    }
}
