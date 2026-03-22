package com.mycompany.pacman;

import java.awt.*;
import javax.swing.*;
 
/**
 * JLabel que muestra el puntaje en pantalla.
 * Se coloca en PacmanPanel con setBounds().
 */
public class PanelPuntos extends JLabel {
 
    public PanelPuntos() {
        setText("PUNTOS: 0");
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
        setHorizontalAlignment(SwingConstants.LEFT);
 
        // Escuchar cambios del sistema de puntos
        SistemaPuntos.setOnChange(this::actualizar);
    }
 
    private void actualizar() {
        setText("PUNTOS: " + SistemaPuntos.getPuntos());
    }
}
 