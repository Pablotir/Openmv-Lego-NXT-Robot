import machine
import time

# Initialize UART interface (adjust UART number and pins as needed)
uart = machine.UART(0, baudrate=9600)

# Function to send data
def send_data(data):
    uart.write(data + '\n')

# Function to receive data
def receive_data():
    if uart.any():
        return uart.readline().decode().strip()
    return None

# Main loop
while True:
    # Example: Send a message
    send_data("Hello NXT")
    print("Sent: Hello NXT")

    # Check for incoming data
    incoming = receive_data()
    if incoming:
        print("Received:", incoming)

    time.sleep(1)
