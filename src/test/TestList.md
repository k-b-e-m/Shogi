TestList
* When Rick tries to place a pawn next to Morty's king, then it is not allowed
* When Ricks's pawn is tried to being placed before a friendly knight, the move is not allowed and gets status "NO_LEGAL_MOVES_AVAILABLE"
* When Ricks's pawn is tried to be placed before the edge of the board, the move is not allowed and gets status "NO_LEGAL_MOVES_AVAILABLE"
* When it is Rick's turn and Morty tries to move, gets status "Player_Not_In_Turn"
* When Rick moves his pawn to Morty's side of the board (last 3 rows), the pawn is promoted to a promoted Pawn.
* When the same sequence of moves is repeated 4 times, the game is a draw.
* When the same sequence of moves is repeated 4 times, and morty make rick check all three times, Morty loses game.
* When Rick moves his pawn in a way that it is in check, the move is not allowed and gets status "Move_Puts_Yourself_In_Check"
* When Morty has Morty's king in a check, Morty must move his king out of check or block the check.
* When Rick has Morty's king in a checkmate, Rick wins the game.


* Done
* [OK] When Morty tries to place a pawn on a column already containing a morty pawn, the addition is not allowed and gets status "Field_Occupied_By_Enemy"
* [OK] When Rick has a knight at his table and places it at the 1,1 on the board, it is placed there and removed from his table.
* [OK] When Rick kills morty brick, the brick is added to Ricks table.
* [OK] When a Board is given with a pawn at 1,1 and it is moved one forward, it is moved forward.
* [OK] When a Board is given with a pawn at 1,1 and it is moved one to the right, it is not moved and gets status not a part of move pattern
* [OK] When a pawn is at position 8,1 (end of board) it will not move and move status is "Out_Of_Board_Move"
* [OK] When a pawn belonging to player Rick is moving onto a field where a pawn belonging to player Morty is standing, the pawn belonging to player Morty is removed from the board
* [OK] When a pawn belonging to Rick is Moving into a field where a Knight belonging to Rick is standing, the pawn cannot move and get status "Field_Occupied_By_Friend"
* [OK] When Morty moves his pawn one forward it is allowed and moved in the right direction
