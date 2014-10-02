#!
import thread
import re
from collections import deque
from lib.androidWrapper import *
from lib.pcWrapper import *
from lib.arduinoWrapper import *

class Main:
	"""
	This class purpose is to clear the checklist for RPi

	"""
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

				pc.write(msg)
				print "writing to pc: ", msg, "|success..."

	def ipRead (self, delay, pc, btq, serq):
		stop_flag = 0
		while stop_flag == 0:
			print 'pcRead in blocking mode while waiting for pc input...'
			msg = pc.read()
			if(msg!=''):
				btq.append(msg)
				serq.append(msg)
				#print "IP queue length after append: ", len(ipq)
				print "append to btq and serq: ", msg, "|success..."
				#print "%s: %s--msg: %s" % ("ipRead", time.ctime(time.time()),msg )
			time.sleep (delay)

	def btWrite (self, delay, android, btq):
		
		stop_flag = 0
		while stop_flag == 0:
			time.sleep (delay)
			#print "btWrite awake - not"
			if len(btq) >0:
				msg = btq.popleft()

				android.write(msg)
				print "Writing to android: ", msg, "|success..."
				#print "Serial queue length after pop: " , len(serialq)

	def btRead (self, delay, android, pcq, serq):
		stop_flag = 0
		while stop_flag == 0:
			print 'btRead in blocking mode while waiting for android input...'
			msg = android.read()
			if(msg!=''):
				#print "From android: %s" % (msg)

				pcq.append(msg)
				serq.append(msg)
				print "append to pc and serial queue: ", msg, "|success"
				#print "BT queue length after append: ", len(btq)
				time.sleep (delay)

	def serialWrite(self, delay, arduino, serq):
		stop_flag = 0
		while stop_flag == 0:
			time.sleep(delay)
			#print "serialWrite awake - not"
			if len(serq) > 0:
				msg = serq.popleft()
				arduino.write(msg)
				print "Writing to arduino: %s|success..." %(msg)
				#print "BT queue length after pop: ", len(btq)
				#print "%s: %s--msg: %s" % ("serialRead", time.ctime(time.time()),msg )
	
	def serialRead(self, delay, arduino, btq, ipq):
		stop_flag = 0
		while stop_flag == 0:
			print "serialRead in blocking mode waiting for arduino input"
			#if arduino.read() !=None: #check for empty string/char when reading.
			msg = arduino.read()
			#append the msg to both bluetooth queue and ip queue
			if re.match(r'[a-zA-Z0-9]+',msg,re.I):

				btq.append(msg)
				ipq.append(msg)
				print "Append to pc and arduino queue: ", msg, "|success..."
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

		thread.start_new_thread (self.ipRead,  		(0.5, self.pc, self.btq, self.serialq))
		thread.start_new_thread (self.ipWrite, 		(0.5, self.pc, self.ipq))
		thread.start_new_thread (self.btRead,		(0.5, self.android, self.ipq, self.serialq))
		thread.start_new_thread (self.btWrite,		(0.5, self.android, self.btq))
		thread.start_new_thread (self.serialRead,	(0.5, self.arduino, self.btq, self.ipq))
		thread.start_new_thread (self.serialWrite,	(0.5, self.arduino, self.serialq))

		#except:
		while True:
			time.sleep(4.0)


test = Main()
test.startServices()
test.mainStart()
