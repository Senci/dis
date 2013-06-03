import os
import json
from singleton import Singleton
from client import Client
import threading

@Singleton
class PersistenceManager:
    """ A Persistence Manager which is serving multiple clients.
    Buffer management:
      * Insertion strategy: non-atomic
      * Replacement strategy: no-steal
      * Propagation strategy: no-force
    """

    def __init__(self):
        # The buffer is a directory with the page ID as key.
        self.buffer       = {}
        self.transactions = 0
        self.lsn          = 0
        self.lock         = threading.Lock()
    
    def beginTransaction(self):
        """ Starts a new transaction. Creates a unique transaction ID and returns it to the client. """
        self.transactions += 1
        return self.transactions

    def commit(self, transactionID):
        """ Commits the transaction specified by the transaction ID.
        When yousing this method with multiple threads be aware to implement it's call thread-safe (aquire & release lock).
        """
        pid = 0
        for page in self.buffer.values():
            if page['taid'] == transactionID:
                pid = page['pid']
                self._persistPage(page)
        if pid != 0:
            del self.buffer[pid]

    def write(self, transactionID, pageID, data):
        """ Writes the given data to the given page ID on behalf of the given transaction to the buffer. """
        self.lsn += 1
        self._writeLog(self.lsn, transactionID, pageID)
        self.buffer[pageID] = { 'pid':pageID, 'lsn':self.lsn, 'taid':transactionID, 'data':data }
        if len(self.buffer) > 5:
            for page in self.buffer.values():
                self._persistPage(page)
            self.buffer.clear()

    def _writeLog(self, lsn, transactionID, pageID):
        """ Writes log data to file 'pm.log' with log-sequence-number, transaction ID and Page ID. """
        f = open('pm.log', 'a')
        logEntry = { 'lsn':lsn, 'pid':pageID, 'taid': transactionID }
        f.write(json.dumps(logEntry, sort_keys=True)+'\n')
        f.close()

    def _persistPage(self, page):
        """ Persists page data to file 'pages/page{pageID}'. """
        filename = 'pages/page'+str(page['pid'])
        f = open(filename, 'w')
        f.write(json.dumps(page, sort_keys=True))

    def clearData(self):
        """ Deletes all persisted pageData, the buffer and the log. Use with caution! """
        # delete log
        f = open('pm.log', 'w')
        f.write('')
        f.close()
        # delete pages
        filelist = [ f for f in os.listdir('pages') if f.startswith("page") ]
        for f in filelist:
            os.remove('pages/'+f)

    def run(self):
        self.clients = []
        for cid in range(5):
            c = Client(cid+1, self)
            self.clients.append(c)
            c.start()