import serial

class arduinoWrapper:

    def __init__(self):
        self.port = '/dev/ttyACM0'
        self.baud = 9600

    def startSerialService(self):
        self.serSock = serial.Serial(self.port, self.baud)
        #init socket connection
        self.serSock.write("")
        self.serSock.write("")
        print "serial link up"

    def stopSerialService(self):
        self.serSock.close()

    def write(self,msg):
        self.serSock.write(msg)
        print "Write to Arduino: %s" %(msg)

    def read(self):
        msg = self.serSock.readline()
        print "Read from Arduino: %s" %(msg)
        return msg

test= arduinoWrapper()
test.startSerialService()
test.write("f")