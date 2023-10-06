package objects;

import java.util.Arrays;
import java.util.Objects;

public class StateInfo {
    private final byte[][] state;
    private final boolean isBlackTurn;

    public StateInfo ( byte[][] state, boolean isBlackTurn ) {
        this.state = state;
        this.isBlackTurn = isBlackTurn;
    }

    @Override
    public boolean equals ( Object obj ) {
        if(this == obj) return true;
        if(!(obj instanceof StateInfo)) return false;
        StateInfo other = (StateInfo) obj;
        return (isBlackTurn == other.isBlackTurn) && (Arrays.deepEquals ( state, other.state ));
    }

    @Override
    public int hashCode () {
        return Objects.hash ( Arrays.deepHashCode ( state ), isBlackTurn);
    }
}
