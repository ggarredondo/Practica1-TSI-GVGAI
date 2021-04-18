package src_garcia_arredondo_guillermo;

import ontology.Types;
import tools.Vector2d;

// Clase Nodo para el comportamiento reactivo.
class NodoReactivo implements Comparable<NodoReactivo> {
    private Vector2d pos; // Posición en el mapa del nodo.
    private int f, g, h; // Coste y heurística.
    private Types.ACTIONS accion; // Acción para llegar al nodo.
    private int veces_accion; // Veces que hay que realizar la Acción para llegar al nodo.

    private int distanciaManhattam(double x1, double x2, double y1, double y2) { return (int) (Math.abs(x1-x2) + Math.abs(y1-y2)); }

    // Se inicializan los atributos dados los parámetros del constructor y se calcula h y f según la posición del enemigo a evitar.
    NodoReactivo(double x, double y, int g, Types.ACTIONS accion, int veces_accion, Vector2d enemigo) {
        this.pos = new Vector2d(x, y);
        this.g = g;
        this.h = distanciaManhattam(pos.x, enemigo.x, pos.y, enemigo.y);
        this.f = this.h - this.g; // La distancia al enemigo menos el gasto que supone el movimiento.
        this.accion = accion;
        this.veces_accion = veces_accion;
    }

    // Varios métodos get para obtener atributos privados de la clase.
    Types.ACTIONS getAccion() { return accion; }
    int getVeces_accion() { return veces_accion; }
    Vector2d getPos() { return pos; }

    // Métodos para generar los hijos del nodo al expandirlo. Se comprueba si hay que girar para alcanzar el nodo y si es así
    // se suma 1 al gasto y al número de veces que hay que ejecutar la acción.
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

    // El operador para ordenar los nodos en la cola de prioridad de mayor a menor f en este caso, ya que buscamos maximizar
    // la distancia al enemigo.
    public int compareTo(NodoReactivo n) { return n.f - this.f; }
}
