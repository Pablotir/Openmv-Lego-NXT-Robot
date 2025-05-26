import lejos.nxt.*;
import lejos.nxt.addon.tetrix.*;
import lejos.util.Delay;

public class EncoderMonitor {
    // Global variables for the integrated encoder
    private static volatile int cumulativeTicks = 0;
    private static volatile int lastReading = 0;
    private static TetrixEncoderMotor motor;
    
    // Calibrated: we measured that a full rotation (360°) gives 540 ticks.
    public static final int PHYSICAL_TICKS_PER_ROTATION = 540;
    // Base conversion: 540/360 = 1.5 ticks per degree.
    public static final double BASE_TICKS_PER_DEGREE = PHYSICAL_TICKS_PER_ROTATION / 360.0;
    // Correction factor to adjust for overshoot (e.g., 90° commanded gives 92° actual, so factor ~0.9783).
    public static final double CORRECTION_FACTOR = 0.9;
    public static final double TICKS_PER_DEGREE = BASE_TICKS_PER_DEGREE * CORRECTION_FACTOR;

    public static void main(String[] args) {
        // Initialize motor controller and motor
        TetrixControllerFactory factory = new TetrixControllerFactory(SensorPort.S1);
        TetrixMotorController controller = factory.newMotorController();
        motor = controller.getEncoderMotor(TetrixMotorController.MOTOR_1);
        
        // Get the initial raw encoder value.
        lastReading = motor.getTachoCount();
        
        // Start the background thread that integrates the raw encoder ticks.
        Thread tracker = new Thread(new EncoderTracker());
        tracker.setDaemon(true);
        tracker.start();
        
        LCD.clear();
        LCD.drawString("Press LEFT: +90°", 0, 0);
        LCD.drawString("Press RIGHT: -90°", 0, 1);
        LCD.drawString("Press ESCAPE: Stop", 0, 2);
        
        while (!Button.ESCAPE.isDown()) {
            int button = Button.waitForAnyPress();
            if (button == Button.ID_LEFT) {
                rotateDegrees(90, 50);
            } else if (button == Button.ID_RIGHT) {
                rotateDegrees(-90, 50);
            }
            Delay.msDelay(500);
        }
        motor.stop();
        LCD.clear();
        LCD.drawString("Stopped", 0, 0);
    }

    /**
     * Rotates the motor by a specified number of degrees.
     * @param degrees The number of degrees to rotate (positive for one direction, negative for the other)
     * @param power   The motor power to apply (1–100)
     */
    private static void rotateDegrees(int degrees, int power) {
        // Record the starting integrated encoder value.
        int startAbsolute = cumulativeTicks + lastReading;
        // Calculate the number of ticks for the requested rotation using our calibrated conversion.
        int targetTicks = (int)(degrees * TICKS_PER_DEGREE);
        int targetAbsolute = startAbsolute + targetTicks;
        
        LCD.clear();
        LCD.drawString("Rotating " + degrees + "°", 0, 0);
        
        motor.setPower(power);
        if (targetTicks > 0) {
            motor.forward();
            while ((cumulativeTicks + lastReading) < targetAbsolute && !Button.ESCAPE.isDown()) {
                Delay.msDelay(20);
            }
        } else {
            motor.backward();
            while ((cumulativeTicks + lastReading) > targetAbsolute && !Button.ESCAPE.isDown()) {
                Delay.msDelay(20);
            }
        }
        motor.stop();
        Delay.msDelay(100);  // Allow time for stopping
        lastReading = motor.getTachoCount(); // update lastReading after stopping
        LCD.clear();
        LCD.drawString("Rotation complete", 0, 0);
        Delay.msDelay(1000);
    }
    
    /**
     * Background thread that continuously polls the raw encoder value,
     * calculates the delta, and adds it to cumulativeTicks.
     */
    private static class EncoderTracker implements Runnable {
        @Override
        public void run() {
            while (true) {
                int current = motor.getTachoCount();
                int delta = current - lastReading;
                cumulativeTicks += delta;
                lastReading = current;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    // Handle interruption if needed.
                }
            }
        }
    }
}
