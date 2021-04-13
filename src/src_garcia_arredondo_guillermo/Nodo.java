package src_garcia_arredondo_guillermo;

import ontology.Types;
import tools.Vector2d;

public class Nodo implements Comparable<Nodo> {
    private int fil, col; // posición en el mapa
    private int f, g, h; // coste y heurística
    private Nodo padre;
    private Types.ACTIONS accion; // acción para llegar al nodo

    public Nodo(int fil, int col, int g, int h, Nodo padre) {
        this.g = g;
        this.h = h;
        this.padre = padre;
        f = this.g + this.h;
    }

    public int compareTo(Nodo n) {
        return this.f - n.f;
    }

    public boolean equals(Vector2d v) {
        return (this.fil == v.x) && (this.col == v.y);
    }
}
