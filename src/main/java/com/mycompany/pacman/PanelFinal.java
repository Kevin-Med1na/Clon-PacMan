package com.mycompany.pacman;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.ImageIcon;


public class PanelFinal extends javax.swing.JPanel {

private ImageIcon imagenFinal;

    public PanelFinal(int ancho, int alto) {
        setPreferredSize(new Dimension(ancho, alto));
        setBackground(Color.BLACK);
        setOpaque(true);
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            imagenFinal = new ImageIcon(getClass().getResource("/img/Game-Over.png"));
        } catch (Exception e) {
            System.out.println("Error cargando Game-Over.png: " + e.getMessage());
        }
    }

    public void iniciar() {
        PacmanPanel.estadoJuego = EstadoJuego.GAME_COMPLETE;
        SonidoManager.reproducirCinematica(); // reutiliza el mismo método
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFinal == null) return;

        int w = getWidth();
        int h = getHeight();
        int imgW = imagenFinal.getIconWidth();
        int imgH = imagenFinal.getIconHeight();

        // Centrar la imagen
        int x = (w - imgW) / 2;
        int y = (h - imgH) / 2;

        g.drawImage(imagenFinal.getImage(), x, y, imgW, imgH, this);
    }


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
