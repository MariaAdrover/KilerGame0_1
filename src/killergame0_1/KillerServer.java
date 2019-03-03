package killergame0_1;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

public class KillerServer implements Runnable, ActionListener {

    private KillerGame0_1 game;
    private ServerSocket serverSocket;
    private int port;
    private boolean configured;

    private JFrame confirmationFrame;
    private JButton confirmPort;
    private JTextField newPort;
    private JButton setNewPort;
    private JLabel actualPortLabel;

    public KillerServer(KillerGame0_1 game) {
        this.game = game;
        this.port = 8000;
        this.configured = false;
        autoConfigurePort();
        getConfigurationFromUser();
        waitForConfirmation();
    }

    public void setServerSocket() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println("KServer: IOexception en setServerSocket");
            System.out.println("KServer --> " + ex);
            this.port++;
        }
    }

    private void autoConfigurePort() {
        while (this.serverSocket == null) {
            setServerSocket();
        }
    }

    private void getConfigurationFromUser() {
        this.confirmationFrame = new JFrame("Confirm server's port");
        Container c = confirmationFrame.getContentPane();
        JPanel pane = new JPanel();

        this.actualPortLabel = new JLabel("ACTUAL server's PORT:" + this.port);
        pane.add(actualPortLabel);

        this.confirmPort = new JButton("CONFIRM");
        confirmPort.addActionListener(this);
        pane.add(confirmPort);

        JLabel newPortLabel = new JLabel("NEW Server's PORT:");
        pane.add(newPortLabel);

        this.newPort = new JTextField(6);
        pane.add(newPort);

        this.setNewPort = new JButton("SET NEW server's PORT");
        setNewPort.addActionListener(this);
        pane.add(setNewPort);

        c.add(pane);

        confirmationFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        confirmationFrame.setSize(300, 300);
        confirmationFrame.setLocation(0, 0);
        confirmationFrame.setResizable(false);
        confirmationFrame.pack();
        confirmationFrame.setVisible(true);

    }

    private void waitForConfirmation() {
        while (!configured) {
            try {
                // Suspende el hilo main hasta que pulsemos confirmar
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(KillerServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void waitForConnectionRequests() throws IOException {
        //DONDE PONGO EL CATCH, EN EL RUN O AQUI?
        Socket socket = serverSocket.accept();
        System.out.println("Server: connection request from " + socket.getInetAddress().getHostAddress());
        ConnectionHandler connectionHandler = new ConnectionHandler(this.game, socket); //He de guardarlos en el KillerGame?
        Thread t = new Thread(connectionHandler);
        t.start();
    }

    @Override
    public void run() {

        while (true) {
            System.out.println("Server: Waiting connection request...");
            try {
                waitForConnectionRequests();
            } catch (IOException e) {
                System.out.println("KServer: IOexception en run");
                System.out.println(e.getMessage());
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException ex) {
                System.out.println("KServer: exception sleep");
                System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        JButton clicked = (JButton)ae.getSource();
        
        if (clicked == this.confirmPort) {
            this.configured = true;
            this.confirmationFrame.dispose();
        } else if (clicked == this.setNewPort) {
            this.port = Integer.parseInt(this.newPort.getText());
            setServerSocket();
            this.actualPortLabel.setText("ACTUAL server's PORT:" + this.port);
        }
    }

    // ====================================================================
    // ========================  Getters & Setters ========================
    // ====================================================================

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}
