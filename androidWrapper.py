from bluetooth import *

class androidWrapper:
	def __init__(self):
		uuid="00001101-0000-1000-8000-00805F9B34FB"

	def connect(self):
		server_sock= BluetoothSocket(RFCOMM)
		server_sock.bind(("",PORT_ANY))
		server_sock.listen(1)
		port= server_sock.getsockname()[1]
		advertise_service( server_sock, "MDPGrp18",
				   service_id= uuid,
				   service_classes= [uuid, SERIAL_PORT_CLASS],
				   profiles= [SERIAL_PORT_PROFILE],
				  )
		print "waiting for connection on RFCOMM channel %d" % (port)
		client_sock, client_info= server_sock.accept()
		print "Accepted connection from ", client_info

	def disconnect(self):
		client_sock.close()
		server_sock.close()

	def write(self,msg):
		self.client_sock.send(msg)
		print "Write to Android: %s" %(msg)

	def read(self):
		msg = self.client_sock.recv(1024)
		print "Read from Android: %s" %(msg)
		return msg

