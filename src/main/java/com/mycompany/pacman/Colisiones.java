package com.mycompany.pacman;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Colisiones {

    // Verifica colisión entre dos labels respetando estados de noqueo
    public static boolean hayColision(JLabel label1, JLabel label2,
            Movible mov1, Movible mov2) {
            if (PacmanPanel.enMuerte) return false; // ← bloquea todo durante muerte
    if (mov1.isNoqueado() || mov2.isNoqueado()) return false;
    return label1.getBounds().intersects(label2.getBounds());
    }

    // Voltea la imagen del label 180 grados
    public static void voltear(JLabel label) {
        ImageIcon iconOriginal = (ImageIcon) label.getIcon();
        int w = iconOriginal.getIconWidth();
        int h = iconOriginal.getIconHeight();

        BufferedImage original = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = original.createGraphics();
        iconOriginal.paintIcon(null, g2, 0, 0);
        g2.dispose();

        BufferedImage volteada = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2v = volteada.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate(w / 2.0, h / 2.0);
        at.rotate(Math.PI);
        at.translate(-w / 2.0, -h / 2.0);
        g2v.drawImage(original, at, null);
        g2v.dispose();

        label.setIcon(new ImageIcon(volteada));
    }
}
