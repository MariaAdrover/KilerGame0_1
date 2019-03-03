package killergame0_1;

import staff.Ufo;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import staff.Alive;
import staff.KillerShip;

public class VisualHandler implements Runnable {

    private final KillerGame0_1 game;
    private KillerClient client;
    private Socket socket;
    private String ip;
    private int port;
    private int serverPort;
    private String localIp;
    private BufferedReader in;
    private PrintWriter out;

    public VisualHandler(KillerGame0_1 game) {
        this.game = game;
        this.socket = null;
        this.ip = null;
        this.localIp = null;
        this.in = null;
        this.out = null;
        this.setClient();
    }

    public synchronized void configure(Socket socket) {
        try {
            this.socket = socket;
            this.ip = this.socket.getInetAddress().getHostAddress();
            //5this.port = socket.getPort();
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.localIp = this.socket.getLocalAddress().getHostAddress();

            if (this == game.getLeftKiller()) { //quitar solo pruebas
                System.out.println("Visual Handler IZQUIERDO: Conectado con " + this.ip);
            } else {
                System.out.println("Visual Handler DERECHO: Conectado con " + this.ip);
            }
        } catch (IOException ex) {
            closeSocket();
        }
    }

    public void sendAlive(Alive obj) {
        if (obj instanceof KillerShip) {
            sendKillerShip((KillerShip) obj);
        } else if (obj instanceof Ufo) {
            sendUfo((Ufo) obj);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void closeSocket() { // quitar, prueba
        if (this.socket != null) {

            try {
                this.socket.close(); // y si da error al cerrar??
            } catch (IOException ex) {

            }

            this.socket = null;
            this.in = null;
            this.out = null;
        }
    }

    private void executeOrder(String message) {
        String object = message.substring(0, 5);

        System.out.println(object);
        switch (object) {
            case "kship":
                welcomeKillerShip(message.substring(6));
                break;
            case "ufooo":
                welcomeUfo(message.substring(6));
                break;
            case "shoot":

                break;
            default:
                break;
        }

    }

    private void processRelayMessage(String message) { // localIp:shipIp>userAction
        // r192.168.1.46:192.168.135>up
        String messContent[] = message.split(">");
        String header[] = messContent[0].split(":");
        String originIp = header[0];
        String shipId = header[1];
        String order = messContent[1];
        System.out.println("origen " + originIp);
        System.out.println("shipId " + shipId);
        // la condicion es  if (!this.localIp.equalsIgnoreCase(originIp)) pero habra que pasarle el puerto para que funcione en el mismo pc
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (this.localIp.equalsIgnoreCase(originIp)) {
            // Comprobamos si el pc tiene la nave
            KillerShip ship = game.getShipById(shipId);

            if (ship == null) {
                // Si el pc no tiene la nave, reenviamos el mensaje
                this.resendMessage(message);
            } else {
                // si el pc tiene la nave ejecutamos la orden del mando
                KillerPad.executePadOrder(game, shipId + ">" + order);  // 192.168.1.46>up
            }
        } else {
            // Si la ip de origen coincide con la ip de destino no procesamos el mensaje
            System.out.println("VH processmessage: He sido yo que he mandado el mensaje. MENSAJE DESCARTADO");
        }
        
    }

    private void processMessage(String message) { //rlocalIp:shipIp>userAction

        String mode = message.substring(0, 1);
        String content = message.substring(1);

        switch (mode) {
            case "d"://el mensaje es para este pc
                executeOrder(content);
                break;
            case "r": // r192.168.1.46:192.168.135>up
                processRelayMessage(content);
                break;
            default:
                System.out.println("VisualHandler: PROTOCOL ERROR unknown MessageMode");
                break;
        }
    }

    private void resendMessage(String message) {
        if (game.getRightKiller().getSocket() != null) {
            this.game.getRightKiller().sendMessage("r" + message);
        } else {
            System.out.println("VH Izquierdo: ERROR, el VH derecho esta  desconectado; no se ha podido reenviar el mensaje");
        }
    }

    private void sendKillerShip(KillerShip ship) {
        double yPercent = (ship.getY() * 100) / this.game.getViewer().getHeight();
        double vX = ship.getX();

        // dkship&2&...
        String message = "d"
                + "kship" + "&"
                + ship.getState() + "&"
                + (int) ship.getX() + "&" //se puede quitar...
                + (int) yPercent + "&" //mando el porcentaje
                + ship.getWidth() + "&"
                + ship.getHeight() + "&"
                + (int) ship.getvX() + "&"
                + (int) ship.getvY() + "&"
                + (int) ship.getV() + "&"
                + ship.getId() + "&"
                + ship.getColor().getRed() + "&"
                + ship.getColor().getGreen() + "&"
                + ship.getColor().getBlue() + "&"
                + ship.getUser();

        sendMessage(message);
    }

    private void sendUfo(Ufo ufo) {
        double yPercent = (ufo.getY() * 100) / this.game.getViewer().getHeight();
        double vX = ufo.getX();

        //quitar la x
        String message = "d"
                + "ufooo" + "&"
                + ufo.getX() + "&"
                + yPercent + "&" //mando el porcentaje
                + ufo.getWidth() + "&"
                + ufo.getHeight() + "&"
                + ufo.getvX() + "&"
                + ufo.getvY() + "&"
                + ufo.getV();

        sendMessage(message);

    }

    private void setClient() {
        this.client = new KillerClient(this.game, this);
        (new Thread(client)).start(); // Creo que es mejor iniciarlo cuando le doy al boton de setear... mientras no hace nada
    }

    private void welcomeKillerShip(String data) {
        // ship parameters: KillerGame0_1 game, int x, int y, int width, int height, double vX, double vY, double v, String id, Color color
        // quitar la x

        System.out.println(data);
        System.out.println("VH welcomeShip: crear killer ship");
        String dataFields[] = data.split("&");
        double x;

        int state = Integer.parseInt(dataFields[0]);
        //int x = Integer.parseInt(d[1]);
        double yPercent = Double.parseDouble(dataFields[2]);
        double y = (yPercent * this.game.getViewer().getHeight()) / 100;
        int width = Integer.parseInt(dataFields[3]);
        int height = Integer.parseInt(dataFields[4]);
        double vX = Double.parseDouble(dataFields[5]);
        double vY = Double.parseDouble(dataFields[6]);
        double v = Double.parseDouble(dataFields[7]);
        String id = dataFields[8];
        int r = Integer.parseInt(dataFields[9]);
        int g = Integer.parseInt(dataFields[10]);
        int b = Integer.parseInt(dataFields[11]);
        Color c = new Color(r, g, b); // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> cambiar a hexadecimal
        String user = dataFields[12];

        if (vX > 0) {
            x = 0;
        } else {
            x = this.game.getViewer().getWidth() - width;
        }

        System.out.println(state + x + "-" + y + "-" + width + "-" + height + "-" + vX + "-" + vY + "-" + v + "-" + id + "-" + r + "-" + g + "-" + b);
        this.game.welcomeKillerShip(state, x, y, width, height, vX, vY, v, id, c, user);
    }

    private void welcomeUfo(String data) { //rehaceeeer
        // public void welcomeUfo(int x, int y, int width, int height, double vX, double vY, double v)
        // quitar la x

        System.out.println(data);
        System.out.println("VH welcomwUfo: crear UFO");
        String d[] = data.split("&");
        int x;

        //int x = Integer.parseInt(d[0]);
        int yPercent = (int) Double.parseDouble(d[1]);
        int y = (yPercent * this.game.getViewer().getHeight()) / 100;
        int width = Integer.parseInt(d[2]);
        int height = Integer.parseInt(d[3]);
        double vX = Double.parseDouble(d[4]);
        double vY = Double.parseDouble(d[5]);
        double v = Double.parseDouble(d[6]);

        if (vX > 0) {
            x = 0;
        } else {
            x = this.game.getViewer().getWidth() - width;
        }

        System.out.println(x + "-" + y + "-" + width + "-" + height + "-" + vX + "-" + vY + "-" + v);
        this.game.welcomeUfo(x, y, width, height, vX, vY, v);
    }

    @Override
    public void run() {
        while (true) {
            if (this.getSocket() != null) { // Puedo poner directamente this.socket
                try {
                    String message = in.readLine();
                    this.processMessage(message);
                } catch (IOException ex) {
                    try {
                        this.socket.close();
                    } catch (IOException ex1) {
                        System.out.println("VH run: error, close socket, poner socket null, ok = false");
                    }

                    this.socket = null;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(VisualHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // ====================================================================
    // ========================  Getters & Setters ========================
    // ====================================================================
    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public KillerClient getClient() {
        return client;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

}
