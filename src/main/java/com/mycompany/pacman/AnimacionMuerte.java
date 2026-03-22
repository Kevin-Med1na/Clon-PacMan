package com.mycompany.pacman;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class AnimacionMuerte {

    ImageIcon[] frames = new ImageIcon[11];
    JLabel label;

    public AnimacionMuerte(JLabel label, int tam) {
        this.label = label;
        cargarFrames(tam);
    }

    private void cargarFrames(int tam) {
        for (int i = 1; i <= 11; i++) {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/img/Muerte-Frame-" + i + ".png")
            );
            // Escalar igual que los demás sprites
            BufferedImage escalada = new BufferedImage(tam, tam, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = escalada.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(icon.getImage(), 0, 0, tam, tam, null);
            g2.dispose();
            frames[i - 1] = new ImageIcon(escalada);
        }
    }

    // Reproduce la animación completa en un hilo aparte
    public void reproducir(Runnable alTerminar) {
        new Thread(() -> {
            for (int i = 0; i < 11; i++) {
                final int idx = i;
                javax.swing.SwingUtilities.invokeLater(()
                        -> label.setIcon(frames[idx])
                );
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
            // Al terminar la animación
            alTerminar.run();
        }).start();
    }
}
