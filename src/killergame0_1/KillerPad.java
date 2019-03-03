package killergame0_1;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import staff.KillerShip;

public class KillerPad implements Runnable {

    private final KillerGame0_1 game;
    private Socket socket; // this.localIp = socket.getLocalAddress().getHostAddress(); --- this.shipID = socket.getInetAddress().getHostAddress();
    private boolean connected;
    private String shipID;
    private String localIp;
    private BufferedReader in;
    private PrintWriter out;

    public KillerPad(KillerGame0_1 game, Socket socket) { //quitar lo de nave y controlled, solo pruebas
        this.game = game;
        this.socket = socket;
        this.configure(socket);
    }
    
    public void askForShip(String request) { // request >>> "new&user&221&77&183");
        // Pide al game que cree una nave
        String shipId = socket.getInetAddress().getHostAddress();
        // Procesar mensaje
        KillerPad.executePadOrder(this.game, shipId + ">" + request);
    }

    private void configure(Socket socket) {
        try {
            this.localIp = socket.getLocalAddress().getHostAddress();
            this.shipID = socket.getInetAddress().getHostAddress();
            System.out.println("localIp = " + localIp);
            System.out.println("ip movil = " + shipID);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.connected = true;
        } catch (IOException ex) {
            System.out.println("KillerPad: ERROR while configuring");
            this.connected = false;
        }
    }

    private void eliminateShip() {
        //volveeer a comprobar si la nave esta, y eliminarla...
        //si no, enviar mensaje para eliminar

        KillerShip nave = game.getShipById(this.shipID);
        if (nave == null) {
            // La nave no esta en este pc. 
            //Enviar mensaje para eliminarla en el equipo que esté ----------------------------------->>>>>>>
        } else {
            nave.setState(0); //cambio estado para parar hilo. rehacer
            this.game.getObjects().remove(nave);
            System.out.println("killerPad: ship deleted");

        }
    }
    
    public void killShip() {
        System.out.println("FP: mando mess ded");
        this.sendMessage("ded");
    }
    
    public static void createNewShip(KillerGame0_1 game, String shipId, String data) { // mia&dd4db7
        String[] dataParts = data.split("&");
        //System.out.println(dataParts);
        String user = dataParts[0];
        int h = Integer.parseInt(dataParts[1], 16);
        Color color = new Color(h);        
        game.createNewKillerShip(shipId, user, color);
    }

    // Se comprueba antes de llamar al metodo, si el game tiene la nave
    // Al metodo se le pasa el game y un message tipo "192.168.1.46>orden a ejecutar:dato1&dato2..."
    public static void executePadOrder(KillerGame0_1 game, String message) { // 
        String[] messContent = message.split(">");
        String shipId = messContent[0];
        String data[] = messContent[1].split(":");
        String order = data[0];
        boolean[] movement;
        KillerShip ship;
        
        switch (order) {
            case "new":
                KillerPad.createNewShip(game, shipId, data[1]);
                
                /*
                
                ship = game.getShipById(shipId);
                if (ship == null) {
                    KillerPad.createNewShip(game, shipId, data[1]);
                }
                */
                
                break;
            case "bye":                
                ship = game.getShipById(shipId);
                game.eliminateObject(ship);
                // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> eliminar el pad
                break;
            case "killme":
                break;
            case "replay":
                ship = game.getShipById(shipId);
                game.reviveShip(ship, 2);
                break;
            case "shoot"://solo para mando >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> prueba
                game.enviarPuntuacionPrueb(shipId);                
                break;
            case "idle":
                movement = new boolean[]{false, false, false, false};
                game.moveShip(shipId, movement);
                break;
            case "right":
                movement = new boolean[]{false, false, false, true};
                game.moveShip(shipId, movement);
                break;
            case "upright":
                movement = new boolean[]{true, false, false, true};
                game.moveShip(shipId, movement);
                break;
            case "downright":
                movement = new boolean[]{false, true, false, true};
                game.moveShip(shipId, movement);
                break;
            case "left":
                movement = new boolean[]{false, false, true, false};
                game.moveShip(shipId, movement);
                break;
            case "upleft":
                movement = new boolean[]{true, false, true, false};
                game.moveShip(shipId, movement);
                break;
            case "downleft":
                movement = new boolean[]{false, true, true, false};
                game.moveShip(shipId, movement);
                break;
            case "up":
                movement = new boolean[]{true, false, false, false};
                game.moveShip(shipId, movement);
                break;
            case "down":
                movement = new boolean[]{false, true, false, false};
                game.moveShip(shipId, movement);
                break;
        }
    }

    public void processMessage(String message) {
        System.out.println(message);

        // si tengo yo la nave compruebo el message y la muevo
        // si no la tengo, reenvio el message
        KillerShip ks = game.getShipById(this.shipID);
        if (ks == null) {
            System.out.println("KillerPad: NO tengo la nave, reenvio el mensaje");
            resendMessage(message);
        } else {
            System.out.println("KillerPad: tengo la nave, la muevo");
            //KillerPad.executePadOrder(game, message); >>> cambiar para poner executePadOrder antiguo
            KillerPad.executePadOrder(game, this.shipID + ">" + message); // 192.168.1.46>up
        }

    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void sendVibrate() {
        String message = "vib";
        this.sendMessage(message);
    }

    private void go() throws IOException {
        //comprobar conexiones, mover a donde??
        if (!connected) {
            System.out.println("killerPad: pad disconnected");
            // Eliminar nave del array de killergame
            eliminateShip();

            // cerrar el socket
            this.socket.close();
            this.socket = null;
        }

    }

    private void resendMessage(String userAction) { //r192.168.168.1.46:192.168.1.35>up
        String message = "r"
                + this.localIp.trim() + ":"
                + this.shipID.trim() + ">"
                + userAction;

        System.out.println("pad: " + message);
        if (this.game.getRightKiller().getSocket() != null) {
            this.game.getRightKiller().sendMessage(message);
        }
        // ----------------> ARREGLAR
        // ----------------> si el mensaje es para eliminar la nave, insistir  hasta que haya conexion
        // ---------------->
    }

    @Override
    public void run() {

        while (connected) {
            try {
                String message = in.readLine();
                if (message != null) {
                    this.processMessage(message);
                } else {
                    this.connected = false;
                }
            } catch (Exception ex) {
                ex.getMessage();
                this.connected = false; // REPASAR
            }
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerPad.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // REPASAR
        try {
            go();
        } catch (IOException ex) {

        }
        
        System.out.println("KillerPad >>>> DISCONNECTED");
    }

    // ====================================================================
    // ========================  Getters & Setters ========================
    // ====================================================================
    public Socket getSocket() {
        return socket;
    }

    public String getShipID() {
        return shipID;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

}
