import socket
import sys

host = ''
port = 2626

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

try:
	s.bind((host,port))
except socket.error as e:
	print(str(e))

s.listen(5)

conn, addr = s.accept()

print('connected to: '+addr[0]+':'+str(addr[1]))