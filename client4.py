import socket
import struct

# for multicast ip and port
group = '224.0.0.1'
port = 55555

#AF_INET is IPv4, SOCK_DGRAM is socket type and IPPROTO_UDP is the protocol
client_sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
# socket level options
# allows us to reuse local addresses and bind the socket to an address that is already in use
client_sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
client_sock.bind(('', port))

# creating a request to add the client socket to the multicast group
# 4sl is a four letter string and 1 signed log integer to make the request
request = struct.pack("4sl", socket.inet_aton(group), socket.INADDR_ANY)
# joining the multicast group
client_sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, request)

while True:
#   # to send data with UDP
#   # recvfrom returns data and address
#   # message should be 4096 bytes
    print(client_sock.recv(4096))
