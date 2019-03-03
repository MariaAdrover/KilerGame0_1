package staff;

import killergame0_1.KillerGame0_1;

public abstract class Controlled extends Alive {
    
    public Controlled (KillerGame0_1 game, int state, double x, double y, int width, int height, double vX, double vY, double speed) {
        super(game, state, x, y, width, height, vX, vY, speed);
    }

    @Override
    public void move() {
        if (up && !down) {
            vY = -v;
        } else if (down && !up) {
            vY = v;
        } else if (up && down || !up && !down) {
            vY = 0;
        }

        if (left && !right) {
            vX = -v;
        } else if (right && !left) {
            vX = v;
        } else if (left && right || !left && !right) {
            vX = 0;
        }

        x += vX;
        y += vY;
        
        /* Movimiento con tiempo
        double actualTime = System.nanoTime();
        double t = (actualTime - time) / 10000000;

        x += vX * t;
        y += vY * t;

        time = System.nanoTime();*/
    }
    
}
