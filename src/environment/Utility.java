package environment;

public class Utility {

    private String action = "LEFT";
    private double utility = 0;

    public Utility() {}

    public Utility(String a, double u) {
        action = a;
        utility = u;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getUtility() {
        return utility;
    }

    public void setUtility(double utility) {
        this.utility = utility;
    }
}
