package Standard;

import Framework.Brick;
import Framework.GameConstants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StandardBrick implements Brick {

    private GameConstants type;
    private List<int[]> movePatterns;

    public StandardBrick(GameConstants type) {
        this.type = type;
        switch (type){
            case PAWN -> {
                movePatterns = new LinkedList<>(Arrays.asList(new int[]{1,0}));
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
}
