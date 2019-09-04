import csv
import re
import sys
from collections import defaultdict

filename = sys.argv[1]
print('Parsing ' + filename + '...')

csv_contents = open(r''+filename + '.csv', "wb")
writer = csv.writer(csv_contents)

file = open(filename, "r")
file.readline()
file.readline()
file.readline()

#Write data headers
writer.writerow(['thread', 'temperature', 'iterations', 'current_distance', 'shorter_routes_accepted', 'longer_routers_accepted'])
# Read file
currentLineToWrite = []
iterDone = False
started = False
finished = False
temp = 0
endDone = False
rowDone = False

threadDict = defaultdict()

with file:
    for line in file:
        line = line.strip()

        if(line.startswith('Temperature')):
            data = line.split(':', 1)[1]
            temp = data
        elif(line.startswith('#')):
            if ("Iterations" in line):
                thread = re.search('#(.+?)Iterations', line)
                thread = thread.group(1)
                threadDict[thread] = [thread, temp]
                data = line.split(':', 1)[1]
                threadDict[thread].extend([data])
            elif ("Current" in line):
                data = line.split(':', 1)[1]
                thread = re.search('#(.+?)Current', line)
                thread = thread.group(1)
                threadDict[thread].extend([data])
            elif ("Shorter" in line):
                data = line.split(':', 1)[1]
                thread = re.search('#(.+?)Shorter', line)
                thread = thread.group(1)
                threadDict[thread].extend([data])
            elif ("Longer" in line):
                data = line.split(':', 1)[1]
                thread = re.search('#(.+?)Longer', line)
                thread = thread.group(1)
                threadDict[thread].extend([data])
        elif(line.startswith('Global current distance')):
            # data = line.split(':', 1)[1]
            # currentLineToWrite.extend(['Global distance'])
            # currentLineToWrite.extend([data])
            finished = True
            iterDone = True
            # rowDone = True
        if(line.startswith('Parallel execution time')):
            label,data = line.split(':')
            currentLineToWrite.extend([label])
            currentLineToWrite.extend([data])
            finished = True
            rowDone = True
        elif(line.startswith('Initial')):
            label,data = line.split(':')
            currentLineToWrite.extend([label])
            currentLineToWrite.extend([data])
            finished = True
            rowDone = True
        elif(line.startswith('threads')):
            args = re.split('=|;', line)
            args = [x.strip(' ') for x in args]
            currentLineToWrite.extend(args)
            finished = True
            rowDone = True
        elif(line.startswith('Shortest distance')):
            label,data = line.split(':')
            currentLineToWrite.extend([label])
            currentLineToWrite.extend([data])
            finished = True
            rowDone = True
        elif(line.startswith('Execution time')):
            label,data = line.split(':')
            currentLineToWrite.extend([label])
            data = data.split()[0]
            currentLineToWrite.extend([data])
            finished = True
            rowDone = True

        if(iterDone):
            for t in threadDict.itervalues():
                lineToWrite = []
                for x in t:
                    lineToWrite.extend([x])
                writer.writerows([lineToWrite])
            threadDict.clear()
            iterDone = False
            started = False
            finished = False

        if(rowDone):
            writer.writerows([currentLineToWrite])
            currentLineToWrite = []
            rowDone = False
            started = False
            finished = False
file.close()
print('Done')
