import socket

#AF_INET is IPv4, SOCK_DGRAM is connectionless UDP protocol
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
# Need to bind socket with IP address and port number
sock.bind(('127.0.0.1', 12345))

while True:
  # to send data with UDP
  # recvfrom returns data and address
  # message should be 4096 bytes
  data, address = sock.recvfrom(4096)
  print(str(data))
  message = bytes("From Server", 'utf-8')
  sock.sendto(message, address)
