package Standard;

import Framework.Brick;
import Framework.Game;
import Framework.GameConstants;
import Framework.Status;

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
    public Status moveBrick(Brick brick, int Deltax, int Deltay) {
        int[] currentCords = findBrickCords(brick);
        int currentY =currentCords[0];
        int currentX = currentCords[1];
        Status legalMove = legalMove(brick,Deltax,Deltay);
        if(!(legalMove == Status.OK)){
            return legalMove;
        }
        moveBrickOnBoard(brick, Deltax, Deltay, currentY, currentX);
        return legalMove;
    }

    private void moveBrickOnBoard(Brick brick, int Deltax, int Deltay, int currentY, int currentX) {
        board[currentY][currentX] = null;
        board[currentY + Deltay][currentX + Deltax] = brick;
    }

    private int[] findBrickCords(Brick brick) {
        int[] currentCoords = new int[2];
        for(int xi = 0; xi< board[0].length;xi++){
            for(int yi = 0;yi<board.length;yi++){
                if(board[yi][xi]!= null &&board[yi][xi].equals(brick)){
                    currentCoords[0] = yi;
                    currentCoords[1] = xi;
                }
            }
        }
        return currentCoords;
    }

    private Status legalMove(Brick brick, int deltax, int deltay) {
        //Checking whether the move is withing the board
        int[] currentCords = findBrickCords(brick);
        if(currentCords[0]+deltay> 8 || currentCords[1]+deltax>8){
            return Status.OUT_OF_BOARD_MOVE;
        }
        //For Checking MovingPattern
        List<int[]> movePatterns = brick.getMovePatterns();
        for(int[] movepattern: movePatterns){
            if(movepattern[0]==deltay && movepattern[1]==deltax){
                return Status.OK;
            }
        }
        return Status.ILLEGAL_MOVE;
    }

    @Override
    public void addBrick(GameConstants typeOfBrick, int x, int y) {
        Brick newBrick = new StandardBrick(typeOfBrick);
        board[y][x] = newBrick;
    }
}
