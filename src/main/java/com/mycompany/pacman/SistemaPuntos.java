package com.mycompany.pacman;

public class SistemaPuntos {

    private static int pelletsComidos = 0;
    private static int puntos = 0;

    // Multiplicador de fantasmas: 1 = primera vez (200), 2 = segunda (400), ...
    private static int multiplicadorFantasmas = 1;

    // Puntos base por comer un fantasma asustado
    private static final int PUNTOS_BASE_FANTASMA = 200;

    // ── Sumar puntos ─────────────────────────────────────────
    public static void sumarPellet() {
        puntos += 10;
        pelletsComidos++; // ← reescribe manualmente
        notificarCambio();
    }
    
    public static void sumarFruta(int puntosFruta) {
    puntos += puntosFruta;
    System.out.println("¡Fruta recogida! +" + puntosFruta + " pts");
    notificarCambio();
    }

    public static int getPelletsComidos() {
        return pelletsComidos;
    }

    public static void resetearPellets() {
        pelletsCom­idos = 0;
    }

    public static void sumarPowerPellet() {
        puntos += 50;
        notificarCambio();
    }

    public static void resetear() {
        puntos = 0;
        multiplicadorFantasmas = 1;
        pelletsComidos = 0;
        notificarCambio();
    }

    /**
     * Suma los puntos por comer un fantasma asustado y duplica el multiplicador
     * para el siguiente fantasma del mismo poder.
     *
     * @return puntos ganados en esta acción (para mostrar en pantalla si se
     * quiere)
     */
    public static int sumarFantasma() {
        int ganados = PUNTOS_BASE_FANTASMA * multiplicadorFantasmas;
        puntos += ganados;
        multiplicadorFantasmas *= 2;
        notificarCambio();
        System.out.println("¡Fantasma comido! +" + ganados + " pts  (total: " + puntos + ")");
        return ganados;
    }

    /**
     * Llama esto cuando termina el efecto del Power Pellet o Pac-Man muere.
     */
    public static void resetearMultiplicador() {
        multiplicadorFantasmas = 1;
    }

    public static int getPuntos() {
        return puntos;
    }

    public static int getMultiplicador() {
        return multiplicadorFantasmas;
    }

    // ── Listener para actualizar la UI ───────────────────────
    private static Runnable onChange = null;

    public static void setOnChange(Runnable r) {
        onChange = r;
    }

    private static void notificarCambio() {
        if (onChange != null) {
            javax.swing.SwingUtilities.invokeLater(onChange);
        }
    }

}
