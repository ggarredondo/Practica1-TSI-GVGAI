package src_garcia_arredondo_guillermo;

import ontology.Types;

public class Nodo implements Comparable<Nodo> {
    private int f, g, h;
    private int x, y;
    private Nodo padre;
    private Types.ACTIONS accion;

    public Nodo(int g, int h, Nodo padre) {
        this.g = g;
        this.h = h;
        this.padre = padre;
        f = this.g + this.h;
    }

    public int compareTo(Nodo n) {
        return this.f - n.f;
    }
}
