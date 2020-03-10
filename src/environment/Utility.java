package environment;

public class Utility {

    private char action = 'L';
    private double utility = 0;

    public Utility() {}

    public Utility(char a, double u) {
        action = a;
        utility = u;
    }

    public char getAction() {
        return action;
    }

    public void setAction(char action) {
        this.action = action;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }
}
