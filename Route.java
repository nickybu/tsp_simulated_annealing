import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Represents route consisting of nodes (cities)
 */
public class Route {

    private ArrayList<Node> route;
    private Random randomRoute;
    private Random random;
    private float distance;

    /**
     * @param numNodes the number of nodes in the route
     * @param startNode the node from which the route begins
     * @param maxCoordinate the maximum x and y coordinate a node can have
     * @param seed seed for Random
     * @param routeSeed seed for random Route
     */
    public Route(int numNodes, int startNode, int maxCoordinate, int seed, boolean routeSeed) {
        route = new ArrayList<Node>();
        if(routeSeed) {
            randomRoute = new Random(seed);
        } else {
            randomRoute = new Random();
        }
        random = new Random();

        // Initialise route with random nodes
        for(int i = 1; i <= numNodes; i++) {
            int x = getRandomCoordinate(maxCoordinate);
            int y = getRandomCoordinate(maxCoordinate);
            route.add(new Node(i, x, y));
        }

        // Move startNode to the beginning of the route if it is not already the first node
        if(startNode != 1) {
            Node n1 = route.get(startNode-1);
            Node n2 = route.get(0);
            route.set(0, n1);
            route.set(startNode-1, n2);
        }
        computeDistance();

        if(routeSeed) {
            shuffle();
        }
    }

    /**
     * Constructor that accepts another Route instance
     * @param route the route to be cloned
     */
    public Route(Route route) {
//        this.route = (ArrayList) route.getRoute().clone();
        this.route = new ArrayList<>(route.getRoute());
        random = new Random();
        computeDistance();
    }

    /**
     * Swap two random nodes around
     */
    public void randomSwap() {
        int a = getRandomIndex();
        int b = getRandomIndex(a);

        Node n1 = route.get(a);
        Node n2 = route.get(b);
        route.set(a, n2);
        route.set(b, n1);

        computeDistance();
    }

    /**
     * Computes the distance of the route
     */
    private void computeDistance() {
        distance = 0;
        for (int i = 0; i < route.size(); i++) {
            // Get start node
            Node start = getNode(i);
            // Get destination node
            Node dest;
            // If we are on the last city set dest to start
            if(i + 1 >= route.size()) {
                dest = getNode(0);
            } else {
                dest = getNode(i+1);
            }
            // Add distance between nodes
            distance += start.distance(dest);
        }
    }

    public void shuffle() {
        Node startNode = route.get(0);
        route.remove(0);
        Collections.shuffle(route);
        route.add(0, startNode);
        computeDistance();

    }

    public float getDistance() {
        return distance;
    }

    public Node getNode(int index) {
        return route.get(index);
    }

    /**
     * Get the index of a random node (excluding the first)
     * @return index
     */
    private int getRandomIndex() {
        int r = -1;
        do {
            r = random.nextInt(route.size()-1);
        } while(r == 0);
        return r;
    }

    /**
     * Get the index of a random node (excluding the first)
     * @param num the new random index cannot be the same as this index
     * @return index
     */
    private int getRandomIndex(int num) {
        int r = -1;
        do{
            r = random.nextInt(route.size()-1);
        }
        while(r == num || r == 0);
        return r;
    }

    /**
     * Generate a random coordinate
     * @param max maximum coordinate value
     * @return coordinate
     */
    private int getRandomCoordinate(int max) {
        return randomRoute.nextInt(max);
    }

    public ArrayList<Node> getRoute() {
        return route;
    }

    @Override
    public String toString() {
        return "Route=" + route;
    }


    /**
     * Write the route to a .csv file
     * @param filename filename to save file with
     */
    public void writeToFile(String filename) {
        try {
            FileWriter fw = new FileWriter(new File("experiments/routes/", filename + ".csv"));
            StringBuilder sb = new StringBuilder();
            sb.append(route.size()+"\n");
            // Write headers
            sb.append("id,");
            sb.append("x,");
            sb.append("y");
            sb.append("\n");

            for(Node n : route) {
                sb.append(n.getId()+",");
                sb.append(n.getX()+",");
                sb.append(n.getY()+",");
                sb.append("\n");
            }
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
