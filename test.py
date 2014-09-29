import thread
import time
from collections import deque

gQueue = deque([1,2,3,4,5])
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
aswin = Test()
aswin.startEngine()
