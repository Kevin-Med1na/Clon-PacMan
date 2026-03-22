package com.mycompany.pacman;

import java.util.List;
import javax.swing.JLabel;

public class Vidas {

    private int vidas;
    private Runnable onGameOver;
    private List<MovimientoFantasma> fantasmas;
    private MovimientoPacman movPacman;
    private AnimacionMuerte animMuerte;

    private Runnable onChange = null;

    public Vidas(int vidasIniciales, Runnable onGameOver) {
        this.vidas = vidasIniciales;
        this.onGameOver = onGameOver;
    }

    /**
     * Registra un callback que se llama (en el EDT) cuando las vidas cambian.
     */
    public void setOnChange(Runnable r) {
        this.onChange = r;
    }

    private void notificarCambio() {
        if (onChange != null) {
            javax.swing.SwingUtilities.invokeLater(onChange);
        }
    }

    public void setFantasmas(List<MovimientoFantasma> fantasmas) {
        this.fantasmas = fantasmas;
    }

    public void setMovPacman(MovimientoPacman movPacman) {
        this.movPacman = movPacman;
    }

    public void setAnimMuerte(AnimacionMuerte animMuerte) {
        this.animMuerte = animMuerte;
    }

    public void perderVida(JLabel pacman, int xInicial, int yInicial) {
        if (PacmanPanel.enMuerte) {
            return;
        }
        PacmanPanel.enMuerte = true;
        vidas--;
        notificarCambio();

        // 1. Detener todo el audio incluyendo frightened
        SonidoManager.detenerTodo();

        // 2. Resetear estado de TODOS los fantasmas inmediatamente
        if (fantasmas != null) {
            fantasmas.forEach(MovimientoFantasma::resetearCompleto);
        }

        // 3. Pausar movimiento
        if (movPacman != null) {
            movPacman.pausar();
        }
        if (fantasmas != null) {
            fantasmas.forEach(MovimientoFantasma::pausar);
        }

        if (animMuerte != null) {
            SonidoManager.reproducirMuerte();
            animMuerte.reproducir(() -> {
                if (vidas <= 0) {
                    PacmanPanel.enMuerte = false;
                    onGameOver.run();
                } else {
                    // 4. Resetear spawn de Pac-Man
                    if (movPacman != null) {
                        movPacman.resetearSpawn();
                    }

                    // 5. Resetear spawn de fantasmas (posición + estado ya limpiados)
                    if (fantasmas != null) {
                        fantasmas.forEach(MovimientoFantasma::resetearSpawn);
                    }

                    // 6. Resetear multiplicador de puntos
                    SistemaPuntos.resetearMultiplicador();
                    PacmanPanel.fruta.reset();

                    // 7. Reanudar
                    PacmanPanel.enMuerte = false;
                    if (movPacman != null) {
                        movPacman.reanudar();
                    }
                    if (fantasmas != null) {
                        fantasmas.forEach(MovimientoFantasma::reanudar);
                    }

                    // 8. Reiniciar solo el sonido normal de fantasmas
                    SonidoManager.reiniciarDespuesDeMorir();
                }
            });
        }
    }

    public int getVidas() {
        return vidas;
    }
}
