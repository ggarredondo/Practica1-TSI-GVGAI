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

public class Agent extends AbstractPlayer {
    Stack<Types.ACTIONS> secuencia;
    Vector2d fescala;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        secuencia = new Stack<>();
        fescala = new Vector2d(stateObs.getWorldDimension().width/stateObs.getObservationGrid().length, stateObs.getWorldDimension().height/stateObs.getObservationGrid()[0].length);
        Vector2d inicio = new Vector2d(Math.floor(stateObs.getAvatarPosition().x/fescala.x), Math.floor(stateObs.getAvatarPosition().y/fescala.y));
        Vector2d portal = stateObs.getPortalsPositions(stateObs.getAvatarPosition())[0].get(0).position;
        Vector2d objetivo = new Vector2d(Math.floor(portal.x/fescala.x), Math.floor(portal.y/fescala.y));
        AEstrella(stateObs, inicio, objetivo);
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS siguiente = Types.ACTIONS.ACTION_NIL;
        if (!secuencia.empty())
            siguiente = secuencia.pop();
        return siguiente;
    }

    private boolean esObstaculo(Vector2d pos, StateObservation stateObs) {
        boolean result = false;
        pos = new Vector2d(pos.x * fescala.x, pos.y * fescala.y);
        Vector2d cuadrante = stateObs.getImmovablePositions(pos)[0].get(0).position;
        if (pos.x == cuadrante.x && pos.y == cuadrante.y)
            result = true;
        return result;
    }

    private void AEstrella(StateObservation stateObs, Vector2d inicio, Vector2d objetivo)
    {
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        ArrayList<Nodo> cerrados = new ArrayList<>();
        Nodo actual = new Nodo(inicio.x, inicio.y, 0, Types.ACTIONS.ACTION_RIGHT, null, objetivo), hijo;
        abiertos.add(actual);

        while (!abiertos.isEmpty() && !actual.esObjetivo()) {
            abiertos.remove(actual);
            cerrados.add(actual);

            hijo = actual.hijoUP(objetivo);
            if (!esObstaculo(hijo.getPos(), stateObs) && !cerrados.contains(hijo))
                abiertos.add(hijo);

            hijo = actual.hijoDOWN(objetivo);
            if (!esObstaculo(hijo.getPos(), stateObs) && !cerrados.contains(hijo))
                abiertos.add(hijo);

            hijo = actual.hijoLEFT(objetivo);
            if (!esObstaculo(hijo.getPos(), stateObs) && !cerrados.contains(hijo))
                abiertos.add(hijo);

            hijo = actual.hijoRIGHT(objetivo);
            if (!esObstaculo(hijo.getPos(), stateObs) && !cerrados.contains(hijo))
                abiertos.add(hijo);

            actual = abiertos.peek();
        }
        if (actual.esObjetivo()) {
            while (actual != null) {
                secuencia.add(actual.getAccion());
                actual = actual.getPadre();
            }
        }
    }
}
