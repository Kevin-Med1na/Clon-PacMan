package com.mycompany.pacman;

import java.awt.*;
import javax.swing.*;

/**
 * Barra de HUD que se muestra en el margen negro fuera del área de juego.
 *
 * Layout: Izquierda → etiqueta "SCORE" pequeña + número grande en amarillo
 * Centro → "PAC-MAN" tenue Derecha → etiqueta "VIDAS" pequeña ENCIMA + iconos
 * DEBAJO
 */
public class HudPanel extends JPanel {

    private final Vidas vidas;

    private ImageIcon spriteVida;
    private static final int VIDA_TAM = 12;
    private static final int VIDA_SEP = 6;

    private static final Color AMARILLO_PACMAN = new Color(255, 213, 0);
    private static final Color GRIS_LABEL = new Color(180, 180, 180);
    private static final Font FONT_LABEL = new Font("Arial", Font.PLAIN, 12);
    private static final Font FONT_SCORE = new Font("Arial", Font.BOLD, 22);
    private static final Font FONT_TITULO = new Font("Arial", Font.BOLD, 12);

    public HudPanel(Vidas vidas) {
        this.vidas = vidas;
        setBackground(Color.BLACK);
        setOpaque(true);
        cargarSpriteVida();
        SistemaPuntos.setOnChange(this::repaint);

        if (vidas != null) {
            vidas.setOnChange(this::repaint);
        }
    }

    private void cargarSpriteVida() {
        try {
            ImageIcon raw = new ImageIcon(
                    getClass().getResource("/img/Pacman-Icon-Vida.png"));
            java.awt.image.BufferedImage scaled
                    = new java.awt.image.BufferedImage(VIDA_TAM, 13,
                            java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaled.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(raw.getImage(), 0, 0, VIDA_TAM, VIDA_TAM, null);
            g2.dispose();
            spriteVida = new ImageIcon(scaled);
        } catch (Exception e) {
            spriteVida = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Línea separadora sutil en la parte superior
        g2.setColor(new Color(50, 50, 50));
        g2.drawLine(0, 0, w, 0);

        // ── Bloque SCORE (izquierda) ─────────────────────────
        // "SCORE" centrado arriba, número abajo
        int scoreX = 30;
        int labelY = h / 2 - 4;   // y de la etiqueta pequeña
        int valorY = h / 2 + 17;  // y del número grande

        g2.setFont(FONT_TITULO);
        g2.setColor(GRIS_LABEL);
        String labelScore = PacmanPanel.estadoJuego == EstadoJuego.GAME_COMPLETE
                ? "BEST SCORE" : "SCORE";
        g2.drawString(labelScore, scoreX, labelY);

        g2.setFont(FONT_SCORE);
        g2.setColor(AMARILLO_PACMAN);
        g2.drawString(String.format("%06d", SistemaPuntos.getPuntos()), scoreX, valorY);

        // ── Bloque VIDAS (derecha) ───────────────────────────
        int vidasActuales = (vidas != null) ? vidas.getVidas() : 0;

        // Calcular ancho total de los iconos
        int totalIconos = vidasActuales > 0
                ? vidasActuales * VIDA_TAM + (vidasActuales - 1) * VIDA_SEP
                : 0;

        // Medir ancho de la etiqueta para alinear ambos a la derecha
        g2.setFont(FONT_TITULO);
        FontMetrics fm = g2.getFontMetrics();
        int labelVidasAncho = fm.stringWidth("VIDAS");

        // El bloque completo arranca en el x que deja 30px al borde derecho
        int bloqueAncho = Math.max(totalIconos, labelVidasAncho);
        int bloqueX = w - 30 - bloqueAncho;

        // Etiqueta "VIDAS" — fila superior (mismo y que "SCORE")
        g2.setColor(GRIS_LABEL);
        g2.drawString("VIDAS", bloqueX, labelY);

        // Iconos — fila inferior (mismo y que el número del score)
        // Centrarlos horizontalmente dentro del bloque
        int iconsOffset = (bloqueAncho - totalIconos) / 2;
        int iconY = valorY - VIDA_TAM;   // alinear base con la base del número

        for (int i = 0; i < vidasActuales; i++) {
            int ix = bloqueX + iconsOffset + i * (VIDA_TAM + VIDA_SEP);
            if (spriteVida != null) {
                g2.drawImage(spriteVida.getImage(), ix, iconY, VIDA_TAM, VIDA_TAM, this);
            } else {
                g2.setColor(AMARILLO_PACMAN);
                g2.fillOval(ix, iconY, VIDA_TAM, VIDA_TAM);
            }
        }

        g2.setColor(new Color(255, 255, 255, 200));
        String nivelStr = "LEVEL " + PacmanPanel.nivelActual;
        FontMetrics fm2 = g2.getFontMetrics();
        int tx = (w - fm2.stringWidth(nivelStr)) / 2; // ← reasigna tx, sin redeclarar
        g2.drawString(nivelStr, tx, h / 2 + 10);
    }
}
