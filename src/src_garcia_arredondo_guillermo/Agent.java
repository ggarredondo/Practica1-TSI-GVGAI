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

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        n_gemas = 0;
        secuencia = new Stack<>();
        fescala = new Vector2d(stateObs.getWorldDimension().width/stateObs.getObservationGrid().length, stateObs.getWorldDimension().height/stateObs.getObservationGrid()[0].length);
        inicio = new Vector2d(Math.floor(stateObs.getAvatarPosition().x/fescala.x), Math.floor(stateObs.getAvatarPosition().y/fescala.y));
        Vector2d portal = stateObs.getPortalsPositions(stateObs.getAvatarPosition())[0].get(0).position;
        objetivo = new Vector2d(Math.floor(portal.x/fescala.x), Math.floor(portal.y/fescala.y));
        hay_gemas = stateObs.getResourcesPositions() != null;
        hay_enemigos = stateObs.getNPCPositions() != null;
        if (!hay_gemas && !hay_enemigos)
            AEstrella(stateObs, inicio, objetivo);
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS siguiente = Types.ACTIONS.ACTION_NIL;
        Vector2d semiobjetivo;
        TimeUnit time = TimeUnit.SECONDS;
        if (!secuencia.empty())
            siguiente = secuencia.pop();
        else if (hay_gemas && n_gemas < 9) {
            semiobjetivo = new Vector2d(inicio.x*fescala.x, inicio.y*fescala.y);
            semiobjetivo = stateObs.getResourcesPositions(semiobjetivo)[0].get(0).position;
            semiobjetivo.x /= fescala.x;
            semiobjetivo.y /= fescala.y;
            AEstrella(stateObs, inicio, semiobjetivo);
            n_gemas += 1;
            inicio = semiobjetivo;
        }
        else if (hay_gemas)
            AEstrella(stateObs, inicio, objetivo);
        return siguiente;
    }

    private boolean esObstaculo(Vector2d pos, StateObservation stateObs) {
        boolean result = false;
        if (hay_gemas && n_gemas < 9 && pos.equals(objetivo))
            result = true;
        else {
            pos = new Vector2d(pos.x * fescala.x, pos.y * fescala.y);
            Vector2d cuadrante = stateObs.getImmovablePositions(pos)[0].get(0).position;
            if (pos.equals(cuadrante))
                result = true;
        }
        return result;
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
                if (!esObstaculo(hijo.getPos(), stateObs) && !cerrados.contains(hijo))
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
