#!/bin/sh

# Compile code
javac *.java

# Program arguments:
# numNodes, startNode, maxCoordinate, startTemp, minTemp, maxIters,
# coolingRate, numUnchangedDist, numUnchangedRoute, seed, timeout

NUM_NODES=(100 1000)

# Run serial code
echo Running serial tests...
for nodes in "${NUM_NODES[@]}"
do
  echo Running test with $nodes nodes...
  x=$((10 * $nodes))
  for run in {1..5}
  do
    java RunSequentialTSP $nodes 42 $x 100 0.0005 $x 0.01 75 15 42 25 > experiments/results/serial/serial_"$nodes"_run_"$run".txt
  done
done
echo Completed serial tests...

# Run parallel code
threads=(2 3 4 6 8)
echo Running parallel tests...
for nodes in "${NUM_NODES[@]}"
do
  echo Running test with $nodes nodes...
  for thread in "${threads[@]}"
  do
    echo Running test with $thread threads...
    x=$((10 * $nodes))
    for run in {1..5}
    do
      java RunParallelTSP $thread $nodes 42 $x 100 0.0005 $x 0.01 75 15 42 25 > experiments/results/parallel/parallel_threads_"$thread"_"$nodes"_run_"$run".txt
    done
  done
done
echo Completed parallel tests...
#done

# Parse output files and convert to .csv
echo Parsing output files to CSV...
for filename in experiments/results/serial/*.txt
do
python parser_serial.py $filename
done

for filename in experiments/results/parallel/*.txt
do
python parser_parallel.py $filename
done
