package com.mycompany.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class PanelCinematica extends javax.swing.JPanel {

    // ── Sprites parte 1 (derecha → izquierda) ────────────────
    private ImageIcon pacmanCerrada;       // Pacman-BocaCerrada-Izquierda.png
    private ImageIcon pacmanNormal;        // Pacman-Izquierda-Normal.png
    private ImageIcon pacmanAbierta;       // Pacman-BocaAbierta-Izquierda.png
    private ImageIcon fantasmaRojo;        // Fantasma-Rojo-Izquierda.png

    // ── Sprites parte 2 (izquierda → derecha) ────────────────
    private ImageIcon pacmanGrandeCerrado; // Pacman-Grande-BocaCerrada.png
    private ImageIcon pacmanGrandeNormal;  // Pacman-Grande-Normal.png
    private ImageIcon pacmanGrandeAbierto; // Pacman-Grande-BocaAbierta.png
    private ImageIcon fantasmaAsustado;    // Fantasma-Asustado-Azul.png

    private static final int TAM_NORMAL = 30;
    private static final int TAM_GRANDE = 55;

    // ── Animación: ciclo cerrada→normal→abierta→normal→... ───
    private static final int[] CICLO = {0, 1, 2, 1}; // 0=cerrada, 1=normal, 2=abierta
    private int indiceCiclo = 0;
    private int tickFrame = 0;
    private static final int TICKS_POR_FRAME = 6; // ~96ms por frame

    // ── Estado ────────────────────────────────────────────────
    private double tiempo = 0;
    private boolean parte1 = true;
    private boolean terminada = false;

    // ── Posiciones ───────────────────────────────────────────
    private double xPacman, yPacman;
    private double xFantasma, yFantasma;

    // ── Parámetros ────────────────────────────────────────────
    private static final double FRECUENCIA = 5.0;
    private static final double AMPLITUD = 20.0;
    private static final double BASE_Y = 0.45;
    private static final double VELOCIDAD = 180;

    // ── Timer ─────────────────────────────────────────────────
    private Timer timer;
    private Runnable onTerminar;

    public PanelCinematica(int ancho, int alto) {
        setPreferredSize(new Dimension(ancho, alto));
        setBackground(Color.BLACK);
        setOpaque(true);
        cargarSprites();
    }

    private ImageIcon escalar(String ruta, int tam) {
        try {
            ImageIcon raw = new ImageIcon(getClass().getResource("/img/" + ruta));
            BufferedImage img = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(raw.getImage(), 0, 0, tam, tam, null);
            g2.dispose();
            return new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("Error sprite cinemática: " + ruta);
            return null;
        }
    }

    private void cargarSprites() {
        pacmanCerrada = escalar("Pacman-BocaCerrada-Izquierda.png", TAM_NORMAL);
        pacmanNormal = escalar("Pacman-Izquierda-Normal.png", TAM_NORMAL);
        pacmanAbierta = escalar("Pacman-BocaAbierta-Izquierda.png", TAM_NORMAL);
        fantasmaRojo = escalar("Fantasma-Rojo-Izquierda.png", TAM_NORMAL);

        pacmanGrandeCerrado = escalar("Pacman-Grande-BocaCerrada.png", TAM_GRANDE);
        pacmanGrandeNormal = escalar("Pacman-Grande-Normal.png", TAM_GRANDE);
        pacmanGrandeAbierto = escalar("Pacman-Grande-BocaAbierta.png", TAM_GRANDE);
        fantasmaAsustado = escalar("Fantasma-Asustado-Azul.png", TAM_NORMAL);
    }

    public void iniciar(Runnable onTerminar) {
        this.onTerminar = onTerminar;
        terminada = false;
        tiempo = 0;
        parte1 = true;
        indiceCiclo = 0;
        tickFrame = 0;

        int ancho = getPreferredSize().width;
        int alto = getPreferredSize().height;

        xPacman = ancho + TAM_NORMAL;
        xFantasma = ancho + TAM_NORMAL + 60;
        yPacman = alto * BASE_Y;
        yFantasma = alto * BASE_Y;

        SonidoManager.reproducirCinematica();

        timer = new Timer(16, e -> actualizar(0.016));
        timer.start();
    }

    private ImageIcon getSpriteActual(boolean esGrande) {
        int frame = CICLO[indiceCiclo];
        if (esGrande) {
            return switch (frame) {
                case 0 ->
                    pacmanGrandeCerrado;
                case 1 ->
                    pacmanGrandeNormal;
                case 2 ->
                    pacmanGrandeAbierto;
                default ->
                    pacmanGrandeNormal;
            };
        } else {
            return switch (frame) {
                case 0 ->
                    pacmanCerrada;
                case 1 ->
                    pacmanNormal;
                case 2 ->
                    pacmanAbierta;
                default ->
                    pacmanNormal;
            };
        }
    }

    private void actualizar(double dt) {
        if (terminada) {
            return; // ← primero, antes de todo
        }
        tiempo += dt;
        tickFrame++;

        if (tickFrame >= TICKS_POR_FRAME) {
            tickFrame = 0;
            indiceCiclo = (indiceCiclo + 1) % CICLO.length;
        }

        int ancho = getWidth() > 0 ? getWidth() : getPreferredSize().width;
        int alto = getHeight() > 0 ? getHeight() : getPreferredSize().height;
        double baseY = alto * BASE_Y;

        if (parte1) {
            xPacman -= VELOCIDAD * dt;
            xFantasma -= VELOCIDAD * dt;
            yPacman = baseY + Math.sin(tiempo * FRECUENCIA) * AMPLITUD;
            yFantasma = baseY + Math.sin(tiempo * FRECUENCIA + 1.0) * AMPLITUD;

            if (tiempo >= 4.5) {
                parte1 = false;
                indiceCiclo = 0;
                tickFrame = 0;
                xFantasma = -TAM_NORMAL - 10;
                xPacman = -TAM_NORMAL - TAM_GRANDE - 60;
                yFantasma = alto * BASE_Y;
                yPacman = alto * BASE_Y;
            }

        } else {
            double t2 = tiempo - 4.5;
            xPacman += VELOCIDAD * dt;
            xFantasma += VELOCIDAD * dt;
            yPacman = baseY + Math.sin(t2 * FRECUENCIA) * AMPLITUD;
            yFantasma = baseY + Math.sin(t2 * FRECUENCIA + 1.0) * AMPLITUD;
        }

        repaint();

        if (tiempo >= 9.0) {
            terminada = true;
            timer.stop();
            SonidoManager.detenerCinematica();
            if (onTerminar != null) {
                javax.swing.SwingUtilities.invokeLater(onTerminar);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (parte1) {
            ImageIcon sprPac = getSpriteActual(false);
            if (sprPac != null) {
                g2.drawImage(sprPac.getImage(),
                        (int) xPacman, (int) yPacman, TAM_NORMAL, TAM_NORMAL, this);
            }
            if (fantasmaRojo != null) {
                g2.drawImage(fantasmaRojo.getImage(),
                        (int) xFantasma, (int) yFantasma, TAM_NORMAL, TAM_NORMAL, this);
            }
        } else {
            if (fantasmaAsustado != null) {
                g2.drawImage(fantasmaAsustado.getImage(),
                        (int) xFantasma, (int) yFantasma, TAM_NORMAL, TAM_NORMAL, this);
            }
            ImageIcon sprPacG = getSpriteActual(true);
            if (sprPacG != null) {
                g2.drawImage(sprPacG.getImage(),
                        (int) xPacman, (int) yPacman, TAM_GRANDE, TAM_GRANDE, this);
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
