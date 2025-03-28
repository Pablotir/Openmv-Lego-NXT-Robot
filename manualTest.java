import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import java.io.DataInputStream;
import lejos.nxt.addon.tetrix.*;
import lejos.util.Delay;

public class manualTest {
    private static final int FORWARD_ROTATION_DEGREES = 450;
    private static final int STRAFE_ROTATION_DEGREES = 344;
    private static final int TURN_ROTATION_DEGREES = 688;

    public static void main(String[] args) {
        TetrixControllerFactory controllerFactory = new TetrixControllerFactory(SensorPort.S1);
        TetrixMotorController motorController = controllerFactory.newMotorController();
        TetrixMotorController motorController2 = controllerFactory.newMotorController();

        TetrixRegulatedMotor backLeft = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_1);
        TetrixRegulatedMotor backRight = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_2);
        TetrixRegulatedMotor frontLeft = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_1);
        TetrixRegulatedMotor frontRight = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_2);

        backLeft.setSpeed(25);
        frontLeft.setSpeed(25);
        backRight.setSpeed(25);
        frontRight.setSpeed(25);

        int mode = 0; // 0: Forward/Backward, 1: Turn Left/Right, 2: Strafe Left/Right

        while (true) {
            LCD.clear();
            switch (mode) {
                case 0:
                    LCD.drawString("Mode: Forward/Backward", 0, 0);
                    break;
                case 1:
                    LCD.drawString("Mode: Turn Left/Right", 0, 0);
                    break;
                case 2:
                    LCD.drawString("Mode: Strafe Left/Right", 0, 0);
                    break;
            }

            if (Button.LEFT.isDown()) {
                switch (mode) {
                    case 0:
                        moveForward(backLeft, frontLeft, backRight, frontRight);
                        break;
                    case 1:
                        rotateLeft(backLeft, frontLeft, backRight, frontRight);
                        break;
                    case 2:
                        strafeLeft(backLeft, frontLeft, backRight, frontRight);
                        break;
                }
            } else if (Button.RIGHT.isDown()) {
                switch (mode) {
                    case 0:
                        moveBackward(backLeft, frontLeft, backRight, frontRight);
                        break;
                    case 1:
                        rotateRight(backLeft, frontLeft, backRight, frontRight);
                        break;
                    case 2:
                        strafeRight(backLeft, frontLeft, backRight, frontRight);
                        break;
                }
            } else if (Button.ENTER.isDown()) {
                mode = (mode + 1) % 3; // Cycle through modes
                Delay.msDelay(500); // Debounce delay
            } else {
                stopMotors(backLeft, frontLeft, backRight, frontRight);
            }
        }
    }

    public static void rotateLeft(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                   TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(-FORWARD_ROTATION_DEGREES, true);
        frontLeft.rotate(-FORWARD_ROTATION_DEGREES, true);
        backRight.rotate(-FORWARD_ROTATION_DEGREES, true);
        frontRight.rotate(FORWARD_ROTATION_DEGREES, true);
    }

    public static void rotateRight(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                    TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(FORWARD_ROTATION_DEGREES, false);
        frontLeft.rotate(FORWARD_ROTATION_DEGREES, false);
        backRight.rotate(FORWARD_ROTATION_DEGREES, false);
        frontRight.rotate(-FORWARD_ROTATION_DEGREES, false);
    }

    public static void moveBackward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                  TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(TURN_ROTATION_DEGREES, true);
        frontLeft.rotate(-TURN_ROTATION_DEGREES, true);
        backRight.rotate(TURN_ROTATION_DEGREES, true);
        frontRight.rotate(TURN_ROTATION_DEGREES, true);
    }

    public static void moveForward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                   TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(-TURN_ROTATION_DEGREES, true);
        frontLeft.rotate(TURN_ROTATION_DEGREES, true);
        backRight.rotate(-TURN_ROTATION_DEGREES, true);
        frontRight.rotate(-TURN_ROTATION_DEGREES, true);
    }

    public static void strafeLeft(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                  TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(STRAFE_ROTATION_DEGREES, true);
        frontLeft.rotate(STRAFE_ROTATION_DEGREES, true);
        backRight.rotate(-STRAFE_ROTATION_DEGREES, true);
        frontRight.rotate(STRAFE_ROTATION_DEGREES, true);
    }

    public static void strafeRight(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                   TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(-STRAFE_ROTATION_DEGREES, true);
        frontLeft.rotate(-STRAFE_ROTATION_DEGREES, true);
        backRight.rotate(STRAFE_ROTATION_DEGREES, true);
        frontRight.rotate(-STRAFE_ROTATION_DEGREES, true);
    }

    public static void stopMotors(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                  TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.stop();
        frontLeft.stop();
        backRight.stop();
        frontRight.stop();
    }
}
