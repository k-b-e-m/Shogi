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

    /**
     * Method for getting captured bricks
     * @return List<Object>, list of bricks at table
     */
    List<Brick> getBricksAtTable();

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
     * Method for adding brick at position
     * @param x, int representing x position of where to place brick
     * @param y int representing y position of where to place brick
     */
    void addBrick(GameConstants typeOfBrick,int x, int y);
}
