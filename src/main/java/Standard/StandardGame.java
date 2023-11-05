package Standard;

import Framework.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StandardGame implements Game {
    private Brick[][] board;
    private HashMap<Player,List<Brick>> table;

    public StandardGame() {
        this.board = new Brick[9][9];
        table = new HashMap<>();
        table.put(Player.RICK,new LinkedList<Brick>());
        table.put(Player.MORTY,new LinkedList<Brick>());
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
    public List<Brick> getBricksAtTable(Player who) {
        return table.get(Player.RICK);
    }

    @Override
    public Brick getBrickAtTable(int index) {
        return table.get(Player.RICK).get(index);
    }

    @Override
    public Status moveBrick(Brick brick, int deltax, int deltay) {

        int[] currentCords = findBrickCords(brick);
        int currentY =currentCords[0];
        int currentX = currentCords[1];
        Status legalMove = legalMove(brick,deltax,deltay);
        if(!(legalMove == Status.OK)){
            return legalMove;
        }
        moveBrickOnBoard(brick, deltax, deltay, currentY, currentX);
        return legalMove;
    }

    private void moveBrickOnBoard(Brick brick, int deltax, int deltay, int currentY, int currentX) {
        if(brick.getPlayer() == Player.MORTY){
            deltax = -deltax;
            deltay = -deltay;
        }
        board[currentY][currentX] = null;
        if(board[currentY + deltay][currentX + deltax] != null){

            Player ownerOfBrickMoved = brick.getPlayer();
            GameConstants typeOfBrickKilled = board[currentY + deltay][currentX + deltax].getType();
            List<int[]> movePatternOfBrickKilled = brick.getMovePatterns();
            table.get(ownerOfBrickMoved).add(new StandardBrick(ownerOfBrickMoved, movePatternOfBrickKilled,typeOfBrickKilled));
        }
        board[currentY + deltay][currentX + deltax] = brick;
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
        //Checking whether the move is to a field occupied by a friend
        if(board[currentCords[0]+deltay][currentCords[1]+deltax]!=null){
            if(board[currentCords[0]+deltay][currentCords[1]+deltax].getPlayer().equals(brick.getPlayer())){
                return Status.Field_Occupied_By_Friend;
            }
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
    public void addBrick(Player owner, List<int[]> movePattern, GameConstants typeOfBrick, int x, int y) {
        Brick newBrick = new StandardBrick(owner,movePattern,typeOfBrick);
        board[y][x] = newBrick;
    }

    @Override
    public Status placeFromTable(Brick brickAtTable, int x, int y) {
        if(board[y][x] != null){
            return Status.Field_Occupied;
        }
        Player playerOwningBrick = brickAtTable.getPlayer();
        boolean brickToBePlacedIsPawn = brickAtTable.getType() == GameConstants.PAWN;
        if(brickToBePlacedIsPawn){
            for(int i = 0; i< board.length; i++){
                Brick brickAtTheseCorrds = board[i][x];
                if(brickAtTheseCorrds != null){
                    boolean brickIsPawn = brickAtTheseCorrds.getType() == GameConstants.PAWN;
                    if(brickAtTheseCorrds.getPlayer().equals(playerOwningBrick) && brickIsPawn){
                        return Status.Pawn_In_Collumn;
                    }
                }
            }
        }
        addBrick(playerOwningBrick,brickAtTable.getMovePatterns(),brickAtTable.getType(),x,y);
        table.get(playerOwningBrick).remove(brickAtTable);
        return Status.OK;
    }
}
