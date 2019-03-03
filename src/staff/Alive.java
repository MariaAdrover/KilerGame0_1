package staff;

import killergame0_1.KillerGame0_1;

public abstract class Alive extends VisibleObject implements Runnable {

    // con move2, es decir con la variable t, pasar x y a double?, que hacemos con intersect? Rect es con int...
    
    // Velocidad
    protected double v;

    // Velocidad en cada eje
    protected double vX;
    protected double vY;

    protected double time;

    /* Aceleracion --> FUTURE
    protected int aX;
    protected int aY; */
    protected boolean up;
    protected boolean right;
    protected boolean down;
    protected boolean left;

    public Alive(KillerGame0_1 game, int state, double x, double y, int width, int height, double vX, double vY, double v) {
        super(game, state, x, y, width, height);
        this.vX = vX;
        this.vY = vY;
        this.v = v;

        up = false;
        right = false;
        down = false;
        left = false;
    }

    @Override
    public void run() {
        this.time = System.nanoTime();
        while (state > 0) {
            try {
                move();
                this.game.testCollision(this);
                Thread.sleep(8);
            } catch (InterruptedException ex) {

            }
        }
        
    }
    
    public void move() {
        
    }

    // ====================================================================
    // ========================  Getters & Setters ========================
    // ====================================================================

    public double getV() {
        return v;
    }

    public double getvX() {
        return vX;
    }

    public double getvY() {
        return vY;
    }
    
    public void setUp(boolean up) {
        this.up = up;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setV(double v) {
        this.v = v;
    }

    public void setvX(double vX) {
        this.vX = vX;
    }

    public void setvY(double vY) {
        this.vY = vY;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }
}
