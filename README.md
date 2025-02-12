# Openmv-Lego-NXT-Robot

This is currently a WIP for my robot  

## Currently using
- Actobotics Channels for the frame
- 3D printed wheels hubs using Lego motorcycle tires
- Lego NXT Brick
- 1 Lego NXT ultrasonic sensor
- 2 Lego NXT motors (only for V1 until I upgrade to a full servo arm)
- 2 HiTechnic motor controllers for Tetrix
- 1 Hitechnic servo controller for Tetrix
- 4 60:1 Torquenado Tetrix motors
- Custom 18650 battery pack (3 2000mah 3.7V batteries)
- OpenMV RT1062
- HC-06 Module (attached to RT062)

### HC-06 Comms.py (made for the RT1062)
Just a sample script to test bluetooth between the RT062 and the NXT. Uses a face detection model and sends the coordinates to it. Additionally, there is a line to move a servo but currently commented just to be able to test communications directly. 

### Test.py
Depreciated version of HC-06 Comms.py

### motorBlue.java
This is the current heart of the program, it combines the script nxt.java (the receiving half of HC-06 Comms.py) and motor.java, and outputs the current  product of where any command sent from the HC-06 will be recieved by the nxt and thus make a move with the robot. 
