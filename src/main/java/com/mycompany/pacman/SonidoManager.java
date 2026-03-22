package com.mycompany.pacman;

import java.net.URL;
import javax.sound.sampled.*;

public class SonidoManager {

    // Clips para sonidos en loop
    private static Clip clipComida;
    private static Clip clipFantasmas;
    private static Clip clipPoder;

    // Flag para saber si el poder está activo
    public static boolean poderActivo = false;

    // ── Reproducir sonido una sola vez ───────────────────────
    public static void reproducir(String archivo) {
        new Thread(() -> {
            try {
                URL url = SonidoManager.class.getResource("/sound/" + archivo + ".wav");
                AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);
                clip.start();
            } catch (Exception e) {
                System.out.println("Error sonido: " + e.getMessage());
            }
        }).start();
    }

    // ── Iniciar loop ─────────────────────────────────────────
    public static Clip iniciarLoop(String archivo) {
        try {
            URL url = SonidoManager.class.getResource("/sound/" + archivo + ".wav");
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            return clip;
        } catch (Exception e) {
            System.out.println("Error loop: " + e.getMessage());
            return null;
        }
    }

    // ── Detener clip ─────────────────────────────────────────
    public static void detener(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    // ── Sonido de inicio: bloquea hasta que termina ──────────
    public static void reproducirInicio(Runnable alTerminar) {
        new Thread(() -> {
            try {
                URL url = SonidoManager.class.getResource("/sound/PacMan_Inicio.wav");
                AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audio);

                // Usar CountDownLatch para esperar que termine exactamente
                java.util.concurrent.CountDownLatch latch
                        = new java.util.concurrent.CountDownLatch(1);

                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        latch.countDown();
                    }
                });

                clip.start();
                latch.await(); // espera real hasta que termine
                clip.close();

            } catch (Exception e) {
                System.out.println("Error inicio: " + e.getMessage());
            }
            alTerminar.run();
        }).start();
    }

    // ── Estado frightened ────────────────────────────────────
    private static volatile boolean frightenedActivo = false;
    private static Thread hiloFrightened = null;

    public static void activarPoder(Runnable alTerminar) {
        reproducirComida();

        if (frightenedActivo) {
            // ← Reiniciar loop además del temporizador
            iniciarLoopFrightened();
            reiniciarTemporizadorFrightened(alTerminar);
            return;
        }

        frightenedActivo = true;
        detenerFantasmas();
        iniciarLoopFrightened();
        iniciarTemporizadorFrightened(alTerminar);
    }

    private static void iniciarLoopFrightened() {
        // Cerrar completamente el clip anterior
        if (clipPoder != null) {
            clipPoder.stop();
            clipPoder.close(); // ← close en vez de solo stop
            clipPoder = null;
        }

        try {
            URL url = SonidoManager.class.getResource("/sound/PacMan_Poder.wav");
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clipPoder = AudioSystem.getClip();
            clipPoder.open(audio);
            clipPoder.setFramePosition(0);
            clipPoder.loop(Clip.LOOP_CONTINUOUSLY);
            clipPoder.start();
        } catch (Exception e) {
            System.out.println("Error frightened loop: " + e.getMessage());
        }
    }

    private static void iniciarTemporizadorFrightened(Runnable alTerminar) {
        // Cancelar hilo anterior si existe
        if (hiloFrightened != null) {
            hiloFrightened.interrupt();
        }

        hiloFrightened = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // Fue interrumpido por otro power pellet, no hacer nada
                return;
            }
            // Solo llega aquí si los 10 segundos terminaron de verdad
            terminarFrightened(alTerminar);
        });
        hiloFrightened.setDaemon(true);
        hiloFrightened.start();
    }

    private static void reiniciarTemporizadorFrightened(Runnable alTerminar) {
        // Interrumpir el hilo anterior para cancelar su temporizador
        if (hiloFrightened != null) {
            hiloFrightened.interrupt();
        }

        // El loop de audio sigue sonando sin interrumpirse
        // Solo reiniciamos el temporizador
        iniciarTemporizadorFrightened(alTerminar);
    }

    private static void terminarFrightened(Runnable alTerminar) {
        frightenedActivo = false;

        // Detener sonido frightened
        detener(clipPoder);
        clipPoder = null;

        // Reanudar sonido normal de fantasmas
        iniciarFantasmas();

        // Notificar al juego que terminó
        if (alTerminar != null) {
            alTerminar.run();
        }
    }

// ── Reiniciar sonidos después de morir ───────────────────
    public static void reiniciarDespuesDeMorir() {
        frightenedActivo = false;
        if (hiloFrightened != null) {
            hiloFrightened.interrupt();
            hiloFrightened = null;
        }
        detener(clipPoder);
        clipPoder = null;
        iniciarFantasmas(); // solo fantasmas, sin frightened
    }

// ── Getter para saber si frightened está activo ──────────
    public static boolean isFrightenedActivo() {
        return frightenedActivo;
    }

    // ── Iniciar sonidos del juego ────────────────────────────
    public static void iniciarSonidosJuego() {
        preCargarComida();
        preCargarFantasmas();
        iniciarFantasmas();
    }

// ── Detener todo al morir Pac-Man ────────────────────────
    public static void detenerTodo() {
        frightenedActivo = false;
        if (hiloFrightened != null) {
            hiloFrightened.interrupt();
            hiloFrightened = null;
        }
        if (clipPoder != null) {
            clipPoder.stop();
            clipPoder.close(); // ← close aquí también
            clipPoder = null;
        }
        detenerFantasmas();
    }

    // ── Sonido de muerte ─────────────────────────────────────
    public static void reproducirMuerte() {
        detenerTodo();
        reproducir("PacMan_Muerte");
    }

    private static Clip[] clipsFantasmas = new Clip[2];
    private static int clipFantasmaActual = 0;

    public static void preCargarFantasmas() {
        try {
            for (int i = 0; i < 2; i++) {
                URL url = SonidoManager.class.getResource("/sound/Fantasma_Movimiento.wav");
                AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                clipsFantasmas[i] = AudioSystem.getClip();
                clipsFantasmas[i].open(audio);
            }
            System.out.println("Fantasmas precargados OK");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static volatile boolean fantasmasSonando = false;
    private static Thread hiloFantasmas = null; // ← referencia al hilo actual

    public static void iniciarFantasmas() {
        if (clipsFantasmas[0] == null) {
            return;
        }

        // Detener hilo anterior completamente
        fantasmasSonando = false;
        if (hiloFantasmas != null) {
            hiloFantasmas.interrupt();
            try {
                hiloFantasmas.join(200);
            } catch (Exception e) {
            } // esperar que termine
            hiloFantasmas = null;
        }

        detenerFantasmas();

        long duracionMs = clipsFantasmas[0].getMicrosecondLength() / 1000;
        fantasmasSonando = true;

        hiloFantasmas = new Thread(() -> {
            int actual = 0;
            while (fantasmasSonando && !frightenedActivo) {
                if (PacmanPanel.estadoJuego != EstadoJuego.PLAYING) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        return;
                    }
                    continue;
                }

                Clip clip = clipsFantasmas[actual];
                clip.setFramePosition(0);
                clip.start();

                try {
                    Thread.sleep(duracionMs - 30);
                } catch (InterruptedException e) {
                    // Hilo interrumpido, salir limpiamente
                    clip.stop();
                    return;
                }

                actual = (actual + 1) % 2;
            }
        });
        hiloFantasmas.setDaemon(true);
        hiloFantasmas.start();
    }

    public static void detenerFantasmas() {
        fantasmasSonando = false;
        if (hiloFantasmas != null) {
            hiloFantasmas.interrupt();
            hiloFantasmas = null;
        }
        for (Clip c : clipsFantasmas) {
            if (c != null) {
                c.stop();
                c.setFramePosition(0);
            }
        }
    }

    private static boolean comidaSonando = false;
    private static Clip clipComidaPreCargado = null;

// Llamar esto una sola vez al iniciar el juego
    private static Clip[] clipsComida = new Clip[2];
    private static int clipActual = 0;

    public static void preCargarComida() {
        try {
            String[] archivos = {"/sound/PacMan_Comida1.wav", "/sound/PacMan_Comida2.wav"};
            for (int i = 0; i < 2; i++) {
                URL url = SonidoManager.class.getResource(archivos[i]);
                AudioInputStream audio = AudioSystem.getAudioInputStream(url);
                clipsComida[i] = AudioSystem.getClip();
                clipsComida[i].open(audio);
            }
            System.out.println("preCargar OK");
        } catch (Exception e) {
            System.out.println("Error precarga: " + e.getMessage());
        }
    }

    public static void reproducirComida() {
        // ← ya no bloquea si poderActivo, el sonido de comer siempre suena
        if (clipsComida[0] == null) {
            return;
        }

        for (int i = 0; i < 2; i++) {
            Clip clip = clipsComida[i];
            if (!clip.isRunning()) {
                clip.setFramePosition(0);
                clip.start();
                return;
            }
        }
    }

    private static Clip clipCinematica = null;

    public static void reproducirCinematica() {
        try {
            URL url = SonidoManager.class.getResource("/sound/Coffe-Break-Music.wav");
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            clipCinematica = AudioSystem.getClip();
            clipCinematica.open(audio);
            clipCinematica.start();
        } catch (Exception e) {
            System.out.println("Error cinemática audio: " + e.getMessage());
        }
    }

    public static void detenerCinematica() {
        detener(clipCinematica);
        clipCinematica = null;
    }
}
