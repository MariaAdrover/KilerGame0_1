package staff;

import java.awt.Color;
import java.awt.Graphics2D;
import killergame0_1.KillerGame0_1;

public class KillerShip extends Controlled {

    private String id;
    private Color color;
    private double safeTimer;
    private String user;

    public KillerShip(KillerGame0_1 game, int state, double x, double y, int width, int height, double vX, double vY, double v, String id, Color color, String user) {
        super(game, state, x, y, width, height, vX, vY, v);
        this.id = id;
        this.color = color;
        this.user = user;
    }

    public void setMovement(boolean[] movement) {
        up = movement[0];
        down = movement[1];
        left = movement[2];
        right = movement[3];
    }

    private void checkSafeState() {
        if (System.currentTimeMillis() - safeTimer > 7000) {
            state = 1;
        }
    }
    
    public void resetSafeTimer() {
        this.safeTimer = System.currentTimeMillis();         
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(this.color);
        if (state == 1) { // estado normal
            g.drawString(user, (int)x, (int)y-5);
            g.fillRect((int) x, (int) y, width, height);
        } else if (state == 2) { // estado a salvo
            g.drawString(user, (int)x, (int)y-5);
            g.drawRect((int) x, (int) y, width, height);
        }
    }
    
    // -1 >> acabar el hilo: muerto y elimino el KillerPad
    // 0 >> muerto pero conectado; la nave no se pinta pero conserva sus propiedades
    // 1 >> normal
    // 2 >> nave a salvo durante 7 segundos
    // 3 

    @Override
    public void run() {
        // this.time = System.nanoTime();
        this.time = System.currentTimeMillis();
        this.safeTimer = System.currentTimeMillis(); 
        while (state >= 0) {
            if (state == 2) {
                this.checkSafeState();
            }
            
            try {
                move();
                this.game.testCollision(this);
                Thread.sleep(8);
            } catch (InterruptedException ex) {

            }
        }

    }

    // ====================================================================
    // ========================  Getters & Setters ========================
    // ====================================================================
    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    public String getUser() {
        return user;
    }

}
