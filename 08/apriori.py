from itertools import combinations

def readTransactionsFromFile(fileName):
	# open the transaction.txt, read each line and save its items into a list
	transactionsFile = open(fileName, 'r')
	transactions = []
	for transactionLine in transactionsFile:
		transactions.append(transactionLine.split())
	print('Finished reading textfile. Now lets do some apriori. =)')
	return transactions

def findFrequentOneItemsets(transactions, minSup):
	# count appearances
	countingDict = {}
	for transaction in transactions:
		for item in transaction:
			if item not in countingDict:
				countingDict[item] = 1
			else:
				countingDict[item] += 1
	# write all items with more occurences then minSup into list
	L1 = []
	for item in countingDict:
		if countingDict[item] >= minSup:
			L1.append(item)
	return L1

def findFrequentTwoItemsets(transactions, minSup, oneItemset):
	possibleTuples = list(combinations(oneItemset,2))
	# count appearances
	countingDict = {}
	for transaction in transactions:
		for possibleTuple in possibleTuples:
			if possibleTuple[0] in transaction and possibleTuple[1] in transaction:
				if possibleTuple not in countingDict:
					countingDict[possibleTuple] = 1
				else:
					countingDict[possibleTuple] += 1
	# write all items with more occurences then minSup into list
	L2 = []
	for item in countingDict:
		if countingDict[item] >= minSup:
			L2.append(item)
	return L2

def findFrequentThreeItemsets(transactions, minSup, twoItemset):
	itemsInSet = ','.join(map(','.join,L2)).split(',')
	itemsInSet = set(itemsInSet)
	allTriples = list(combinations(itemsInSet,3))

	possibleTriples = []
	# just use triples which contain a tuple of twoItemset
	for aTriple in allTriples:
		for tupleInSet in twoItemset:
			if tupleInSet[0] in aTriple and tupleInSet[1] in aTriple:
				possibleTriples.append(aTriple)
				break

	# count appearances
	countingDict = {}
	for transaction in transactions:
		for possibleTriple in possibleTriples:
			if possibleTriple[0] in transaction and possibleTriple[1] in transaction and possibleTriple[2] in transaction:
				if possibleTriple not in countingDict:
					countingDict[possibleTriple] = 1
				else:
					countingDict[possibleTriple] += 1
	# write all items with more occurences then minSup into list
	L3 = []
	for item in countingDict:
		if countingDict[item] >= minSup:
			L3.append(item)
	return L3


transactions = readTransactionsFromFile('transactions.txt')

# the minimal support is 1% of all transactions
minSup = len(transactions)/100
print('Minimal support calculated. It is %d' % minSup)

L1 = findFrequentOneItemsets(transactions, minSup)
print('Frequent One Itemset calculated! There are %d items in this set.' % len(L1))

# the calculation takes some time, so we've hardcoded the result to speed things up
# L2 = findFrequentTwoItemsets(transactions, minSup, L1)
L2 = [('789', '829'), ('217', '283'), ('829', '368'), ('390', '722'), ('682', '368'), ('692', '368'), ('227', '390'), ('704', '39'), ('825', '704'), ('346', '217'), ('825', '39')]
print('Frequent Two Itemset calculated! There are the following %d items in this set.' % len(L2))
for item in L2:
	print(item)

L3 = findFrequentThreeItemsets(transactions, minSup, L2)
print('Frequent Three Itemsets calculated! There are the following %d items in this set.' % len(L3))
for item in L3:
	print(item)