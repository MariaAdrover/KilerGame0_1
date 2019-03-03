package killergame0_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private KillerGame0_1 game;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String ip;

    public ConnectionHandler(KillerGame0_1 game, Socket socket) {
        this.game = game;
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();

        try { //crear aqui o cuando los necesite?, atributos o no?
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {

        }
    }

    private void closeConnection() {
        try {
            this.socket.close();
        } catch (IOException ex) {
            System.out.println("ConnectionHandler ERROR: request does not match KCP (Killer Connection Protocol)");
        }
    }

    private void configureVH(VisualHandler vh, String port) {
        System.out.println("ConnectionHandler: configurando VH...");
        if (vh.getSocket() == null) {
            System.out.println("ConnectionHandler:  VH no esta OK, lo configuro...");
            vh.configure(socket);
            vh.setPort(Integer.parseInt(port));
            (new Thread(vh)).start();            
            this.out.println("ok"); // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Es necesario esperar confirmación?

            if (vh == this.game.getLeftKiller()) { //QUITAR
                System.out.println("ConnectionHandler: configurado VisualHandler IZQUIERDO");

            } else {
            System.out.println("ConnectionHandler: configurado VisualHandler DERECHO");
            }
        } else {
            System.out.println("ConnectionHandler: VisualHandler ya esta ok");
        }

    }

    private void processKillerPadRequest(String request) {        
        this.game.createNewKillerPad(this.socket, request);
        
        /*        
        KillerPad pad = game.getPadById(this.socket.getInetAddress().getHostAddress());
        if (pad == null) { // sii no existe un pad para la conexion recibida creo el pad
            this.game.createNewKillerPad(this.socket, request);
        } else { // si no, reconfiguro el pad
            pad.configure(socket);
        }
        */
    }

    private void processLeftVHrequest(String port) {
        VisualHandler vh = this.game.getLeftKiller();
        configureVH(vh, port);
    }

    private void processRightVHrequest(String port) {
        VisualHandler vh = this.game.getRightKiller();
        configureVH(vh, port);
    }

    private void processRequest(String request) {
        // from:L --> PREVIOUS Visual Handler
        // from:R --> NEXT Visual Handler
        // from:P --> Killer PAD
        System.out.println("ConnectionHandler: recibido " + request);
        String header = request.substring(0, 5);
        String data = request.substring(5);

        switch (header) {
            case "fromL":
                System.out.println("ConnectionHandler: procesando left");
                this.processLeftVHrequest(data);
                break;
            case "fromR":
                System.out.println("ConnectionHandler: procesando right");
                this.processRightVHrequest(data);
                break;
            case "fromP": // desde el movil >>> out.println("fromPnew&"+user+"&221&77&183");
                this.processKillerPadRequest(data); // el movil enviara usuario, configracion, etc... al conectarse
                break;
            default:
                this.closeConnection();
                break;
        }
    }

    @Override
    public void run() {
        try {
            String request = in.readLine();
            processRequest(request);

        } catch (IOException ex) {

        }
    }

}
