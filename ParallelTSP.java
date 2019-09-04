import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelTSP {

    private DecimalFormat df = new DecimalFormat("#.0000");
    private Route route;
    private final float initDistance;
    private final int numThreads;
    // Counters for the number of shorter and longer routes accepted
    private int counterShortRoute, counterLongRoute;
    private final int timeout;

    public ParallelTSP(int numThreads, int numNodes, int startNode, int maxCoordinate, int seed, int timeout) {
        this.numThreads = numThreads - 1; // exclude master/main thread
        this.timeout = timeout;
        route = new Route(numNodes, startNode, maxCoordinate, seed, true);
        initDistance = route.getDistance();
        counterShortRoute = 0;
        counterLongRoute = 0;
    }

    public Route simulateAnnealing(float startTemp, float minTemp, int maxIters, float coolingRate, int numUnchangedDist, int numUnchangedRoute) throws InterruptedException {
        System.out.println("Simulating annealing...");
        long timeoutStart = System.nanoTime();

        // Initialise temperature
        float temp = startTemp;

        // Counter for the number of unchanged routes
        int counterNoNewRoutes = 0;
        long parallelTime = 0;
        ArrayList<Route> newRoutes = new ArrayList<>();

        // Create thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        System.out.println("Main thread id: " + Thread.currentThread().getId());
        List<SATask> futureList = new ArrayList<>();

        while(temp > minTemp) {
            // Initialise SATasks
            for(int i = 0; i < numThreads; i++) {
                SATask saTask;
                if(temp == startTemp) {
                    // Initial iteration so give each worker a shuffled route
                    saTask = new SATask(route, maxIters, temp, numUnchangedDist, true);
                } else {
                    saTask = new SATask(route, maxIters, temp, numUnchangedDist, false);
                }
                futureList.add(saTask);
            }

            System.out.println("\nTemperature: " + df.format(temp));

            // Execute iterations on each thread
            List<Future<ResultsObject>> futures = null;
            try {
                long startTime = System.nanoTime();
                futures = executorService.invokeAll(futureList, 10, TimeUnit.MINUTES);
                parallelTime += (System.nanoTime() - startTime);

                // Get all ResultsObjects from each worker
                for(Future<ResultsObject> future : futures) {
                    ResultsObject results = future.get();
                    newRoutes.add(results.getRoute());
                    counterShortRoute += results.getCounterShortRoute();
                    counterLongRoute += results.getCounterLongRoute();
                }
            } catch (ExecutionException e) {
                System.err.println("Execution error " + e.getMessage());
                System.exit(1);
            }

            // Find the best route
            int bestRoute = -1;
            float bestDistance = initDistance;
            for(int i = 0; i < newRoutes.size(); i++) {
                if(newRoutes.get(i).getDistance() <= bestDistance) {
                    bestDistance = newRoutes.get(i).getDistance();
                    bestRoute = i;
                }
            }

            // Update route
            route = new Route(newRoutes.get(bestRoute));
            System.out.println("Global current distance: " + route.getDistance() + "\n");

            // Update temperature
            temp *= 1 - coolingRate;

            //
            if (counterLongRoute == 0 && counterShortRoute == 0) {
                counterNoNewRoutes++;
                if(counterNoNewRoutes >= numUnchangedRoute) {
                    System.out.println("Last "+ numUnchangedRoute +" routes have been unchanged. Stopping simulated annealing process...\n");
                    break;
                }
            } else {
                counterNoNewRoutes = 0;
            }

            counterShortRoute = 0;
            counterLongRoute = 0;
            futureList.clear();
            newRoutes.clear();

            // Check timeout
            if(TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - timeoutStart) >= timeout) {
                System.err.println("Timeout reached. Stopping...");
                break;
            }
        }

        // Shutdown executorService
        try {
            executorService.shutdown();
            executorService.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Tasks interrupted");
        } finally {
            if (!executorService.isTerminated()) {
                System.err.println("Cancelling pending tasks...");
            }
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        double elapsedTime = TimeUnit.MILLISECONDS.convert(parallelTime, TimeUnit.NANOSECONDS) / 1000.0;
        System.out.println("Parallel execution time: " + elapsedTime);
        System.out.println("Initial route distance: " + initDistance);
        return route;
    }

    public Route getRoute() {
      return route;
    }
}
