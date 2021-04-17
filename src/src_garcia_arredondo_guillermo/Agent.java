package src_garcia_arredondo_guillermo;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class Agent extends AbstractPlayer {
    Stack<Types.ACTIONS> secuencia;
    Vector2d fescala;
    int n_gemas;
    boolean hay_gemas, hay_enemigos;
    Vector2d inicio, objetivo;
    ArrayList<Vector2d> obstaculos;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // Inicializar n_gemas, la pila de pasos, la escala, la posición inicio del avatar y la del portal
        n_gemas = 0;
        secuencia = new Stack<>();
        fescala = new Vector2d(stateObs.getWorldDimension().width/stateObs.getObservationGrid().length, stateObs.getWorldDimension().height/stateObs.getObservationGrid()[0].length);
        inicio = new Vector2d(Math.floor(stateObs.getAvatarPosition().x/fescala.x), Math.floor(stateObs.getAvatarPosition().y/fescala.y));
        Vector2d portal = stateObs.getPortalsPositions(stateObs.getAvatarPosition())[0].get(0).position;
        objetivo = new Vector2d(Math.floor(portal.x/fescala.x), Math.floor(portal.y/fescala.y));

        // Añadir los obstáculos del mapa a una lista de obstáculos para luego sortearlos
        obstaculos = new ArrayList<>();
        ArrayList<Observation> immovables = stateObs.getImmovablePositions(stateObs.getAvatarPosition())[0];
        for (Observation immovable : immovables)
            obstaculos.add(new Vector2d(immovable.position.x / fescala.x, immovable.position.y / fescala.y));

        // Comprobar el nivel de los 5 en el que nos encontramos
        hay_gemas = stateObs.getResourcesPositions() != null;
        hay_enemigos = stateObs.getNPCPositions() != null;
        if (!hay_gemas && !hay_enemigos)
            AEstrella(stateObs, inicio, objetivo);
    }

    // Para ralentizar el programa y depurar el camino obtenido
    private void sleep(long ms) {
        TimeUnit time = TimeUnit.MILLISECONDS;
        try { time.sleep(ms); }
        catch (InterruptedException e) { System.out.println("Interrupted while Sleeping"); }
    }

    private Vector2d getClosestGem(StateObservation stateObs, Vector2d inicio) {
        Vector2d pos = new Vector2d(inicio.x*fescala.x, inicio.y*fescala.y), min_v = new Vector2d();
        int min_d = Integer.MAX_VALUE, d;
        ArrayList<Observation> obs = stateObs.getResourcesPositions(pos)[0];
        for (int i = 0; i < obs.size() && i < 3; ++i) {
            pos = obs.get(i).position;
            pos.set(pos.x/fescala.x, pos.y/fescala.y);
            d = (int) (Math.abs(inicio.x-pos.x) + Math.abs(inicio.y-pos.y));
            if (d < min_d) {
                min_d = d;
                min_v = pos;
            }
        }
        return min_v;
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (stateObs.getAvatarResources().get(6) != null)
            n_gemas = stateObs.getAvatarResources().get(6);
        Vector2d semiobjetivo;
        Types.ACTIONS siguiente = Types.ACTIONS.ACTION_NIL;
        if (!secuencia.empty()) {
            siguiente = secuencia.pop();
        }
        else if (hay_gemas && n_gemas < 9) {
            semiobjetivo = getClosestGem(stateObs, inicio);
            AEstrella(stateObs, inicio, semiobjetivo);
            inicio = semiobjetivo;
            siguiente = secuencia.pop();
        }
        else if (hay_gemas) {
            AEstrella(stateObs, inicio, objetivo);
            siguiente = secuencia.pop();
        }
        return siguiente;
    }

    private void AEstrella(StateObservation stateObs, Vector2d inicio, Vector2d objetivo)
    {
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        ArrayList<Nodo> cerrados = new ArrayList<>();
        Stack<Nodo> sucesores = new Stack<>();
        Nodo actual = new Nodo(inicio.x, inicio.y, 0, stateObs.getAvatarLastAction(), null, objetivo), hijo;
        abiertos.add(actual);

        while (!abiertos.isEmpty() && !actual.esObjetivo()) {
            abiertos.remove(actual);
            cerrados.add(actual);
            sucesores.push(actual.hijoUP(objetivo));
            sucesores.push(actual.hijoDOWN(objetivo));
            sucesores.push(actual.hijoLEFT(objetivo));
            sucesores.push(actual.hijoRIGHT(objetivo));

            while (!sucesores.empty()) {
                hijo = sucesores.pop();
                if (!obstaculos.contains(hijo.getPos()) && !cerrados.contains(hijo))
                    abiertos.add(hijo);
            }

            actual = abiertos.peek();
        }
        if (actual.esObjetivo()) {
            while (actual.getPadre() != null) {
                secuencia.add(actual.getAccion());
                actual = actual.getPadre();
            }
        }
    }
}
