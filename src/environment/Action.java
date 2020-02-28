//package environment;
//
//import utilities.Constants;
//
//public class Action {
//    private String optimalAction = "Left";
//
//    public Action() {}
//
//    public Action(String action) {
//        optimalAction = action;
//    }
//
//    public static String generateAction() {
//        int index = (int) (Math.random() * Constants.ACTION_LIST.length);
//        return Constants.ACTION_LIST[index];
//    }
//
//    public String getOptimalAction() {
//        return optimalAction;
//    }
//
//    public void setOptimalAction(String optimalAction) {
//        this.optimalAction = optimalAction;
//    }
//}
