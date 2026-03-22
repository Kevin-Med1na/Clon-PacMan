package com.mycompany.pacman;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class AnimacionPacman {

    ImageIcon[] framesDerecha = new ImageIcon[2];
    ImageIcon[] framesIzquierda = new ImageIcon[2];
    ImageIcon[] framesArriba = new ImageIcon[2];
    ImageIcon[] framesAbajo = new ImageIcon[2];

    int frameActual = 0;
    JLabel label;

    int ultimoDx = 1;
    int ultimoDy = 0;

    public AnimacionPacman(JLabel label) {
        this.label = label;
        cargarYRotarSprites();
    }

    private void cargarYRotarSprites() {

        // Cargar los 2 sprites base mirando a la derecha
        ImageIcon base1 = new ImageIcon(getClass().getResource("/img/Pacman-Derecha-Normal.png"));
        ImageIcon base2 = new ImageIcon(getClass().getResource("/img/Pacman-BocaAbierta-Derecha.png"));

        // Derecha → son los originales
        framesDerecha[0] = base1;
        framesDerecha[1] = base2;

        // Izquierda → voltear horizontalmente (180° en Y)
        framesIzquierda[0] = rotarImagen(base1, 0, true);
        framesIzquierda[1] = rotarImagen(base2, 0, true);

        // Arriba → rotar 90° antihorario
        framesArriba[0] = rotarImagen(base1, -90, false);
        framesArriba[1] = rotarImagen(base2, -90, false);

        // Abajo → rotar 90° horario
        framesAbajo[0] = rotarImagen(base1, 90, false);
        framesAbajo[1] = rotarImagen(base2, 90, false);
    }

    private ImageIcon rotarImagen(ImageIcon icon, int grados, boolean voltearHorizontal) {
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        BufferedImage original = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = original.createGraphics();
        icon.paintIcon(null, g2, 0, 0);
        g2.dispose();

        BufferedImage resultado = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2r = resultado.createGraphics();

        AffineTransform at = new AffineTransform();
        at.translate(w / 2.0, h / 2.0);

        if (voltearHorizontal) {
            at.scale(-1, 1); // espejo horizontal
        } else {
            at.rotate(Math.toRadians(grados));
        }

        at.translate(-w / 2.0, -h / 2.0);
        g2r.drawImage(original, at, null);
        g2r.dispose();

        return new ImageIcon(resultado);
    }

    // Llamar esto cada cierto tiempo para alternar frames
    public void siguienteFrame(int dx, int dy) {
        ultimoDx = dx; // guardar última dirección
        ultimoDy = dy;
        frameActual = (frameActual + 1) % 2;

        if (dx == 1) {
            label.setIcon(framesDerecha[frameActual]);
        } else if (dx == -1) {
            label.setIcon(framesIzquierda[frameActual]);
        } else if (dy == -1) {
            label.setIcon(framesArriba[frameActual]);
        } else if (dy == 1) {
            label.setIcon(framesAbajo[frameActual]);
        }
    }

    // Mostrar frame quieto (boca cerrada mirando a la derecha)
    public void quieto() {
        // Muestra el frame cerrado de la última dirección
        if (ultimoDx == 1) {
            label.setIcon(framesDerecha[0]);
        } else if (ultimoDx == -1) {
            label.setIcon(framesIzquierda[0]);
        } else if (ultimoDy == -1) {
            label.setIcon(framesArriba[0]);
        } else if (ultimoDy == 1) {
            label.setIcon(framesAbajo[0]);
        }
    }
    
    public void resetear() {
    ultimoDx = 1;
    ultimoDy = 0;
    label.setIcon(framesDerecha[0]); // siempre mirando a la derecha al spawn
}
}
