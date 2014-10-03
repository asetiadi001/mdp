import serial
import glob

class arduinoWrapper:
	def __init__(self):
		self.port = glob.glob("/dev/ttyACM*")
		self.baud = 115200


	def startSerialService(self, ready3):
		self.serSock = serial.Serial(self.port, self.baud)
		#init socket connection
		self.serSock.write("")
		self.serSock.write("")
		print "serial link up"
		ready3[0]=True

	def stopSerialService(self):
		self.serSock.close()

	def write(self,msg):
			self.serSock.write(msg)
			#print "Received: %s Send to Arduino: %s" %(msg, self.translation[msg])

	def read(self):
		msg = self.serSock.readline()
		#print "Read from Arduino: %s" %(msg)
		return msg
