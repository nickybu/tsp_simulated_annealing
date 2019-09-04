/**
 * Stores the results returned by each worker running the SA inner loop.
 */
public class ResultsObject {
    private Route route;
    private int counterShortRoute, counterLongRoute;

    public ResultsObject(Route route, int counterShortRoute, int counterLongRoute) {
        this.route = route;
        this.counterShortRoute = counterShortRoute;
        this.counterLongRoute = counterLongRoute;
    }

    public Route getRoute() {
        return route;
    }

    public int getCounterShortRoute() {
        return counterShortRoute;
    }

    public int getCounterLongRoute() {
        return counterLongRoute;
    }
}
