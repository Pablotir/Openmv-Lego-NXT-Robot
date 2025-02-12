import time
from machine import UART

# Initialize UART for communication with the HC-06 Bluetooth module
uart = UART(0, 9600)  # Use correct UART port and baud rate

# Send a command to the NXT running leJOS over Bluetooth
#connecting to a specific address
uart.write("AT+CONNLINK=98D331F0E2F5")
# You need to send a Bluetooth-specific message or a simple command that leJOS can handle.
# Here is an example of sending a custom command
nxt_command = "Hello"  # Replace with an actual command understood by leJOS
uart.write(nxt_command)
# Wait for a response (optional)
time.sleep(1)

# Read the response from the NXT (up to 64 bytes)
response = uart.read(64)

if response:
    print("Response from NXT:", response)
else:
    print("No response from NXT.")
