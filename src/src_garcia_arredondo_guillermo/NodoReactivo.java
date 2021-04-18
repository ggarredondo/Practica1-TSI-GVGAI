package src_garcia_arredondo_guillermo;

import ontology.Types;
import tools.Vector2d;

import java.util.ArrayList;

class NodoReactivo implements Comparable<NodoReactivo> {
    private Vector2d pos;
    private int f, g, h;
    private Types.ACTIONS accion;
    private int veces_accion;

    private int distanciaManhattam(double x1, double x2, double y1, double y2) { return (int) (Math.abs(x1-x2) + Math.abs(y1-y2)); }

    NodoReactivo(double x, double y, int g, Types.ACTIONS accion, int veces_accion, Vector2d enemigo) {
        this.pos = new Vector2d(x, y);
        this.g = g;
        this.h = distanciaManhattam(pos.x, enemigo.x, pos.y, enemigo.y);
        this.f = this.h - this.g;
        this.accion = accion;
        this.veces_accion = veces_accion;
    }

    Types.ACTIONS getAccion() { return accion; }
    int getVeces_accion() { return veces_accion; }
    Vector2d getPos() { return pos; }

    NodoReactivo hijoUP(Vector2d enemigo) {
        int turn = ((Types.ACTIONS.ACTION_UP != accion) ? 1 : 0);
        return new NodoReactivo(pos.x, pos.y-1, 1+turn, Types.ACTIONS.ACTION_UP, 1+turn, enemigo);
    }
    NodoReactivo hijoDOWN(Vector2d enemigo) {
        int turn = ((Types.ACTIONS.ACTION_DOWN != accion) ? 1 : 0);
        return new NodoReactivo(pos.x, pos.y+1, 1+turn, Types.ACTIONS.ACTION_DOWN, 1+turn, enemigo);
    }
    NodoReactivo hijoLEFT(Vector2d enemigo) {
        int turn = ((Types.ACTIONS.ACTION_LEFT != accion) ? 1 : 0);
        return new NodoReactivo(pos.x-1, pos.y, 1+turn, Types.ACTIONS.ACTION_LEFT, 1+turn, enemigo);
    }
    NodoReactivo hijoRIGHT(Vector2d enemigo) {
        int turn = ((Types.ACTIONS.ACTION_RIGHT != accion) ? 1 : 0);
        return new NodoReactivo(pos.x+1, pos.y, 1+turn, Types.ACTIONS.ACTION_RIGHT, 1+turn, enemigo);
    }

    public int compareTo(NodoReactivo n) { return n.f - this.f; }
}
