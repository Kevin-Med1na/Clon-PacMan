package com.mycompany.pacman;

public class GestorNivel {

    private static boolean cinematicaMostrada = false;
    private static final int NIVEL_MAXIMO = 4;

    // Fruta por nivel
    public static String getFrutaSprite() {
        return switch (PacmanPanel.nivelActual) {
            case 1 ->
                "Cereza.png";
            case 2 ->
                "Fresa.png";
            case 3 ->
                "Manzana.png";
            case 4 ->
                "Naranja.png";
            default ->
                "Cereza.png"; // fallback
        };
    }

    public static int getFrutaPuntos() {
        return switch (PacmanPanel.nivelActual) {
            case 1 ->
                100;
            case 2 ->
                200;
            case 3 ->
                300;
            case 4 ->
                400;
            default ->
                100;
        };
    }

    // Verifica si quedan pellets
    public static boolean nivelCompletado() {
        for (Pellet p : PacmanPanel.pellets) {
            if (!p.recogido) {
                return false;
            }
        }
        return true;
    }

    public static boolean debeMostrarCinematica() {
        if (PacmanPanel.nivelActual == 3 && !cinematicaMostrada) {
            cinematicaMostrada = true; // ← marcar como mostrada
            return true;
        }
        return false;
    }

    public static void resetearCinematica() {
        cinematicaMostrada = false; // llamar si reinician la partida
    }



    public static boolean esFinDelJuego() {
        return PacmanPanel.nivelActual > NIVEL_MAXIMO;
    }
}
