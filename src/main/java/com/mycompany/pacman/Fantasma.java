package com.mycompany.pacman;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Fantasma {

    public JLabel label;
    public String color;
    public EstadoFantasma estado = EstadoFantasma.NORMAL;

    // Sprites normales
    ImageIcon spriteArriba, spriteAbajo, spriteIzquierda, spriteDerecha;

    // Sprites asustado
    ImageIcon spriteAsustadoAzul;
    ImageIcon spriteAsustadoBlanco;

    // Sprites muerto (ojos)
    ImageIcon ojosArriba, ojosAbajo, ojosIzquierda, ojosDerecha;

    int dirX = 1, dirY = 0;

    public Fantasma(String color, int x, int y, int tam) {
        this.color = color;
        cargarSprites(color, tam);

        label = new JLabel();
        label.setIcon(spriteDerecha); // sprite inicial
        label.setBounds(x, y, tam, tam);
    }

    private void cargarSprites(String color, int tam) {
        spriteArriba = escalar(new ImageIcon(getClass().getResource("/img/Fantasma-" + color + "-Arriba.png")), tam);
        spriteAbajo = escalar(new ImageIcon(getClass().getResource("/img/Fantasma-" + color + "-Abajo.png")), tam);
        spriteIzquierda = escalar(new ImageIcon(getClass().getResource("/img/Fantasma-" + color + "-Izquierda.png")), tam);
        spriteDerecha = escalar(new ImageIcon(getClass().getResource("/img/Fantasma-" + color + "-Derecha.png")), tam);

        spriteAsustadoAzul = escalar(new ImageIcon(getClass().getResource("/img/Fantasma-Asustado-Azul.png")), tam);
        spriteAsustadoBlanco = escalar(new ImageIcon(getClass().getResource("/img/Fantasma-Asustado-Blanco.png")), tam);

        ojosArriba = escalarConDimension(new ImageIcon(getClass().getResource("/img/Ojos-Fantasmas-Arriba.png")), 20, 10);
        ojosAbajo = escalarConDimension(new ImageIcon(getClass().getResource("/img/Ojos-Fantasmas-Abajo.png")), 20, 10);
        ojosIzquierda = escalarConDimension(new ImageIcon(getClass().getResource("/img/Ojos-Fantasmas-Izquierda.png")), 20, 10);
        ojosDerecha = escalarConDimension(new ImageIcon(getClass().getResource("/img/Ojos-Fantasmas-Derecha.png")), 20, 10);
    }

    private ImageIcon escalar(ImageIcon icon, int tam) {
        BufferedImage escalada = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g2 = escalada.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(icon.getImage(), 0, 0, tam, tam, null);
        g2.dispose();
        return new ImageIcon(escalada);
    }

    private ImageIcon escalarConDimension(ImageIcon icon, int ancho, int alto) {
        BufferedImage escalada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = escalada.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(icon.getImage(), 0, 0, ancho, alto, null);
        g2.dispose();
        return new ImageIcon(escalada);
    }

    // Actualiza sprite según estado y dirección
    public void actualizarSprite(int dx, int dy) {
        this.dirX = dx;
        this.dirY = dy;

        switch (estado) {
            case NORMAL ->
                actualizarSpriteNormal(dx, dy);
            case ASUSTADO ->
                label.setIcon(spriteAsustadoAzul); // parpadeo lo maneja el hilo
            case MUERTO ->
                actualizarSpriteOjos(dx, dy);
        }
    }

    public void actualizarSpriteNormal(int dx, int dy) {
        if (dx == 1) {
            label.setIcon(spriteDerecha);
        } else if (dx == -1) {
            label.setIcon(spriteIzquierda);
        } else if (dy == -1) {
            label.setIcon(spriteArriba);
        } else if (dy == 1) {
            label.setIcon(spriteAbajo);
        }

    }

    public void actualizarSpriteOjos(int dx, int dy) {
        if (dx == 1) {
            label.setIcon(ojosDerecha);
        } else if (dx == -1) {
            label.setIcon(ojosIzquierda);
        } else if (dy == -1) {
            label.setIcon(ojosArriba);
        } else if (dy == 1) {
            label.setIcon(ojosAbajo);
        }
    }

    public void cambiarEstado(EstadoFantasma nuevoEstado) {
        // MUERTO tiene prioridad, no se puede sobreescribir con ASUSTADO
        if (estado == EstadoFantasma.MUERTO && nuevoEstado == EstadoFantasma.ASUSTADO) {
            return;
        }
        estado = nuevoEstado;
    }
}
