import sensor, image, time, math, machine
from machine import UART

# ----- Camera & Pose Calibration -----
f_x = (2.8 / 3.984) * 160   # focal length in pixels (x)
f_y = (2.8 / 2.952) * 120   # focal length in pixels (y)
c_x = 160 * 0.5             # image center x in pixels
c_y = 120 * 0.5             # image center y in pixels

# ----- Desired Pose -----
DESIRED_DISTANCE = 1.0    # desired forward distance in meters
DESIRED_LATERAL  = 0.0    # desired lateral offset (centered)
DESIRED_YAW      = 0.0    # desired yaw in degrees

# ----- Mecanum Wheel & Turn Parameters -----
wheel_diam_mm = 100
wheel_circumference = math.pi * wheel_diam_mm  # in mm
R = 150  # effective turning radius in mm

# ----- UART Setup -----
uart = UART(1, 9600)
uart.init(9600, bits=8, parity=None, stop=1, timeout_char=1000)

# We'll send commands at most every 1.5 seconds.
last_command_time = time.ticks_ms()
def send_all_commands(forward_cmd, forward_val, strafe_cmd, strafe_val, rotate_cmd, rotate_val):
    forward_val = int(forward_val)
    strafe_val = int(strafe_val)
    rotate_val = int(rotate_val)
    global last_command_time
    if time.ticks_diff(time.ticks_ms(), last_command_time) >= 1500:
        uart.write(forward_cmd + " " + str(forward_val) + "\n")
        print(forward_cmd + " " + str(forward_val) + "\n")
        time.sleep(4)  # 2-second delay

        uart.write(strafe_cmd + " " + str(strafe_val) + "\n")
        print(strafe_cmd + " " + str(strafe_val) + "\n")
        time.sleep(3)  # 2-second delay

        uart.write(rotate_cmd + " " + str(rotate_val) + "\n")
        print(rotate_cmd + " " + str(rotate_val) + "\n")
        time.sleep(2)  # 2-second delay

        last_command_time = time.ticks_ms()
        print("Forward: {} {} deg, Strafe: {} {} deg, Rotate: {} {} deg".format(
            forward_cmd, forward_val, strafe_cmd, strafe_val, rotate_cmd, rotate_val))
        return True
    return False

# ----- Fallback Distance Calculation -----
def calculate_distance(tag):
    corners = tag.corners
    width_pixels = math.sqrt((corners[0][0] - corners[1][0])**2 + (corners[0][1] - corners[1][1])**2)
    height_pixels = math.sqrt((corners[0][0] - corners[3][0])**2 + (corners[0][1] - corners[3][1])**2)
    avg_pixel_size = (width_pixels + height_pixels) / 2
    return (TAG_SIZE * f_x) / avg_pixel_size

# Tag physical size (in meters)
TAG_SIZE = 0.09

# ----- Camera Setup -----
sensor.reset()
sensor.set_pixformat(sensor.RGB565)
sensor.set_framesize(sensor.QQVGA)
sensor.skip_frames(time=2000)
sensor.set_auto_gain(False)
sensor.set_auto_whitebal(False)

clock = time.clock()

# State variables for printing only at first detection and after 15 seconds.
first_detection_time = None
printed_final = False

while True:
    clock.tick()
    img = sensor.snapshot()
    tags = img.find_apriltags(fx=f_x, fy=f_y, cx=c_x, cy=c_y, estimate_tag_pose=True)

    if tags:
        tag = tags[0]  # Use the first detected tag

        # Calculate distance using fallback method.
        distance = calculate_distance(tag)
        # Calculate lateral offset in meters: scale pixel offset by (distance / f_x).
        lateral_offset = ((tag.cx - c_x) / f_x) * distance

        # Calculate yaw from tag's z_rotation (converted to degrees)
        yaw = (180 * tag.z_rotation) / math.pi
        # Snap yaw to the range [-180, 180]
        yaw = ((yaw + 180) % 360) - 180

        # Draw tag info for visualization.
        img.draw_rectangle(tag.rect, color=(255, 0, 0))
        img.draw_cross(tag.cx, tag.cy, color=(0, 255, 0))
        img.draw_string(tag.cx, tag.cy-10, "ID:%d" % tag.id, color=(255,255,0), scale=1)
        axis_len = 20
        yaw_rad = math.radians(yaw)
        x_end = int(tag.cx + axis_len * math.cos(yaw_rad))
        y_end = int(tag.cy - axis_len * math.sin(yaw_rad))
        img.draw_arrow(tag.cx, tag.cy, x_end, y_end, color=(255, 0, 0))

        # Compute errors.
        error_distance = distance - DESIRED_DISTANCE
        error_lateral = lateral_offset - DESIRED_LATERAL
        # Since desired yaw is 0, the error is simply the current yaw
        error_yaw = yaw

        # Compute motor rotation degrees for forward and strafe.
        error_distance_mm = error_distance * 1000
        motor_rot_forward = (error_distance_mm / wheel_circumference) * 360

        error_lateral_mm = error_lateral * 1000
        motor_rot_strafe = (error_lateral_mm / wheel_circumference) * 360

        # Determine rotation direction and compute motor rotation degrees based on absolute yaw error.
        if error_yaw < 0:
            rotate_cmd = "right"
        else:
            rotate_cmd = "left"

        # Use the absolute value for computing the arc length and rotation degree
        theta = math.radians(abs(error_yaw))
        arc_length = theta * R
        motor_rot_turn = (arc_length / wheel_circumference) * 360

        # Determine command strings for forward/backward and strafe.
        if error_distance > 0:
            forward_cmd = "forward"
        else:
            forward_cmd = "backward"
        forward_val = abs(motor_rot_forward)

        if error_lateral > 0:
            strafe_cmd = "strafe_right"
        else:
            strafe_cmd = "strafe_left"
        strafe_val = abs(motor_rot_strafe)

        # On first detection, print the key information and send commands.
        if first_detection_time is None:
            first_detection_time = time.ticks_ms()
            print("Initial Detection:")
            print("Distance: {:.2f} m, Lateral: {:.3f} m, Yaw: {:.2f}°".format(distance, lateral_offset, yaw))
            print("error distance: {:.2f} m, error lateral: {:.3f} m, error yaw: {:.2f}°".format(error_distance, error_lateral, error_yaw))
            send_all_commands(forward_cmd, forward_val, strafe_cmd, strafe_val, rotate_cmd, motor_rot_turn)

        # After 15 seconds, print updated information (once).
        if first_detection_time and (not printed_final) and (time.ticks_diff(time.ticks_ms(), first_detection_time) >= 15000):
            print("After 15 seconds:")
            print("Distance: {:.2f} m, Lateral: {:.3f} m, Yaw: {:.2f}°".format(distance, lateral_offset, yaw))
            printed_final = True
    else:
        # If no tag is detected, you might choose to send stop commands or do nothing.
        pass

    # Uncomment to see frame rate if desired.
    # print("FPS:", clock.fps())
