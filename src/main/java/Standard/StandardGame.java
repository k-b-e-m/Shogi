package Standard;

import Framework.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.List.copyOf;

public class StandardGame implements Game {
    private Brick[][] board;
    private List<Brick>[][] threatMap;
    private HashMap<Player,List<Brick>> table;
    //Currently only implements one king at a time.
    private HashMap<Player,Brick> kings;

    public StandardGame() {
        this.board = new Brick[9][9];
        this.threatMap = new LinkedList[9][9];
        table = new HashMap<>();
        kings = new HashMap<>();
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
        threatMapUpdater(currentX + deltax, currentY + deltay, brick);
        for(Brick threatingBrick : threatMap[currentY][currentX]){
            threatMapUpdater(currentX, currentY, threatingBrick);
        }
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
        Status outOfBoardMove = moveIsOutOfBoard(deltax, deltay, currentCords);
        if(outOfBoardMove != null) return outOfBoardMove;

        //Checking whether the move is to a field occupied by a friend
        Status field_Occupied_By_Friend = moveIsOccupiedByFriend(brick, deltax, deltay, currentCords);
        if (field_Occupied_By_Friend != null) return field_Occupied_By_Friend;

        //Checking whether king is in check
        Player player = brick.getPlayer();
        Status kingInCheck = checkingForCheckMove(brick, deltax, deltay, player, currentCords);
        if (kingInCheck != null) return kingInCheck;

        //Check whether move results in putting oneself in check
        if(brick.getType() != GameConstants.KING){
            Status putsOneselfInCheck = oneInOwnCheckByMovingNonKing(currentCords);
            if (putsOneselfInCheck != null) return putsOneselfInCheck;
        }
        else{
            Status putsOneselfInCheck = oneInOwnCheckByMovingKing(currentCords, deltax, deltay,brick);
            if (putsOneselfInCheck != null) return putsOneselfInCheck;
        }


        //Movepattern Checks
        GameConstants brickType = brick.getType();
        //For Rook
        if(brickType == GameConstants.ROOK){
            return moveCheckerForRook(brick, deltax, deltay, brickType, currentCords);
        }

        //For Checking MovingPattern for other bricks
        Status ok = moveCheckerForBasicPieces(brick, deltax, deltay);
        if (ok != null) return ok;
        return Status.ILLEGAL_MOVE;
    }

    private Status oneInOwnCheckByMovingKing(int[] currentCords, int deltax, int deltay, Brick brick) {
        List<Brick> newThreat = threatMap[currentCords[0]+deltay][currentCords[1]+deltax];
        if(newThreat!=null){
            board[currentCords[0]][currentCords[1]] = null;
            for(Brick b: threatMap[currentCords[0]][currentCords[1]]){
                threatMapUpdater(currentCords[1],currentCords[0],b);
            }
            newThreat = threatMap[currentCords[0]+deltay][currentCords[1]+deltax];
            Status statusForMove = newThreat.stream().filter(b -> b.getPlayer().equals(computeOpponent(brick.getPlayer()))).collect(Collectors.toList()).size() >0 ? Status.PUTS_ONESELF_IN_CHECK:null;
            board[currentCords[0]][currentCords[1]] = brick;
            return statusForMove;
        }
        return null;
    }

    private Status oneInOwnCheckByMovingNonKing(int[] currentCords) {
        int x = currentCords[1];
        int y = currentCords[0];
        Brick brickToMove = board[y][x];
        board[y][x] = null;
        Iterator<Brick> itearator = copyOf(threatMap[y][x]).iterator() ;
        while(itearator.hasNext()){
            Brick brick = itearator.next();
            threatMapUpdater(x,y,brick);
        }
        boolean kingIsInCheck = getCheck(brickToMove.getPlayer());
        board[y][x] = brickToMove;
        itearator = copyOf(threatMap[y][x]).iterator();
        while(itearator.hasNext()){
            Brick brick = itearator.next();
            threatMapUpdater(x,y,brick);
        }
        if(kingIsInCheck){
            return Status.PUTS_ONESELF_IN_CHECK;
        }
        return null;
    }

    private static Status moveCheckerForBasicPieces(Brick brick, int deltax, int deltay) {
        List<int[]> movePatterns = brick.getMovePatterns();
        for(int[] movepattern: movePatterns){
            if(movepattern[0]== deltay && movepattern[1]== deltax){
                return Status.OK;
            }
        }
        return null;
    }


    private Status moveCheckerForRook(Brick brick, int deltax, int deltay, GameConstants brickType, int[] currentCords) {
        System.out.println("Rook");
        boolean mayMove = false;
        List<int[]> movePatterns = brick.getMovePatterns();
        for(int[] movepattern: movePatterns){
            if(movepattern[0]== deltay && movepattern[1]== deltax){
                mayMove = true;
            }
        }
        if(mayMove){
            if(deltay !=0){
                if(deltay >0){
                    for(int i = 1; i< deltay; i++){
                        if(board[currentCords[0]+i][currentCords[1]]!=null){
                            return Status.MOVE_BLOCKED_BY_PIECE;
                        }
                    }
                }
                else{
                    for(int i = -1; i> deltay; i--){
                        if(board[currentCords[0]+i][currentCords[1]]!=null){
                            return Status.MOVE_BLOCKED_BY_PIECE;
                        }
                    }
                }
            }
            else if (deltax != 0){
                if(deltax >0){
                    for(int i = 1; i< deltax; i++){
                        if(board[currentCords[0]][currentCords[1]+i]!=null){
                            return Status.MOVE_BLOCKED_BY_PIECE;
                        }
                    }
                }
                else{
                    for(int i = -1; i> deltax; i--){
                        if(board[currentCords[0]][currentCords[1]+i]!=null){
                            return Status.MOVE_BLOCKED_BY_PIECE;
                        }
                    }
                }
            }
        }
        return Status.OK;
    } //TODO Consider if refactoring is needed

    private Status checkingForCheckMove(Brick brick, int deltax, int deltay, Player player, int[] currentCords) {
        boolean kingIsInCheck = getCheck(player);
        if(kingIsInCheck){
            boolean newMoveisInOfCheck = false;
            boolean brickIsKing = brick.getType() == GameConstants.KING;
            if(brickIsKing) {
                newMoveisInOfCheck = threatMap[currentCords[0] + deltay][currentCords[1] + deltax].stream().map(brick1 -> brick1.getPlayer()).collect(java.util.stream.Collectors.toList()).contains(computeOpponent(player));
            }
            else{
                Brick king = kings.get(player);
                int[] kingCords = findBrickCords(king);
                int kingX = kingCords[1];
                int kingY = kingCords[0];
                newMoveisInOfCheck = threatMap[kingY][kingX].stream().map(brick1 -> brick1.getPlayer()).collect(java.util.stream.Collectors.toList()).contains(computeOpponent(player));

            }
            if(newMoveisInOfCheck){
                return Status.KING_IN_CHECK;
            }
        }
        return null;
    }

    private Status moveIsOccupiedByFriend(Brick brick, int deltax, int deltay, int[] currentCords) {
        if(board[currentCords[0]+ deltay][currentCords[1]+ deltax]!=null){
            if(board[currentCords[0]+ deltay][currentCords[1]+ deltax].getPlayer().equals(brick.getPlayer())){
                return Status.Field_Occupied_By_Friend;
            }
        }
        return null;
    }

    private static Status moveIsOutOfBoard(int deltax, int deltay, int[] currentCords) {
        if(currentCords[0]+ deltay > 8 || currentCords[1]+ deltax >8){
            return Status.OUT_OF_BOARD_MOVE;
        }
        return null;
    }

    @Override
    public void addBrick(Player owner, List<int[]> movePattern, GameConstants typeOfBrick, int x, int y) {
        Brick newBrick = new StandardBrick(owner,movePattern,typeOfBrick);
        board[y][x] = newBrick;
        boolean newBrickIsKing = newBrick.getType() == GameConstants.KING;
        if(newBrickIsKing){
            kings.put(newBrick.getPlayer(),newBrick);
        }
        threatMapUpdater(x, y, newBrick);
    }

    private void threatMapUpdater(int x, int y, Brick brick) {
        if(threatMap[y][x] == null){
            threatMap[y][x] = new LinkedList<>();
        }
        //Clear for previous threts by brick
        for(int i = 0; i< threatMap.length; i++){
            for(int j = 0; j<threatMap[0].length;j++){
                if(threatMap[i][j]!= null){
                    threatMap[i][j].remove(brick);
                }
            }
        }
        //Add the new threat for brick
        if (brick.getType() == GameConstants.ROOK) {
            for(int i = y; i< board.length; i++){
                if(threatMap[i][x] == null){
                    threatMap[i][x] = new LinkedList<>();
                }
                if(board[i][x]!= null){
                    threatMap[i][x].add(brick);
                    break;
                }
                else{
                    threatMap[i][x].add(brick);
                }

            }
            for(int i = y-1; i> -1; i--){
                if(threatMap[i][x] == null){
                    threatMap[i][x] = new LinkedList<>();
                }
                if(board[i][x]!= null){
                    threatMap[i][x].add(brick);
                    break;
                }
                else{
                    threatMap[i][x].add(brick);
                }

            }

            for(int i = x; i< board.length; i++){
                if(threatMap[y][i] == null){
                    threatMap[y][i] = new LinkedList<>();
                }
                if(board[y][i]!= null){
                    threatMap[y][i].add(brick);threatMap[y][i].add(brick);
                    break;
                }
                else{
                }

            }
            for(int i = x-1; i< -1; i--){
                if(threatMap[y][i] == null){
                    threatMap[y][i] = new LinkedList<>();
                }
                if(board[y][i]!= null){
                    threatMap[y][i].add(brick);
                    break;
                }
                else{
                    threatMap[y][i].add(brick);
                }

            }
        }
        else {
            for (int[] move : brick.getMovePatterns()) {
                boolean outOfBoard = y + move[0] >= board.length || x + move[1] >= board[0].length || y + move[0] < 0 || x + move[1] < 0;
                if (outOfBoard) {
                    continue;
                }
                if (threatMap[y + move[0]][x + move[1]] == null) {
                    threatMap[y + move[0]][x + move[1]] = new LinkedList<>();
                }

                threatMap[y + move[0]][x + move[1]].add(brick);
            }
        }

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

    @Override
    public boolean getCheck(Player player) {
        //For games where no king is present
        if(!kings.containsKey(player)){
            return false;
        }
        int[] kingCords = findBrickCords(kings.get(player));
        int kingX = kingCords[1];
        int kingY = kingCords[0];
        boolean enemyIsThreateningKing = 0 != threatMap[kingY][kingX].stream().map(brick -> brick.getPlayer()).count();
        return enemyIsThreateningKing;
    }

    public void printThreatMap(){
        for(int i = 0; i< threatMap.length; i++){
            for(int j = 0; j<threatMap[0].length;j++){
                if(threatMap[i][j] == null){
                    System.out.print("  0  ");
                }else{
                    System.out.print(threatMap[i][j].size() + " ");
                }
            }
            System.out.println();
        }
    }
    public void printBoard(){
        for(int i = 0; i< board.length; i++){
            for(int j = 0; j<board[0].length;j++){
                if(board[i][j] == null){
                    System.out.print("  0  ");
                }else{
                    System.out.print(board[i][j].getType() + " " + board[i][j].getPlayer().toString().charAt(0));
                }
            }
            System.out.println();
        }
    }
    private Player computeOpponent(Player player){
        return player == Player.RICK ? Player.MORTY : Player.RICK;
    }
}
