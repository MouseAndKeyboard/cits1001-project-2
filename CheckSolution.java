
    
/**
 * CheckSolution is a utility class which can check if
 * a board position in an Aquarium puzzle is a solution.
 *
 * @author Lyndon While
 * @version 2020
 */
import java.util.Arrays; 

public class CheckSolution
{
    /**
     * Non-constructor for objects of class CheckSolution
     */
    private CheckSolution(){}
    
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
            else if (status[0] != 0 && status[0] != 1 && status[0] != 2) {
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
        String tick = "\u2713";
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
        
        return tick + tick + tick;
    }
}
