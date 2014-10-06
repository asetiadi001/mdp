import threading
import thread
import time
from collections import deque
import re

gQueue = deque([1,2,3,4,5])
exitFlag = 0
class Test():

	def __init__(self):
		global gQueue
		self.aList= ['a','b','c','d','e']

	def startPrint(self, delay):
		global gQueue
		while True:
			if len(gQueue) == 0:
				break
			time.sleep(delay)
			msg = gQueue.popleft()
			print msg

	def startInsert(self, delay, count):
		global gQueue
		while True:
			if count == 5:
				break
			time.sleep(delay)
			gQueue.append(self.aList[count])
			print "inserted " + self.aList[count]
			count=count+1

	def startEngine(self):
		thread.start_new_thread(self.startPrint, (0.5, ))
		thread.start_new_thread(self.startInsert, (1, 0))
		while True:
			time.sleep(3)
			if len(gQueue)==0:
				break

class MultiThread(threading.Thread):
	def __init__(self, threadID, name, counter):
		threading.Thread.__init__(self)
		self.threadID = threadID
		self.name = name
		self.couter = counter

	def run(self):
		print "Starting " + self.name

class MyThread(threading.Thread):
	def __init__(self, tname, count, jump):
		threading.Thread.__init__(self)
		self.tname = tname
		self.count = count
		self.jump = jump

	def run(self):
		print "Starting %s" % (self.tname)
		self.increasingNum(self.name, self.count, self.jump)
		print "Done looping for ", self.name

	def increasingNum(self, name, count, jump):
		global exitFlag
		for num in range(0, count, jump):
			if exitFlag:
				thread.exit()
			if num==3:
				exitFlag = 1
			print num
			time.sleep(1)

