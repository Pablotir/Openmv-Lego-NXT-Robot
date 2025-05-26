import lejos.nxt.*;
import lejos.nxt.addon.tetrix.*;
import lejos.util.Delay;

public class manualTest {
    public static void main(String[] args) {
        TetrixControllerFactory controllerFactory = new TetrixControllerFactory(SensorPort.S1);
        TetrixMotorController motorController = controllerFactory.newMotorController();

        TetrixRegulatedMotor backLeft = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_1);
        TetrixRegulatedMotor backRight = motorController.getRegulatedMotor(TetrixMotorController.MOTOR_2);

        backLeft.setSpeed(50);
        backRight.setSpeed(50);

        backLeft.forward();
        backRight.forward();
        
        Delay.msDelay(3000);
        
        backLeft.stop();
        backRight.stop();
    }
}
