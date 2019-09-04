import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class SequentialTSP {

    private DecimalFormat df = new DecimalFormat("#.0000");
//    private final double BOLTZMANN = 1.38064852e-23;
    private Route route;
    // Counters for the number of shorter and longer routes accepted
    private int counterShortRoute, counterLongRoute;
    // The initial route distance
    private final float initDistance;
    private final int timeout;

    public SequentialTSP(int numNodes, int startNode, int maxCoordinate, int seed, int timeout) {
        route = new Route(numNodes, startNode, maxCoordinate, seed, false);
        this.timeout = timeout;
        counterShortRoute = 0;
        counterLongRoute = 0;
        initDistance = route.getDistance();
    }

    /**
     * The Simulated Annealing process
     * @param startTemp starting temperature
     * @param minTemp stopping condition for temperature
     * @param maxIters maximum number of iterations for inner loop
     * @param coolingRate the cooling rate at which to cool the temperature
     * @param numUnchangedDist stopping condition for the number of iterations the route distance has gone unchanged
     * @param numUnchangedRoute stopping condition for the number of iterations the route remains the same
     * @return the solution route
     */
    public Route simulateAnnealing(float startTemp, float minTemp, int maxIters, float coolingRate, int numUnchangedDist, int numUnchangedRoute) {
        System.out.println("Simulating annealing...");
        long timeoutStart = System.nanoTime();

        // Initialise temperature
        float temp = startTemp;

        // Counter for the number of iterations
        int iterCounter;
        // Counter for the total number of iterations
        int totalIters = 0;
        // Counter for the number of unchanged distance at the same temperature
        int counterDistUnchangedAtTemp = 0;
        // Counter for the number of unchanged routes
        int counterNoNewRoutes = 0;
        Route newRoute;

        while(temp > minTemp) {
            newRoute = new Route(route);
            for (iterCounter = 0; iterCounter < maxIters; iterCounter++) {
                float prevDist = route.getDistance();
                // Swap two random cities
                newRoute.randomSwap();
                double acceptRoute = acceptanceFunction(route.getDistance(), newRoute.getDistance(), temp);
                if (acceptRoute == 1) {
                    counterShortRoute++;
                    route = new Route(newRoute);
                } else if (acceptRoute > Math.random()) {
                    counterLongRoute++;
                    route = new Route(newRoute);
                } else {
                    newRoute = new Route(route);
                }

                // If less than required new routes being taken at current temperature then decrease temperature
                if (route.getDistance() == prevDist) {
                    counterDistUnchangedAtTemp++;
                    if (counterDistUnchangedAtTemp >= numUnchangedDist) {
                        counterDistUnchangedAtTemp = 0;
                        break;
                    }
                } else {
                    counterDistUnchangedAtTemp = 0;
                }
            }

            // Update temperature
            temp *= 1 - coolingRate;

            // Print stats
            System.out.println("\nTemperature: " + df.format(temp));
            System.out.println("Iterations: " + iterCounter);
            System.out.println("Current distance: " + df.format(route.getDistance()));
            System.out.println("Shorter routes accepted: " + counterShortRoute);
            System.out.println("Longer routes accepted: " + counterLongRoute);

            if (counterLongRoute == 0 && counterShortRoute == 0) {
                counterNoNewRoutes++;
                if(counterNoNewRoutes >= 10) {
                    System.out.println("Last "+ numUnchangedRoute +" routes have been unchanged. Stopping simulated annealing process...\n");
                    break;
                }
            } else {
                counterNoNewRoutes = 0;
            }

            totalIters += iterCounter;
            counterShortRoute = 0;
            counterLongRoute = 0;

            // Check timeout
            if(TimeUnit.NANOSECONDS.toMinutes(System.nanoTime() - timeoutStart) >= timeout) {
                System.err.println("Timeout reached. Stopping...");
                break;
            }

        }
        System.out.println("\nTotal iterations: " + totalIters);
        System.out.println("Initial route distance: " + initDistance);
        return route;
    }

    /**
     * Acceptance function to accept a route based on the cost (distance) and current temperature
     * More likely to accept worse solutions at higher temperatures
     * @param temp the current temperature
     * @return probability of accepting this route
     */
    private float acceptanceFunction(float prevDist, float newDist, float temp) {
        if(newDist < prevDist) {
            return 1;
        }
        return (float) Math.exp((prevDist - newDist)/(temp));
    }

    public Route getRoute() {
        return route;
    }
}
