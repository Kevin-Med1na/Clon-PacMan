package com.mycompany.pacman;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PacmanPanel extends JPanel {

    public static BufferedImage mapa;
    public static int ANCHO;
    public static int ALTO;
    public static JLabel pacman;

    public static Fantasma fantasmaRojo;
    public static Fantasma fantasmaAzul;
    public static Fantasma fantasmaRosa;
    public static Fantasma fantasmaNaranja;

    public static ImageIcon spritePellet;
    public static ImageIcon spritePelletPower;

    public static boolean enMuerte = false;
    public static Fruta fruta;

    public static volatile EstadoJuego estadoJuego = EstadoJuego.STARTING;
    public static JLabel readyLabel;
    
    public static int nivelActual = 1;
    

    public PacmanPanel() {
        
        
        setLayout(null);
        cargarMapa(); // ← llama el método
        cargarSprites();
        SistemaWaypoints.inicializar();      // ← inicializa waypoints
        pellets = SistemaWaypoints.generarPellets(); // ← genera pellets sobre caminos

        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);

        pacman = new JLabel();
        pacman.setIcon(new ImageIcon(getClass().getResource("/img/Pacman-Derecha-Normal.png")));
        pacman.setBounds(400, 326, 19, 20); // posición inicial — ajusta según tu mapa
        pacman.setVisible(false); 
        add(pacman);

        readyLabel = new JLabel();
        readyLabel.setIcon(new ImageIcon(getClass().getResource("/img/Ready.png")));
        readyLabel.setBounds(370, 325, 80, 30);
        readyLabel.setVisible(true);
        add(readyLabel);

        fruta = new Fruta();
        add(fruta.label);

        int tam = 20;
        fantasmaRojo = new Fantasma("Rojo", 370, 300, tam);
        fantasmaAzul = new Fantasma("Azul", 400, 300, tam);
        fantasmaRosa = new Fantasma("Rosa", 430, 300, tam);
        fantasmaNaranja = new Fantasma("Naranja", 460, 300, tam);

        add(fantasmaRojo.label);
        add(fantasmaAzul.label);
        add(fantasmaRosa.label);
        add(fantasmaNaranja.label);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                Color c = new Color(mapa.getRGB(x, y));
                System.out.println("Pixel [" + x + "," + y + "] → R:" + c.getRed()
                        + " G:" + c.getGreen() + " B:" + c.getBlue()
                        + " esPared:" + esPared(x, y));
            }
        });
    }

    private void cargarSprites() {
        spritePellet = new ImageIcon(getClass().getResource("/img/Pellet.png"));
        spritePelletPower = new ImageIcon(getClass().getResource("/img/Pellet_Power.png"));
    }

    private void cargarMapa() {
        try {
            var url = getClass().getResource("/img/Mapa.png");
            BufferedImage original = ImageIO.read(url);

            ANCHO = 800;
            ALTO = 600;

            mapa = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = mapa.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(original, 0, 0, ANCHO, ALTO, null);
            g2.dispose();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Método clave: pregunta si un píxel es pared
    public static boolean esPared(int x, int y) {
        // Si está en la zona del portal (bordes izquierdo y derecho) no es pared
        if (x <= 0 || x >= ANCHO) {
            return false; // ← cambia true por false
        }
        if (y < 0 || y >= ALTO) {
            return true;
        }

        Color c = new Color(mapa.getRGB(x, y));
        return c.getBlue() > 80
                && c.getBlue() > c.getRed() * 2
                && c.getBlue() > c.getGreen();
    }

    /*public void debugParedes(Graphics g) {
        for (int x = 0; x < ANCHO; x += 2) { // saltamos de 2 en 2 para que sea más rápido
            for (int y = 0; y < ALTO; y += 2) {
                if (esPared(x, y)) {
                    g.setColor(Color.RED);
                    g.fillRect(x, y, 2, 2);
                }
            }
        }
    }*/
    public static List<Pellet> pellets = new ArrayList<>();

    private void generarPellets() {
        int separacion = 30;

        for (int x = 15; x < ANCHO - 15; x += separacion) {
            for (int y = 15; y < ALTO - 15; y += separacion) {
                if (!esParedZona(x, y, 8)) {
                    pellets.add(new Pellet(x, y, false));
                }
            }

            // Verificar explícitamente la franja inferior
            int yFinal = ALTO - 20; // y=580
            if (!esParedZona(x, yFinal, 8)) {
                pellets.add(new Pellet(x, yFinal, false));
            }
        }

        // Power pellets hardcodeados — usa el debug del mouse para afinar
        pellets.add(new Pellet(50, 45, true));
        pellets.add(new Pellet(750, 50, true));
        pellets.add(new Pellet(50, 560, true));
        pellets.add(new Pellet(750, 560, true));
    }

    // Verifica un área de radio r alrededor del punto
    private boolean esParedZona(int x, int y, int r) {
        // Primero verificar el píxel exacto
        if (esPared(x, y)) {
            return true;
        }

        // Luego verificar el área alrededor
        for (int dx = -r; dx <= r; dx += 2) { // saltar de 2 en 2 para más rapidez
            for (int dy = -r; dy <= r; dy += 2) {
                if (esPared(x + dx, y + dy)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
       super.paintComponent(g);
    if (mapa != null) g.drawImage(mapa, 0, 0, this);

    // Pintar pellets
    for (Pellet p : pellets) {
        if (!p.recogido) {
            if (p.esPowerPellet) {
                spritePelletPower.paintIcon(this, g, p.x - 8, p.y - 8);
            } else {
                spritePellet.paintIcon(this, g, p.x - 3, p.y - 3);
            }
        }
    }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
