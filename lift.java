import lejos.nxt.*;

public class lift {
    public static void main(String[] args) {
        Motor.A.setSpeed(600);
        Motor.B.setSpeed(600);
        Motor.C.setSpeed(600);

        while (true) {
            if (Button.LEFT.isDown()) {
                Motor.A.backward();
                Motor.B.backward();
                Motor.C.backward();
            } else if (Button.RIGHT.isDown()) {
                Motor.A.forward();
                Motor.B.forward();
                Motor.C.forward();
            } else {
                // Stop all motors if no direction button is pressed
                Motor.A.stop(true);
                Motor.B.stop(true);
                Motor.C.stop();
            }

            if (Button.ESCAPE.isDown()) {
                break;
            }

            Thread.yield();
        }
    }
}
