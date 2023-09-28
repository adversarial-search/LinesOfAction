package objects;

public class StateGenerationDTO {
    byte [][]state;
    byte row;
    byte column;
    byte opponentPiece;
    byte playerPiece;

    public StateGenerationDTO(byte[][] state, byte row, byte column, byte opponentPiece, byte playerPiece) {
        this.state = state;
        this.row = row;
        this.column = column;
        this.opponentPiece = opponentPiece;
        this.playerPiece = playerPiece;
    }

    public byte[][] getState() {
        return state;
    }

    public byte getRow() {
        return row;
    }

    public byte getColumn() {
        return column;
    }

    public byte getOpponentPiece() {
        return opponentPiece;
    }

    public byte getPlayerPiece() {
        return playerPiece;
    }
}
