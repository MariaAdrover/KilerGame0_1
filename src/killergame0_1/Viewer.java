package killergame0_1;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_3BYTE_BGR;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;
import staff.VisibleObject;

public class Viewer extends Canvas implements Runnable {

    private BufferedImage espacio;
    private BufferedImage frame;
    private int width;
    private int height;
    private Graphics2D frameGraphics2D;
    private final KillerGame0_1 game;

    private double time;
    private double fps;
    private double target;

    public Viewer(KillerGame0_1 game) {
        super();
        this.game = game;
        this.time = System.nanoTime();
        this.calculateSize();
        this.createBackground();
        this.createFrameImage();
        this.setSize(this.width, this.height);
        this.setBackground(Color.black);

        this.fps = 35;
        this.target = 1000000000 / fps;

        this.setVisible(true);
    }

    private void calculateSize() {        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.width = screenSize.width;
        this.height = screenSize.height - ((screenSize.height * 7) / 100);
    }

    private void createBackground() {
        try {
            this.espacio = ImageIO.read(new File("img/espacio.jpg"));
        } catch (IOException e) {
            System.out.println("Viewer: createBackground File not found");
        }
    }

    private void createFrameImage() {
        this.frame = new BufferedImage(this.width, this.height, TYPE_3BYTE_BGR);
        this.frameGraphics2D = (Graphics2D) this.frame.getGraphics();
    }

    private String getLocalIp() {
        String ipVal = null;
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            ipVal = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipVal;
    }

    private void pintarFrame() {
        // Pintar fondo
        frameGraphics2D.fillRect(0, 0, this.width, this.height);
        frameGraphics2D.drawImage(espacio, 0, 0, this.width, this.height, this);

        // Pintar server & VH info
        paintSystemInfo();

        // Pintar objetos
        paintObjects();

        this.getGraphics().drawImage(frame, 0, 0, this);
    }

    private void paintObjects() {
        for (int i = 0; i < game.getObjects().size(); i++) {
            VisibleObject o = game.getObjects().get(i);
            o.render(frameGraphics2D);
        }
    }

    private void paintSystemInfo() {
        // Pintar local info
        frameGraphics2D.setColor(Color.green);
        frameGraphics2D.drawString("Server's PORT: " + game.getServer().getPort(), 50, 50);
        
        //no se como sacar la ip local
        frameGraphics2D.drawString("local IP: " + getLocalIp(), 50, 70);

        //Pintar VH info
        frameGraphics2D.setColor(Color.white);
        String ipLeft = game.getLeftKiller().getIp();
        String ipRight = game.getRightKiller().getIp();
        int portLeft = game.getLeftKiller().getPort();
        int portRight = game.getRightKiller().getPort();
                
        if (ipLeft != null && portLeft != 0) {
            frameGraphics2D.drawString("Left VH: " + ipLeft + " / " + portLeft + " / " + game.getLeftKiller().getServerPort(), 50, 100);
        }

        if (game.getLeftKiller().getSocket() == null) {
            frameGraphics2D.drawString("Left VH SOCKET is NULL", 50, 120);
        } else {
            frameGraphics2D.drawString("Left VH SOCKET is OK", 50, 120);
        }

        if (ipRight != null && portRight != 0) {
            frameGraphics2D.drawString("Right VH: " + ipRight + " / " + portRight + " / " + game.getRightKiller().getServerPort(), 50, 150);
        }

        if (game.getRightKiller().getSocket() == null) {
            frameGraphics2D.drawString("Right VH SOCKET is NULL", 50, 170);
        } else {
            frameGraphics2D.drawString("Right VH SOCKET is OK", 50, 170);
        }

    }

    @Override
    public void paint(Graphics g) {
        //g.drawImage(frame, 0, 0, this);
    }

    @Override
    public void run() {
        /*while(true) {
            try {
                this.pintarFrame();
                Thread.sleep(30);
            } catch (InterruptedException ex) {
                
            }
        }*/

        double diferencia = System.nanoTime() - time;
        while (true) {
            if (diferencia > target) {
                this.pintarFrame();
                time = System.nanoTime();
            }
            
            diferencia = System.nanoTime() - time;
        }
    }

}
