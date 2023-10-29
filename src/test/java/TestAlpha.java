import Framework.Brick;
import Framework.Game;
import Framework.GameConstants;
import Standard.StandardGame;
import org.junit.jupiter.api.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAlpha {

    @Test
    public void WhenPawnIsMovedOneUpItIsOkay(){
        Game game = new StandardGame();
        game.addBrick(GameConstants.PAWN,1,1);
        Brick pawnAt1_1 = game.getBrickAtBoard(1,1);
        game.moveBrick(pawnAt1_1,0,1);
        assertThat(game.getBrickAtBoard(1,2).getType(), is(GameConstants.PAWN));
    }

}
