import serial
import glob
import serial.serialutil

class arduinoWrapper:
	def __init__(self):
		self.port = glob.glob("/dev/ttyACM*")[0]
		self.baud = 9600
		self.arg = False

	def startSerialService(self, ready3):
		self.serSock = serial.Serial(self.port, self.baud)
		#init socket connection
		self.serSock.write("")
		self.serSock.write("")
		print "serial link up"
		ready3[0]=True

	def setArg(self):
		self.arg = True

	def stopSerialService(self):
		self.serSock.close()

	def write(self,msg):
			self.serSock.write(msg)
			#print "Received: %s Send to Arduino: %s" %(msg, self.translation[msg])

	def read(self):
		while True:
			try:
				if self.arg:
					raise serial.serialutil.SerialException
				msg = self.serSock.readline()
				return msg
			except serial.serialutil.SerialException:
				#inWaiting return the number of chars in the receive buffer.
				print "number of ", self.serSock.inWaiting()
		#print "Read from Arduino: %s" %(msg)
