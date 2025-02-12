import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import java.io.DataInputStream;
import lejos.nxt.addon.tetrix.*;
import lejos.util.Delay;

public class motorBlue {
    private static final String HC06_ADDR_OR_NAME = "HC-06";
    private static final int ULTRASONIC_THRESHOLD = 20; // cm

    public static void main(String[] args) {
        // Initialize Tetrix motor controller
        TetrixControllerFactory controllerFactory = new TetrixControllerFactory(SensorPort.S1);
        TetrixMotorController motorController = controllerFactory.newMotorController();
        TetrixMotorController motorController2 = controllerFactory.newMotorController();

        // Get regulated motors from the controllers
        TetrixRegulatedMotor backLeft = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_1);     // 3-inch wheel
        TetrixRegulatedMotor backRight = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_2);    // 4-inch wheel
        TetrixRegulatedMotor frontLeft = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_1);   // 3-inch wheel
        TetrixRegulatedMotor frontRight = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_2);  // 4-inch wheel

        // Set the speed of the motors
        backLeft.setSpeed(100);
        frontLeft.setSpeed(100);
        backRight.setSpeed(100);
        frontRight.setSpeed(100);

        // Initialize Ultrasonic Sensor
        UltrasonicSensor ultrasonic = new UltrasonicSensor(SensorPort.S2);

        // LCD and Bluetooth initialization
        LCD.clear();
        LCD.drawString("Connecting...", 0, 0);

        // Attempt to connect to the HC-06 module
        BTConnection connection = Bluetooth.connect(HC06_ADDR_OR_NAME, NXTConnection.RAW);

        if (connection == null) {
            LCD.clear();
            LCD.drawString("Conn failed", 0, 0);
            return;
        }

        LCD.clear();
        LCD.drawString("Connected!", 0, 0);

        DataInputStream dis = connection.openDataInputStream();

        // Main loop to read commands and control motors
        while (true) {
            try {
                if (dis.available() > 0) {
                    String command = dis.readLine().trim().toLowerCase();

                    // Check ultrasonic sensor distance
                    int distance = ultrasonic.getDistance();
                    if (distance <= ULTRASONIC_THRESHOLD) {
                        Sound.beep();
                        stopMotors(backLeft, frontLeft, backRight, frontRight);
                        LCD.clear();
                        LCD.drawString("Obstacle!", 0, 0);
                        continue;
                    }

                    // Execute motor commands
                    switch (command) {
                        case "forward":
                            moveForward(backLeft, frontLeft, backRight, frontRight);
                            Delay.msDelay(1500);
                            stopMotors(backLeft, frontLeft, backRight, frontRight);
                            break;
                        case "backward":
                            moveBackward(backLeft, frontLeft, backRight, frontRight);
                            Delay.msDelay(1500);
                            stopMotors(backLeft, frontLeft, backRight, frontRight);
                            break;
                        case "left":
                            rotateLeft(backLeft, frontLeft, backRight, frontRight);
                            Delay.msDelay(1050);
                            stopMotors(backLeft, frontLeft, backRight, frontRight);
                            break;
                        case "right":
                            rotateRight(backLeft, frontLeft, backRight, frontRight);
                            Delay.msDelay(1050);
                            stopMotors(backLeft, frontLeft, backRight, frontRight);
                            break;
                        default:
                            stopMotors(backLeft, frontLeft, backRight, frontRight);
                            LCD.clear();
                            LCD.drawString("Invalid cmd", 0, 0);
                    }
                }
                Thread.sleep(200);
            } catch (Exception e) {
                LCD.clear();
                LCD.drawString("Error", 0, 0);
                break;
            }
        }

        // Close connection
        try {
            dis.close();
            connection.close();
        } catch (Exception e) {
            // Ignore errors on close
        }
    }

    // Motor control methods
    public static void moveForward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(-918, true);   // 3-inch wheel
        frontLeft.rotate(-918, true);  // 3-inch wheel
        backRight.rotate(-688, true);  // 4-inch wheel
        frontRight.rotate(-688, true); // 4-inch wheel
    }

    public static void moveBackward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(918, false);   // 3-inch wheel
        frontLeft.rotate(918, false);  // 3-inch wheel
        backRight.rotate(688, false);  // 4-inch wheel
        frontRight.rotate(688, false); // 4-inch wheel
    }

    public static void rotateLeft(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        int rotationDegrees = 70;
        backLeft.rotate(rotationDegrees, true);
        frontLeft.rotate(-rotationDegrees, true);
        backRight.rotate(rotationDegrees, true);
        frontRight.rotate(-rotationDegrees, true);
    }

    public static void rotateRight(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        int rotationDegrees = 70;
        backLeft.rotate(-rotationDegrees, true);
        frontLeft.rotate(rotationDegrees, true);
        backRight.rotate(-rotationDegrees, true);
        frontRight.rotate(rotationDegrees, true);
    }

    public static void stopMotors(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.stop();
        frontLeft.stop();
        backRight.stop();
        frontRight.stop();
    }
}
