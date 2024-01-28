package Framework;

import java.util.List;

public interface Game {

    /**
     * Method for getting the board
     * @return int[][], representing the board
     */
    Brick[][] getBoard();

    /**
     * Method for getting brick at position
     * @param x, int representing x coordinate
     * @param y, int representing y cooridnate
     * @return Object, Brick at position
     */
    Brick getBrickAtBoard(int x, int y);

    boolean canPromote(Brick brick);

    /**
     * Method for getting bricks at table
     * @param who, Player representing player
     * @return List<Object>, list of bricks at table
     */
    List<Brick> getBricksAtTable(Player who);

    /**
     * Method for getting brick at position
     * @param index int representing index of brick
     * @return Brick, Brick at position
     */
    Brick getBrickAtTable(int index);

    /**
     * Method for moving a brick
     *
     * @param brick,  Brick to move
     * @param Deltax, int representing x value to move
     * @param Deltay, int representing y value to move
     * @return
     */
    Status moveBrick(Brick brick, int Deltax, int Deltay);


    /**
     * Method for placing a brick from the table
     * @param
     * @param brickAtTable Brick representing brick at table
     * @param x int representing x position of where to place brick
     * @param y int representing y position of where to place brick
     * @return Status representing status of placement
     */



    /**
     * Method for adding a brick to the board
     * @param brick Brick representing brick to add
     * @param x int representing x position of where to place brick
     * @param y int representing y position of where to place brick
     */
    void addBrick(Brick brick, int x, int y);

    /**
     * method for placing a brick from the table on the board
     * @param brickAtTable  Brick representing brick at table
     * @param x int representing x position of where to place brick
     * @param y int representing y position of where to place brick
     * @return Status representing status of placement
     */
    Status placeFromTable(Brick brickAtTable, int x, int y);

    boolean getCheck(Player player);

     void promote(Brick brick);

    boolean playerIsInCheckmate(Player player);
}
