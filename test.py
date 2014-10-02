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

def printTime(threadName, delay, counter):
	while counter:
		if exitFlag:
			thread.exit()
		print ""

if re.match(r'([0-7]).*', "aswin"):
	print True
else:
	print False