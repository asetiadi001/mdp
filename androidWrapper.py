from bluetooth import *

class androidWrapper:
	def __init__(self):
		self.uuid="00001101-0000-1000-8000-00805F9B34FB"
		self.server_sock= BluetoothSocket(RFCOMM)
		self.server_sock.bind(("",PORT_ANY))
		self.client_sock, self.client_info = None

	def startBTService(self):
		self.server_sock.listen(1)
		port= self.server_sock.getsockname()[1]
		advertise_service( self.server_sock, "MDPGrp18",
				   service_id= self.uuid,
				   service_classes= [self.uuid, SERIAL_PORT_CLASS],
				   profiles= [SERIAL_PORT_PROFILE],
				  )
		print "waiting for connection on RFCOMM channel %d" % (port)
		self.client_sock, self.client_info= self.server_sock.accept()
		print "Accepted connection from ", self.client_info

	def stopBTService(self):
		self.client_sock.close()
		self.client_info = None
		self.server_sock.close()

	def write(self,msg):
		self.client_sock.send(msg)
		print "Write to Android: %s" %(msg)

	def read(self):
		msg = self.client_sock.recv(1024)
		print "Read from Android: %s" %(msg)
		return msg

