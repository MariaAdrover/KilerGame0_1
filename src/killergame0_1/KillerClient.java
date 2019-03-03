package killergame0_1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class KillerClient implements Runnable {

    private KillerGame0_1 game;
    private VisualHandler vh;

    public KillerClient(KillerGame0_1 game, VisualHandler vh) {
        this.game = game;
        this.vh = vh;
    }

    private void sendRequest(PrintWriter out) {
        if (this.vh == game.getLeftKiller()) {
            System.out.println("KillerClient: solicito conexion con pc izquierdo");
            out.println("fromR" + game.getServer().getPort());
        } else {
            System.out.println("KillerClient: solicito conexion con pc derecho");
            out.println("fromL" + game.getServer().getPort());
        }

    }

    private void setVHsocket(Socket socket, BufferedReader in) throws IOException {
        String response = in.readLine();
        System.out.println("KillerClient: recibido del ClientHandler: " + response);
        if (this.vh.getSocket() == null && response.equalsIgnoreCase("ok")) {
            this.vh.configure(socket);
            this.vh.setPort(socket.getPort());
        }
    }

    @Override
    public void run() {

        while (true) {
            if (this.vh.getIp() != null && this.vh.getSocket() == null) {
                try {
                    // Solicitar conexion al servidor
                    // Pasar a otro metodo
                    System.out.println("KillerClient: SOLICITO CONEXION a " + this.vh.getIp());
                    Socket socket = new Socket(this.vh.getIp(), this.vh.getPort());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("KillerClient: envio request");
                    // Enviar solicitud al ClientHandler para que asigne el socket a uno de los VisualHandlers
                    sendRequest(out);

                    System.out.println("KillerClient: configuro el VH");
                    // Esperar confirmacion del clientHandler del servidor 
                    // para asignar la gestion del socket a nuestro VisualHandler >>>>>>>>>>>>>>>>> es necesario??
                    // Si el VisualHandler sigue sin estar configurado, configurarlo y arrancar su thread
                    setVHsocket(socket, in);

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("KillerClient: ERROR al asignar el socket");
                    System.out.println(ex.getMessage());
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
