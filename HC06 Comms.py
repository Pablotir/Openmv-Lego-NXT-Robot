import sensor
import time
import image
from machine import UART

# Initialize the sensor
sensor.reset()
sensor.set_contrast(3)
sensor.set_gainceiling(16)
sensor.set_framesize(sensor.QVGA)
sensor.set_pixformat(sensor.GRAYSCALE)

# Load Haar Cascade for face detection
face_cascade = image.HaarCascade("frontalface", stages=25)

# FPS clock
clock = time.clock()

# Initialize the UART for communication with HC-06 (TX=Pin 4, RX=Pin 5)
uart = UART(1, 9600)
uart.init(9600, bits=8, parity=None, stop=1, timeout_char=1000)

# Time control variable to manage command frequency
last_command_time = 0
command_interval = 5000  # 5000 milliseconds (5 seconds)

while True:
    clock.tick()
    img = sensor.snapshot()

    # Detect faces
    faces = img.find_features(face_cascade, threshold=0.75, scale_factor=1.25)

    current_time = time.ticks_ms()  # Get current time in milliseconds

    if faces and time.ticks_diff(current_time, last_command_time) > command_interval:
        # Only send the command if 5 seconds have passed since the last command
        uart.write("forward\n")
        last_command_time = current_time  # Update the last command time
        print("Forward command sent")

    # Draw rectangles around detected faces for visualization
    for face in faces:
        img.draw_rectangle(face)
        x, y, w, h = face
        print("Face detected at (x, y, w, h):", x, y, w, h)

