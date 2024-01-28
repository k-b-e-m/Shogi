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
    private Brick brickUpgrade;

    public StandardBrick(Player owner,List<int[]> movePatterns, GameConstants type, Brick promotedVersionOfBrick) {
        this.type = type;
        this.owner = owner ;
        this.movePatterns = movePatterns;
        this.type = type;
        this.brickUpgrade = promotedVersionOfBrick;
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

    @Override
    public Brick getPromoteBrick() {
        return brickUpgrade == null ? this : brickUpgrade;
    }

    @Override
    public void setOwner(Player player) {
        this.owner = player;
    }

    @Override
    public String toString() {
        return  "" + type + " " + owner;
    }
}

