import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RunSequentialTSP {
    public static void main(String[] args) {
        int numNodes = -1, maxCoordinate = -1, maxIters = -1, seed = -1, timeout = -1, startNode = -1, numUnchangedDist = -1, numUnchangedRoute = -1;
        float startTemp = -1.0f, minTemp = -1.0f, coolingRate = -1.0f;

        if(args.length > 0) {
            try {
                numNodes = Integer.parseInt(args[0]);
                startNode = Integer.parseInt(args[1]);
                maxCoordinate = Integer.parseInt(args[2]);
                startTemp = Float.parseFloat(args[3]);
                minTemp = Float.parseFloat(args[4]);
                maxIters = Integer.parseInt(args[5]);
                coolingRate = Float.parseFloat(args[6]);
                numUnchangedDist = Integer.parseInt(args[7]);
                numUnchangedRoute = Integer.parseInt(args[8]);
                seed = Integer.parseInt(args[9]);
                timeout = Integer.parseInt(args[10]);

                validateArgs(numNodes, startNode, startTemp, minTemp, maxIters, coolingRate, timeout);
            } catch (Exception e) {
                System.err.println("Invalid input argument:" + e.getMessage());
                System.err.println("Please provide arguments [numNodes, startNode, maxCoordinate, startTemp, minTemp, maxIters, numUnchangedDist, numUnchangedRoute, coolingRate, seed, timeout]");
                System.exit(1);
            }
        } else {
          System.err.println("Please provide arguments [numNodes, startNode, maxCoordinate, startTemp, minTemp, maxIters, numUnchangedDist, numUnchangedRoute, coolingRate, seed, timeout]");
            System.exit(1);
        }

        System.out.println("Setting up...");
        long startTime = System.nanoTime();
        DateFormat df = new SimpleDateFormat("ddMMyy_HHmmss");
        SequentialTSP sequentialTSP = new SequentialTSP(numNodes, startNode, maxCoordinate, seed, timeout);
        // Write initial route to file
        sequentialTSP.getRoute().writeToFile("initial_route_"+df.format(new Date()));
        Route solution = sequentialTSP.simulateAnnealing(startTemp, minTemp, maxIters, coolingRate, numUnchangedDist, numUnchangedRoute);

        System.out.println("numNodes=" + numNodes + "; startNode=" + startNode + "; maxCoordinate=" + maxCoordinate + "; startTemp=" + startTemp + "; minTemp=" + minTemp +
                "; maxIters=" + maxIters + "; coolingRate=" + coolingRate + "; numUnchangedDist=" + numUnchangedDist + "; numUnchangedRoute=" + numUnchangedRoute +"; seed=" + seed + "; timeout=" + timeout +";");
        System.out.println("Shortest distance: " + solution.getDistance());
        // Write solution route to file
        solution.writeToFile("solution_route_"+df.format(new Date()));
        double elapsedTime = TimeUnit.MILLISECONDS.convert((System.nanoTime() - startTime), TimeUnit.NANOSECONDS) / 1000.0;
        System.out.println("Execution time: " + new DecimalFormat("#.###########").format(elapsedTime) + " seconds");
    }

    private static void validateArgs(int numNodes, int startNode, double startTemp, double minTemp, int iters, double coolingRate, int timeout) throws Exception {
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
