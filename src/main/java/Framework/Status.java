package Framework;

import org.hamcrest.Matcher;

public enum Status {
    OK, ILLEGAL_MOVE, OUT_OF_BOARD_MOVE, Field_Occupied_By_Friend, Field_Occupied, Pawn_In_Collumn, MOVE_BLOCKED_BY_PIECE, PUTS_ONESELF_IN_CHECK, PLAYER_NOT_IN_TURN;
}
