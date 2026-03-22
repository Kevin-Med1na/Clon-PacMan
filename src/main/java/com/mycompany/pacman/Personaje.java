package com.mycompany.pacman;

import javax.swing.JLabel;

public class Personaje {

    public JLabel pacman;
    public int vidas;
    public int xInicial;
    public int yInicial;

    public Personaje(JLabel pacman, int vidas, int xInicial, int yInicial) {
        this.pacman = pacman;
        this.vidas = vidas;
        this.xInicial = xInicial;
        this.yInicial = yInicial;
    }

    public void perderVida(Runnable onGameOver) {
        vidas--;
        System.out.println("Vidas: " + vidas);
        // Regresar a posición inicial
        pacman.setLocation(xInicial, yInicial);
        if (vidas <= 0) {
            onGameOver.run();
        }
    }

    public void resetPosicion() {
        pacman.setLocation(xInicial, yInicial);
    }
}
