import socket

#AF_INET is IPv4, SOCK_DGRAM is connectionless UDP protocol
client_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

message = "From Client"
client_sock.sendto(message.encode("utf-8"),('127.0.0.1', 12345))
data, address = client_sock.recvfrom(4096)
print("Server Says")
print(str(data))
client_sock.close()