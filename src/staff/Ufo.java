package staff;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import killergame0_1.KillerGame0_1;
import staff.Autonomous;

public class Ufo extends Autonomous {
    
    private double centerX;
    private double centerY;
    public Ufo(KillerGame0_1 game, int state, double x, double y, int width, int height, double vX, double vY, double v) {
        super(game, state, x, y, width, height, vX, vY, v);
        centerX = x + width / 2;
        centerY = y + height / 2;
    }

    /*
    @Override
    public void run() {
        while (state > 0) {

            try {
                move();
                this.game.testCollision(this);
                Thread.sleep(8);
            } catch (InterruptedException ex) {

            }
        }
    }*/

    @Override
    public void move() {
        /* Movimiento con tiempo
        double actualTime = System.nanoTime();
        double t = (actualTime - time) / 10000000;

        x += vX * v * t;
        y += vY * v * t;
        
        time = System.nanoTime();
        */
               
        x += vX * v;
        y += vY * v;
        
    }

    @Override
    public boolean intersect(VisibleObject o) {   
        Rectangle me = new Rectangle((int)this.x, (int)this.y, this.width, this.height);        
        Rectangle obstacle = new Rectangle((int)o.x, (int)o.y, o.width, o.height);
        
        return me.intersects(obstacle);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.green);
        g.fillOval((int)x, (int)y, width, height);
    }

}
