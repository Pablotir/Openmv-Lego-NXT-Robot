import lejos.nxt.*;
import lejos.robotics.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import java.io.DataInputStream;
import lejos.nxt.addon.tetrix.*;
import lejos.util.Delay;

public class motorBlue {
    private static final String HC06_ADDR_OR_NAME = "HC-06";

    // Calibration constants
    public static final int PHYSICAL_TICKS_PER_ROTATION = 540;
    public static final double BASE_TICKS_PER_DEGREE = PHYSICAL_TICKS_PER_ROTATION / 360.0;
    public static final double CORRECTION_FACTOR = 0.9;
    public static final double TICKS_PER_DEGREE = BASE_TICKS_PER_DEGREE * CORRECTION_FACTOR;

    public static void main(String[] args) {
        TetrixControllerFactory controllerFactory = new TetrixControllerFactory(SensorPort.S1);
        TetrixMotorController motorController = controllerFactory.newMotorController();
        TetrixMotorController motorController2 = controllerFactory.newMotorController();

        TetrixRegulatedMotor backLeft = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_1);
        TetrixRegulatedMotor backRight = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_2);
        TetrixRegulatedMotor frontLeft = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_1);
        TetrixRegulatedMotor frontRight = motorController2.getRegulatedMotor(TetrixMotorController.MOTOR_2);

        backLeft.setSpeed(50);
        backRight.setSpeed(50);
        frontLeft.setSpeed(50);
        frontRight.setSpeed(50);

        backLeft.resetTachoCount();
        backRight.resetTachoCount();
        frontLeft.resetTachoCount();
        frontRight.resetTachoCount();
        
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
                    StringBuilder commandBuilder = new StringBuilder();
                    while (dis.available() > 0) {
                        commandBuilder.append((char) dis.readByte());
                    }
                    String command = commandBuilder.toString().trim().toLowerCase();

                    // Manually parse the command using indexOf and substring
                    String action = "";
                    String distanceStr = "";
                    int spaceIndex = command.indexOf(' ');
                    if (spaceIndex != -1) {
                        action = command.substring(0, spaceIndex);
                        distanceStr = command.substring(spaceIndex + 1);
                    } else {
                        action = command; // No distance provided
                    }

                    int distance = 0;
                    if (!distanceStr.equals("")) {
                        try {
                            distance = Integer.parseInt(distanceStr);
                        } catch (NumberFormatException e) {
                            LCD.drawString("Invalid dist", 0, 1);
                        }
                    }

                    LCD.clear();
                    LCD.drawString("Action: " + action, 0, 0);
                    LCD.drawString("Dist: " + distance, 0, 1);

                    // Convert the double distance to int when calling the movement functions.
                    int distanceInt = distance;

                    // Perform the action
                    switch (action) {
                        case "forward":
                            moveForward(backLeft, frontLeft, backRight, frontRight, distanceInt);
                            break;
                        case "backward":
                            moveBackward(backLeft, frontLeft, backRight, frontRight, distanceInt);
                            break;
                        case "left":
                            rotateLeft(backLeft, frontLeft, backRight, frontRight, distanceInt);
                            break;
                        case "right":
                            rotateRight(backLeft, frontLeft, backRight, frontRight, distanceInt);
                            break;
                        case "strafe_left":
                            strafeLeft(backLeft, frontLeft, backRight, frontRight, distanceInt);
                            break; 
                        case "strafe_right":
                            strafeRight(backLeft, frontLeft, backRight, frontRight, distanceInt);
                            break;
                        default:
                            stopMotors(backLeft, frontLeft, backRight, frontRight);
                            LCD.drawString("Stop", 0, 0);
                    }
                }

                Delay.msDelay(200);
            } catch (Exception e) {
                LCD.clear();
                LCD.drawString("Error occurred", 0, 0);
                break;
            }
        }

        try {
            dis.close();
            connection.close();
        } catch (Exception e) {
            // Ignore errors during cleanup
        }
    }

    /**
     * Rotates the motors to the left by a specified number of degrees.
     */
    public static void rotateLeft(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft, 
                                  TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight, int degrees) {
        int targetTicks = (int)(degrees * TICKS_PER_DEGREE);
        // For rotateLeft, left motors should move forward (targetTicks) and right motors backward (-targetTicks)
        // Passing false for the right motors indicates backward rotation.
        rotateMotor(backLeft, frontLeft, backRight, frontRight, 
                    targetTicks, targetTicks, targetTicks, targetTicks, 
                    true, true, false, false);
    }
    
    /**
     * Rotates the motors to the right by a specified number of degrees.
     * (Already fixed as per your instructions.)
     */
    public static void rotateRight(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                   TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight, int degrees) {
        int targetTicks = (int)(degrees * TICKS_PER_DEGREE);
        rotateMotor(backLeft, frontLeft, backRight, frontRight, 
                    targetTicks, targetTicks, targetTicks, targetTicks, 
                    false, false, true, true);
    }

    /**
     * Moves the robot backward by a specified distance.
     */
    public static void moveBackward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                    TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight, int distance) {
        int targetTicks = (int)(distance * TICKS_PER_DEGREE);
        // For moveBackward:
        // backLeft: forward rotation (+targetTicks)
        // frontLeft: backward rotation (-targetTicks)
        // backRight: forward rotation (+targetTicks)
        // frontRight: forward rotation (+targetTicks)
        rotateMotor(backLeft, frontLeft, backRight, frontRight, 
                    targetTicks, targetTicks, targetTicks, targetTicks, 
                    true, false, true, true);
    }

    /**
     * Moves the robot forward by a specified distance.
     */
    public static void moveForward(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                   TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight, int distance) {
        int targetTicks = (int)(distance * TICKS_PER_DEGREE);
        // For moveForward:
        // backLeft: backward rotation (-targetTicks)
        // frontLeft: forward rotation (+targetTicks)
        // backRight: backward rotation (-targetTicks)
        // frontRight: backward rotation (-targetTicks)
        rotateMotor(backLeft, frontLeft, backRight, frontRight, 
                    targetTicks, targetTicks, targetTicks, targetTicks, 
                    false, true, false, false);
    }

    /**
     * Strafes the robot to the left by a specified distance.
     */
    public static void strafeLeft(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                  TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight, int distance) {
        int targetTicks = (int)(distance * TICKS_PER_DEGREE);
        // For strafeLeft:
        // backLeft: forward rotation (+targetTicks)
        // frontLeft: forward rotation (+targetTicks)
        // backRight: backward rotation (-targetTicks)
        // frontRight: forward rotation (+targetTicks)
        rotateMotor(backLeft, frontLeft, backRight, frontRight, 
                    targetTicks, targetTicks, targetTicks, targetTicks, 
                    true, true, false, true);
    }

    /**
     * Strafes the robot to the right by a specified distance.
     */
    public static void strafeRight(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                   TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight, int distance) {
        int targetTicks = (int)(distance * TICKS_PER_DEGREE);
        // For strafeRight:
        // backLeft: backward rotation (-targetTicks)
        // frontLeft: backward rotation (-targetTicks)
        // backRight: forward rotation (+targetTicks)
        // frontRight: backward rotation (-targetTicks)
        rotateMotor(backLeft, frontLeft, backRight, frontRight, 
                    targetTicks, targetTicks, targetTicks, targetTicks, 
                    false, false, true, false);
    }

    /**
     * Stops all four motors.
     */
    public static void stopMotors(TetrixRegulatedMotor backLeft, TetrixRegulatedMotor frontLeft,
                                  TetrixRegulatedMotor backRight, TetrixRegulatedMotor frontRight) {
        backLeft.stop();
        frontLeft.stop();
        backRight.stop();
        frontRight.stop();
    }
    
    /**
     * Rotates 4 motors simultaneously.
     * 
     * @param motorBL      Back-left motor.
     * @param motorFL      Front-left motor.
     * @param motorBR      Back-right motor.
     * @param motorFR      Front-right motor.
     * @param targetTicksBL Target tick value for back-left motor (positive value).
     * @param targetTicksFL Target tick value for front-left motor (positive value).
     * @param targetTicksBR Target tick value for back-right motor (positive value).
     * @param targetTicksFR Target tick value for front-right motor (positive value).
     * @param forwardBL    Boolean indicating forward rotation for back-left motor.
     * @param forwardFL    Boolean indicating forward rotation for front-left motor.
     * @param forwardBR    Boolean indicating forward rotation for back-right motor.
     * @param forwardFR    Boolean indicating forward rotation for front-right motor.
     */
    private static void rotateMotor(TetrixRegulatedMotor motorBL, TetrixRegulatedMotor motorFL, 
                                    TetrixRegulatedMotor motorBR, TetrixRegulatedMotor motorFR,
                                    int targetTicksBL, int targetTicksFL, int targetTicksBR, int targetTicksFR,
                                    boolean forwardBL, boolean forwardFL, boolean forwardBR, boolean forwardFR) {
        // Reset each motor's tachometer count.
        motorBL.resetTachoCount();
        motorFL.resetTachoCount();
        motorBR.resetTachoCount();
        motorFR.resetTachoCount();

        // Start each motor's rotation asynchronously.
        if (forwardBL) {
            motorBL.rotate(targetTicksBL, true);
        } else {
            motorBL.rotate(-targetTicksBL, true);
        }
        if (forwardFL) {
            motorFL.rotate(targetTicksFL, true);
        } else {
            motorFL.rotate(-targetTicksFL, true);
        }
        if (forwardBR) {
            motorBR.rotate(targetTicksBR, true);
        } else {
            motorBR.rotate(-targetTicksBR, true);
        }
        if (forwardFR) {
            motorFR.rotate(targetTicksFR, true);
        } else {
            motorFR.rotate(-targetTicksFR, true);
        }

        // Wait until each motor's tachometer count reaches its target.
        // Since targets are positive, we check according to the direction.
        // Back-left motor:
        if (forwardBL) {
            while (motorBL.getTachoCount() < targetTicksBL) {
                Delay.msDelay(10);
            }
        } else {
            while (motorBL.getTachoCount() > -targetTicksBL) {
                Delay.msDelay(10);
            }
        }
        motorBL.stop();

        // Front-left motor:
        if (forwardFL) {
            while (motorFL.getTachoCount() < targetTicksFL) {
                Delay.msDelay(10);
            }
        } else {
            while (motorFL.getTachoCount() > -targetTicksFL) {
                Delay.msDelay(10);
            }
        }
        motorFL.stop();

        // Back-right motor:
        if (forwardBR) {
            while (motorBR.getTachoCount() < targetTicksBR) {
                Delay.msDelay(10);
            }
        } else {
            while (motorBR.getTachoCount() > -targetTicksBR) {
                Delay.msDelay(10);
            }
        }
        motorBR.stop();

        // Front-right motor:
        if (forwardFR) {
            while (motorFR.getTachoCount() < targetTicksFR) {
                Delay.msDelay(10);
            }
        } else {
            while (motorFR.getTachoCount() > -targetTicksFR) {
                Delay.msDelay(10);
            }
        }
        motorFR.stop();
    }
}
