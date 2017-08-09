package fourofakind;

/**
 * The NodePosition class is used for storing coordinates of nodes relative to 
 * the scene.
 * 
 * @author William Hess
 */

public class NodePosition {
    private double x;
    private double y;

    public NodePosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
