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

	def ipWrite (self, delay, pc, btq):
		stop_flag = 0
		while stop_flag == 0:
			time.sleep (delay)
			if len(btq) >0:
				msg = btq.popleft()
				print "BT queue length after pop: " , len(btq)
				pc.write(msg)
				print "%s: %s --msg: %s" % ("ipWrite", time.ctime(time.time()), msg)


	def ipRead (self, delay, pc, ipq):
		stop_flag = 0
		while stop_flag == 0:
			time.sleep (delay)
			msg = pc.read()
			if msg !='':
				ipq.append(msg)
				print "IP queue length after append: ", len(ipq)
				print "%s: %s--msg: %s" % ("ipRead", time.ctime(time.time()),msg )

	def btWrite (self, delay, android, ipq):
		stop_flag = 0
		while stop_flag == 0:
			time.sleep (delay)
			if len(ipq) >0:
				msg = ipq.popleft()
				print "IP queue length after pop: " , len(ipq)
				android.write(msg)
				print "%s: %s --msg: %s" % ("btWrite", time.ctime(time.time()), msg)

	def btRead (self, delay, android, btq):
		stop_flag = 0
		while stop_flag == 0:
			time.sleep (delay)
			msg = android.read()
			if msg !='':
				btq.append(msg)
				print "BT queue length after append: ", len(btq)
				print "%s: %s--msg: %s" % ("btRead", time.ctime(time.time()),msg )

	def serialWrite(self, delay):
		stop_flag = 0


	def startServices(self):
		ready1=[False]
		ready2=[False]
		thread.start_new_thread(self.android.startBTService, (1.0,ready1))
		thread.start_new_thread(self.pc.startIPService, (1.0,ready2))
		while True:
			if ready1[0]!=True or ready2[0]!=True:
				pass
			else:
				print "break off"
				break

	def mainStart(self):
		print "entering mainStart"
		thread.start_new_thread (self.ipWrite, (0.5, self.pc, self.btq))
		thread.start_new_thread (self.ipRead,  (0.5, self.pc, self.ipq))
		thread.start_new_thread (self.btWrite, (0.5, self.android, self.ipq))
		thread.start_new_thread (self.btRead,  (0.5, self.android, self.btq))
		#except:
		while True:
			time.sleep(4.0)


test = Main()
test.startServices()
test.mainStart()