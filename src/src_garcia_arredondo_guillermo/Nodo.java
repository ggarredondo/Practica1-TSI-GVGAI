package src_garcia_arredondo_guillermo;

import ontology.Types;
import tools.Vector2d;

class Nodo implements Comparable<Nodo> {
    private Estado st; // posición en el mapa y orientación
    private int f, g, h; // coste y heurística
    private Nodo padre;

    private int distanciaManhattam(double x1, double x2, double y1, double y2) { return (int) (Math.abs(x1-x2) + Math.abs(y1-y2)); }

    Nodo(double x, double y, int g, Types.ACTIONS accion, Nodo padre, Vector2d objetivo) {
        st = new Estado(new Vector2d(x, y), accion);
        this.g = g;
        this.h = distanciaManhattam(x, objetivo.x, y, objetivo.y);
        this.padre = padre;
        f = this.g + this.h;
    }

    public int compareTo(Nodo n) { return this.f - n.f; }

    boolean esObjetivo() { return h==0; }

    Estado getSt() { return st; }
    Types.ACTIONS getAccion() { return st.last; }
    Nodo getPadre() { return padre; }

    Nodo hijoUP(Vector2d objetivo) { return new Nodo(st.pos.x, st.pos.y-((Types.ACTIONS.ACTION_UP == st.last) ? 1 : 0), g+1, Types.ACTIONS.ACTION_UP, this, objetivo); }
    Nodo hijoDOWN(Vector2d objetivo) { return new Nodo(st.pos.x, st.pos.y+((Types.ACTIONS.ACTION_DOWN == st.last) ? 1 : 0), g+1, Types.ACTIONS.ACTION_DOWN, this, objetivo); }
    Nodo hijoLEFT(Vector2d objetivo) { return new Nodo(st.pos.x-((Types.ACTIONS.ACTION_LEFT == st.last) ? 1 : 0), st.pos.y,g+1, Types.ACTIONS.ACTION_LEFT, this, objetivo); }
    Nodo hijoRIGHT(Vector2d objetivo) { return new Nodo(st.pos.x+((Types.ACTIONS.ACTION_RIGHT == st.last) ? 1 : 0), st.pos.y, g+1, Types.ACTIONS.ACTION_RIGHT, this, objetivo); }
}