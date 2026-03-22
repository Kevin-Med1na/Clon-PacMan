package com.mycompany.pacman;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Fruta {

    public JLabel label;
    private boolean visible         = false;
    private boolean primeraUsada    = false;
    private boolean segundaUsada    = false;
    private Thread  hiloDesaparecer = null;
    
    public static final int PUNTOS = 100;
    private static final int TIEMPO_MS = 9000;
    private static final int SPAWN_X   = 400;
    private static final int SPAWN_Y   = 340;
    private static final int TAM       = 20;

    public Fruta() {
        label = new JLabel();
        label.setBounds(SPAWN_X - TAM / 2, SPAWN_Y - TAM / 2, TAM, TAM);
        label.setVisible(false);
        actualizarSprite();
    }

    // Actualiza el sprite según el nivel actual
    public void actualizarSprite() {
        try {
            ImageIcon icon = escalar(
                new ImageIcon(getClass().getResource("/img/" + GestorNivel.getFrutaSprite())), TAM);
            label.setIcon(icon);
        } catch (Exception e) {
            System.out.println("Error sprite fruta: " + e.getMessage());
        }
    }

    private ImageIcon escalar(ImageIcon icon, int tam) {
        java.awt.image.BufferedImage escalada =
            new java.awt.image.BufferedImage(tam, tam, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = escalada.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                            java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(icon.getImage(), 0, 0, tam, tam, null);
        g2.dispose();
        return new javax.swing.ImageIcon(escalada);
    }

    public void verificarAparicion(int pellets) {
        if (pellets == 70 && !primeraUsada) {
            primeraUsada = true;
            mostrar();
        } else if (pellets == 170 && !segundaUsada) {
            segundaUsada = true;
            mostrar();
        }
    }

    private void mostrar() {
        visible = true;
        javax.swing.SwingUtilities.invokeLater(() -> label.setVisible(true));
        if (hiloDesaparecer != null) hiloDesaparecer.interrupt();
        hiloDesaparecer = new Thread(() -> {
            try { Thread.sleep(TIEMPO_MS); ocultar(); }
            catch (InterruptedException e) {}
        });
        hiloDesaparecer.setDaemon(true);
        hiloDesaparecer.start();
    }

    public void ocultar() {
        visible = false;
        javax.swing.SwingUtilities.invokeLater(() -> label.setVisible(false));
        if (hiloDesaparecer != null) { hiloDesaparecer.interrupt(); hiloDesaparecer = null; }
    }

    public boolean verificarColision(javax.swing.JLabel pacman) {
        if (!visible) return false;
        return label.getBounds().intersects(pacman.getBounds());
    }

    public int getPuntos() { return GestorNivel.getFrutaPuntos(); }

    // Reset completo para nuevo nivel
    public void resetNivel() {
        ocultar();
        primeraUsada = false;
        segundaUsada = false;
        actualizarSprite(); // ← actualiza sprite para el nuevo nivel
    }
    
    public void reset() {
        ocultar();
        primeraUsada = false;
        segundaUsada = false;
    }
}
