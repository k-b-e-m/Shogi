import Framework.Brick;
import Framework.GameConstants;
import Framework.Status;
import Standard.StandardGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAlpha {

    private StandardGame game;

    @BeforeEach

    public void setUp(){
        game = new StandardGame();
    }

    @Test
    public void WhenPawnIsMovedOneUpItIsOkay(){
        //Given a game Where there is a pawn at position 1,1.
        game.addBrick(GameConstants.PAWN,1,1);
        Brick pawnAt1_1 = game.getBrickAtBoard(1,1);
        //When the brick is moved up one
        Status movedBrick = game.moveBrick(pawnAt1_1,0,1);
        //Then The move is allowed
        assertThat(movedBrick, is(Status.OK));
        //and the brick is moved one up
        assertThat(game.getBrickAtBoard(1,2).getType(), is(GameConstants.PAWN));
    }

    @Test
    public void shouldGiveStatusNotOkayWhenPawnIsMovedToSide(){
        //Given a a game with a pawn at position 1,1
        game.addBrick(GameConstants.PAWN,1,1);
        Brick pawn = game.getBrickAtBoard(1,1);
        //When the pawn is moved one to the side:
        Status brickMoved = game.moveBrick(pawn,1,0);
        //Then it is not allowed
        assertThat(brickMoved, is(Status.ILLEGAL_MOVE));
        //and the brick is not moved
        assertThat(game.getBrickAtBoard(2,1), nullValue());
    }

    @Test
    public void shouldGiveStatusOutOfBoardMoveWhenPawnTriesToMoveOverEdge(){
        //Given a game where the pawn is at position 1,8 (Edge of board)
        game.addBrick(GameConstants.PAWN,1,8);
        Brick brickAtBoard = game.getBrickAtBoard(1, 8);
        //When the brick is moved out the board
        Status movedBrick = game.moveBrick(brickAtBoard,0,1);
        //Then it gets status  Out_OF_BOARD_MOVE
        assertThat(movedBrick,is(Status.OUT_OF_BOARD_MOVE));
    }

}
