# Run all clients first
# Then run server

import socket
import time
# for multicast ip and port
group = '224.0.0.1'
port = 12345

# max hops the packet will be sent
hop = 1
#AF_INET is IPv4, SOCK_DGRAM is socket type and IPPROTO_UDP is the protocol
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
# socket options to specify IP protocol and the Time to Live Settings which is 1 because we are only
# delivering messages to devices on same subnetwork
sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, hop)

for i in range(10):
  sock.sendto(b"Hello From Server", (group, port))
  time.sleep(5)


# Protocol Design: https://blog.finxter.com/how-to-send-udp-multicast-in-python/