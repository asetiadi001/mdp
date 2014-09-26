import time
from bluetooth import *

class androidWrapper:
	def __init__(self):
		self.uuid="00001101-0000-1000-8000-00805F9B34FB"
		self.server_sock= BluetoothSocket(RFCOMM)
		self.server_sock.bind(("",PORT_ANY))
		self.client_sock = None
		self.client_info = None
		self.server_sock.listen(1)
		self.port = self.server_sock.getsockname()[1]
		advertise_service( self.server_sock, "MDPGrp18",
				   service_id= self.uuid,
				   service_classes= [self.uuid, SERIAL_PORT_CLASS],
				   profiles= [SERIAL_PORT_PROFILE],
				  )

	def startBTService(threadName, self, delay, finish1):
		while True:
			time.sleep(delay)
			if self.client_sock is not None:
				finish1= True
				print "Accepted connection from ", self.client_info
				break
			print "waiting for connection on RFCOMM channel %d" % (self.port)
			self.client_sock, self.client_info = self.server_sock.accept()


	def stopBTService(self):
		self.client_sock.close()
		self.client_info = None
		self.server_sock.close()

	def write(self,msg):
		self.client_sock.send(msg)
		print "Write to Android: %s" %(msg)

	def read(self):
		if self.client_sock is not None:
			msg = self.client_sock.recv(1024)
			print "Read from Android: %s" %(msg)
			return msg
		else:
			return None

