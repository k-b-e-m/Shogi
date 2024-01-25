import Framework.Brick;
import Framework.GameConstants;
import Framework.Player;
import Framework.Status;
import Standard.StandardGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static Framework.PredefinedMovepatterns.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAlpha {

    private StandardGame game;

    @BeforeEach

    public void setUp() {
        game = new StandardGame();
    }

    @Test
    public void WhenPawnIsMovedOneUpItIsOkay() {
        //Given a game Where there is a pawn at position 1,1.
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 1);
        Brick pawnAt1_1 = game.getBrickAtBoard(1, 1);
        //When the brick is moved up one
        Status movedBrick = game.moveBrick(pawnAt1_1, 0, 1);
        //Then The move is allowed
        assertThat(movedBrick, is(Status.OK));
        //and the brick is moved one up
        assertThat(game.getBrickAtBoard(1, 2).getType(), is(GameConstants.PAWN));
    }

    @Test
    public void shouldGiveStatusNotOkayWhenPawnIsMovedToSide() {
        //Given a a game with a pawn at position 1,1
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 1);
        Brick pawn = game.getBrickAtBoard(1, 1);
        //When the pawn is moved one to the side:
        Status brickMoved = game.moveBrick(pawn, 1, 0);
        //Then it is not allowed
        assertThat(brickMoved, is(Status.ILLEGAL_MOVE));
        //and the brick is not moved
        assertThat(game.getBrickAtBoard(2, 1), nullValue());
    }

    @Test
    public void shouldGiveStatusOutOfBoardMoveWhenPawnTriesToMoveOverEdge() {
        //Given a game where the pawn is at position 1,8 (Edge of board)
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 8);
        Brick brickAtBoard = game.getBrickAtBoard(1, 8);
        //When the brick is moved out the board
        Status movedBrick = game.moveBrick(brickAtBoard, 0, 1);
        //Then it gets status  Out_OF_BOARD_MOVE
        assertThat(movedBrick, is(Status.OUT_OF_BOARD_MOVE));
    }

    @Test
    public void shoudlGiveStatusField_Occupied_By_FriendWhenRickMovesPawnAtOwnKnight() {
        //Given a game where Rick has a pawn at position 1,1 and a knight at 2,1
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 1);
        game.addBrick(Player.RICK, KnightPattern, GameConstants.KNIGHT, 1, 2);
        Brick pawn = game.getBrickAtBoard(1, 1);
        //When Rick moves the brick
        Status moveBrick = game.moveBrick(pawn, 0, 1);
        //Then it gets status Field_Occupied_By_Friend
        assertThat(moveBrick, is(Status.Field_Occupied_By_Friend));

    }

    @Test
    public void shouldAllowRickToMovePawnAndRemoveMortyBrickWhenPawnMovesToMortyField() {
        //Given a game where Rick has a pawn at position (1,1) and morty has a pawn at position (1,2)
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 1);
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 1, 2);
        Brick pawn = game.getBrickAtBoard(1, 1);
        //When Rick moves the pawn to (1,2)
        Status moveBrick = game.moveBrick(pawn, 0, 1);
        //Then the move is allowed
        assertThat(moveBrick, is(Status.OK));
        //and the pawn at (1,2) belongs to Rick
        assertThat(game.getBrickAtBoard(1, 2).getPlayer(), is(Player.RICK));
    }

    @Test
    public void shouldMoveMortyPawnAtPosition_1_1_to_0_1_WhenMortyMovesBrick() {
        //Given a game where Morty has a pawn at position (1,1)
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 1, 1);
        Brick pawn = game.getBrickAtBoard(1, 1);
        //When Morty moves the pawn to (0,1)
        Status moveBrick = game.moveBrick(pawn, 0, 1);
        //Then the move is allowed
        assertThat(moveBrick, is(Status.OK));
        //and the pawn at (0,1) belongs to Morty
        assertThat(game.getBrickAtBoard(1, 0).getPlayer(), is(Player.MORTY));
    }

    @Test
    public void shouldAddMortyPawnToRickTable() {
        //Given a game where Rick pawn is infornt of morty pawn
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 1);
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 1, 2);
        Brick pawn = game.getBrickAtBoard(1, 1);
        //When Rick moves the pawn to (1,2)
        Status moveBrick = game.moveBrick(pawn, 0, 1);
        //Then the move is allowed
        assertThat(moveBrick, is(Status.OK));
        //and there is added a pawn to ricks table of type pawn
        assertThat(game.getBricksAtTable(Player.RICK).get(0).getType(), is(GameConstants.PAWN));
    }

    @Test
    public void shouldAddKnightBrickAndRemoveFromRickTableWhenKnightFromTableIsPlaced() {
        //Given a game where Rick has a knight at his table from killing morty knight
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 1);
        game.addBrick(Player.MORTY, KnightPattern, GameConstants.KNIGHT, 1, 2);
        Brick pawn = game.getBrickAtBoard(1, 1);
        game.moveBrick(pawn, 0, 1);
        //When Rick places his knight at (3,3) from his table
        game.placeFromTable(game.getBrickAtTable(0), 3, 3);
        //Then the knight is placed at (3,3)
        assertThat(game.getBrickAtBoard(3, 3).getType(), is(GameConstants.KNIGHT));
        //and the knight is removed from ricks table
        assertThat(game.getBricksAtTable(Player.RICK).size(), is(0));

    }

    @Test
    public void shouldGiveStatusPawn_IN_CollumnWhenMortyTriesToPlaceAPawnAtACollumnContainingAPawn() {
        //Given a game where Morty has a pawn at 1,1 and has a pawn at his table
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 1, 2);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 1);
        game.moveBrick(game.getBrickAtBoard(1, 1), 0, 1);
        //When Morty tries to place a pawn at (3,1)
        Status placedBrick = game.placeFromTable(game.getBrickAtTable(0), 1, 3);
        //Then gets Status Pawn_In_Collumn
        assertThat(placedBrick, is(Status.Pawn_In_Collumn));
    }

    @Test
    public void shouldGiveMortyInCheckWhenKingIsThreatenedByPlacedPawn() {
        //Given a game where morty has a king at 1,5 and Rick threatens mortys king with a pawn at 1,4
        game.addBrick(Player.MORTY, KnightPattern, GameConstants.KING, 1, 5);
        game.addBrick(Player.RICK, new LinkedList<>(Arrays.asList(new int[]{1, 0})), GameConstants.PAWN, 1, 4);
        //When Checking if morty is in check
        boolean mortyCheck = game.getCheck(Player.MORTY);
        //Then it is true
        assertThat(mortyCheck, is(true));
    }

    @Test
    public void shouldGiveMortyInCheckWhenPawnIsMovedToCheckKing() {
        //Given a game where morty has a king at 1,5 and rick has a pawn at 1,3
        game.addBrick(Player.MORTY, KnightPattern, GameConstants.KING, 1, 5);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 3);
        //When Rick move his pawn to and checks mortys king
        Brick brickToBeMoved = game.getBrickAtBoard(1, 3);
        game.moveBrick(brickToBeMoved, 0, 1);
        Boolean mortyCheck = game.getCheck(Player.MORTY);
        assertThat(mortyCheck, is(true));
    }

    @Test
    public void shouldGiveStatus_PUTS_ONESELF_IN_CHECK_WhenKingIsInCheckAndHasAPossibleMove() {
        //Given a game where morty is in check and it is his turn
        game.addBrick(Player.MORTY, KingPattern, GameConstants.KING, 1, 5);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 4);
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 4, 4);
        //When morty tries to move his pawn to 4,5
        Brick brickToBeMoved = game.getBrickAtBoard(4, 4);
        Status mortyMove = game.moveBrick(brickToBeMoved, 0, 1);
        //Then he gets status KING_IN_CHECK
        assertThat(mortyMove, is(Status.PUTS_ONESELF_IN_CHECK));
    }

    @Test
    public void shouldAllowMortyToMoveKingOutOfCheck(){
        //Given a game where morty has his king in check
        game.addBrick(Player.MORTY, KingPattern, GameConstants.KING, 1, 5);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 1, 4);
        //When Morty Moves his brick out of check
        Brick brickToBeMoved = game.getBrickAtBoard(1, 5);
        Status mortyMove = game.moveBrick(brickToBeMoved, 1, 0);
        //Then he gets status OK
        assertThat(mortyMove,is(Status.OK));
    }

    @Test
    public void shouldNotAllowRookToMoveThroughPiecesYAxis(){
        //Given a game where morty has a rook at 1,1 and rick has a pawn at 1,2
        game.addBrick(Player.MORTY, RookPattern, GameConstants.ROOK, 1, 1);
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 1, 2);
        //When Rick tries to move his pawn to 1,3
        Brick brickToBeMoved = game.getBrickAtBoard(1, 1);
        Status rickMove = game.moveBrick(brickToBeMoved, 0, 2);
        //Then he gets status MOVE_BLOCKED_BY_PIECE
        assertThat(rickMove, is(Status.MOVE_BLOCKED_BY_PIECE));
    }

    @Test
    public void shouldNotAllowRookToMoveThroughPiecesXaxis(){
        //Given a game where morty has a rook at 1,1 and rick has a pawn at 1,2
        game.addBrick(Player.MORTY, RookPattern, GameConstants.ROOK, 3, 1);
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 2, 1);
        //When Rick tries to move his pawn to 1,3
        Brick brickToBeMoved = game.getBrickAtBoard(3, 1);
        Status mortyMove = game.moveBrick(brickToBeMoved, -2, 0);
        //Then he gets status MOVE_BLOCKED_BY_PIECE
        assertThat(mortyMove, is(Status.MOVE_BLOCKED_BY_PIECE));
    }


    @Test
    public void shouldNotAllowToMovePawnThatPutsOneSelfInCheck(){
        //Given a game where morty has a King at 1,1, a pawn at 2,1 and rick has a rook at 5,1
        game.addBrick(Player.MORTY, KingPattern, GameConstants.KING, 1, 1);
        game.addBrick(Player.MORTY, PawnPattern, GameConstants.PAWN, 2, 1);
        game.addBrick(Player.RICK, RookPattern, GameConstants.ROOK, 5, 1);
        //When Morty tries to move his pawn to 2,2
        Brick brickToBeMoved = game.getBrickAtBoard(2, 1);
        Status mortyMove = game.moveBrick(brickToBeMoved, 0, 1);
        //Then he gets status PUTS_ONESELF_IN_CHECK
        assertThat(mortyMove, is(Status.PUTS_ONESELF_IN_CHECK));
    }

    @Test
    public void shouldNotAllowMoveKingIntoCheck(){
        //Given a game where morty has a King at 1,1 and rick has a rook at 5,1
        game.addBrick(Player.MORTY, KingPattern, GameConstants.KING, 1, 1);
        game.addBrick(Player.RICK, RookPattern, GameConstants.ROOK, 5, 1);
        //When Morty tries to move his king to 2,1
        Brick brickToBeMoved = game.getBrickAtBoard(1, 1);
        Status mortyMove = game.moveBrick(brickToBeMoved, 1, 0);
        //Then he gets status PUTS_ONESELF_IN_CHECK
        assertThat(mortyMove,is(Status.PUTS_ONESELF_IN_CHECK));
    }

    @Test
    public void shouldNotAllowBishopToMoveThroughBricks(){
        //Given a game where morty has a bishop at 1,1 and rick has a pawn at 2,2
        game.addBrick(Player.RICK, BishopPattern, GameConstants.BISHOP, 5, 1);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 6, 2);
        //When Morty tries to move his bishop to 3,3
        Brick brickToBeMoved = game.getBrickAtBoard(5, 1);
        Status mortyMove = game.moveBrick(brickToBeMoved, 2, 2);
        //Then he gets status MOVE_BLOCKED_BY_PIECE
        assertThat(mortyMove, is(Status.MOVE_BLOCKED_BY_PIECE));
    }

    @Test
    public void shouldNotAllowQueenToMoveThroughObjects(){
        //Given a game where morty has a queen at 1,1 and rick has a pawn at 2,2
        game.addBrick(Player.RICK, QueenPattern, GameConstants.QUEEN, 5, 1);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 6, 2);
        //When Morty tries to move his queen to 3,3
        Brick brickToBeMoved = game.getBrickAtBoard(5, 1);
        Status mortyMove = game.moveBrick(brickToBeMoved, 2, 2);
        //Then he gets status MOVE_BLOCKED_BY_PIECE
        assertThat(mortyMove, is(Status.MOVE_BLOCKED_BY_PIECE));
    }

    @Test
    public void shouldHaveBishopAtCorrectThreatMap(){
        //Given a bishop at 5,5
        game.addBrick(Player.RICK, BishopPattern, GameConstants.BISHOP, 5, 5);
        //When checking the threat map at 6,6 it should be true
        boolean threatAt66 = game.getThreatMap()[6][6].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.BISHOP);
        boolean threatAt44 = game.getThreatMap()[4][4].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.BISHOP);
        boolean threatAt64 = game.getThreatMap()[6][4].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.BISHOP);
        boolean threatAt46 = game.getThreatMap()[4][6].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.BISHOP);
        assertThat(threatAt66, is(true));
        assertThat(threatAt44, is(true));
        assertThat(threatAt64, is(true));
        assertThat(threatAt46, is(true));
    }

    @Test
    public void shouldHaveBishopAtCorrectThreatMapWhenPawnIsInWay(){
        //Given a bishop at 5,5 and a pawn at 6,6
        game.addBrick(Player.RICK, BishopPattern, GameConstants.BISHOP, 5, 5);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 6, 6);
        //When checking the threat map at 6,6 it should be true
        boolean threatAt66 = game.getThreatMap()[6][6].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.BISHOP);
        boolean threatAt77 = game.getThreatMap()[7][7].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.BISHOP);
        game.printBoard();
        System.out.println("");
        game.printThreatMap();
        assertThat(threatAt66, is(true));
        assertThat(threatAt77, is(false));
    }
    @Test
    public void shouldUpdateThreatMapForQueenCorrect(){
        //Given a queen at 5,5
        game.addBrick(Player.RICK, QueenPattern, GameConstants.QUEEN, 5, 5);
        //When checking the threat map around the queen
        boolean threatAt66 = game.getThreatMap()[6][6].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt44 = game.getThreatMap()[4][4].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt64 = game.getThreatMap()[6][4].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt46 = game.getThreatMap()[4][6].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt54 = game.getThreatMap()[5][4].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt56 = game.getThreatMap()[5][6].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt45 = game.getThreatMap()[4][5].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt65 = game.getThreatMap()[6][5].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        //Then it should be true
        assertThat(threatAt66, is(true));
        assertThat(threatAt44, is(true));
        assertThat(threatAt64, is(true));
        assertThat(threatAt46, is(true));
        assertThat(threatAt54, is(true));
        assertThat(threatAt56, is(true));
        assertThat(threatAt45, is(true));
        assertThat(threatAt65, is(true));
    }
    @Test
    public void shouldUpdateThreatMapCorrectWhenPawnIsInWayForQueen(){
        //Given a queen at 5,5 and a pawn at 6,6 and at 5,6
        game.addBrick(Player.RICK, QueenPattern, GameConstants.QUEEN, 5, 5);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 6, 6);
        game.addBrick(Player.RICK, PawnPattern, GameConstants.PAWN, 5, 6);
        game.printThreatMap();
        //When checking the threat map around the queen
        boolean threatAt66 = game.getThreatMap()[6][6].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt77 = game.getThreatMap()[7][7].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        boolean threatAt57 = game.getThreatMap()[5][7].stream().map(brick -> brick.getType()).collect(Collectors.toList()).contains(GameConstants.QUEEN);
        //Then it should have updated correctly
        assertThat(threatAt66, is(true));
        assertThat(threatAt77, is(false));
        assertThat(threatAt57, is(false));
    }

}
