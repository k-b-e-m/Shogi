package Standard;

import Framework.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.List.copyOf;

public class StandardGame implements Game {
    private Brick[][] board;
    private Set<Brick>[][] threatMap;
    private HashMap<Player,List<Brick>> table;
    //Currently only implements one king at a time.
    private HashMap<Player,Brick> kings;

    /**
     * Constructor for StandardGame
     */
    public StandardGame() {
        this.board = new Brick[9][9];
        this.threatMap = new HashSet[9][9];
        table = new HashMap<>();
        kings = new HashMap<>();
        table.put(Player.RICK,new LinkedList<Brick>());
        table.put(Player.MORTY,new LinkedList<Brick>());
    }

    /**
     * Method for getting the board
     * @return Brick[][], representing the board
     */
    @Override
    public Brick[][] getBoard() {
        return new Brick[0][];
    }

    /**
     * Method for getting brick at position
     * @param x int representing x coordinate
     * @param y int representing y cooridnate
     * @return Brick, Brick at position
     */
    @Override
    public Brick getBrickAtBoard(int x, int y) {
        return board[y][x];
    }

    /**
     * Method saying whether it is okay to promote
     * @param brick, brick to promote
     * @return boolean, indicating whether it is allowed to promote.
     */
    @Override
    public boolean canPromote(Brick brick){
        boolean brickOwnerIsRick = brick.getPlayer() == Player.RICK;
        int[] coordsOfBrick = findBrickCords(brick);
        if(brickOwnerIsRick)
        {
            System.out.println(coordsOfBrick[0]);
            return coordsOfBrick[0] >= board.length-4;
        }
        else{
            return coordsOfBrick[0] <= 3;
        }
    }

    /**
     * Method for getting bricks at table
     * @param who Player representing player
     * @return List<Brick>, list of bricks at table
     */
    @Override
    public List<Brick> getBricksAtTable(Player who) {
        return table.get(Player.RICK);
    }

    /**
     * Method for getting brick at position
     * @param index int representing index of brick
     * @return Brick, Brick at position
     */
    @Override
    public Brick getBrickAtTable(int index) {
        return table.get(Player.RICK).get(index);
    }

    /**
     * Method for moving a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @return Status representing status of move
     */
    @Override
    public Status moveBrick(Brick brick, int deltax, int deltay) {

        int[] currentCords = findBrickCords(brick);
        int currentY =currentCords[0];
        int currentX = currentCords[1];
        Status legalMove = legalMove(brick,deltax,deltay);
        boolean moveIsOkay = legalMove == Status.OK;
        if(!moveIsOkay){
            return legalMove;
        }
        moveBrickOnBoard(brick, deltax, deltay, currentY, currentX);
        threatMapUpdater(currentX + deltax, currentY + deltay, brick);
        threatMapUpdaterForBrickAtPosition(currentY, currentX);
        return legalMove;
    }

    private void threatMapUpdaterForBrickAtPosition(int currentY, int currentX) {
      Set<int[]> listOfBrickCordsForPosition = new HashSet<>(copyOf(threatMap[currentY][currentX]).stream().map(brick -> findBrickCords(brick)).collect(Collectors.toList()));
        for (int[] brickCords : listOfBrickCordsForPosition) {
            Brick currentBrick = board[brickCords[0]][brickCords[1]];
            if(currentBrick == null){
                continue;
            }
            threatMapUpdater(brickCords[1], brickCords[0], currentBrick);
        }
    }

    /**
     * Method for moving a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param currentY int representing current y position of brick
     * @param currentX int representing current x position of brick
     */
    private void moveBrickOnBoard(Brick brick, int deltax, int deltay, int currentY, int currentX) {
        boolean mortyOwnsBrick = brick.getPlayer() == Player.MORTY;
        if(mortyOwnsBrick){
            deltax = -deltax;
            deltay = -deltay;
        }
        boolean boardAtSpotIsOccupied = board[currentY + deltay][currentX + deltax] != null;
        if(boardAtSpotIsOccupied){
            killABrick(brick, deltax, deltay, currentY, currentX);
        }
        movingOfBrick(brick, deltax, deltay, currentY, currentX);
    }

    /**
     * Method for moving a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param currentY int representing current y position of brick
     * @param currentX int representing current x position of brick
     */
    private void movingOfBrick(Brick brick, int deltax, int deltay, int currentY, int currentX) {
        board[currentY][currentX] = null;
        board[currentY + deltay][currentX + deltax] = brick;
    }

    /**
     * Method for killing a brick
     * @param brick Brick to kill
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param currentY int representing current y position of brick
     * @param currentX int representing current x position of brick
     */
    private void killABrick(Brick brick, int deltax, int deltay, int currentY, int currentX) {
        Player ownerOfBrickMoved = brick.getPlayer();
        Brick brickKilled = board[currentY + deltay][currentX + deltax];
        GameConstants typeOfBrickKilled = brickKilled.getType();
        Brick promotedVersionOfBrickKilled = brickKilled.getPromoteBrick();
        List<int[]> movePatternOfBrickKilled = brick.getMovePatterns();
        table.get(ownerOfBrickMoved).add(new StandardBrick(ownerOfBrickMoved, movePatternOfBrickKilled,typeOfBrickKilled,promotedVersionOfBrickKilled));
    }

    /**
     * Method for finding the coordinates of a brick
     * @param brick Brick to find coordinates of
     * @return int[], representing coordinates of brick
     */
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

    /**
     * Method for checking whether a move is legal
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @return Status representing status of move
     */
    private Status legalMove(Brick brick, int deltax, int deltay) {
        //Checking whether the move is withing the board
        int[] currentCords = findBrickCords(brick);
        Status outOfBoardMove = moveIsOutOfBoard(deltax, deltay, currentCords);
        boolean insideBoard = outOfBoardMove == null;
        if(!insideBoard) return outOfBoardMove;

        //Checking whether the move is to a field occupied by a friend
        Status field_Occupied_By_Friend = moveIsOccupiedByFriend(brick, deltax, deltay, currentCords);
        boolean isOccupiedByFriend = field_Occupied_By_Friend != null;
        if (isOccupiedByFriend) return field_Occupied_By_Friend;



        //Check whether move results in putting oneself in check
        boolean brickIsKing = brick.getType() == GameConstants.KING;
        if(!brickIsKing){
            Status putsOneselfInCheck = oneInOwnCheckByMovingNonKing(currentCords,deltax,deltay);
            if (putsOneselfInCheck != null) return putsOneselfInCheck;

        }
        else{
            Status putsOneselfInCheck = oneInOwnCheckByMovingKing(currentCords, deltax, deltay,brick);
            if (putsOneselfInCheck != null) return putsOneselfInCheck;
        }


        //Movepattern Checks
        GameConstants brickType = brick.getType();
        //For Rook
        boolean brickIsRook = brickType == GameConstants.ROOK;
        if(brickIsRook){
            return moveCheckerForRook(brick, deltax, deltay, brickType, currentCords);
        }
        boolean brickIsBishop = brickType == GameConstants.BISHOP;
        if(brickIsBishop){
            return moveCheckerForBishop(brick, deltax, deltay, brickType, currentCords);
        }
        boolean brickIsQueen = brickType == GameConstants.QUEEN;
        if(brickIsQueen){
            return moveCheckerForQueen(brick, deltax, deltay);
        }

        //For Checking MovingPattern for other bricks
        Status ok = moveCheckerForBasicPieces(brick, deltax, deltay);
        boolean moveIsInMoveSet = ok == null;
        if (!moveIsInMoveSet) return ok;
        return Status.ILLEGAL_MOVE;
    }

    /**
     * Method for checking whether a move results in putting oneself in check
     * @param currentCords int[] representing current coordinates of brick
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param brick Brick to move
     * @return Status representing status of move
     */
    private Status oneInOwnCheckByMovingKing(int[] currentCords, int deltax, int deltay, Brick brick) {
        boolean spotIsThreatened = threatMap[currentCords[0]+deltay][currentCords[1]+deltax].stream().filter(b -> b.getPlayer() == computeOpponent(brick.getPlayer())).count()>0;
        if(spotIsThreatened){
            return Status.PUTS_ONESELF_IN_CHECK;
        }
        return null;
    }

    /**
     * Method for checking whether a move results in putting oneself in check
     * @param currentCords int[] representing current coordinates of brick
     * @return Status representing status of move
     */
    private Status oneInOwnCheckByMovingNonKing(int[] currentCords,int deltaX, int deltaY) {
        int x = currentCords[1];
        int y = currentCords[0];
        Brick brickToMove = getBrickAtBoard(x,y);
        Player player = brickToMove.getPlayer();
        Brick oldBrick = getBrickAtBoard(x+deltaX,y+deltaY);
        board[y][x] = null;
        board[y+deltaY][x+deltaX] = brickToMove;
        threatMapUpdater(x+deltaX,y+deltaY,brickToMove);
        threatMapUpdaterForBrickAtPosition(y,x);
        boolean playerIsInCheck = getCheck(player);
        board[y][x] = brickToMove;
        board[y+deltaY][x+deltaX] = oldBrick;
        if(oldBrick!= null){
            threatMapUpdater(x+deltaX,y+deltaY,oldBrick);
        }
        threatMapUpdaterForBrickAtPosition(y+deltaY,x+deltaX);
        if(playerIsInCheck){
            return Status.PUTS_ONESELF_IN_CHECK;
        }
        return null;
    }

    /**
     * Method for checking whether a move is in the move pattern of a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @return boolean representing whether move is in move pattern
     */
    private boolean  checkMoveInMovePattern(Brick brick, int deltax, int deltay) {
        boolean mayMove = false;
        List<int[]> movePatterns = brick.getMovePatterns();
        for(int[] movepattern: movePatterns){
            if(movepattern[0]== deltay && movepattern[1]== deltax){
                mayMove = true;
            }
        }
        return mayMove;
    }


    /**
     * Method for checking whether a move is in the move pattern of a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @return Status representing status of move
     */
    private Status moveCheckerForBasicPieces(Brick brick, int deltax, int deltay) {
        return checkMoveInMovePattern(brick,deltax,deltay)? Status.OK : null;
    }

    /**
     * Method for checking whether a move is in the move pattern of a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param brickType GameConstants representing type of brick
     * @param currentCords int[] representing current coordinates of brick
     * @return Status representing status of move
     */
    private Status moveCheckerForBishop(Brick brick, int deltax, int deltay, GameConstants brickType, int[] currentCords) {
        if(!checkMoveInMovePattern(brick, deltax, deltay)){
            return Status.ILLEGAL_MOVE;
        }
        // Check positive y and x
        if(deltax >0 && deltay >0){
            for(int i = 1; i< deltay; i++){
                if(board[currentCords[0]+i][currentCords[1]+i]!=null){
                    return Status.MOVE_BLOCKED_BY_PIECE;
                }
            }
        }
        //Check negative y and positive x
        else if(deltax >0 && deltay <0){
            for(int i = 1; i< deltay; i++){
                if(board[currentCords[0]-i][currentCords[1]+i]!=null){
                    return Status.MOVE_BLOCKED_BY_PIECE;
                }
            }
        }
        //Check positive y and negative x
        else if(deltax <0 && deltay >0){
            for(int i = 1; i< deltay; i++){
                if(board[currentCords[0]+i][currentCords[1]-i]!=null){
                    return Status.MOVE_BLOCKED_BY_PIECE;
                }
            }
        }

        //check negative y and negative x
        else if(deltax <0 && deltay <0){
            for(int i = 1; i< deltay; i++){
                if(board[currentCords[0]-i][currentCords[1]-i]!=null){
                    return Status.MOVE_BLOCKED_BY_PIECE;
                }
            }
        }

        return Status.OK;
    }

    /**
     * Method for checking whether a move is in the move pattern of a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param brickType GameConstants representing type of brick
     * @param currentCords int[] representing current coordinates of brick
     * @return Status representing status of move
     */
    private Status moveCheckerForRook(Brick brick, int deltax, int deltay, GameConstants brickType, int[] currentCords) {
        boolean mayMove = checkMoveInMovePattern(brick, deltax, deltay);
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
    }

    /**
     * Method for checking whether a move is in the move pattern of a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @return Status representing status of move
     */
    private Status moveCheckerForQueen(Brick brick, int deltax, int deltay) {
        Status verticalMovement = moveCheckerForRook(brick, deltax, deltay, GameConstants.ROOK, findBrickCords(brick));
        Status diagonalMovement = moveCheckerForBishop(brick, deltax, deltay, GameConstants.BISHOP, findBrickCords(brick));
        if(verticalMovement == Status.OK && diagonalMovement == Status.OK){
            return Status.OK;
        }
        else{
            return Status.MOVE_BLOCKED_BY_PIECE;
        }
    }



    /**
     * Method for checking whether a move is in the move pattern of a brick
     * @param brick Brick to move
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param currentCords int[] representing current coordinates of brick
     * @return Status representing status of move
     */
    private Status moveIsOccupiedByFriend(Brick brick, int deltax, int deltay, int[] currentCords) {
        if(board[currentCords[0]+ deltay][currentCords[1]+ deltax]!=null){
            if(board[currentCords[0]+ deltay][currentCords[1]+ deltax].getPlayer().equals(brick.getPlayer())){
                return Status.Field_Occupied_By_Friend;
            }
        }
        return null;
    }

    /**
     * Method for checking whether a move is in the move pattern of a brick
     * @param deltax int representing x value to move
     * @param deltay int representing y value to move
     * @param currentCords int[] representing current coordinates of brick
     * @return Status representing status of move
     */
    private static Status moveIsOutOfBoard(int deltax, int deltay, int[] currentCords) {
        if(currentCords[0]+ deltay > 8 || currentCords[1]+ deltax >8|| currentCords[0] + deltay < 0 || currentCords[1] + deltax<0){
            return Status.OUT_OF_BOARD_MOVE;
        }
        return null;
    }


    /**
     * Method for adding brick at position
     * @param brick, Brick to add
     * @param x int representing x position of where to place brick
     * @param y int representing y position of where to place brick
     */
    @Override
    public void addBrick(Brick brick,int x, int y) {
        board[y][x] = brick;
        boolean newBrickIsKing = brick.getType() == GameConstants.KING;
        if(newBrickIsKing){
            kings.put(brick.getPlayer(),brick);
        }
        threatMapUpdater(x, y, brick);
        //Update for bricks affected by placement of new brick
        if(threatMap[y][x]!= null){
            threatMapUpdaterForBrickAtPosition(y, x);
        }

    }

    /**
     * Method for updating the threat map
     * @param x int representing x coordinate of brick
     * @param y int representing y coordinate of brick
     * @param brick Brick representing brick
     */
    private void threatMapUpdater(int x, int y, Brick brick) {
        createNewSetForThreatMapSpot(x, y);
        clearPreviousThreatFromBrick(brick);
        addNewThreatsToThreatMap(x, y, brick);
    }

    private void addNewThreatsToThreatMap(int x, int y, Brick brick) {
        if (brick.getType() == GameConstants.ROOK) {
            rookThreatMap(x, y, brick);

        }
        else if (brick.getType() == GameConstants.BISHOP){
            bishopThreatMap(x, y, brick);

        }
        else if (brick.getType() == GameConstants.QUEEN) {
            rookThreatMap(x, y, brick);
            bishopThreatMap(x, y, brick);
        } else {
            for (int[] move : brick.getMovePatterns()) {
                boolean outOfBoard = y + move[0] >= board.length || x + move[1] >= board[0].length || y + move[0] < 0 || x + move[1] < 0;
                if (outOfBoard) {
                    continue;
                }
                createNewSetForThreatMapSpot(x + move[1], y + move[0]);

                threatMap[y + move[0]][x + move[1]].add(brick);
            }
        }
    }

    private void clearPreviousThreatFromBrick(Brick brick) {
        for(int i = 0; i< threatMap.length; i++){
            for(int j = 0; j<threatMap[0].length;j++){
                if(threatMap[i][j]!= null && threatMap[i][j].contains(brick)){
                    threatMap[i][j].remove(brick);
                }
            }
        }
    }

    private void createNewSetForThreatMapSpot(int x, int y) {
        if(threatMap[y][x] == null){
            threatMap[y][x] = new HashSet<>();
        }
    }

    /**
     * Method for updating the threat map
     * @param x int representing x coordinate of brick
     * @param y int representing y coordinate of brick
     * @param brick Brick representing brick
     */
    private void rookThreatMap(int x, int y, Brick brick) {
        //for the positive Y axis
        for(int i = 1; i+ y < board.length; i++){
            createNewSetForThreatMapSpot(x, y + i);
            threatMap[y +i][x].add(brick);
            if(board[y +i][x] != null){
                break;
            }
        }
        //for the negative Y axis
        for(int i = 1; y -i>= 0; i++){
            createNewSetForThreatMapSpot(x, y - i);
            threatMap[y -i][x].add(brick);
            if(board[y -i][x] != null){
                break;
            }
        }
        //for the positive X axis
        for(int i = 1; i+ x < board.length; i++){
            createNewSetForThreatMapSpot(x + i, y);
            threatMap[y][x +i].add(brick);
            if(board[y][x +i] != null){
                break;
            }
        }
        //for the negative X axis
        for(int i = 1; x -i>= 0; i++){
            createNewSetForThreatMapSpot(x - i, y);
            threatMap[y][x -i].add(brick);
            if(board[y][x -i] != null){
                break;
            }
        }
    }
    /**
     * Method for updating the threat map
     * @param x int representing x coordinate of brick
     * @param y int representing y coordinate of brick
     * @param brick Brick representing brick
     */
    private void bishopThreatMap(int x, int y, Brick brick) {
        //For the positive Y and X axis
        for(int i = 0; i+ y < board.length && i+ x < board[0].length; i++){
            createNewSetForThreatMapSpot(x + i, y + i);
            threatMap[y +i][x +i].add(brick);
            if(board[y +i][x +i] != null && board[y +i][x +i] != brick){
                break;
            }
        }
        //For the positive Y and negative X axis
        for(int i = 0; i+ y < board.length && x -i>= 0; i++){
            createNewSetForThreatMapSpot(x - i, y + i);
            threatMap[y +i][x -i].add(brick);
            if(board[y +i][x -i] != null && board[y +i][x -i] != brick){
                break;
            }
        }
        //For the negative Y and negative X axis
        for(int i = 0; y -i>= 0 && x -i>= 0; i++){
            createNewSetForThreatMapSpot(x - i, y - i);
            threatMap[y -i][x -i].add(brick);
            if(board[y -i][x -i] != null && board[y -i][x -i] != brick){
                break;
            }
        }
        //For the negative Y and positive X axis
        for(int i = 0; y -i>= 0 && x +i<board.length; i++){
            int test = x +i;
            createNewSetForThreatMapSpot(x + i, y - i);
            threatMap[y -i][x +i].add(brick);
            if(board[y -i][x +i] != null && board[y -i][x +i] != brick){
                break;
            }
        }
    }
    /**
     * Method for placing a brick from the table
     * @param brickAtTable Brick representing brick at table
     * @param x int representing x position of where to place brick
     * @param y int representing y position of where to place brick
     * @return Status representing status of placement
     */
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
        addBrick(brickAtTable,x,y);

        table.get(playerOwningBrick).remove(brickAtTable);
        return Status.OK;
    }
    /**
     * Method for getting the threat map
     * @return Set<Brick>[][], representing the threat map
     */
    public Set<Brick>[][] getThreatMap() {
        return threatMap;
    }
    /**
     * Method for getting the threat map
     * @return Set<Brick>[][], representing the threat map
     */
    @Override
    public boolean getCheck(Player player) {
        //For games where no king is present
        if(!kings.containsKey(player)){
            return false;
        }
        int[] kingCords = findBrickCords(kings.get(player));
        int kingX = kingCords[1];
        int kingY = kingCords[0];
        boolean enemyIsThreateningKing = 0 != threatMap[kingY][kingX].stream().map(brick -> brick.getPlayer()).filter(p -> ! p.equals(player)).count();
        return enemyIsThreateningKing;
    }

    @Override
    public void promote(Brick brick) {
        boolean isAllowedToPromote = canPromote(brick);
        if(!isAllowedToPromote){
            return;
        }
        replaceBrickWithPromotedBrick(brick);
    }

    @Override
    public boolean playerIsInCheckmate(Player player) {
        Status checkmateChecker = Status.PUTS_ONESELF_IN_CHECK;
        Set<Brick> playersBricks = getAllBricksBelongToPlayer(player);
        for(Brick brick:playersBricks){
            List<int[]> movePatternForBrick = brick.getMovePatterns();
            for(int[] move : movePatternForBrick){
                Status checkForLegalMove = legalMove(brick,move[1],move[0]);
                boolean validMove = checkForLegalMove == Status.OK;
                if(validMove){
                    checkmateChecker = checkForLegalMove;
                    break;
                }
            }
        }
        boolean legalMoveExist = checkmateChecker == Status.OK;
        return !legalMoveExist;
    }

    private Set<Brick> getAllBricksBelongToPlayer(Player player) {
        Set<Brick> result = new HashSet<>();
        for(int i = 0; i< board.length; i++){
            for(int j = 0; j<board[0].length;j++){
                if(board[i][j]!= null && board[i][j].getPlayer() == player){
                    result.add(board[i][j]);
                }
            }
        }
        return result;
    }

    private void replaceBrickWithPromotedBrick(Brick brick) {
        int[] currentCords = findBrickCords(brick);
        int currentY =currentCords[0];
        int currentX = currentCords[1];
        board[currentY][currentX] = brick.getPromoteBrick();
    }

    /**
     * Method for printing threatMap
     */
    public void printThreatMap(){
        for(int i = 0; i< threatMap.length; i++){
            for(int j = 0; j<threatMap[0].length;j++){
                String color = "\u001B[0m";
                if(board[i][j]!= null){
                    if(board[i][j].getType()== GameConstants.QUEEN){
                        color = "\u001B[34m";}
                    else {
                        color = "\u001B[31m";
                    }
                }
                if(threatMap[i][j] == null){
                    System.out.print(color + "  0  ");
                }else{
                    System.out.print(color + "  " + threatMap[i][j].size()+ "  ");
                }
            }
            System.out.println();
        }
    }
    /**
     * Method for printing board
     */
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
    /**
     * Method for computing opponent
     * @param player Player representing player
     * @return Player representing opponent
     */
    private Player computeOpponent(Player player){
        return player == Player.RICK ? Player.MORTY : Player.RICK;
    }
}
