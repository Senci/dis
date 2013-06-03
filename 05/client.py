import threading
import time
import datetime
import random

class Client(threading.Thread):
    def __init__(self, clientID, persistenceManager):
        threading.Thread.__init__(self)
        self.persistenceManager = persistenceManager
        self.clientID = clientID
        self.transactionID = 0
        self.lock = threading.Lock()

    def write(self, pageID, data):
        """ writes data to page with given pageID """
        print('[Client'+str(self.clientID)+'][TA'+str(self._getTransaction())+'] Writing to page '+str(pageID))
        self.persistenceManager.write(self._getTransaction(), pageID, data)


    def commit(self):
        """ commits the current transaction """
        self.persistenceManager.lock.acquire()
        print('[Client'+str(self.clientID)+'][TA'+str(self._getTransaction())+'] Transaction commiting.')
        self.persistenceManager.commit(self._getTransaction())
        self.transactionID = 0
        self.persistenceManager.lock.release()

    def _getTransaction(self):
        """ if the client already has a transaction the transaction ID is returned, otherwise a new transaction is created. """
        if self.transactionID == 0:
            self.transactionID = self.persistenceManager.beginTransaction()
        return self.transactionID

    def run(self):
        """ starts the client execution """
        transactions = random.randint(2,4)
        for t in range(transactions):
            writes = random.randint(1,5)
            for w in range(writes):
                pageID = self.clientID*10 + random.randint(1,9)
                self.write(pageID, datetime.datetime.now().isoformat())
                time.sleep(float(random.randint(100,400))/1000);
            self.commit()
