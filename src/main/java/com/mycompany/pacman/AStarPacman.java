package com.mycompany.pacman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class AStarPacman {

    /**
     * Calcula la ruta óptima desde un waypoint inicio hasta un waypoint objetivo
     * usando A* sobre el grafo de waypoints existente.
     *
     * @param inicio   waypoint de partida
     * @param objetivo waypoint destino (casa de fantasmas)
     * @return lista de waypoints en orden desde inicio hasta objetivo, o lista vacía si no hay ruta
     */
    public static List<Waypoint> calcularRuta(Waypoint inicio, Waypoint objetivo) {
        PriorityQueue<NodoAStarPacman> abierta = new PriorityQueue<>();
        Set<Integer> cerrada = new HashSet<>(); // ids de waypoints ya explorados

        NodoAStarPacman raiz = new NodoAStarPacman(inicio, null, 0, objetivo);
        abierta.add(raiz);

        while (!abierta.isEmpty()) {
            NodoAStarPacman actual = abierta.poll();

            // ¿Llegamos al objetivo?
            if (actual.waypoint.id == objetivo.id) {
                return reconstruirRuta(actual);
            }

            // Saltar si ya fue explorado
            if (cerrada.contains(actual.waypoint.id)) continue;
            cerrada.add(actual.waypoint.id);

            // Explorar conexiones del waypoint actual
            for (int idVecino : actual.waypoint.conexiones) {
                if (cerrada.contains(idVecino)) continue;

                Waypoint vecino = SistemaWaypoints.getWaypoint(idVecino);
                double distancia = Math.sqrt(
                    Math.pow(vecino.x - actual.waypoint.x, 2) +
                    Math.pow(vecino.y - actual.waypoint.y, 2)
                );
                double nuevoCosto = actual.costoReal + distancia;
                NodoAStarPacman sucesor = new NodoAStarPacman(vecino, actual, nuevoCosto, objetivo);
                abierta.add(sucesor);
            }
        }

        // No se encontró ruta
        System.out.println("A* no encontró ruta desde " + inicio.id + " hasta " + objetivo.id);
        return new ArrayList<>();
    }

    private static List<Waypoint> reconstruirRuta(NodoAStarPacman nodoFinal) {
        List<Waypoint> ruta = new ArrayList<>();
        NodoAStarPacman actual = nodoFinal;

        while (actual != null) {
            ruta.add(actual.waypoint);
            actual = actual.padre;
        }

        Collections.reverse(ruta);
        return ruta;
    }
}