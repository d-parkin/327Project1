import socket
import time
# for multicast ip and port
group = '224.0.0.1'
port = 12345

# max hops the packet will be sent
hop = 3
#AF_INET is IPv4, SOCK_DGRAM is socket type and IPPROTO_UDP is the protocol
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, hop)

for i in range(10):
  sock.sendto(b"Hello From Server", (group, port))
  time.sleep(5)
# while True:
#   # to send data with UDP
#   # recvfrom returns data and address
#   # message should be 4096 bytes
#   data, address = sock.recvfrom(4096)
#   print(str(data))
#   message = bytes("From Server", 'utf-8')
#   sock.sendto(message, (group, port))
