
/**
 * CheckSolution is a utility class which can check if
 * a board position in an Aquarium puzzle is a solution.
 *
 * @author Lyndon While
 * @version 2020
 */
import java.util.Arrays;
import java.util.*;
import java.lang.Math; 

public class CheckSolution
{
    /**
     * Non-constructor for objects of class CheckSolution
     */
    private CheckSolution(){}

    /*
     * AUTOSOLVER COMPONENT
     * Returns the number of conflicts in a given puzzle
     */
    public static int conflicts(Aquarium p) {
        int conflicts = 0;

        // Valid row counts
        int[] rowCounts = rowCounts(p);
        for (int row = 0; row < p.getSize(); ++row) {
            if (p.getRowTotals()[row] != rowCounts[row])
                conflicts++;
        }

        // Valid column counts
        int[] colCounts = columnCounts(p);
        for (int col = 0; col < p.getSize(); ++col) {
            if (p.getColumnTotals()[col] != colCounts[col])
                conflicts++;
        }

        // Is there water above a row which has either air or mixed?
        for (int row = 0; row < p.getSize() - 1; ++row) {
            Integer[] aquariumsRowFull = Arrays.stream(p.getAquariums()[row]).boxed().toArray( Integer[]::new );
            Set<Integer> aquariumsRow =  new HashSet<Integer>(Arrays.asList(aquariumsRowFull));
            for (int aquarium : aquariumsRow) {
                int[] status = rowStatus(p, aquarium, row);
                int[] nextStatus = rowStatus(p, aquarium, row + 1);
                if(status[1] != 2 && nextStatus[1] == 3)
                    conflicts++;
            }
        }

        // Is there split water
        for (int row = 0; row < p.getSize(); ++row) {
            Integer[] aquariumsRowFull = Arrays.stream(p.getAquariums()[row]).boxed().toArray( Integer[]::new );
            Set<Integer> aquariumsRow = new HashSet<Integer>(Arrays.asList(aquariumsRowFull));
            for (int aquarium : aquariumsRow) {
                int[] status = rowStatus(p, aquarium, row);
                if (status[1] == 3)
                    conflicts++;
            }
        }

        return conflicts;
    }

    /*
     * AUTOSOLVER COMPONENT
     * Returns the change in the number of conflicts in the puzzle given a particular move
     */
    public static int conflictsDelta(Aquarium p, int waterToMoveRow, int waterToMoveCol, int destinationRow, int destinationCol) {            
        int oldConflicts = conflicts(p);
        p.leftClick(destinationRow, destinationCol);
        p.leftClick(waterToMoveRow, waterToMoveCol);
        int newConflicts = conflicts(p);
        p.leftClick(destinationRow, destinationCol);
        p.leftClick(waterToMoveRow, waterToMoveCol);

        return newConflicts - oldConflicts;
    }

    // based on: https://enacademic.com/dic.nsf/enwiki/962116
    /*
     * AUTOSOLVER COMPONENT
     * (In theory) should return an aquarium in the solved state
     */
    public static Aquarium solve(Aquarium p, int maxSteps) {
        p.clear();

        int total = 0;
        for (int rowConstraint : p.getRowTotals())
            total += rowConstraint;

        // assign "total" number of water squares
        for (int row = 0; row < p.getSize(); ++row) {
            for (int col = 0; col < p.getSize(); ++col) {
                if (total > 0) {
                    p.leftClick(row, col);
                    total--;
                } else {
                    break;
                }
            }
        }

        // simplify the problem by pre-solving some rows
        // todo....

        // run the constraint solver
        for (int i = 0; i < maxSteps; ++i){
            if (isSolution(p).equals("\u2713\u2713\u2713")) return p;

            // randomly get x,y of a conflicted water tile
            // Conflicted tiles are tiles which when removed would reduce the total number of conflicts
            ArrayList<int[]> conflictingTiles = new ArrayList<>();   
            for (int row = 0; row < p.getSize(); ++row) {
                for (int col = 0; col < p.getSize(); ++col) {
                    if (p.getSpaces()[row][col] == Space.WATER) {
                        int[] position = {row, col};
                        conflictingTiles.add(position);
                    }
                }
            }

            int range = conflictingTiles.size();

            int[] randomConflictedTile = new int[2];

            randomConflictedTile = conflictingTiles.get((int)(Math.random() * range));

            int minDeltaRow = -1;
            int minDeltaCol = -1;
            int minDelta = Integer.MAX_VALUE;
            for (int newRow = 0; newRow < p.getSize(); ++newRow) {
                for (int newCol = 0; newCol < p.getSize(); ++newCol) {
                    if (p.getSpaces()[newRow][newCol] != Space.WATER) {
                        int conflictsDelta = conflictsDelta(p, randomConflictedTile[0], randomConflictedTile[1], newRow, newCol);
                        if (conflictsDelta < minDelta) {
                            // Not considering the circumstance where there are multiple moves 
                            //     which are both minDelta... (perhaps a TODO?)
                            minDelta = conflictsDelta;
                            minDeltaRow = newRow;
                            minDeltaCol = newCol;
                        }
                    }
                }
            }
            p.leftClick(randomConflictedTile[0], randomConflictedTile[1]);
            p.leftClick(minDeltaRow, minDeltaCol);
        }
        return p;
    }

    /**
     * Returns the number of water squares in each row of Aquarium puzzle p, top down.
     */
    public static int[] rowCounts(Aquarium p)
    {
        int[] rowsCounts = new int[p.getSize()];
        Space[][] spaces = p.getSpaces();

        for (int row = 0; row < rowsCounts.length; ++row) {
            for (Space space : spaces[row]) {
                if (space == Space.WATER)
                    rowsCounts[row]++;
            }
        }

        return rowsCounts;
    }

    /**
     * Returns the number of water squares in each column of Aquarium puzzle p, left to right.
     */
    public static int[] columnCounts(Aquarium p)
    {
        int[] columnCounts = new int[p.getSize()];
        Space[][] spaces = p.getSpaces();

        for (int row = 0; row < columnCounts.length; ++row) {
            for (int col = 0; col < columnCounts.length; ++col) {
                if (spaces[row][col] == Space.WATER)
                    columnCounts[col]++;
            }
        }

        return columnCounts;
    }

    /**
     * Returns a 2-int array denoting the collective status of the spaces 
     * in the aquarium numbered t on Row r of Aquarium puzzle p. 
     * The second element will be the column index c of any space r,c which is in t, or -1 if there is none. 
     * The first element will be: 
     * 0 if there are no spaces in t on Row r; 
     * 1 if they're all water; 
     * 2 if they're all not-water; or 
     * 3 if they're a mixture of water and not-water. 
     */
    public static int[] rowStatus(Aquarium p, int t, int r)
    {
        Space[] row = p.getSpaces()[r];

        boolean exists = false;
        boolean hasWater = false;
        boolean hasAir = false;
        int[] collectiveStatus = { 0, -1 };
        for (int columnIndex = 0; columnIndex < row.length; columnIndex++) {
            if (p.getAquariums()[r][columnIndex] == t){
                exists = true;
                collectiveStatus[1] = columnIndex;
                if (row[columnIndex] == Space.WATER) {
                    hasWater = true;
                } else {
                    hasAir = true;
                }
            }
        }

        if (hasWater && hasAir) {
            collectiveStatus[0] = 3;
        } 
        else if (!hasWater && hasAir) {
            collectiveStatus[0] = 2;
        }
        else if (hasWater && !hasAir) {
            collectiveStatus[0] = 1;
        }

        return collectiveStatus;
    }

    /**
     * Returns a statement on whether the aquarium numbered t in Aquarium puzzle p is OK. 
     * Every row must be either all water or all not-water, 
     * and all water must be below all not-water. 
     * Returns "" if the aquarium is ok; otherwise 
     * returns the indices of any square in the aquarium, in the format "r,c". 
     */
    public static String isAquariumOK(Aquarium p, int t)
    {
        boolean allWater = false;

        for (int row = 0; row < p.getSize(); row++)
        {
            int[] status = rowStatus(p, t, row);

            if (status[0] == 1){
                allWater = true;
            }
            else if (status[0] == 2 && allWater) {
                return row + "," + status[1];
            }
            else if (status[0] == 3) {
                return row + "," + status[1];
            }
        }

        return "";
    }

    /**
     * Returns a statement on whether we have a correct solution to Aquarium puzzle p. 
     * Every row and column must have the correct number of water squares, 
     * and all aquariums must be OK. 
     * Returns three ticks if the solution is correct; 
     * otherwise see the LMS page for the expected results. 
     */
    public static String isSolution(Aquarium p)
    {
        int size = p.getSize();

        for (int col = 0; col < size; ++col) {
            for (int row = 0; row < size; ++row) {
                if (p.getRowTotals()[row] != rowCounts(p)[row]){
                    return "Row " + row + " is wrong";
                }

                if (p.getColumnTotals()[col] != columnCounts(p)[col]) {
                    return "Column " + col + " is wrong";
                }

                if (!isAquariumOK(p, p.getAquariums()[row][col]).isEmpty()){
                    return "The aquarium at " + row + "," + col + " is wrong";
                }
            }
        }
        String tick = "\u2713";
        return tick + tick + tick;
    }
}
