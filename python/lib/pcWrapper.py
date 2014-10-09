import socket
import time

class pcWrapper:
	def __init__(self):
		self.tcp_ip = "192.168.18.1"
		self.port = 5143
		#create socket object with above network and port
		self.ipSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
		#tells the kernel to reuse the socket even though it was left  in a TIME_WAIT state from previous execution
		#thus the socket can be reused to send data rapidly
		self.ipSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR,1)
		self.ipSocket.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST,1)
		self.ipSocket.bind((self.tcp_ip, self.port))
		self.pcaddr = None

	def startIPService(self,ready2):
			print "waiting for WIFI connection..."
			self.pcaddr = self.ipSocket.recvfrom(1024)[1]
			print "wifi link up"
			ready2[0]=True

	def stopIPService(self):
		self.ipSocket.close()

	def write(self, msg):
		#return the number of byte sent
		self.ipSocket.sendto(msg, self.pcaddr)
		#print "Write to PC: %s" %(msg)
	
	def read(self):
			msg = self.ipSocket.recv(1024)
			#print "Read from PC: %s" % (msg)
			return msg

#test= pcWrapper()
#temp = False
#test.startIPService(temp)
#while True:
#	input=int(raw_input("enter 0 to read or 1 to write:"))
#	if input==0:
#		test.read()
#	elif input==1:
#		test.write(raw_input("enter msg:"))
#	else:
#		break


