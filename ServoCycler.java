import lejos.nxt.addon.tetrix.*;
import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

public class ServoCycler {
    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 180;
    private static final int STEP_DEGREES = 15;

    private static final int[] SERVO_PORTS = {
        TetrixServoController.SERVO_2,
        TetrixServoController.SERVO_3,
        TetrixServoController.SERVO_4,
        TetrixServoController.SERVO_5,
        TetrixServoController.SERVO_6
    };

    public static void main(String[] args) {
        TetrixControllerFactory controllerFactory = new TetrixControllerFactory(SensorPort.S1);
        TetrixServoController servoController = controllerFactory.newServoController();

        TetrixServo[] servos = new TetrixServo[SERVO_PORTS.length];
        int[] positions = new int[SERVO_PORTS.length];

        for (int i = 0; i < SERVO_PORTS.length; i++) {
            servos[i] = servoController.getServo(SERVO_PORTS[i]);
            positions[i] = 90; // Start at center
            servos[i].setAngle(positions[i]);
        }

        int currentIndex = 0;

        while (true) {
            TetrixServo currentServo = servos[currentIndex];
            int currentPosition = positions[currentIndex];

            System.out.println("Servo " + (currentIndex + 2) + " Pos: " + currentPosition + "°");

            if (Button.LEFT.isDown()) {
                currentPosition = Math.max(currentPosition - STEP_DEGREES, MIN_POSITION);
                currentServo.setAngle(currentPosition);
                positions[currentIndex] = currentPosition;
                Delay.msDelay(300);
            } else if (Button.RIGHT.isDown()) {
                currentPosition = Math.min(currentPosition + STEP_DEGREES, MAX_POSITION);
                currentServo.setAngle(currentPosition);
                positions[currentIndex] = currentPosition;
                Delay.msDelay(300);
            } else if (Button.ENTER.isDown()) {
                currentIndex = (currentIndex + 1) % servos.length;
                System.out.println("Switched to Servo " + (currentIndex + 2));
                Delay.msDelay(500);
            } else if (Button.ESCAPE.isDown()) {
                break;
            }

            Delay.msDelay(100);
        }

        servoController.flt(); // Float all servos on exit
        System.out.println("Exited.");
    }
}
