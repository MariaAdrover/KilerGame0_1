package killergame0_1;

public class KillerAction {
    public String[] moveShipIdle(String act, String localIp, String shipId, String message, String up, String down, String left, String right) {
        String[] action = new String[8];
        action[0] = act;
        action[1] = localIp;
        action[2] = shipId;
        action[3] = message;
        action[4] = up;
        action[5] = down;
        action[6] = left;
        action[7] = right;
        
        return action;
    }
}
