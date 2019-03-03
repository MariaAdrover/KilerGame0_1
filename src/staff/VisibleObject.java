package staff;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import killergame0_1.KillerGame0_1;

// USE KillerGame
// No ha de tenerlo como atributo?
// que clases son abstractas??
// se puede hacer una interfaz Deadble
public abstract class VisibleObject implements Renderizable, Collisionable {
    protected KillerGame0_1 game;
    protected int state;
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    
    public VisibleObject(KillerGame0_1 game, int state, double x, double y, int width, int height) {
        this.game = game;
        this.state = state;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(Graphics2D g) {
        
    }

    @Override
    public boolean intersect(VisibleObject o) {   
        Rectangle me = new Rectangle((int)this.x, (int)this.y, this.width, this.height);        
        Rectangle obstacle = new Rectangle((int)o.x, (int)o.y, o.width, o.height);
        
        return me.intersects(obstacle);
    }

    // ====================================================================
    // ========================  Getters & Setters ========================
    // ====================================================================

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
}
