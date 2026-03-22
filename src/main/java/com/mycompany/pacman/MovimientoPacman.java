package com.mycompany.pacman;

import java.util.List;
import javax.swing.JLabel;

public class MovimientoPacman extends Thread implements Movible {

    JLabel object;
    AnimacionPacman animacion;

    int dirX = 1, dirY = 0;         // dirección actual
    int dirDeseadaX = 0, dirDeseadaY = 0; // dirección pedida por el jugador

    Waypoint waypointActual;
    boolean enMovimiento = false;
    int ticksAnimacion = 0;
    int velocidad = 3;

    public MovimientoPacman(JLabel object) {
        this.object = object;
        this.animacion = new AnimacionPacman(object);
        animacion.quieto();

        waypointActual = SistemaWaypoints.getWaypoint(70);
        object.setLocation(
                waypointActual.x - object.getWidth() / 2,
                waypointActual.y - object.getHeight() / 2
        );
    }

    public void cambiarDireccion(int dx, int dy) {
        this.dirDeseadaX = dx;
        this.dirDeseadaY = dy;
        this.enMovimiento = true;
    }

    // Busca el waypoint conectado en la dirección dada
    private Waypoint siguienteEn(Waypoint desde, int dx, int dy) {
        for (int id : desde.conexiones) {
            Waypoint w = SistemaWaypoints.getWaypoint(id);
            if (w.soloFantasmas) {
                continue;
            }

            int difX = w.x - desde.x;
            int difY = w.y - desde.y;

            boolean mismaFila = Math.abs(difY) < 20;
            boolean mismaColumna = Math.abs(difX) < 20;

            if (dx == 1 && difX > 0 && mismaFila) {
                return w;
            }
            if (dx == -1 && difX < 0 && mismaFila) {
                return w;
            }
            if (dy == 1 && difY > 0 && mismaColumna) {
                return w;
            }
            if (dy == -1 && difY < 0 && mismaColumna) {
                return w;
            }
        }
        return null;
    }

    // Mueve suavemente hacia un waypoint destino
    private void moverHacia(Waypoint destino) {
        int destinoX = destino.x - object.getWidth() / 2;
        int destinoY = destino.y - object.getHeight() / 2;

        while (true) {
            if (interrumpir) { // ← sale del loop inmediatamente
                interrumpir = false;
                return;
            }
            if (pausado) {
                try {
                    Thread.sleep(16);
                } catch (Exception e) {
                }
                continue;
            }
            int px = object.getLocation().x;
            int py = object.getLocation().y;

            // Teleporte si llegó a un portal
            if (Math.abs(px - destinoX) <= velocidad
                    && Math.abs(py - destinoY) <= velocidad) {
                object.setLocation(destinoX, destinoY);

                if (destino.esPortal) {
                    Waypoint otroPortal = destino.id == 27
                            ? SistemaWaypoints.getWaypoint(35)
                            : SistemaWaypoints.getWaypoint(27);

                    Waypoint despuesDelPortal = null;
                    for (int id : otroPortal.conexiones) {
                        Waypoint w = SistemaWaypoints.getWaypoint(id);
                        if (!w.soloFantasmas) {
                            despuesDelPortal = w;
                            break;
                        }
                    }

                    if (despuesDelPortal != null) {
                        dirX = despuesDelPortal.x > otroPortal.x ? 1 : -1;
                        dirY = 0;
                        object.setLocation(
                                otroPortal.x - object.getWidth() / 2,
                                otroPortal.y - object.getHeight() / 2
                        );
                        waypointActual = otroPortal;
                        moverHacia(despuesDelPortal);
                        waypointActual = despuesDelPortal; // ← solo esta, sin la de abajo
                    }
                } else {
                    waypointActual = destino; // ← solo para casos normales
                }
                break;
            }

            // Mover hacia el destino
            int nuevoX = px + (px < destinoX ? velocidad : px > destinoX ? -velocidad : 0);
            int nuevoY = py + (py < destinoY ? velocidad : py > destinoY ? -velocidad : 0);
            object.setLocation(nuevoX, nuevoY);

            ticksAnimacion++;
            if (ticksAnimacion >= 8) {
                ticksAnimacion = 0;
                animacion.siguienteFrame(dirX, dirY);
            }

            verificarPellets();

            try {
                Thread.sleep(16);
            } catch (Exception e) {
            }
        }
    }

    Runnable onNivelCompleto;

    public void setOnNivelCompleto(Runnable r) {
        this.onNivelCompleto = r;
    }

    private void verificarPellets() {
        int px = object.getLocation().x;
        int py = object.getLocation().y;
        int ancho = object.getWidth();
        int alto = object.getHeight();

        for (Pellet p : PacmanPanel.pellets) {
            if (!p.recogido && p.colisionaCon(px, py, ancho, alto)) {
                p.recogido = true;
                if (p.esPowerPellet) {
                    System.out.println("Power Pellet!");
                    SistemaPuntos.sumarPowerPellet();
                    SonidoManager.activarPoder(() -> {
                        SistemaPuntos.resetearMultiplicador();
                    });
                    if (fantasmas != null) {
                        fantasmas.forEach(f -> f.asustar(10));
                    }
                } else {
                    SonidoManager.reproducirComida(); // ← una vez por pellet
                    SistemaPuntos.sumarPellet();
                    System.out.println("Pellets comidos: " + SistemaPuntos.getPelletsComidos()); // ← debug
                    //SistemaPuntos.resetearMultiplicador();
                    PacmanPanel.fruta.verificarAparicion(SistemaPuntos.getPelletsComidos());
                }
                PacmanPanel panel = (PacmanPanel) object.getParent();
                panel.repaint();
            }
        }

        if (GestorNivel.nivelCompletado()
                && PacmanPanel.estadoJuego == EstadoJuego.PLAYING) {

            PacmanPanel.estadoJuego = EstadoJuego.LEVEL_COMPLETE;
            PacmanPanel.nivelActual++;
            System.out.println("Nivel completado! Pasando al nivel " + PacmanPanel.nivelActual);

            // Llamar al gestor de nivel desde el frame
            if (onNivelCompleto != null) {
                onNivelCompleto.run();
            }
        }

        // Verificar colisión con fruta
        if (PacmanPanel.fruta.verificarColision(object)) {
            PacmanPanel.fruta.ocultar();
            SistemaPuntos.sumarFruta(Fruta.PUNTOS);
        }
    }

    // Atributo en MovimientoPacman
    List<MovimientoFantasma> fantasmas;

    public void setFantasmas(List<MovimientoFantasma> fantasmas) {
        this.fantasmas = fantasmas;
    }

    // En MovimientoPacman agrega:
    boolean pausado = false;
    boolean interrumpir = false;

    public void pausar() {
        pausado = true;
        interrumpir = true; // ← interrumpe el moverHacia actual
        enMovimiento = false;
        animacion.quieto();
    }

    public void reanudar() {
        interrumpir = false;
        pausado = false;
    }

    public void resetearSpawn() {
        interrumpir = true; // ← también aquí
        waypointActual = SistemaWaypoints.getWaypoint(70);
        object.setLocation(
                waypointActual.x - object.getWidth() / 2,
                waypointActual.y - object.getHeight() / 2
        );
        dirX = 0;
        dirY = 0; // ← poner en 0 para que no haya dirección activa
        dirDeseadaX = 0;
        dirDeseadaY = 0;
        enMovimiento = false;
        animacion.resetear();
        SistemaPuntos.resetearMultiplicador();
    }

    @Override
    public boolean isNoqueado() {
        return false; // Pac-Man no se noquea en este juego
    }

    @Override
    public void run() {
        /*  while (true) {
            if (pausado || !enMovimiento) {
                try {
                    Thread.sleep(16);
                } catch (Exception e) {
                }
                continue;
            }

            // Primero intenta la dirección deseada
            Waypoint siguiente = siguienteEn(waypointActual, dirDeseadaX, dirDeseadaY);

            if (siguiente != null) {
                dirX = dirDeseadaX;
                dirY = dirDeseadaY;
            } else {
                // Si no puede, sigue recto en la dirección actual
                siguiente = siguienteEn(waypointActual, dirX, dirY);
            }

            if (siguiente != null) {
                moverHacia(siguiente);
            } else {
                // Chocó con pared
                enMovimiento = false;
                animacion.quieto();
            }

            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }*/
        while (true) {
            // Bloquear si el juego no está en PLAYING
            if (PacmanPanel.estadoJuego != EstadoJuego.PLAYING
                    || pausado || !enMovimiento) {
                try {
                    Thread.sleep(16);
                } catch (Exception e) {
                }
                continue;
            }

            Waypoint siguiente = siguienteEn(waypointActual, dirDeseadaX, dirDeseadaY);
            if (siguiente != null) {
                dirX = dirDeseadaX;
                dirY = dirDeseadaY;
            } else {
                siguiente = siguienteEn(waypointActual, dirX, dirY);
            }

            if (siguiente != null) {
                moverHacia(siguiente);
            } else {
                enMovimiento = false;
                animacion.quieto();
            }

            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }

    }

}
