/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.pacman;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import javax.swing.*;
import java.util.List;

public class PacmanFrame extends javax.swing.JFrame {

    // ── Dimensiones ──────────────────────────────────────────
    /**
     * Alto de la barra HUD que aparece debajo del mapa.
     */
    static final int HUD_ALTO = 70;

    PacmanPanel panel;
    MovimientoPacman mov;

    public PacmanFrame() {
        panel = new PacmanPanel();

        // Estado inicial ANTES de cualquier hilo o sonido
        PacmanPanel.estadoJuego = EstadoJuego.STARTING;

        // ── Movimiento Pac-Man ───────────────────────────────
        mov = new MovimientoPacman(PacmanPanel.pacman);

        // ── Movimiento fantasmas ─────────────────────────────
        MovimientoFantasma movRojo = new MovimientoFantasma(
                PacmanPanel.fantasmaRojo, PacmanPanel.pacman, SistemaWaypoints.getWaypoint(31));
        MovimientoFantasma movAzul = new MovimientoFantasma(
                PacmanPanel.fantasmaAzul, PacmanPanel.pacman, SistemaWaypoints.getWaypoint(30));
        MovimientoFantasma movRosa = new MovimientoFantasma(
                PacmanPanel.fantasmaRosa, PacmanPanel.pacman, SistemaWaypoints.getWaypoint(32));
        MovimientoFantasma movNaranja = new MovimientoFantasma(
                PacmanPanel.fantasmaNaranja, PacmanPanel.pacman, SistemaWaypoints.getWaypoint(32));

        AnimacionMuerte animMuerte = new AnimacionMuerte(PacmanPanel.pacman, 30);

        List<MovimientoFantasma> listaFantasmas = new ArrayList<>();
        listaFantasmas.add(movRojo);
        listaFantasmas.add(movAzul);
        listaFantasmas.add(movRosa);
        listaFantasmas.add(movNaranja);

        mov.setFantasmas(listaFantasmas);

        mov.setOnNivelCompleto(() -> {
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                } // pequeña pausa
                iniciarNuevoNivel(mov, listaFantasmas);
            }).start();
        });

        // ── Vidas ────────────────────────────────────────────
        Vidas vidas = new Vidas(3, () -> {
            JOptionPane.showMessageDialog(null, "Game Over!\nPuntaje final: " + SistemaPuntos.getPuntos());
            System.exit(0);
        });

        vidas.setFantasmas(listaFantasmas);
        vidas.setMovPacman(mov);
        vidas.setAnimMuerte(animMuerte);

        int pacXInicial = SistemaWaypoints.getWaypoint(70).x - PacmanPanel.pacman.getWidth() / 2;
        int pacYInicial = SistemaWaypoints.getWaypoint(70).y - PacmanPanel.pacman.getHeight() / 2;

        movRojo.setVidas(vidas, pacXInicial, pacYInicial);
        movAzul.setVidas(vidas, pacXInicial, pacYInicial);
        movRosa.setVidas(vidas, pacXInicial, pacYInicial);
        movNaranja.setVidas(vidas, pacXInicial, pacYInicial);

        movRojo.setMovPacman(mov);
        movAzul.setMovPacman(mov);
        movRosa.setMovPacman(mov);
        movNaranja.setMovPacman(mov);

        // Arrancar hilos — quedan bloqueados por STARTING internamente
        mov.start();
        movRojo.start();
        movAzul.start();
        movRosa.start();
        movNaranja.start();

        // ── HUD ──────────────────────────────────────────────
        HudPanel hud = new HudPanel(vidas);

        // ── Layout del frame ─────────────────────────────────
        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(Color.BLACK);
        raiz.add(panel, BorderLayout.CENTER);
        hud.setPreferredSize(new Dimension(PacmanPanel.ANCHO, HUD_ALTO));
        raiz.add(hud, BorderLayout.SOUTH);

        setContentPane(raiz);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Pac-Man");
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        requestFocusInWindow();

        // ── Teclado ──────────────────────────────────────────
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_LEFT ->
                        mov.cambiarDireccion(-1, 0);
                    case java.awt.event.KeyEvent.VK_RIGHT ->
                        mov.cambiarDireccion(1, 0);
                    case java.awt.event.KeyEvent.VK_UP ->
                        mov.cambiarDireccion(0, -1);
                    case java.awt.event.KeyEvent.VK_DOWN ->
                        mov.cambiarDireccion(0, 1);
                }
            }
        });

        // ── Secuencia de inicio ───────────────────────────────
        // Precargar audio ANTES de reproducir intro
        SonidoManager.preCargarComida();
        SonidoManager.preCargarFantasmas();
        SonidoManager.iniciarFantasmas(); // hilo arranca pero espera PLAYING internamente

        // Reproducir intro y pasar a PLAYING cuando termine
        SonidoManager.reproducirInicio(() -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                PacmanPanel.readyLabel.setVisible(false);
                PacmanPanel.pacman.setVisible(true);
            });
            PacmanPanel.estadoJuego = EstadoJuego.PLAYING;
            // iniciarFantasmas ya está corriendo, detectará el cambio a PLAYING solo
        });
    }

    private void iniciarNuevoNivel(
            MovimientoPacman mov,
            List<MovimientoFantasma> listaFantasmas) {

        if (GestorNivel.esFinDelJuego()) {
            SonidoManager.detenerTodo();
            mov.pausar();
            listaFantasmas.forEach(MovimientoFantasma::pausar);
            javax.swing.SwingUtilities.invokeLater(this::mostrarPantallaFinal);
            return;
        }

        if (GestorNivel.debeMostrarCinematica()) {
            mostrarCinematica(mov, listaFantasmas);
            return; // mostrarCinematica llamará a iniciarNuevoNivel al terminar
        }

        // 1. Cambiar estado
        PacmanPanel.estadoJuego = EstadoJuego.STARTING;

        // 2. Detener todo audio
        SonidoManager.detenerTodo();

        // 3. Resetear estados de todos los fantasmas
        listaFantasmas.forEach(MovimientoFantasma::resetearCompleto);

        // 4. Pausar movimiento
        mov.pausar();
        listaFantasmas.forEach(MovimientoFantasma::pausar);

        // 5. Regenerar pellets en el panel
        PacmanPanel.pellets = SistemaWaypoints.generarPellets();

        // 6. Resetear fruta para el nuevo nivel
        PacmanPanel.fruta.resetNivel();

        // 7. Resetear contador de pellets
        SistemaPuntos.resetearPellets();

        // 8. Resetear multiplicador
        SistemaPuntos.resetearMultiplicador();

        // 9. Reposicionar Pac-Man y fantasmas
        mov.resetearSpawn();
        listaFantasmas.forEach(MovimientoFantasma::resetearSpawn);

        // 10. Ocultar Pac-Man y mostrar Ready
        javax.swing.SwingUtilities.invokeLater(() -> {
            PacmanPanel.pacman.setVisible(false);
            PacmanPanel.readyLabel.setVisible(true);
            panel.repaint();
        });

        // 11. Reproducir intro y arrancar
        SonidoManager.preCargarFantasmas();
        SonidoManager.iniciarFantasmas();
        SonidoManager.reproducirInicio(() -> {
            javax.swing.SwingUtilities.invokeLater(() -> {
                PacmanPanel.readyLabel.setVisible(false);
                PacmanPanel.pacman.setVisible(true);
            });
            PacmanPanel.estadoJuego = EstadoJuego.PLAYING;
            mov.reanudar();
            listaFantasmas.forEach(MovimientoFantasma::reanudar);
        });
    }

    private void mostrarCinematica(
            MovimientoPacman mov,
            List<MovimientoFantasma> listaFantasmas) {

        // 1. Pausar todo
        PacmanPanel.estadoJuego = EstadoJuego.CINEMATIC;
        SonidoManager.detenerTodo();
        mov.pausar();
        listaFantasmas.forEach(MovimientoFantasma::pausar);

        // 2. Crear panel de cinemática
        PanelCinematica panelCin = new PanelCinematica(
                PacmanPanel.ANCHO, PacmanPanel.ALTO);

        // 3. Swapear el panel del juego por la cinemática
        JPanel raiz = (JPanel) getContentPane();
        raiz.remove(panel);
        raiz.add(panelCin, BorderLayout.CENTER);
        raiz.revalidate();
        raiz.repaint();

        // 4. Iniciar cinemática
        panelCin.iniciar(() -> {
            // Al terminar: restaurar panel de juego
            javax.swing.SwingUtilities.invokeLater(() -> {
                raiz.remove(panelCin);
                raiz.add(panel, BorderLayout.CENTER);
                raiz.revalidate();
                raiz.repaint();

                // Iniciar nivel 3
                iniciarNuevoNivel(mov, listaFantasmas);
            });
        });
    }

    private void mostrarPantallaFinal() {
        PanelFinal panelFinal = new PanelFinal(PacmanPanel.ANCHO, PacmanPanel.ALTO);

        JPanel raiz = (JPanel) getContentPane();
        raiz.remove(panel);
        raiz.add(panelFinal, BorderLayout.CENTER);
        raiz.revalidate();
        raiz.repaint();

        panelFinal.iniciar();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PacmanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PacmanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PacmanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PacmanFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        SwingUtilities.invokeLater(() -> new PacmanFrame());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
