# Openmv-Lego-NXT-Robot

This is currently a WIP for my robot  

## Currently using
- Actobotics Channels for the frame
- 3D printed wheels hubs using Lego motorcycle tires
- 2 Tetrix omni wheels
- Lego NXT Brick running Lejos CFW
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

This is the current heart of the program, it combines the script nxt.java (the receiving half of HC-06 Comms.py) and motor.java, and outputs the current product of where any command sent from the HC-06 will be recieved by the nxt and thus make a move with the robot. 

### AprilTagFinder.py
This is currently the working algorithm I invented for getting the robot to traverse down a hallway making sure it visits 10 Apriltags before going to its final destination (this part is a WIP until motors are calibrated).
- Currently, this script is also being optimized to ensure that the camera is still detecting while the robot moves since time.wait() causes the camera to sleep until the NXT finishes moving for a period of time
- Main goal in the future is to have the robot align itself to the exact middle of the hallway using the NXT's ultrasonic sensor and also ensure that the robot can gravitate towards any tag it sees and line itself up for whatever the next tag might be (most likely a tag higher that the current making it easier to plot the robot down in any location and it will still find the final destination)
