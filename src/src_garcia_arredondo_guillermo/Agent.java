package src_garcia_arredondo_guillermo;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

public class Agent extends AbstractPlayer {
    private ArrayDeque<Types.ACTIONS> secuencia; // Cola de acciones a seguir por el agente.
    private Vector2d fescala; // Escala del mapa para hacer los cálculos de las posiciones de píxeles a grid.
    private int n_gemas; // Número de gemas.
    private final int max_gemas = 9; // Número de gemas que necesitamos.
    private Types.ACTIONS orientacion; // Orientación del agente, dada siempre por la última acción.
    private boolean hay_gemas, hay_enemigos; // Booleanos que comprueban si hay gemas o enemigos para saber el nivel en el que nos encontramos.
    private Vector2d inicio, objetivo; // Posición inicial del agente y del objetivo a alcanzar.
    private ArrayList<Vector2d> obstaculos, gemas; // Listas de obstáculos a evitar y gemas que conseguir.

    // Constructor de Agent.
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Inicializar n_gemas, la orientación (el agente empieza mirando a la derecha), la pila de pasos, la escala, la posición inicio del avatar y la del portal.
        n_gemas = 0;
        orientacion = Types.ACTIONS.ACTION_RIGHT;
        secuencia = new ArrayDeque<>();
        fescala = new Vector2d(stateObs.getWorldDimension().width/stateObs.getObservationGrid().length, stateObs.getWorldDimension().height/stateObs.getObservationGrid()[0].length);
        inicio = new Vector2d(Math.floor(stateObs.getAvatarPosition().x/fescala.x), Math.floor(stateObs.getAvatarPosition().y/fescala.y));
        Vector2d portal = stateObs.getPortalsPositions(stateObs.getAvatarPosition())[0].get(0).position;
        objetivo = new Vector2d(Math.floor(portal.x/fescala.x), Math.floor(portal.y/fescala.y));

        // Añadir los obstáculos del mapa a una lista de obstáculos para luego sortearlos en el A*.
        obstaculos = new ArrayList<>();
        ArrayList<Observation> immovables = stateObs.getImmovablePositions(stateObs.getAvatarPosition())[0];
        for (Observation immovable : immovables)
            obstaculos.add(new Vector2d(immovable.position.x / fescala.x, immovable.position.y / fescala.y));

        // Comprobar si hay gemas o hay enemigos para distinguir el nivel en el que nos encontramos.
        hay_gemas = stateObs.getResourcesPositions() != null;
        hay_enemigos = stateObs.getNPCPositions() != null;
        if (!hay_gemas && !hay_enemigos)
            AEstrella(inicio, objetivo, orientacion); // Si no hay gemas ni enemigos, se llama a A* para iniciar el comportamiento deliberativo simple.
        else if (hay_gemas) {
            // Si hay gemas, añadimos sus posiciones en una lista para luego llevar cuenta de las gemas conseguidas en el camino obtenido por A*.
            gemas = new ArrayList<>();
            ArrayList<Observation> gems = stateObs.getResourcesPositions()[0];
            for (Observation gem : gems)
                gemas.add(new Vector2d(gem.position.x/fescala.x, gem.position.y/fescala.y));
            // Y se llama a Greedy para iniciar el comportamiento deliberativo compuesto.
            Greedy(stateObs);
        }
    }

    // Función principal act que devuelve la siguiente acción a realizar.
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS siguiente = Types.ACTIONS.ACTION_NIL; // Se inicializa la acción 'siguiente' a nil.
        if (!secuencia.isEmpty()) // Si la secuencia no está vacía, se saca la primera acción.
            siguiente = secuencia.pollFirst();
        else if (hay_enemigos) { // Si la secuencia está vacía y hay enemigos, se planifica para evitarlos y se saca la primera acción.
            Esquivar(stateObs);
            siguiente = secuencia.pollFirst();
        }
        return siguiente;
    }

    // Función principal para el comportamiento reactivo. En vez de la propuesta de mapas de calor, decidí implementar una
    // función que devolviese la acción más viable en un instante dado para mantenerse alejado del enemigo, inspirándome en A*.
    // Para ello, lo que 'Esquivar' hace es generar un nodo de la posición actual y obtener de él cuatro nodos hijos
    // dadas las cuatro direcciones principales (UP, DOWN, LEFT, RIGHT). Estos nodos hijos vienen con un coste asociado al
    // número de movimientos que tienen que hacer para desplazarse (2 si tienen que gastar un tick en girar, 1 si no) y una
    // heurística dada por la distancia manhattam al enemigo más cercano. A diferencia de A*, aquí lo que se busca es maximizar
    // f, que es dado por h - g, con tal de maximizar la distancia a ese enemigo más cercano. Dado ese f, se introducen
    // los cuatro nodos sucesores en una cola de prioridad ordenados de manera descendente y se escoge el primero.
    private void Esquivar(StateObservation stateObs)
    {
        // Obtenemos las posiciones ordenadas por cercanía al avatar y las introducimos en la lista de obstáculos
        // para asegurarnos que el avatar no se lanza hacia uno intentando evitar el otro.
        ArrayList<Observation> npcs = stateObs.getNPCPositions(new Vector2d(inicio.x*fescala.x, inicio.y*fescala.y))[0];
        ArrayList<Vector2d> enemigos = new ArrayList<>();
        for (Observation npc : npcs) enemigos.add(new Vector2d(npc.position.x / fescala.x, npc.position.y / fescala.y));
        obstaculos.addAll(enemigos);

        // Se genera el nodo raíz en la posición donde se encuentra el avatar y dado el enemigo más cercano.
        // Se inicializa la pila de sucesores y la cola de prioridad.
        NodoReactivo nodo = new NodoReactivo(inicio.x, inicio.y, 0, orientacion, 0, enemigos.get(0)), escogido;
        Stack<NodoReactivo> sucesores = new Stack<>();
        PriorityQueue<NodoReactivo> abiertos = new PriorityQueue<>();

        // Se generan los sucesores
        sucesores.push(nodo.hijoUP(enemigos.get(0)));
        sucesores.push(nodo.hijoDOWN(enemigos.get(0)));
        sucesores.push(nodo.hijoLEFT(enemigos.get(0)));
        sucesores.push(nodo.hijoRIGHT(enemigos.get(0)));
        // Se introducen en la pila todos los sucesores cuya posición sea alcanzable (no sea obstáculo).
        while (!sucesores.empty()) {
            nodo = sucesores.pop();
            if (!obstaculos.contains(nodo.getPos()))
                abiertos.add(nodo);
        }
        obstaculos.removeAll(enemigos);

        // Se escoge el nodo que maximice f, se actualiza inicio a la nueva posición y se añade a secuencia los pasos a seguir para llegar
        // a esa nueva posición (ya que puede ser que necesitemos hacer dos pasos para llegar, que sean girar y desplazarnos).
        escogido = abiertos.peek();
        inicio = escogido.getPos();
        orientacion = escogido.getAccion();
        secuencia.clear(); // Se limpia 'secuencia' por si se ha interrumpido el trayecto durante el comportamiento deliberativo-reactivo.
        for (int i = 0; i < escogido.getVeces_accion(); i++)
            secuencia.addFirst(escogido.getAccion());

    }

    // Devuelve la posición de la gema más cercana al avatar por distancia manhattam.
    private Vector2d getClosestGem(Vector2d start) {
        int d, min = Integer.MAX_VALUE;
        Vector2d min_v = null;
        for (Vector2d gema : gemas) {
            d = (int) (Math.abs(start.x - gema.x) + Math.abs(start.y - gema.y));
            if (d < min) { // Se guarda la posición cuya distancia al avatar sea mínima.
                min = d;
                min_v = gema;
            }
        }
        return min_v;
    }

    // Función que planifica el camino para obtener todas las gemas.
    private void Greedy(StateObservation stateObs)
    {
        ArrayDeque<Types.ACTIONS> total = new ArrayDeque<>(); // Se inicializa una cola a la que se le irá introduciendo la secuencia sucesiva de una gema a otra.
        Vector2d semiobjetivo;
        while (n_gemas < max_gemas) { // Mientras no hayamos obtenido el número de gemas necesitado.
            semiobjetivo = getClosestGem(inicio); // Escogemos la gema más cercana a la posición inicio (inicializado a la posición del avatar).
            AEstrella(inicio, semiobjetivo, orientacion); // Se calcula con A* el camino a esa gema desde inicio.
            total.addAll(secuencia); // Se añade la secuencia obtenida entera al final de la cola 'total'. Importante que sea al final, porque sino perdemos el orden.
            secuencia.clear(); // Se limpia la cola de 'secuencia'. Esto es para evitar que se desordenen las acciones, ya que en A* se añaden al principio de la cola.
            inicio = semiobjetivo; // Se actualiza inicio al semiobjetivo que supuestamente hemos cumplido.
            orientacion = total.peekLast(); // Actualizamos la orientación a la última acción realizada.
        }
        AEstrella(inicio, objetivo, orientacion); // Una vez recogidas todas las gemas, se planifica el camino al portal.
        total.addAll(secuencia); // Se añade la secuencia obtenida entera al final de la cola 'total'.
        orientacion = total.peekLast(); // Se actualiza la orientación a la última acción realizada.
        secuencia = total; // Asignamos el plan de acciones completo ya obtenido a la cola 'secuencia'.
    }

    // Función A* simple. No actualiza padres ni sucesores de abiertos porque consideré que el juego no era lo suficientemente
    // complejo como para necesitarlo y que solo iba a lastrar el tiempo de ejecución (sobre todo a la hora de replanificar).
    // Nota: se utiliza otra clase nodo porque la representación del problema es distinta. En vez de incrementar el coste si
    // hay que girar, se tiene el giro como un nodo en sí mismo (además de la posición).
    private void AEstrella(Vector2d inicio, Vector2d objetivo, Types.ACTIONS ultima)
    {
        // Se inicializa una cola de prioridad abiertos, una lista de cerrados, una pila de sucesores y el nodo actual
        // al nodo raíz donde comenzará el camino.
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        ArrayList<Nodo> cerrados = new ArrayList<>();
        Stack<Nodo> sucesores = new Stack<>();
        Nodo actual = new Nodo(inicio.x, inicio.y, 0, ultima, null, objetivo), hijo;
        abiertos.add(actual); // Se añade el nodo raíz a abiertos.

        // Mientras abiertos tenga elementos y el nodo actual no sea objetivo.
        while (!abiertos.isEmpty() && !actual.esObjetivo()) {
            abiertos.remove(actual); // Se elimina el nodo actual de abiertos.
            cerrados.add(actual); // Se añade el nodo actual a cerrados.
            // Se expande el nodo actual.
            sucesores.push(actual.hijoUP(objetivo));
            sucesores.push(actual.hijoDOWN(objetivo));
            sucesores.push(actual.hijoLEFT(objetivo));
            sucesores.push(actual.hijoRIGHT(objetivo));

            while (!sucesores.empty()) { // Mientras haya sucesores en la pila.
                hijo = sucesores.pop(); // Cogemos el primer sucesor de la pila.
                // Si su posición es accesible (no hay obstáculo) y no se encuentra en cerrados, se añade a abiertos.
                if (!obstaculos.contains(hijo.getPos()) && !cerrados.contains(hijo))
                    abiertos.add(hijo);
            }

            actual = abiertos.peek(); // Se escoge el mejor nodo de abiertos (que es el primero de la cola de prioridad).
        }
        if (actual.esObjetivo()) { // Si el nodo actual es objetivo.
            // Mientras actual no sea el nodo raíz, obtenemos la acción del nodo y pasamos al padre.
            while (actual.getPadre() != null) {
                secuencia.addFirst(actual.getAccion());
                // Además, si en el mapa hay gemas, comprobamos que la posición del nodo contiene gema
                // (si está en la lista gemas) y si es así borramos esa gema de la lista y sumamos 1 al número de gemas.
                if (hay_gemas) {
                    if (gemas.contains(actual.getPos())) {
                        gemas.remove(actual.getPos());
                        n_gemas++;
                    }
                }
                actual = actual.getPadre();
            }
        }
    }
}
