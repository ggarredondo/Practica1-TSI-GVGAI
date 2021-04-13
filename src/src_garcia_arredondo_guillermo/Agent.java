package src_garcia_arredondo_guillermo;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

public class Agent extends AbstractPlayer {
    Stack<Nodo> secuencia;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        secuencia = new Stack<>();
    }

    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return Types.ACTIONS.ACTION_NIL;
    }

    public void AEstrella(StateObservation stateObs)
    {
        PriorityQueue<Nodo> abiertos = new PriorityQueue<>();
        ArrayList<Nodo> cerrados = new ArrayList<>();
    }
}
