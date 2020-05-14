
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
        int[] rows = new int[p.getSize()];
        
        for (int rowIndex = 0; rowIndex < p.getSize(); ++rowIndex) {
            int waterSquares = 0;
            
            for (int colIndex = 0; colIndex < p.getSize(); ++colIndex) {
                if(p.getSpaces()[rowIndex][colIndex] == Space.WATER){
                    waterSquares += 1;
                }
            }
            
            rows[rowIndex] = waterSquares; 
        }
        
        return rows;
    }
    
    /**
     * Returns the number of water squares in each column of Aquarium puzzle p, left to right.
     */
    public static int[] columnCounts(Aquarium p)
    {
        int[] columns = new int[p.getSize()];
        
        for (int colIndex = 0; colIndex < p.getSize(); ++colIndex) {
            int waterSquares = 0;
            
            for (int rowIndex = 0; rowIndex < p.getSize(); ++rowIndex) {
                if(p.getSpaces()[rowIndex][colIndex] == Space.WATER){
                    waterSquares += 1;
                }
            }
            
            columns[colIndex] = waterSquares; 
        }
        
        return columns;
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
        int[] status = new int[2];
        boolean noSpaces = true;
        boolean allWater = true;
        boolean allNotWater = true;
        int c = -1;
        
        for (int colIndex = 0; colIndex < p.getSize(); ++colIndex) {
            
            if(p.getAquariums()[r][colIndex] == t){
                noSpaces = false;
                c = colIndex;
                
                if(p.getSpaces()[r][colIndex] != Space.WATER){
                    allWater = false;
                }
                
                if(p.getSpaces()[r][colIndex] == Space.WATER){
                    allNotWater = false;
                }
            }
        }
        
        if(noSpaces){
            status[0] = 0;
        } else if(!allWater && !allNotWater){
            status[0] = 3;
        } else if(allNotWater){
            status[0] = 2;
        } else if(allWater){
            status[0] = 1;
        }
     
        status[1] = c;
                
        return status;
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
        // TODO 19
        return null;
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
        // TODO 20
        return null;
    }
}
