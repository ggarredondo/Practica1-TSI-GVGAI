package src_garcia_arredondo_guillermo;

import ontology.Types;
import tools.Vector2d;

class Nodo implements Comparable<Nodo> {
    private Vector2d pos; // posición en el mapa
    private int f, g, h; // coste y heurística
    private Nodo padre;
    private Types.ACTIONS accion; // la acción de llegada al nodo

    private int distanciaManhattam(double x1, double x2, double y1, double y2) { return (int) (Math.abs(x1-x2) + Math.abs(y1-y2)); }

    Nodo(double x, double y, int g, Types.ACTIONS accion, Nodo padre, Vector2d objetivo) {
        this.pos = new Vector2d(x, y);
        this.g = g;
        this.h = distanciaManhattam(x, objetivo.x, y, objetivo.y);
        this.accion = accion;
        this.padre = padre;
        f = this.g + this.h;
    }

    @Override
    public boolean equals(Object o) { return pos.equals(((Nodo) o).pos) && accion == ((Nodo) o).accion; }

    public int compareTo(Nodo n) { return this.f - n.f; }

    boolean esObjetivo() { return h==0; }

    Vector2d getPos() { return pos; }
    Types.ACTIONS getAccion() { return accion; }
    Nodo getPadre() { return padre; }
    int getCoste() { return g; }
    int getHeuristica() { return h; }

    void setHeuristica(int h) {
        this.h = h;
        f = g + this.h;
    }

    Nodo hijoUP(Vector2d objetivo) { return new Nodo(pos.x, pos.y-((Types.ACTIONS.ACTION_UP == accion) ? 1 : 0), g+1, Types.ACTIONS.ACTION_UP, this, objetivo); }
    Nodo hijoDOWN(Vector2d objetivo) { return new Nodo(pos.x, pos.y+((Types.ACTIONS.ACTION_DOWN == accion) ? 1 : 0), g+1, Types.ACTIONS.ACTION_DOWN, this, objetivo); }
    Nodo hijoLEFT(Vector2d objetivo) { return new Nodo(pos.x-((Types.ACTIONS.ACTION_LEFT == accion) ? 1 : 0), pos.y,g+1, Types.ACTIONS.ACTION_LEFT, this, objetivo); }
    Nodo hijoRIGHT(Vector2d objetivo) { return new Nodo(pos.x+((Types.ACTIONS.ACTION_RIGHT == accion) ? 1 : 0), pos.y, g+1, Types.ACTIONS.ACTION_RIGHT, this, objetivo); }
}