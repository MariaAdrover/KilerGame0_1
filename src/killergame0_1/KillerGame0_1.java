package killergame0_1;

import staff.Ufo;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import javafx.embed.swing.JFXPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import staff.Alive;
import staff.KillerShip;
import staff.VisibleObject;

/**
 *
 * @author miaad
 */
public class KillerGame0_1 extends JFrame implements ActionListener {

    private JFrame configurationWindow;
    private KillerServer server;
    private VisualHandler leftKiller;
    private VisualHandler rightKiller;
    private ArrayList<KillerPad> pads;
    private ArrayList<VisibleObject> objects;
    private Viewer viewer;

    private JButton setLeft;
    private JTextField leftIp;
    private JTextField leftPort;

    private JButton setRight;
    private JTextField rightIp;
    private JTextField rightPort;

    private JButton play;

    public KillerGame0_1() {
        // Crear pantalla de configuracion
        this.createWindow();

        // Crear server
        this.server = new KillerServer(this);

        // Crear VisualHandlers con socket null  y sin ip
        this.leftKiller = new VisualHandler(this);
        this.rightKiller = new VisualHandler(this);

        // Crear array para los KillerPads
        this.pads = new ArrayList<>();

        // Crear viewer
        this.viewer = new Viewer(this);

        Container c = this.getContentPane();
        c.add(viewer);

        // Crear objetos iniciales
        this.objects = new ArrayList<>();
        prepareGameObjects();

        this.setTitle("01001011.01000111");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocation(0, 0);
        //this.setResizable(false);
        this.pack();
    }

    public void createNewKillerPad(Socket socket, String request) {
        KillerPad pad = new KillerPad(this, socket);
        this.pads.add(pad);
        (new Thread(pad)).start();
        pad.askForShip(request);
    }

    public void blockPath(Alive obj, int obstaclePosition) {
        // para que un obj no pueda pasar a traves de un muro
        // Poner el metodo en el objeto?
        switch (obstaclePosition) {
            case 1:
                obj.setY(0);
                //this.sendVibrate(obj);
                break;
            case 3:
                obj.setY(this.viewer.getHeight() - obj.getHeight());
                //this.sendVibrate(obj);
                break;
        }
    }

    public void boiiiingStatic(Alive obj) {
        // rebotar arriba y abajo
        // Poner el metodo en el objeto?
        obj.setvY(-obj.getvY());
    }

    public void boiiiingAlive(Alive obj, Alive obstacle) {
        // rebotar arriba y abajo
        // Poner el metodo en el objeto?
        if ((obj.getX() < obstacle.getX() && obj.getvX() > 0) || (obstacle.getX() < obj.getX() && obstacle.getvX() > 0)) { // mirar >= y <=
            obj.setvX(-obj.getvX());
            obstacle.setvX(-obstacle.getvX());
        }
        if ((obj.getY() < obstacle.getY() && obj.getvY() > 0) || (obstacle.getY() < obj.getY() && obstacle.getvY() > 0)) { // mirar >= y <=
            obj.setvY(-obj.getvY());
            obstacle.setvY(-obstacle.getvY());
        }

    }

    public void createNewKillerShip(String id, String user, Color color) {
        KillerShip ship = new KillerShip(this, 2, 200, 100, 60, 60, 0, 0, 4, id, color, user);

        this.objects.add(ship);
        (new Thread(ship)).start();

        System.out.println("KillerGame: creo nave con ID " + id);
    }

    public KillerShip getShipById(String shipID) {
        KillerShip ship = null;

        for (int i = 0; i < this.objects.size(); i++) {
            if ((this.objects.get(i) instanceof KillerShip) && ((KillerShip) this.objects.get(i)).getId().equalsIgnoreCase(shipID)) {
                ship = (KillerShip) this.objects.get(i);
            }
        }

        return ship;
    }
    
    public void enviarPuntuacionPrueb(String shipId) { // >>>>>>>>>>>>>>>>>>>>>> prueba mando
        for (int i = 0; i < this.pads.size(); i++) {
            if (this.pads.get(i).getShipID().equalsIgnoreCase(shipId)) {
                this.pads.get(i).sendMessage("pnt3");
            }            
        }
    }
    
    /*    
    public KillerPad getPadById(String padId) {
        KillerPad pad = null;
        
        return pad;
    }
    */

    public void eliminateObject(VisibleObject obj) {
        // Camnio el estado del objeto para finalizar su hilo
        obj.setState(-1);
        // Elimino el objeto del array de objetos
        this.objects.remove(obj);
        //this.pads.get(0).setConnected(false); >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> eliminar el que toca
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> eliminar el que toca
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> eliminar el pad si obj instanceof KillerShip
    }

    public void reviveShip(KillerShip ship, int state) {
        // en principio state=2, a salvo
        // para que la nave esté a salvo
        ship.setState(state);
        if (state == 2) {
            ship.resetSafeTimer();
        }
    }

    public void killShip(KillerShip ship) {
        // cambiamos el estado de la nave, pero no la eliminamos
        // conservamos el las propiedades del objeto y lo conservamos en el array
        // conservamos el pad
        // el estado cambia a 0, por lo que la nave no se pintará
        System.out.println("KG: mato la nave");
        ship.setState(0);
        this.pads.get(0).killShip(); // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> poner que busque la nave
    }

    public void moveShip(String shipId, boolean[] movement) {
        KillerShip ship;
        for (int i = 0; i < this.objects.size(); i++) {
            if ((this.objects.get(i)) instanceof KillerShip && ((KillerShip) this.objects.get(i)).getId().equalsIgnoreCase(shipId)) {
                ship = (KillerShip) this.objects.get(i);
                ship.setMovement(movement);
            }
        }
    }

    public void sendLeft(Alive obj) {
        // Si el handler derecho no esta conectado mando los objetos a la parte izquierda de la pantalla
        obj.setX(0);
    }

    public void sendRight(Alive obj) {
        // Si el handler izquierdo no esta conectado mando los objetos a la parte derecha de la pantalla
        obj.setX(this.viewer.getWidth() - obj.getWidth());
    }

    public void sendOutSpaceLeft(Alive obj) {
        //mandar a la pantalla de la izquierda
        this.leftKiller.sendAlive(obj);
        this.eliminateObject(obj);
    }

    public void sendOutSpaceRight(Alive obj) {
        //mandar a la pantalla de la derecha
        this.rightKiller.sendAlive(obj);
        this.eliminateObject(obj);
    }

    public void startGame() {
        // Server
        (new Thread(this.server)).start();
        // Visual handlers
        (new Thread(this.leftKiller)).start();
        (new Thread(this.rightKiller)).start();
        // Viewer
        (new Thread(this.viewer)).start();
        // Objects
        for (Object o : this.objects) {
            if (o instanceof Alive) {
                (new Thread((Alive) o)).start();
            }
        }

        // Music
        //JFXPanel jfxPanel = new JFXPanel();
        //KillerRadio.playAudio("sound/8-bit-Arcade4.wav");
        this.setVisible(true);
        configurationWindow.setVisible(true);
    }

    public synchronized void testCollision(Alive obj) {
        // Testear choques con los bordes
        int crashBorder = testBorders(obj);
        if (crashBorder != 0) {
            KillerRules.requestBorderRule(this, obj, crashBorder);
        }

        // Testear choques con otrs objetos
        VisibleObject obstacle;
        for (int i = 0; i < this.objects.size(); i++) {
            obstacle = this.objects.get(i);
            if (obj != obstacle && obj.intersect(obstacle)) {// repasar lo de que no te quedes parado...
                //System.out.println("crash, request rule");
                KillerRules.requestRule(this, obj, obstacle);
            }
        }
    }

    public void welcomeKillerShip(int state, double x, double y, int width, int height, double vX, double vY, double v, String id, Color color, String user) {
        KillerShip ship = new KillerShip(this, state, x, y, width, height, vX, vY, v, id, color, user);

        // Poner la v que lleva en cada eje
        if (vX > 0) {
            ship.setRight(true);
            ship.setLeft(false);
        } else if (vX < 0) {
            ship.setRight(false);
            ship.setLeft(true);
        } else {
            ship.setRight(false);
            ship.setLeft(false);
        }

        if (vY > 0) {
            ship.setUp(false);
            ship.setDown(true);
        } else if (vY < 0) {
            ship.setUp(true);
            ship.setDown(false);
        } else {
            ship.setUp(false);
            ship.setDown(false);
        }

        this.objects.add(ship);
        (new Thread(ship)).start();

        System.out.println("KillerGame: creo nave con ID " + id + " enviada desde otro pc");
    }

    public void welcomeUfo(int x, int y, int width, int height, double vX, double vY, double v) {
        Ufo ufo = new Ufo(this, 1, x, y, width, height, vX, vY, v);
        this.objects.add(ufo);
        (new Thread(ufo)).start();
    }

    private void createWindow() {
        configurationWindow = new JFrame("Configuration");
        Container c = configurationWindow.getContentPane();
        JPanel pane = new JPanel();

        JLabel leftIpLabel = new JLabel("Left VH IP:");
        pane.add(leftIpLabel);

        this.leftIp = new JTextField(16);
        pane.add(leftIp);

        JLabel leftPortLabel = new JLabel("Left VH Port:");
        pane.add(leftPortLabel);

        this.leftPort = new JTextField(6);
        pane.add(leftPort);

        this.setLeft = new JButton("Set Left VH");
        setLeft.addActionListener(this);
        pane.add(setLeft);

        JLabel rightIpLabel = new JLabel("Right VH IP:");
        pane.add(rightIpLabel);

        this.rightIp = new JTextField(16);
        pane.add(rightIp);

        JLabel rightPortLabel = new JLabel("Right VH Port:");
        pane.add(rightPortLabel);

        this.rightPort = new JTextField(6);
        pane.add(rightPort);

        this.setRight = new JButton("Set Right VH");
        setRight.addActionListener(this);
        pane.add(setRight);

        this.play = new JButton("Play");
        play.addActionListener(this);
        pane.add(play);

        c.add(pane);

        configurationWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
        configurationWindow.setSize(300, 300);
        configurationWindow.setLocation(0, 0);
        configurationWindow.setResizable(false);
        configurationWindow.pack();
    }

    private void prepareGameObjects() {
        //this.objects.add(new Ufo(this, 1, 850, 600, 30, 30, 1, -1, 4));
        //this.objects.add(new Ufo(this, 1, 400, 600, 30, 30, 1, -1, 2));
        //this.objects.add(new Ufo(this, 1, 500, 350, 30, 30, 1, 1, 3));
        this.objects.add(new Ufo(this, 1, 300, 288, 30, 30, -1, -1, 1));
        this.objects.add(new Ufo(this, 1, 155, 400, 30, 30, 1, -1, 2));
        //this.objects.add(new Ufo(this, 1, 400, 378, 30, 30, -1, 1, 2));
        //this.objects.add(new Ufo(this, 1, 900, 800, 30, 30, -1, 1, 1));
        //this.objects.add(new Ufo(this, 1, 500, 666, 30, 30, -1, -1, 5));
    }

    private int testBorders(Alive obj) {
        // Comprobar varias posibilidades a la vez
        int crashed = 0; // no choca

        if (obj.getY() < 0) { // 1 --> choca arriba
            crashed = 1;
        } else if ((obj.getX() + obj.getWidth() > this.viewer.getWidth()) && (obj.getvX() > 0)) { // 1 --> choca derecha
            crashed = 2;
        } else if (obj.getY() + obj.getHeight() > this.viewer.getHeight()) { // 1 --> choca abajo
            crashed = 3;
        } else if ((obj.getX() < 0) && (obj.getvX() < 0)) { // 1 --> choca izquierda
            crashed = 4;
        }

        return crashed;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JButton clicked = (JButton) ae.getSource();

        if (clicked == this.setLeft) {
            this.leftKiller.setIp(this.leftIp.getText());
            this.leftKiller.setPort(Integer.parseInt(this.leftPort.getText()));
        } else if (clicked == this.setRight) {
            this.rightKiller.setIp(this.rightIp.getText());
            this.rightKiller.setPort(Integer.parseInt(this.rightPort.getText()));
        }
        if (clicked == this.play) {

        }
    }

    public static void main(String[] args) {
        KillerGame0_1 game = new KillerGame0_1();
        game.startGame();
    }

    // ====================================================================
    // ========================  Getters & Setters ========================
    // ====================================================================
    public ArrayList<VisibleObject> getObjects() {
        return objects;
    }

    public Viewer getViewer() {
        return viewer;
    }

    public VisualHandler getLeftKiller() {
        return leftKiller;
    }

    public VisualHandler getRightKiller() {
        return rightKiller;
    }

    public KillerServer getServer() {
        return server;
    }

}


/*
    private void addWalls() {
        //Revisar posiciones x, y
        this.objects.add(new Wall(this, 0, 0, this.viewer.getWidth(), 1, 0)); // arriba
        this.objects.add(new Wall(this, this.viewer.getWidth(), 0, 1, this.viewer.getHeight(), 3)); // derecha
        this.objects.add(new Wall(this, 0, this.viewer.getHeight(), this.viewer.getWidth(), 1, 6)); // abajo
        this.objects.add(new Wall(this, 0, 0, 1, this.viewer.getHeight(), 9)); // izquierda
    }
 */
