package com.mycompany.pacman;

import java.util.*;

public class SistemaWaypoints {

    public static List<Waypoint> waypoints = new ArrayList<>();

    private static void add(int id, int x, int y) {
        waypoints.add(new Waypoint(id, x, y, false));
    }

    private static void add(int id, int x, int y, boolean soloFantasmas) {
        waypoints.add(new Waypoint(id, x, y, soloFantasmas));
    }

    private static void conectar(int id1, int id2) {
        waypoints.get(id1).conexiones.add(id2);
        waypoints.get(id2).conexiones.add(id1);
    }

    public static Waypoint getWaypoint(int id) {
        return waypoints.get(id);
    }

    public static void inicializar() {
        waypoints.clear();

        // ════════════════════════════════════════════
        // WAYPOINTS
        // ════════════════════════════════════════════
        // Zona 1 - y=45
        add(0, 55, 45);
        add(1, 195, 45);
        add(2, 360, 45);
        add(3, 445, 45);
        add(4, 605, 45);
        add(5, 745, 45);

        // Zona 2 - y=115
        add(6, 55, 115);
        add(7, 195, 115);
        add(8, 275, 115);
        add(9, 360, 115);
        add(10, 445, 115);
        add(11, 525, 115);
        add(12, 605, 115);
        add(13, 745, 115);

        // Zona 3 - y=165
        add(14, 55, 165);
        add(15, 195, 165);
        add(16, 275, 165);
        add(17, 360, 165);
        add(18, 445, 165);
        add(19, 525, 165);
        add(20, 605, 165);
        add(21, 745, 165);

        // Zona 4 - y=225
        add(22, 275, 225);
        add(23, 360, 225);
        add(24, 400, 225); // salida fantasmas
        add(25, 445, 225);
        add(26, 525, 225);

        // Zona 5 - y=280
        add(27, 11, 280);          // portal izquierdo
        add(28, 195, 280);
        add(29, 275, 280);
        add(30, 335, 280, true);    // casa fantasmas
        add(31, 400, 280, true);    // casa fantasmas
        add(32, 470, 280, true);    // casa fantasmas
        add(33, 525, 280);
        add(34, 605, 280);
        add(35, 790, 280);          // portal derecho

        // Zona 6 - y=340
        add(36, 275, 340);
        add(37, 525, 340);

        // Zona 7 - y=400
        add(38, 55, 400);
        add(39, 195, 400);
        add(40, 275, 400);
        add(41, 360, 400);
        add(42, 445, 400);
        add(43, 525, 400);
        add(44, 605, 400);
        add(45, 745, 400);

        // Zona 8 - y=450
        add(46, 55, 450);
        add(47, 115, 450);
        add(48, 195, 450);
        add(49, 275, 450);
        add(50, 360, 450);
        add(51, 445, 450);
        add(52, 525, 450);
        add(53, 605, 450);
        add(54, 685, 450);
        add(55, 745, 450);

        // Zona 9 - y=505
        add(56, 55, 505);
        add(57, 115, 505);
        add(58, 195, 505);
        add(59, 275, 505);
        add(60, 360, 505);
        add(61, 445, 505);
        add(62, 525, 505);
        add(63, 605, 505);
        add(64, 685, 505);
        add(65, 745, 505);

        // Zona 10 - y=560
        add(66, 55, 560);
        add(67, 360, 560);
        add(68, 445, 560);
        add(69, 745, 560);

        add(70, 400, 340);

        // Marcar portales
        waypoints.get(27).esPortal = true;
        waypoints.get(35).esPortal = true;

        // ════════════════════════════════════════════
        // CONEXIONES HORIZONTALES
        // ════════════════════════════════════════════
        // Zona 1
        conectar(0, 1);
        conectar(1, 2);
        conectar(3, 4);
        conectar(4, 5);

        // Zona 2 (todas conectadas)
        conectar(6, 7);
        conectar(7, 8);
        conectar(8, 9);
        conectar(9, 10);
        conectar(10, 11);
        conectar(11, 12);
        conectar(12, 13);

        // Zona 3
        conectar(14, 15);
        conectar(16, 17);
        conectar(18, 19);
        conectar(20, 21);

        // Zona 4
        conectar(22, 23);
        conectar(23, 24);
        conectar(24, 25);
        conectar(25, 26);

        // Zona 5
        conectar(27, 28);
        conectar(28, 29);
        conectar(30, 31);
        conectar(31, 32); // ghost only
        conectar(33, 34);
        conectar(34, 35);

        //Zona 6
        conectar(36, 70);
        conectar(70, 37);

        // Zona 7
        conectar(38, 39);
        conectar(39, 40);
        conectar(40, 41);
        conectar(42, 43);
        conectar(43, 44);
        conectar(44, 45);

        // Zona 8
        conectar(46, 47);
        conectar(48, 49);
        conectar(49, 50);
        conectar(50, 51);
        conectar(51, 52);
        conectar(52, 53);
        conectar(54, 55);

        // Zona 9
        conectar(56, 57);
        conectar(57, 58);
        conectar(59, 60);
        conectar(61, 62);
        conectar(63, 64);
        conectar(64, 65);

        // Zona 10
        conectar(66, 67);
        conectar(67, 68);
        conectar(68, 69);

        // ════════════════════════════════════════════
        // CONEXIONES VERTICALES
        // ════════════════════════════════════════════
        // Columna x=55
        conectar(0, 6);
        conectar(6, 14);
        // gap: 14 → 38 (hay pared entre y=165 y y=400)
        conectar(38, 46);
        // gap: 46 → 56 (hay pared entre y=450 y y=505)
        conectar(56, 66);

        // Columna x=115
        conectar(47, 57);

        // Columna x=195
        conectar(1, 7);
        conectar(7, 15);
        conectar(15, 28);
        conectar(28, 39);
        conectar(39, 48);
        conectar(48, 58);

        // Columna x=275
        conectar(8, 16);
        // gap: 16 → 22
        conectar(22, 29);
        conectar(29, 36);
        conectar(36, 40);
        // gap: 40 → 49
        conectar(49, 59);

        // Columna x=360
        conectar(2, 9);
        // gap: 17 → 23
        conectar(17, 23);
        // gap: 23 → 41
        conectar(41, 50);
        // gap: 50 → 60
        conectar(60, 67);

        // Columna x=400 (solo fantasmas: salida de la casa)
        conectar(24, 31);

        // Columna x=445
        conectar(3, 10);
        // gap: 10 → 18
        conectar(18, 25);
        // gap: 25 → 42
        conectar(42, 51);
        // gap: 51 → 61
        conectar(61, 68);

        // Columna x=525
        conectar(11, 19);
        // gap: 19 → 26
        conectar(26, 33);
        conectar(33, 37);
        conectar(37, 43);
        // gap: 43 → 52
        conectar(52, 62);

        // Columna x=605 (una sola columna sin gaps)
        conectar(4, 12);
        conectar(12, 20);
        conectar(20, 34);
        conectar(34, 44);
        conectar(44, 53);
        conectar(53, 63);

        // Columna x=685
        conectar(54, 64);

        // Columna x=745
        conectar(5, 13);
        conectar(13, 21);
        // gap: 21 → 45
        conectar(45, 55);
        // gap: 55 → 65
        conectar(65, 69);
    }

    // Waypoint más cercano
    public static Waypoint waypointMasCercano(int x, int y, boolean esPacman) {
        Waypoint masCercano = null;
        double minDist = Double.MAX_VALUE;

        for (Waypoint w : waypoints) {
            if (esPacman && w.soloFantasmas) {
                continue;
            }
            double dist = Math.sqrt(Math.pow(x - w.x, 2) + Math.pow(y - w.y, 2));
            if (dist < minDist) {
                minDist = dist;
                masCercano = w;
            }
        }
        return masCercano;
    }

    // Generar pellets entre waypoints conectados
    public static List<Pellet> generarPellets() {
        Set<Integer> zonaExcluida = new HashSet<>(Arrays.asList(36, 37, 70));
        List<Pellet> lista = new ArrayList<>();
        int separacion = 19;

        for (Waypoint w1 : waypoints) {
            for (int id2 : w1.conexiones) {
                if (id2 <= w1.id) {
                    continue;
                }

                Waypoint w2 = waypoints.get(id2);
                if (w1.soloFantasmas || w2.soloFantasmas) {
                    continue;
                }
                if (w1.esPortal || w2.esPortal) {
                    continue;
                }

                // ← Excluir conexiones que involucren zona de spawn
                if (zonaExcluida.contains(w1.id) || zonaExcluida.contains(w2.id)) {
                    continue;
                }

                double dx = w2.x - w1.x;
                double dy = w2.y - w1.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                int pasos = (int) (dist / separacion);

                for (int i = 1; i < pasos; i++) {
                    int px = (int) (w1.x + dx * i / pasos);
                    int py = (int) (w1.y + dy * i / pasos);
                    lista.add(new Pellet(px, py, false));
                }
            }

            // Pellet en el waypoint mismo — excluir zona de spawn
            if (!w1.soloFantasmas && !w1.esPortal && !zonaExcluida.contains(w1.id)) {
                lista.add(new Pellet(w1.x, w1.y, false));
            }
        }

        // Power pellets
        lista.add(new Pellet(55, 45, true));
        lista.add(new Pellet(745, 45, true));
        lista.add(new Pellet(55, 560, true));
        lista.add(new Pellet(745, 560, true));

        System.out.println("Pellets generados: " + lista.size());
        return lista;
    }
}
