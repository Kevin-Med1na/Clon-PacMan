package com.mycompany.pacman;

import java.util.ArrayList;
import java.util.List;

public class Waypoint {

    public int id, x, y;
    public boolean soloFantasmas;
    public boolean esPortal;
    public List<Integer> conexiones = new ArrayList<>();

    public Waypoint(int id, int x, int y, boolean soloFantasmas) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.soloFantasmas = soloFantasmas;
    }
}
