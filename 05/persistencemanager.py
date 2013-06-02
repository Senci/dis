import json
from singleton import Singleton

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
    
    def beginTransaction(self):
        """ Starts a new transaction. Creates a unique transaction ID and returns it to the client. """
        self.transactions += 1
        return self.transactions

    def commit(self, transactionID):
        """ Commits the transaction specified by the transaction ID. """
        for page in self.buffer:
            if page['taid'] == transactionID:
                self._persistPage(page)
                del self.buffer[page['pid']]

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
        f.write(json.dumps(logEntry)+'\n')
        f.close()

    def _persistPage(self, page):
        """ Persists page data to file 'pages/page{pageID}'. """
        filename = 'pages/page'+str(page['pid'])
        f = open(filename, 'w')
        f.write(json.dumps(page))

    def run(self):
        for i in range(10):
            self.write(1, 10+i, 'data'+str(i))