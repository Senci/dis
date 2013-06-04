import threading
import time

class ThreadBlocker(threading.Thread):
	def __init__(self, lock):
		threading.Thread.__init__(self)
		self.lock = lock
		self.blocking = False

	def blockThread(self):
		if not self.blocking:
			self.lock.acquire()
			self.blocking = True
			while self.blocking:
				time.sleep(float(1)/200)

	def releaseThread(self):
		self.blocking = False
		self.lock.release()