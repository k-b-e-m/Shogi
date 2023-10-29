package Standard;

import Framework.Brick;
import Framework.GameConstants;

public class StandardBrick implements Brick {

    private GameConstants type;

    public StandardBrick(GameConstants type) {
        this.type = type;
    }

    @Override
    public GameConstants getType() {
        return type;
    }
}
