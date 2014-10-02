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
	This class purpose is to clear the checklist for RPi

	"""
	def __init__(self):
		self.android = androidWrapper()
		self.pc = pcWrapper()
		self.arduino = arduinoWrapper()

		self.ipq = deque([])
		self.btq = deque([])
		self.serialq = deque([])
		self.destination ={
			0:self.writeNone,
			1:self.writePC,
			2:self.writeAndroid,
			3:self.writePCAn,
			4:self.writeArduino,
			5:self.writePCAr,
			6:self.writeArAn,
			7:self.writeAll
		}
	def writeNone(self):
		pass

	def writeArduino(self, msg):
		self.arduino.write(msg)
		print "Written to arduino: %s" % (msg)

	def writeAndroid(self, msg):
		self.android.write(msg)
		print "Written to android: %s" % (msg)

	def writeArAn(self, msg):
		self.writeArduino(msg)
		self.writeAndroid(msg)

	def writePC(self, msg):
		self.pc.write(msg)
		print "Written to PC: %s" % (msg)

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
		while  True:
			print 'ipRead in blocking mode while waiting for pc input...'
			msg = pc.read()
			if(re.match(r'[0-7].*', msg)):
				self.destination[msg[0]](msg[1:])
			else:
				print "Warning: [%s] from pc is in the wrong format!!!" % (msg)
			time.sleep (delay)

	def btRead (self, delay, android):
		stop_flag = 0
		while stop_flag == 0:
			print 'btRead in blocking mode while waiting for android input...'
			msg = android.read()
			if(re.match(r'[0-7].*', msg)):
				self.destination[msg[0]](msg[1:])
			else:
				print "Warning: [%s] from bluetooth is in the wrong format!!!" % (msg)
			time.sleep (delay)

	def serialRead(self, delay, arduino):
		stop_flag = 0
		while stop_flag == 0:
			print "serialRead in blocking mode while waiting for arduino input..."
			msg = arduino.read()
			#append the msg to both bluetooth queue and ip queue
			if(re.match(r'[0-7].*', msg)):
				self.destination[msg[0]](msg[1:])
			else:
				print "Warning: [%s] from arduino is in the wrong format!!!" % (msg)
			time.sleep(delay)

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
				time.sleep(3)

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