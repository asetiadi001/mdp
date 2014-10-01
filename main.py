import thread

from collections import deque
from androidWrapper import *
from pcWrapper import *
from arduinoWrapper import *

class Main:

	def __init__(self):
		self.android = androidWrapper()
		self.pc = pcWrapper()
		self.arduino = arduinoWrapper()

		self.ipq = deque([])
		self.btq = deque([])
		self.serialq = deque([])

	def ipWrite (self, delay, pc, ipq):
		stop_flag = 0
		while stop_flag == 0:
			time.sleep (delay)
			if len(ipq) >0:
				msg = ipq.popleft()
				#print "WiFi queue length after pop: " , len(ipq)
				print "writing to pc: ", msg
				pc.write(msg)

	def ipRead (self, delay, pc, ipq):
		stop_flag = 0
		while stop_flag == 0:
			msg = pc.read()
			if(msg!=''):
				ipq.append(msg)
				print "IP queue length after append: ", len(ipq)

				print "%s: %s--msg: %s" % ("ipRead", time.ctime(time.time()),msg )
			time.sleep (delay)

	def btWrite (self, delay, android, serialq):
		
		stop_flag = 0
		while stop_flag == 0:
			time.sleep (delay)
			#print "btWrite awake - not"
			if len(serialq) >0:
				msg = serialq.popleft()
				print "Writing to android: ", msg
				android.write(msg)
				#print "Serial queue length after pop: " , len(serialq)

	def btRead (self, delay, android, btq):
		stop_flag = 0
		while stop_flag == 0:
			print 'btRead in blocking mode'
			msg = android.read()
			if(msg!=''):
				#print "From android: %s" % (msg)
				print "Append android queue: ", msg
				btq.append(msg)
				#print "BT queue length after append: ", len(btq)
				time.sleep (delay)

	def serialWrite(self, delay, arduino, btq):
		stop_flag = 0
		while stop_flag == 0:
			time.sleep(delay)
			#print "serialWrite awake - not"
			if len(btq) > 0:
				msg = btq.popleft()
				print "Writing to arduino: %s" %(msg)
				arduino.write(msg)
				#print "BT queue length after pop: ", len(btq)
				#print "%s: %s--msg: %s" % ("serialRead", time.ctime(time.time()),msg )
	
	def serialRead(self, delay, arduino, serialq, ipq):
		stop_flag = 0
		while stop_flag == 0:
			print "serialRead in blocking mode"
			#if arduino.read() !=None: #check for empty string/char when reading.
			msg = arduino.read()
			#append the msg to both bluetooth queue and ip queue
			if(msg!=''):
				print "Append to pc and arduino queue: ", msg
				serialq.append(msg)
				ipq.append(msg)

				#print "From arduino: ",msg
				#print "Serial queue length after append: ", len(serialq)
				#print "IP queue length after append: ", len(ipq)
			time.sleep(delay)

	def startServices(self):
		ready1=[False]
		ready2=[False]
		ready3=[False]
		thread.start_new_thread(self.android.startBTService, (ready1,))
		thread.start_new_thread(self.pc.startIPService, (ready2,))
		thread.start_new_thread(self.arduino.startSerialService, (ready3,))
		while True:
			if ready1[0]!=True or ready2[0]!=True or ready3[0]!=True:
				pass
			else:
				print "break off"
				time.sleep(3)
				break

	def mainStart(self):
		print "entering mainStart"
		print "waiting for start command from android"
		while(self.android.read()!='START'):
			time.sleep(0.5)
		print "start received...\nstarting communication:"

		#thread.start_new_thread (self.ipRead,  (0.5, self.pc, self.btq, self.serialq))
		thread.start_new_thread (self.ipWrite, (0.5, self.pc, self.ipq))
		thread.start_new_thread (self.btRead,  (0.5, self.android, self.btq))
		thread.start_new_thread (self.btWrite, (0.5, self.android, self.serialq))
		thread.start_new_thread (self.serialRead,  (0.5, self.arduino, self.serialq, self.ipq))
		thread.start_new_thread (self.serialWrite, (0.5, self.arduino, self.btq))

		#except:
		while True:
			time.sleep(4.0)


test = Main()
test.startServices()
test.mainStart()
