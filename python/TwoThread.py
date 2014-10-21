#!
__author__ = 'aswin'
import thread
import re
from collections import deque
from lib.androidWrapper import *
from lib.pcWrapper import *
from lib.arduinoWrapper import *

class ThreeThread:
	"""
	This class will spawn 3 thread. Each 3 thread has a blocking function (read)
	that will block until a msg is received in the corresponding channel. Once a message
	arrive, it will first check if the first char is 0-7. If yes, it is then used to
	determine the destination of the message. The string is then passed to each corresponding
	function as arg msg with the first char removed. E.g: "6FORWARD" will send "FORWARD" to
	arduino and android.

	"""
	def __init__(self):
		self.android = AndroidSim()
		self.pc 	 = PCWrapper()
		self.arduino = ArduinoWrapper()
		self.stopflag= False

		self.msgToPC = ''

		#self.ipq = deque([])
		#self.btq = deque([])
		#self.serialq = deque([])
		self.destination ={
			#'0':self.writeNone,
			'1':self.writePC,
			'2':self.writeAndroid,
			'3':self.writePCAn,
			'4':self.writeArduino,
			'5':self.writePCAr,
			'6':self.writeArAn,
			'7':self.writeAll
		}
	#def writeNone(self, msg):
	#	pass

	def writeArduino(self, msg):
		self.arduino.write(msg)
		print "Send to arduino: [%s]\n" % (msg)

	def writeAndroid(self, msg):
		#temp = [False]
		while True:
			try:
				self.android.write(msg)
				break
			except BluetoothError:
				print "connection reset by peer"
				self.android.startBTService()
				#continue
		print "Send to android: [%s]\n" % (msg)

	def writeArAn(self, msg):
		self.writeArduino(msg)
		self.writeAndroid(msg)

	def writePC(self, msg):
		self.msgToPC=msg
		self.pc.write(msg)
		print "Send to PC: [%s]\n" % (msg)

	def writePCAr(self, msg):
		self.writePC(msg)
		self.writeArduino(msg)

	def writePCAn(self, msg):
		self.writePC(msg)
		self.writeAndroid(msg)

	def writeAll(self, msg):
		self.writePC(msg)
		self.writeAndroid(msg)
		self.writeArduino(msg)

	def ipRead (self, delay, pc):
		while  not self.stopflag:
			#print '(system):ipRead in blocking mode while waiting for pc input...'
			#assume pc string does not get chopped
			msg = pc.read()
			if msg is None:
				print "msg from pc is None"

			elif msg == 'T':
				self.pc.write(self.msgToPC)
			elif (re.match(r'[1-7].+', msg)):
				print "Received from pc: [%s]" % (msg)
				self.destination[msg[0]](msg[1:])
			else:
				print "Warning: [%s] from pc is in the wrong format!!! Destination from 1-7 only!!!" % (msg)
			#time.sleep (delay)

	def btRead (self, delay, android):
		while not self.stopflag:
			#print '(system):btRead in blocking mode while waiting for android input...'
			#BluetoothError Connection reset by peer (in btRead thread) will cause program to stop
			try:
				msg = android.read()
			except BluetoothError:
				#if disconnected by peer, restart accept method to establish bt connection, then continue to next loop
				print "connection reset by peer"
				self.android.startBTService()
				continue

			if msg is None:
				print "msg from android is Null"
			elif (re.match(r'[0-7].+', msg)):
				print "Received from android: [%s]" % (msg)
				self.destination[msg[0]](msg[1:])
			else:
				print "Warning: [%s] from bluetooth is in the wrong format!!!" % (msg)
			#time.sleep (delay)

	def serialRead(self, delay, arduino):
		while not self.stopflag:
			#print "(system):serialRead in blocking mode while waiting for arduino input..."
			msg = arduino.read()
			#append the msg to both bluetooth queue and ip queue
			if msg is None:
				print "msg from arduino is None"
			elif (re.match(r'[0-7].+', msg)):
				print "Received from arduino: [%s]" % (msg)
				matchObj = re.match(r'\w+', msg[1:])
				temp = matchObj.group()
				self.destination[msg[0]](temp)
			else:
				print "Warning: [%s] from arduino is in the wrong format!!!" % (msg)
			#time.sleep(delay)

	def startServices(self):
		ready1=[False]
		ready2=[False]
		ready3=[False]
		thread.start_new_thread(self.android.startBTService, (ready1,))
		thread.start_new_thread(self.pc.startIPService, (ready2,))
		thread.start_new_thread(self.arduino.startSerialService, (ready3,))

		while True:
			if ready1[0] is True and ready2[0] is True and ready3[0] is True:
				break
			else:
				time.sleep(1)

	def mainStart(self):
		print "All connection service is up...\nstarting communication:"
		thread.start_new_thread (self.ipRead,  		(0.5, self.pc))
		thread.start_new_thread (self.btRead,		(0.5, self.android))
		thread.start_new_thread (self.serialRead,	(0.5, self.arduino))

		#except:
		while True:
			time.sleep(4.0)

test = ThreeThread()
test.startServices()
test.mainStart()