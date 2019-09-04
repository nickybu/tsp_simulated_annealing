import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RunParallelTSP {

    public static void main(String[] args) {
        int numNodes = -1, maxCoordinate = -1, maxIters = -1, seed = -1, timeout = -1, startNode = -1, numThreads = -1, numUnchangedDist = -1, numUnchangedRoute = -1;
        float startTemp = -1.0f, minTemp = -1.0f, coolingRate = -1.0f;

        if(args.length > 0) {
            try {
                numThreads = Integer.parseInt(args[0]);
                numNodes = Integer.parseInt(args[1]);
                startNode = Integer.parseInt(args[2]);
                maxCoordinate = Integer.parseInt(args[3]);
                startTemp = Float.parseFloat(args[4]);
                minTemp = Float.parseFloat(args[5]);
                maxIters = Integer.parseInt(args[6]);
                coolingRate = Float.parseFloat(args[7]);
                numUnchangedDist = Integer.parseInt(args[8]);
                numUnchangedRoute = Integer.parseInt(args[9]);
                seed = Integer.parseInt(args[10]);
                timeout = Integer.parseInt(args[11]);

                validateArgs(numThreads, numNodes, startNode, startTemp, minTemp, maxIters, coolingRate, timeout);
            } catch (Exception e) {
                System.err.println("Invalid input argument:" + e.getMessage());
                System.err.println("Please provide arguments [numThreads, numNodes, startNode, maxCoordinate, startTemp, minTemp, maxIters, numUnchangedDist, numUnchangedRoute, coolingRate, seed, timeout]");
                System.exit(1);
            }
        } else {
          System.err.println("Please provide arguments [numThreads, numNodes, startNode, maxCoordinate, startTemp, minTemp, maxIters, numUnchangedDist, numUnchangedRoute, coolingRate, seed, timeout]");
            System.exit(1);
        }

        ParallelTSP parallelTSP;
        try {
            System.out.println("Setting up...");
            DateFormat df = new SimpleDateFormat("ddMMyy_HHmmss");
            System.out.println("\nRunning on " + numThreads + " threads...");
            long startTime = System.nanoTime();
            parallelTSP = new ParallelTSP(numThreads, numNodes, startNode, maxCoordinate, seed, timeout);
            // Write initial route to file
            parallelTSP.getRoute().writeToFile("initial_route_"+df.format(new Date()));
            Route solution = parallelTSP.simulateAnnealing(startTemp, minTemp, maxIters, coolingRate, numUnchangedDist, numUnchangedRoute);
            // Write solution route to file
            solution.writeToFile("solution_route_"+df.format(new Date()));
            double elapsedTime = TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0;
            System.out.println("threads: " + numThreads + "; numNodes=" + numNodes + "; startNode=" + startNode + "; maxCoordinate=" + maxCoordinate + "; startTemp=" + startTemp + "; minTemp=" + minTemp +
            "; maxIters=" + maxIters + "; coolingRate=" + coolingRate + "; numUnchangedDist=" + numUnchangedDist + "; numUnchangedRoute=" + numUnchangedRoute +"; seed=" + seed + "; timeout=" + timeout +";");
            System.out.println("Shortest distance: " + solution.getDistance());
            System.out.println("Execution time: " + new DecimalFormat("#.###########").format(elapsedTime) + " seconds");
        } catch (InterruptedException e) {
            System.out.println("Interrupted..." + e.getMessage());
        }
    }

    private static void validateArgs(int numThreads, int numNodes, int startNode, double startTemp, double minTemp, int iters, double coolingRate, int timeout) throws Exception {
        if(numThreads < 2) {
            throw new Exception("numThreads needs to be >= 2");
        }
        if(numNodes <= 1) {
            throw new Exception("numNodes needs to be > 0!");
        }
        if(startNode < 1 || startNode > numNodes) {
            throw new Exception("numNodes needs to be > 0!");
        }
        if(startTemp <= 0) {
            throw new Exception("startTemp needs to be > 0!");
        }
        if(minTemp < 0) {
            throw new Exception("minTemp needs to be >= 0!");
        }
        if(iters <= 0) {
            throw new Exception("iters needs to be > 0!");
        }
        if(coolingRate <= 0) {
            throw new Exception("coolingRate needs to be > 0!");
        }
        if(timeout <= 0) {
            throw new Exception("timeout needs to be > 0!");
        }
    }
}
