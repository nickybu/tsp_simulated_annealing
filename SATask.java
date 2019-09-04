import java.text.DecimalFormat;
import java.util.concurrent.Callable;

public class SATask implements Callable<ResultsObject> {

    private DecimalFormat df = new DecimalFormat("#.000");
    private Route route;
    private int maxIters;
    private float temp;
    private final int numUnchangedDist;

    public SATask(Route route, int maxIters, float temp, int numUnchangedDist, boolean shuffle) {
        this.route = new Route(route);
        this.maxIters = maxIters;
        this.temp = temp;
        this.numUnchangedDist = numUnchangedDist;

        if(shuffle) {
            this.route.shuffle();
        }
    }

    @Override
    public ResultsObject call() throws Exception {
        Route newRoute = new Route(route);
        int id = (int) (Thread.currentThread().getId());
        int localIterCounter, counterShortRoute = 0, counterLongRoute = 0, counterDistUnchangedAtTemp = 0;

        for (localIterCounter = 0; localIterCounter < maxIters; localIterCounter++) {
            float prevDist = route.getDistance();
            // Swap two random cities
            newRoute.randomSwap();
            double acceptRoute = acceptanceFunction(route.getDistance(), newRoute.getDistance(), temp);

            if (acceptRoute == 1) {
                route = new Route(newRoute);
                counterShortRoute++;
            } else if (acceptRoute > Math.random()) {
                route = new Route(newRoute);
                counterLongRoute++;
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

        // Print stats
        System.out.println("#" + id + " Iterations: " + localIterCounter);
        System.out.println("#" + id + " Current distance: " + df.format(route.getDistance()));
        System.out.println("#" + id + " Shorter routes accepted: " + counterShortRoute);
        System.out.println("#" + id + " Longer routes accepted: " + counterLongRoute);

        return new ResultsObject(route, counterShortRoute, counterLongRoute);
    }

    /**
     * Acceptance function to accept a route based on the cost (distance) and current temperature
     * More likely to accept worse solutions at higher temperatures
     * @param temp the current temperature
     * @return probability of accepting this route
     */
    private float acceptanceFunction(float shortestDist, float newDist, float temp) {
        if(newDist < shortestDist) {
            return 1;
        }
        return (float) Math.exp((shortestDist - newDist)/(temp));
    }
}
