/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pacman;


public class NodoAStarPacman implements Comparable<NodoAStarPacman>{
     public Waypoint waypoint;
    public NodoAStarPacman padre;
    public double costoReal;    // g(n)
    public double heuristica;  // h(n)
    public double f;           // f(n) = g(n) + h(n)

    public NodoAStarPacman(Waypoint waypoint, NodoAStarPacman padre, double costoReal, Waypoint objetivo) {
        this.waypoint  = waypoint;
        this.padre     = padre;
        this.costoReal = costoReal;
        this.heuristica = distanciaEuclidiana(waypoint, objetivo);
        this.f          = costoReal + heuristica;
    }

    private double distanciaEuclidiana(Waypoint a, Waypoint b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    @Override
    public int compareTo(NodoAStarPacman otro) {
        return Double.compare(this.f, otro.f);
    }
}
