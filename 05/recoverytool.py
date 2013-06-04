from persistencemanager import PersistenceManager
import json

class RecoveryTool:
	def __init__(self):
		self.persistenceManager = PersistenceManager.Instance()

	""" Analyzes logs and recovers database """
	def redoRecovery(self):
		""" @todo: i am a comment """
		logFile = open('pm.log', 'r')
		while True:
			currentLine = logFile.readline()
			if currentLine == '':
				break
			try:
				logEntry = json.loads(currentLine)
				pageID = logEntry['pid']
				pageFile = open('pages/page'+str(pageID), 'r')
				page = json.loads(pageFile.read())
				print('[RecoveryTool] Found log entry for page'+str(pageID)+' with lsn '+str(page['lsn'])+' the lsn in the log is '+str(logEntry['lsn'])+'.')
				if page['lsn'] < logEntry['lsn']:
					page['lsn'] = logEntry['lsn']
					page['data'] = logEntry['data']
					self.persistenceManager._persistPage(page)
					print('[RecoveryTool] Found winner! recovering "pages/page'+str(pageID)+'".')
				pageFile.close()
			except ValueError:
				print('[RecoveryTool] Trying to process Page '+str(pageID)+'! There probably has been a JSON-Decoding error. The Log-File might be corrupted.')
			except FileNotFoundError:
				pageFile = open('pages/page'+str(pageID), 'w')
				print('[RecoveryTool] Found log entry for page'+str(pageID)+'! There currently is no page entry for this page in persitence storage.')
				page = {'lsn':logEntry['lsn'], 'data':logEntry['data'], 'pid':pageID, 'taid':logEntry['taid']}
				self.persistenceManager._persistPage(page)
				print('[RecoveryTool] Found winner! recovering "pages/page'+str(pageID)+'".')
				pageFile.close()
		logFile.close()

