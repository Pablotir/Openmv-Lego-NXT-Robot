import sensor, image, time, math

# Camera setup
sensor.reset()
sensor.set_pixformat(sensor.JPEG)
sensor.set_framesize(sensor.HD)
sensor.set_quality(80)
sensor.skip_frames(time=2000)  # Allow the camera to adjust

# AprilTag size in meters (you need to know the physical size of the tag)
TAG_SIZE = 0.1  # 10 cm

# Camera intrinsic parameters (focal length in pixels)
# These values are specific to your camera and can be calibrated
FX = 248.89  # Example focal length in pixels (replace with your calibrated value)
FY = 248.89  # Example focal length in pixels (replace with your calibrated value)

# Function to calculate distance to the AprilTag
def calculate_distance(tag):
    # Get the corners of the tag
    corners = tag.corners  # corners is a tuple of four (x, y) tuples

    # Calculate the width and height of the tag in pixels
    width_pixels = math.sqrt((corners[0][0] - corners[1][0])**2 + (corners[0][1] - corners[1][1])**2)
    height_pixels = math.sqrt((corners[0][0] - corners[3][0])**2 + (corners[0][1] - corners[3][1])**2)

    # Debug: Print the pixel width and height
    print("Width (pixels): {:.2f}, Height (pixels): {:.2f}".format(width_pixels, height_pixels))

    # Average the width and height to get a more accurate distance
    avg_pixel_size = (width_pixels + height_pixels) / 2

    # Calculate the distance using the formula: distance = (TAG_SIZE * focal_length) / (avg_pixel_size)
    distance = (TAG_SIZE * FX) / avg_pixel_size

    return distance

# Main loop
clock = time.clock()
last_print_time = time.ticks_ms()  # Track the last time the distance was printed

while(True):
    clock.tick()
    img = sensor.snapshot()

    # Detect AprilTags
    tags = img.find_apriltags()

    for tag in tags:
        # Draw a rectangle around the detected tag
        img.draw_rectangle(tag.rect, color=(255, 0, 0))  # Draw the bounding box

        # Calculate the distance to the tag
        distance = calculate_distance(tag)

        # Print the distance to the tag
        print("Distance to tag: {:.2f} meters".format(distance))

    # Print the distance every second
    current_time = time.ticks_ms()
    if time.ticks_diff(current_time, last_print_time) >= 1000:  # 1000 ms = 1 second
        last_print_time = current_time  # Reset the timer
