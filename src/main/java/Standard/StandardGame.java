package Standard;

import Framework.Brick;
import Framework.Game;
import Framework.GameConstants;

import java.util.List;

public class StandardGame implements Game {
    private Brick[][] board;

    public StandardGame() {
        this.board = new Brick[9][9];
    }

    @Override
    public Brick[][] getBoard() {
        return new Brick[0][];
    }

    @Override
    public Brick getBrickAtBoard(int x, int y) {
        return board[y][x];
    }

    @Override
    public List<Brick> getBricksAtTable() {
        return null;
    }

    @Override
    public void moveBrick(Brick brick, int Deltax, int Deltay) {
        int currentX = -1;
        int currentY = -1;
        for(int xi = 0; xi< board[0].length;xi++){
            for(int yi = 0;yi<board.length;yi++){
                if(board[yi][xi]!= null &&board[yi][xi].equals(brick)){
                    board[yi][xi] = null;
                    currentY = yi;
                    currentX = xi;
                }
            }
        }
        board[currentY+Deltay ][currentX+Deltax] = brick;
    }

    @Override
    public void addBrick(GameConstants typeOfBrick, int x, int y) {
        Brick newBrick = new StandardBrick(typeOfBrick);
        board[y][x] = newBrick;
    }
}
