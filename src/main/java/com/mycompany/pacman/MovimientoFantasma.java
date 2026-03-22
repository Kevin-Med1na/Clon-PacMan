package com.mycompany.pacman;

import java.awt.Label;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JLabel;

public class MovimientoFantasma extends Thread implements Movible {

    Fantasma fantasma;
    JLabel pacman;
    Movible movPacman;
    Vidas vidas;

    // Velocidad
    int velocidadNormal = 2;
    int velocidadActual = 2;

    // Hilo del parpadeo
    Thread hiloPoder = null;

    // Waypoint de la casa para regresar
    static final int ID_WAYPOINT_CASA = 31; // ajusta según tu waypoint central de la casa

    int pacmanXInicial;
    int pacmanYInicial;

    int dirX = 1, dirY = 0;
    int velocidad = 2;
    boolean noqueado = false;
    Random random = new Random();

    boolean colisionando = false;
    boolean interrumpir = false;
    Waypoint waypointInicial;
    Waypoint waypointActual;
    static final int DISTANCIA_PERSECUCION = 200;

    public MovimientoFantasma(Fantasma fantasma, JLabel pacman, Waypoint waypointInicial) {
        this.waypointInicial = waypointInicial;
        this.fantasma = fantasma;
        this.pacman = pacman;
        this.waypointActual = waypointInicial;

        // Alinear fantasma con su waypoint inicial
        fantasma.label.setLocation(
                waypointInicial.x - fantasma.label.getWidth() / 2,
                waypointInicial.y - fantasma.label.getHeight() / 2
        );

        System.out.println("movPacman asignado: " + (movPacman != null));
    }

    public void setMovPacman(Movible movPacman) {
        this.movPacman = movPacman;
    }

    public void setVidas(Vidas vidas, int xInicial, int yInicial) {
        this.vidas = vidas;
        this.pacmanXInicial = xInicial;
        this.pacmanYInicial = yInicial;
    }

    @Override
    public boolean isNoqueado() {
        return noqueado;
    }

    public void noquear() {
        if (noqueado) {
            return;
        }
        noqueado = true;
        Colisiones.voltear(fantasma.label);
        new Thread(() -> {
            try {
                Thread.sleep(2500);
            } catch (Exception e) {
            }
            fantasma.label.setIcon(
                    (javax.swing.ImageIcon) fantasma.label.getClientProperty("iconOriginal")
            );
            noqueado = false;
        }).start();
    }

    // ── Distancia a Pac-Man ──────────────────────────────────
    private int distanciaAPacman() {
        int fx = fantasma.label.getLocation().x + fantasma.label.getWidth() / 2;
        int fy = fantasma.label.getLocation().y + fantasma.label.getHeight() / 2;
        int px = pacman.getLocation().x + pacman.getWidth() / 2;
        int py = pacman.getLocation().y + pacman.getHeight() / 2;
        return Math.abs(fx - px) + Math.abs(fy - py);
    }

    // ── Elegir siguiente waypoint ────────────────────────────
    private Waypoint elegirSiguiente() {
        switch (fantasma.estado) {
            case MUERTO -> {
                return null;
            } // regresarACasa lo maneja aparte
            case ASUSTADO -> {
                return aleatorio();
            } // siempre aleatorio
            default -> { // NORMAL
                if (distanciaAPacman() <= DISTANCIA_PERSECUCION) {
                    return perseguir();
                } else {
                    return aleatorio();
                }
            }
        }
    }

    private Waypoint perseguir() {
        int px = pacman.getLocation().x + pacman.getWidth() / 2;
        int py = pacman.getLocation().y + pacman.getHeight() / 2;

        Waypoint mejor = null;
        double menorDist = Double.MAX_VALUE;

        for (int id : waypointActual.conexiones) {
            Waypoint w = SistemaWaypoints.getWaypoint(id);

            double dist = Math.sqrt(Math.pow(w.x - px, 2) + Math.pow(w.y - py, 2));
            if (dist < menorDist) {
                menorDist = dist;
                mejor = w;
            }
        }
        return mejor;
    }

    private Waypoint aleatorio() {
        java.util.List<Integer> conexiones = waypointActual.conexiones;
        if (conexiones.isEmpty()) {
            return null;
        }

        java.util.List<Waypoint> opciones = new java.util.ArrayList<>();
        for (int id : conexiones) {
            Waypoint w = SistemaWaypoints.getWaypoint(id);
            // ← ya no filtra portales
            int difX = w.x - waypointActual.x;
            int difY = w.y - waypointActual.y;
            boolean esContrario = (dirX == 1 && difX < 0)
                    || (dirX == -1 && difX > 0)
                    || (dirY == 1 && difY < 0)
                    || (dirY == -1 && difY > 0);
            if (!esContrario) {
                opciones.add(w);
            }
        }

        if (opciones.isEmpty()) {
            for (int id : conexiones) {
                opciones.add(SistemaWaypoints.getWaypoint(id));
            }
        }

        return opciones.get(random.nextInt(opciones.size()));
    }

    // ── Mover hacia waypoint ─────────────────────────────────
    private void moverHacia(Waypoint destino) {
        int destinoX = destino.x - fantasma.label.getWidth() / 2;
        int destinoY = destino.y - fantasma.label.getHeight() / 2;

        int difX = destino.x - waypointActual.x;
        int difY = destino.y - waypointActual.y;
        if (difX > 0) {
            dirX = 1;
            dirY = 0;
        } else if (difX < 0) {
            dirX = -1;
            dirY = 0;
        } else if (difY > 0) {
            dirX = 0;
            dirY = 1;
        } else if (difY < 0) {
            dirX = 0;
            dirY = -1;
        }

        if (fantasma.estado == EstadoFantasma.MUERTO) {
            fantasma.actualizarSpriteOjos(dirX, dirY);
        } else {
            fantasma.actualizarSprite(dirX, dirY);
        }

        while (true) {
            // ← Sale inmediatamente si fue interrumpido
            if (interrumpir) {
                interrumpir = false;
                return;
            }

            if (noqueado || pausado) {
                try {
                    Thread.sleep(16);
                } catch (Exception e) {
                }
                continue;
            }

            int px = fantasma.label.getLocation().x;
            int py = fantasma.label.getLocation().y;

            int tolerancia = velocidadActual + 2;
            if (Math.abs(px - destinoX) <= tolerancia
                    && Math.abs(py - destinoY) <= tolerancia) {
                fantasma.label.setLocation(destinoX, destinoY);

                if (destino.esPortal) {
                    Waypoint otroPortal = destino.id == 27
                            ? SistemaWaypoints.getWaypoint(35)
                            : SistemaWaypoints.getWaypoint(27);
                    Waypoint despues = null;
                    for (int id : otroPortal.conexiones) {
                        Waypoint w = SistemaWaypoints.getWaypoint(id);
                        if (!w.soloFantasmas) {
                            despues = w;
                            break;
                        }
                    }
                    if (despues != null) {
                        dirX = despues.x > otroPortal.x ? 1 : -1;
                        dirY = 0;
                        fantasma.label.setLocation(
                                otroPortal.x - fantasma.label.getWidth() / 2,
                                otroPortal.y - fantasma.label.getHeight() / 2
                        );
                        waypointActual = otroPortal;
                        moverHacia(despues);
                        waypointActual = despues;
                    }
                } else {
                    waypointActual = destino;
                }
                break;
            }

            int nuevoX = px + (px < destinoX ? velocidadActual : px > destinoX ? -velocidadActual : 0);
            int nuevoY = py + (py < destinoY ? velocidadActual : py > destinoY ? -velocidadActual : 0);
            fantasma.label.setLocation(nuevoX, nuevoY);

            if (movPacman != null && Colisiones.hayColision(
                    fantasma.label, pacman, this, movPacman)) {
                if (!colisionando) {
                    colisionando = true;
                    if (fantasma.estado == EstadoFantasma.ASUSTADO) {
                        morir();
                    } else if (fantasma.estado == EstadoFantasma.NORMAL) {
                        if (vidas != null) {
                            vidas.perderVida(pacman, pacmanXInicial, pacmanYInicial);
                        }
                    }
                }
            } else {
                colisionando = false;
            }

            try {
                Thread.sleep(16);
            } catch (Exception e) {
            }
        }
    }

    // ── Activar estado asustado ──────────────────────────────
    public void asustar(int segundos) {
        // MUERTO no se asusta
        if (fantasma.estado == EstadoFantasma.MUERTO) {
            return;
        }

        fantasma.cambiarEstado(EstadoFantasma.ASUSTADO);
        velocidadActual = Math.max(1, velocidadNormal - 3); // reducir velocidad

        // Cancelar hilo anterior si existe
        if (hiloPoder != null) {
            hiloPoder.interrupt();
        }

        hiloPoder = new Thread(() -> {
            try {
                // Primeros 7 segundos: sprite azul fijo
                Thread.sleep(7000);

                // Últimos 3 segundos: parpadeo azul/blanco
                long fin = System.currentTimeMillis() + 3000;
                boolean azul = true;
                while (System.currentTimeMillis() < fin) {
                    if (fantasma.estado != EstadoFantasma.ASUSTADO) {
                        return;
                    }
                    fantasma.label.setIcon(azul // ← directo, sin label = fantasma.label
                            ? fantasma.spriteAsustadoAzul
                            : fantasma.spriteAsustadoBlanco);
                    azul = !azul;
                    Thread.sleep(300);
                }

                // Termina el efecto
                if (fantasma.estado == EstadoFantasma.ASUSTADO) {
                    fantasma.cambiarEstado(EstadoFantasma.NORMAL);
                    velocidadActual = velocidadNormal;
                    fantasma.actualizarSpriteNormal(dirX, dirY);
                }

            } catch (InterruptedException e) {
                // interrumpido por nueva power pellet
            }
        });
        hiloPoder.start();
    }

// Atributos nuevos
    int velocidadMuerto = 6; // considerablemente más rápido
    List<Waypoint> rutaCasa = new ArrayList<>();

// ── Atributos para estado MUERTO ─────────────────────────
    private int indiceRuta = 0;
    private boolean regresandoACasa = false;
    private long tiempoUltimoAvance = 0;
    private static final long TIMEOUT_MS = 5000; // máx 5s sin avanzar

    // ── Regreso a casa usando moverHacia existente ───────────
    private void ejecutarRegresoACasa() {
        if (rutaCasa.isEmpty()) {
            iniciarRegreso();
            if (rutaCasa.isEmpty()) {
                revivirEnCasa();
                return;
            }
        }

        // Recorrer la ruta waypoint por waypoint usando moverHacia que ya funciona
        for (int i = indiceRuta; i < rutaCasa.size(); i++) {
            if (fantasma.estado != EstadoFantasma.MUERTO) {
                return;
            }
            if (pausado) {
                i--;
                continue;
            } // esperar si está pausado

            Waypoint destino = rutaCasa.get(i);
            indiceRuta = i;
            tiempoUltimoAvance = System.currentTimeMillis();

            moverHacia(destino); // ← reusar el método que ya funciona
            waypointActual = destino;

            // Actualizar ojos según dirección
            fantasma.actualizarSpriteOjos(dirX, dirY);

            // ¿Llegó a la casa?
            if (destino.id == ID_WAYPOINT_CASA) {
                revivirEnCasa();
                return;
            }

            // Timeout entre waypoints
            if (System.currentTimeMillis() - tiempoUltimoAvance > TIMEOUT_MS) {
                System.out.println("Timeout entre waypoints, recalculando");
                rutaCasa.clear();
                indiceRuta = 0;
                iniciarRegreso();
                return;
            }
        }

        // Si terminó el for sin llegar a la casa
        System.out.println("Ruta completada sin llegar, recalculando");
        rutaCasa.clear();
        indiceRuta = 0;
        iniciarRegreso();
    }

// ── Morir ────────────────────────────────────────────────
    public void morir() {
        if (hiloPoder != null) {
            hiloPoder.interrupt();
            hiloPoder = null;
        }

        // ← Sumar puntos con multiplicador antes de cambiar estado
        int puntos = SistemaPuntos.sumarFantasma();
        System.out.println("Fantasma comido: +" + puntos);

        rutaCasa.clear();
        indiceRuta = 0;
        regresandoACasa = false;

        fantasma.cambiarEstado(EstadoFantasma.MUERTO);
        velocidadActual = velocidadMuerto;
        fantasma.actualizarSpriteOjos(dirX, dirY);
        calcularRutaACasa();
    }

// ── Iniciar regreso a casa ────────────────────────────────
    private void iniciarRegreso() {
        Waypoint objetivo = SistemaWaypoints.getWaypoint(ID_WAYPOINT_CASA);

        // Waypoint más cercano a la posición actual del fantasma
        int fx = fantasma.label.getLocation().x + fantasma.label.getWidth() / 2;
        int fy = fantasma.label.getLocation().y + fantasma.label.getHeight() / 2;
        Waypoint inicio = SistemaWaypoints.waypointMasCercano(fx, fy, false);

        if (inicio == null || objetivo == null) {
            System.out.println("ERROR: waypoint inicio u objetivo es null, reviviendo");
            revivirEnCasa();
            return;
        }

        // Si ya está en la casa
        if (inicio.id == objetivo.id) {
            revivirEnCasa();
            return;
        }

        rutaCasa = AStarPacman.calcularRuta(inicio, objetivo);

        if (rutaCasa.isEmpty()) {
            System.out.println("A* sin ruta, intentando desde waypointActual");
            rutaCasa = AStarPacman.calcularRuta(waypointActual, objetivo);
        }

        if (rutaCasa.isEmpty()) {
            System.out.println("ERROR FATAL: sin ruta a casa, reviviendo directamente");
            revivirEnCasa();
            return;
        }

        // Saltar el primer waypoint si ya estamos muy cerca de él
        indiceRuta = 0;
        if (rutaCasa.size() > 1) {
            double distPrimero = Math.sqrt(
                    Math.pow(fx - rutaCasa.get(0).x, 2)
                    + Math.pow(fy - rutaCasa.get(0).y, 2)
            );
            if (distPrimero < velocidadActual * 3) {
                indiceRuta = 1;
            }
        }

        regresandoACasa = true;
        tiempoUltimoAvance = System.currentTimeMillis();
    }

// ── Avanzar un paso hacia casa ────────────────────────────
// Este método se llama UNA vez por iteración del run()
    private void avanzarHaciaACasa() {
        if (!regresandoACasa || rutaCasa.isEmpty()) {
            iniciarRegreso();
            return;
        }

        // Timeout: si lleva demasiado tiempo sin avanzar, recalcular
        if (System.currentTimeMillis() - tiempoUltimoAvance > TIMEOUT_MS) {
            System.out.println("Timeout en regreso a casa, recalculando");
            rutaCasa.clear();
            regresandoACasa = false;
            iniciarRegreso();
            return;
        }

        // ¿Ya recorrió toda la ruta?
        if (indiceRuta >= rutaCasa.size()) {
            System.out.println("Ruta completada, reviviendo");
            revivirEnCasa();
            return;
        }

        Waypoint destino = rutaCasa.get(indiceRuta);

        int destinoX = destino.x - fantasma.label.getWidth() / 2;
        int destinoY = destino.y - fantasma.label.getHeight() / 2;
        int px = fantasma.label.getLocation().x;
        int py = fantasma.label.getLocation().y;

        int difX = destinoX - px;
        int difY = destinoY - py;

        // Tolerancia: si está suficientemente cerca del waypoint, avanzar al siguiente
        int tolerancia = velocidadActual + 2;
        if (Math.abs(difX) <= tolerancia && Math.abs(difY) <= tolerancia) {
            // Alinear exactamente al waypoint
            fantasma.label.setLocation(destinoX, destinoY);
            waypointActual = destino;
            tiempoUltimoAvance = System.currentTimeMillis();

            // ¿Llegó a la casa?
            if (destino.id == ID_WAYPOINT_CASA) {
                revivirEnCasa();
                return;
            }

            indiceRuta++;
            return;
        }

        // Mover hacia el waypoint actual de la ruta
        int nuevoX = px + (difX > 0 ? velocidadActual : difX < 0 ? -velocidadActual : 0);
        int nuevoY = py + (difY > 0 ? velocidadActual : difY < 0 ? -velocidadActual : 0);
        fantasma.label.setLocation(nuevoX, nuevoY);

        // Actualizar sprite de ojos según dirección
        int dx = difX > 0 ? 1 : difX < 0 ? -1 : 0;
        int dy = difY > 0 ? 1 : difY < 0 ? -1 : 0;
        if (dx != 0 || dy != 0) {
            fantasma.actualizarSpriteOjos(dx, dy);
        }
    }

// ── Revivir en la casa ────────────────────────────────────
    private void revivirEnCasa() {
        // Limpiar todo
        rutaCasa.clear();
        indiceRuta = 0;
        regresandoACasa = false;

        // Restaurar estado
        fantasma.cambiarEstado(EstadoFantasma.NORMAL);
        velocidadActual = velocidadNormal;
        waypointActual = SistemaWaypoints.getWaypoint(ID_WAYPOINT_CASA);

        // Posicionar exactamente en la casa
        fantasma.label.setLocation(
                waypointActual.x - fantasma.label.getWidth() / 2,
                waypointActual.y - fantasma.label.getHeight() / 2
        );

        // Restaurar sprite normal según última dirección
        fantasma.actualizarSpriteNormal(dirX, dirY);
    }

// ── Calcular ruta A* a la casa ───────────────────────────
    private void calcularRutaACasa() {
        Waypoint objetivo = SistemaWaypoints.getWaypoint(ID_WAYPOINT_CASA);

        // Waypoint más cercano al fantasma
        int fx = fantasma.label.getLocation().x + fantasma.label.getWidth() / 2;
        int fy = fantasma.label.getLocation().y + fantasma.label.getHeight() / 2;
        Waypoint inicio = SistemaWaypoints.waypointMasCercano(fx, fy, false);

        rutaCasa = AStarPacman.calcularRuta(inicio, objetivo);

        if (rutaCasa.isEmpty()) {
            System.out.println("A* sin ruta, reintentando desde waypointActual");
            rutaCasa = AStarPacman.calcularRuta(waypointActual, objetivo);
        }

        if (rutaCasa.isEmpty()) {
            System.out.println("ERROR: No se encontró ruta a casa, reviviendo en sitio");
            revivirEnCasa(); // fallback: revivir directamente
        }
    }

// ── Regresar a casa siguiendo ruta A* ───────────────────
    private void regresarACasa() {
        if (rutaCasa.isEmpty()) {
            calcularRutaACasa();
            return;
        }

        // Recorrer la ruta waypoint por waypoint
        for (int i = 0; i < rutaCasa.size(); i++) {
            if (fantasma.estado != EstadoFantasma.MUERTO) {
                return;
            }

            Waypoint destino = rutaCasa.get(i);

            // Saltar el primer waypoint si ya estamos cerca de él
            if (i == 0) {
                int fx = fantasma.label.getLocation().x + fantasma.label.getWidth() / 2;
                int fy = fantasma.label.getLocation().y + fantasma.label.getHeight() / 2;
                double dist = Math.sqrt(Math.pow(fx - destino.x, 2) + Math.pow(fy - destino.y, 2));
                if (dist < velocidadActual * 2) {
                    continue;
                }
            }

            moverHacia(destino);
            waypointActual = destino;

            // Actualizar sprite de ojos según dirección
            fantasma.actualizarSpriteOjos(dirX, dirY);

            // ¿Llegó a la casa?
            if (destino.id == ID_WAYPOINT_CASA) {
                revivirEnCasa();
                return;
            }
        }

        // Si recorrió toda la ruta pero no llegó a la casa
        if (fantasma.estado == EstadoFantasma.MUERTO) {
            System.out.println("Ruta completada sin llegar a casa, recalculando");
            calcularRutaACasa();
        }
    }

    boolean pausado = false;

    public void pausar() {
        interrumpir = true; // ← interrumpe moverHacia inmediatamente
        pausado = true;
    }

    public void resetearCompleto() {
        interrumpir = true; // ← interrumpe cualquier moverHacia activo

        if (hiloPoder != null) {
            hiloPoder.interrupt();
            hiloPoder = null;
        }

        rutaCasa.clear();
        indiceRuta = 0;
        regresandoACasa = false;
        velocidadActual = velocidadNormal;
        colisionando = false;

        fantasma.estado = EstadoFantasma.NORMAL;
        fantasma.actualizarSpriteNormal(dirX, dirY);
    }

    public void resetearSpawn() {
        resetearCompleto();

        waypointActual = waypointInicial; // ← waypoint correcto desde el inicio
        dirX = 1;
        dirY = 0;

        fantasma.label.setLocation(
                waypointInicial.x - fantasma.label.getWidth() / 2,
                waypointInicial.y - fantasma.label.getHeight() / 2
        );

        pausado = false;
    }

    public void reanudar() {
        interrumpir = false; // ← limpiar antes de reanudar
        pausado = false;
    }

    @Override
    public void run() {
        while (true) {
            if (pausado) {
                try {
                    Thread.sleep(16);
                } catch (Exception e) {
                }
                continue;
            }

            if (fantasma.estado == EstadoFantasma.MUERTO) {
                ejecutarRegresoACasa(); // ← usa moverHacia internamente
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                }
                continue;
            }

            Waypoint siguiente = elegirSiguiente();
            if (siguiente != null) {
                moverHacia(siguiente);
            }

            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }
    }
}
