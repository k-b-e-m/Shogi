package Framework;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PredefinedMovepatterns {

    public static final List<int[]> RookPattern = new LinkedList<>(Arrays.asList(new int[]{0,1}, new int[]{0,2}, new int[]{0,3}, new int[]{0,4}, new int[]{0,5}, new int[]{0,6}, new int[]{0,7}, new int[]{0,-1}, new int[]{0,-2}, new int[]{0,-3}, new int[]{0,-4}, new int[]{0,-5}, new int[]{0,-6}, new int[]{0,-7}, new int[]{1,0}, new int[]{2,0}, new int[]{3,0}, new int[]{4,0}, new int[]{5,0}, new int[]{6,0}, new int[]{7,0}, new int[]{-1,0}, new int[]{-2,0}, new int[]{-3,0}, new int[]{-4,0}, new int[]{-5,0}, new int[]{-6,0}, new int[]{-7,0}));
    public  static final List<int[]> PawnPattern = new LinkedList<>(Arrays.asList(new int[]{1,0}));

    public static final List<int[]> KingPattern = new LinkedList<>(Arrays.asList(new int[]{1,0}, new int[]{1,1}, new int[]{0,1}, new int[]{-1,1}, new int[]{-1,0}, new int[]{-1,-1}, new int[]{0,-1}, new int[]{1,-1}));

    public static final List<int[]> KnightPattern = new LinkedList<>(Arrays.asList(new int[]{2, 1}, new int[]{2, -1}));

    public static final List<int[]> BishopPattern = new LinkedList<>(Arrays.asList(new int[]{1,1}, new int[]{2,2}, new int[]{3,3}, new int[]{4,4}, new int[]{5,5}, new int[]{6,6}, new int[]{7,7}, new int[]{1,-1}, new int[]{2,-2}, new int[]{3,-3}, new int[]{4,-4}, new int[]{5,-5}, new int[]{6,-6}, new int[]{7,-7}, new int[]{-1,1}, new int[]{-2,2}, new int[]{-3,3}, new int[]{-4,4}, new int[]{-5,5}, new int[]{-6,6}, new int[]{-7,7}, new int[]{-1,-1}, new int[]{-2,-2}, new int[]{-3,-3}, new int[]{-4,-4}, new int[]{-5,-5}, new int[]{-6,-6}, new int[]{-7,-7}));

    public static final List<int[]> QueenPattern = new LinkedList<>(Arrays.asList(new int[]{0,1}, new int[]{0,2}, new int[]{0,3}, new int[]{0,4}, new int[]{0,5}, new int[]{0,6}, new int[]{0,7}, new int[]{0,-1}, new int[]{0,-2}, new int[]{0,-3}, new int[]{0,-4}, new int[]{0,-5}, new int[]{0,-6}, new int[]{0,-7}, new int[]{1,0}, new int[]{2,0}, new int[]{3,0}, new int[]{4,0}, new int[]{5,0}, new int[]{6,0}, new int[]{7,0}, new int[]{-1,0}, new int[]{-2,0}, new int[]{-3,0}, new int[]{-4,0}, new int[]{-5,0}, new int[]{-6,0}, new int[]{-7,0}, new int[]{1,1}, new int[]{2,2}, new int[]{3,3}, new int[]{4,4}, new int[]{5,5}, new int[]{6,6}, new int[]{7,7}, new int[]{1,-1}, new int[]{2,-2}, new int[]{3,-3}, new int[]{4,-4}, new int[]{5,-5}, new int[]{6,-6}, new int[]{7,-7}, new int[]{-1,1}, new int[]{-2,2}, new int[]{-3,3}, new int[]{-4,4}, new int[]{-5,5}, new int[]{-6,6}, new int[]{-7,7}, new int[]{-1,-1}, new int[]{-2,-2}, new int[]{-3,-3}, new int[]{-4,-4}, new int[]{-5,-5}, new int[]{-6,-6}, new int[]{-7,-7}));
}

