import csv
import re
import sys

filename = sys.argv[1]
print('Parsing ' + filename + '...')

csv_contents = open(r''+filename + '.csv', "wb")
writer = csv.writer(csv_contents)

file = open(filename, "r")
file.readline()
file.readline()
file.readline()

#Write data headers
writer.writerow(['temperature', 'iterations', 'current_distance', 'shorter_routes_accepted', 'longer_routers_accepted'])
# Read file
currentLineToWrite = []
rowDone = False
started = False
finished = False

with file:
    for line in file:
        line = line.strip()

        if(line.startswith('Temperature')):
            if(started != True):
                started = True
            data = line.split(':', 1)[1]
            currentLineToWrite.extend([data])
        elif(line.startswith('Iterations')):
            data = line.split(':', 1)[1]
            currentLineToWrite.extend([data])
        elif(line.startswith('Current distance')):
            data = line.split(':', 1)[1]
            currentLineToWrite.extend([data])
        elif(line.startswith('Shorter routes accepted')):
            data = line.split(':', 1)[1]
            currentLineToWrite.extend([data])
        elif(line.startswith('Longer routes accepted')):
            data = line.split(':', 1)[1]
            currentLineToWrite.extend([data])
            finished = True
            if(started and finished):
                rowDone = True
        elif(line.startswith('Total')):
            started = True
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
        elif(line.startswith('numNodes')):
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

        if(rowDone):
            writer.writerows([currentLineToWrite])
            currentLineToWrite = []
            rowDone = False
            started = False
            finished = False
file.close()
print('Done')
