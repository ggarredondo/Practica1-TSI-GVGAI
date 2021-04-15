package src_garcia_arredondo_guillermo;

import ontology.Types;
import tools.Vector2d;

class Estado {
    Vector2d pos;
    Types.ACTIONS last;

    Estado(Vector2d pos, Types.ACTIONS last) {
        this.pos = pos;
        this.last = last;
    }

    public boolean equals(Estado e) {
        return pos.equals(e.pos) && last == e.last;
    }
}
