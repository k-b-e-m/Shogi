package Framework;

import java.util.List;

public interface Brick {

    GameConstants getType();


    List<int[]> getMovePatterns();

    Player getPlayer();

    Brick getPromoteBrick();

    void setOwner(Player player);
}
