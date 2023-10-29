package Standard;

import Framework.Brick;
import Framework.GameConstants;
import Framework.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StandardBrick implements Brick {

    private GameConstants type;
    private List<int[]> movePatterns;
    private Player owner;

    public StandardBrick(Player owner,GameConstants type) {
        this.type = type;
        this.owner = owner ;
        switch (type){
            case PAWN -> {
                movePatterns = new LinkedList<>(Arrays.asList(new int[]{1,0}));
            }
            case KNIGHT -> {
                movePatterns = new LinkedList<>(Arrays.asList(new int[]{2,1},new int[]{2,-1}));
            }
        }
    }

    @Override
    public GameConstants getType() {
        return type;
    }

    @Override
    public List<int[]> getMovePatterns() {
        return movePatterns;
    }

    @Override
    public Player getPlayer() {
        return owner;
    }
}
