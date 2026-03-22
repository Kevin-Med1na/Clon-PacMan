package com.mycompany.pacman;

public class Pellet {

    public int x, y;
    public boolean recogido = false;
    public boolean esPowerPellet; // true = grande, false = pequeño

    public Pellet(int x, int y, boolean esPowerPellet) {
        this.x = x;
        this.y = y;
        this.esPowerPellet = esPowerPellet;
    }

    // Radio de colisión amplio para compensar movimiento libre
    public boolean colisionaCon(int px, int py, int anchoPacman, int altoPacman) {
        // Centro de Pac-Man
        int centroPacX = px + anchoPacman / 2;
        int centroPacY = py + altoPacman / 2;

        // Distancia entre Pac-Man y el pellet
        int distancia = (int) Math.sqrt(
                Math.pow(centroPacX - x, 2)
                + Math.pow(centroPacY - y, 2)
        );

        // Radio según tipo de pellet
        int radio = esPowerPellet ? 20 : 15;
        return distancia <= radio;
    }
}
