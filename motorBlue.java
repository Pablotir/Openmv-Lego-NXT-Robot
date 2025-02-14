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
        TetrixControllerFactory controllerFactory = new TetrixControllerFactory(SensorPort.S1);
        TetrixMotorController motorController = controllerFactory.newMotorController();
        TetrixMotorController motorController2 = controllerFactory.newMotorController();

        TetrixRegulatedMotor backLeft = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_1);
        TetrixRegulatedMotor backRight = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_2);
        TetrixRegulatedMotor frontLeft = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_1);
        TetrixRegulatedMotor frontRight = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_2);

        backLeft.setSpeed(100);
        frontLeft.setSpeed(100);
        backRight.setSpeed(100);
        frontRight.setSpeed(100);

        UltrasonicSensor ultrasonic = new UltrasonicSensor(SensorPort.S2);

        LCD.clear();
        LCD.drawString("Connecting...", 0, 0);

        BTConnection connection = Bluetooth.connect(HC06_ADDR_OR_NAME, NXTConnection.RAW);
        if (connection == null) {
            LCD.clear();
            LCD.drawString("Conn failed", 0, 0);
            return;
        }

        LCD.clear();
        LCD.drawString("Connected!", 0, 0);

        DataInputStream dis = connection.openDataInputStream();

        while (true) {
            try {
                if (dis.available() > 0) {
                    String command = dis.readLine().trim().toLowerCase();

                    int distance = ultrasonic.getDistance();
                    if (distance <= ULTRASONIC_THRESHOLD) {
                        Sound.beep();
                        stopMotors(backLeft, frontLeft, backRight, frontRight);
                        LCD.clear();
                        LCD.drawString("Obstacle!", 0, 0);
                        continue;
                    }

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
                        case "pause":
                            Delay.msDelay(5000);
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

        try {
            dis.close();
            connection.close();
        } catch (Exception e) {
        }
    }

    public static void moveForward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(-918, true);
        frontLeft.rotate(-918, true);
        backRight.rotate(-688, true);
        frontRight.rotate(-688, true);
    }

    public static void moveBackward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.rotate(918, false);
        frontLeft.rotate(918, false);
        backRight.rotate(688, false);
        frontRight.rotate(688, false);
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
