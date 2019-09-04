/**
 * Represents a Node in the Symmetric Travelling Salesman Problem
 * Euclidean distance from Node A to Node B is the same in both directions, i.e. symmetrics
 */
public class Node {
    private int id, x, y;

    public Node(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    /**
     * Computes the Euclidean distance from this Node to another
     * @param node
     * @return euclidean distance
     */
    public double distance(Node node) {
        // Euclidean distance
        return Math.sqrt(Math.pow(x - node.getX(), 2) + Math.pow(y - node.getY(), 2));
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
