package staff;

import killergame0_1.KillerGame0_1;

public abstract class Autonomous extends Alive {
    
    public Autonomous (KillerGame0_1 game, int state, double x, double y, int width, int height, double vX, double vY, double v) {
        super(game, state, x, y, width, height, vX, vY, v);
    }

    
}
