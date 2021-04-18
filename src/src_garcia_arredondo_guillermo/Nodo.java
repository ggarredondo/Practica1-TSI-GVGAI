package src_garcia_arredondo_guillermo;

import ontology.Types;
import tools.Vector2d;

// Clase Nodo para A*.
class Nodo implements Comparable<Nodo> {
    private Vector2d pos; // Posición en el mapa del nodo.
    private int f, g, h; // Coste y heurística.
    private Nodo padre;
    private Types.ACTIONS accion; // La acción de llegada al nodo.

    private int distanciaManhattam(double x1, double x2, double y1, double y2) { return (int) (Math.abs(x1-x2) + Math.abs(y1-y2)); }

    // Se inicializan los atributos dados los parámetros del constructor y se calcula h y f según el objetivo.
    Nodo(double x, double y, int g, Types.ACTIONS accion, Nodo padre, Vector2d objetivo) {
        this.pos = new Vector2d(x, y);
        this.g = g;
        this.h = distanciaManhattam(x, objetivo.x, y, objetivo.y);
        this.accion = accion;
        this.padre = padre;
        f = this.g + this.h;
    }

    // Sobrecarga de 'equals' para comprobar si un nodo se encuentra en cerrados usando contains de ArrayList pero solo
    // comprobando que la posición y la acción sean iguales.
    @Override
    public boolean equals(Object o) { return pos.equals(((Nodo) o).pos) && accion == ((Nodo) o).accion; }

    // El operador para ordenar los Nodos en abiertos de menor a mayor f.
    public int compareTo(Nodo n) { return this.f - n.f; }

    // Si la heurística es 0, es que tenemos el nodo objetivo.
    boolean esObjetivo() { return h==0; }

    // Varios métodos get para obtener atributos privados de la clase.
    Vector2d getPos() { return pos; }
    Types.ACTIONS getAccion() { return accion; }
    Nodo getPadre() { return padre; }

    // Métodos para generar los nodos hijo al expandir el nodo, en los que se comprueba si hay desplazamiento comparando la acción del padre
    // con la acción para llegar al hijo.
    Nodo hijoUP(Vector2d objetivo) { return new Nodo(pos.x, pos.y-((Types.ACTIONS.ACTION_UP == accion) ? 1 : 0), g+1, Types.ACTIONS.ACTION_UP, this, objetivo); }
    Nodo hijoDOWN(Vector2d objetivo) { return new Nodo(pos.x, pos.y+((Types.ACTIONS.ACTION_DOWN == accion) ? 1 : 0), g+1, Types.ACTIONS.ACTION_DOWN, this, objetivo); }
    Nodo hijoLEFT(Vector2d objetivo) { return new Nodo(pos.x-((Types.ACTIONS.ACTION_LEFT == accion) ? 1 : 0), pos.y,g+1, Types.ACTIONS.ACTION_LEFT, this, objetivo); }
    Nodo hijoRIGHT(Vector2d objetivo) { return new Nodo(pos.x+((Types.ACTIONS.ACTION_RIGHT == accion) ? 1 : 0), pos.y, g+1, Types.ACTIONS.ACTION_RIGHT, this, objetivo); }
}