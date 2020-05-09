
/**
 * Aquarium represents a single problem in the game Aquarium.
 *
 * @author Lyndon While 
 * @version 2020
 */

import java.util.ArrayList;
import java.util.Arrays;

public class Aquarium
{
    private int   size;         // the board is size x size
    private int[] columnTotals; // the totals at the top of the columns, left to right
    private int[] rowTotals;    // the totals at the left of the rows, top to bottom 
    
    // the board divided into aquariums, numbered from 1,2,3,...
    // spaces with the same number are part of the same aquarium
    private int[][] aquariums;
    // the board divided into spaces, each empty, water, or air
    private Space[][] spaces;

    /**
     * Constructor for objects of class Aquarium. 
     * Creates, initialises, and populates all of the fields.
     */
    public Aquarium(String filename)
    {
        FileIO fileHandler = new FileIO(filename);
        ArrayList<String> lines = fileHandler.getLines();
        
        columnTotals = parseLine(lines.get(0));
        rowTotals = parseLine(lines.get(1));
        
        size = columnTotals.length;
        
        spaces = new Space[size][size];
        aquariums = new int[size][size];
        for (int lineNumber = 3; lineNumber < lines.size(); ++lineNumber) {
            aquariums[lineNumber - 3] = parseLine(lines.get(lineNumber));
            
            Space[] spaceRow = new Space[size];
            Arrays.fill(spaceRow, Space.EMPTY);
            spaces[lineNumber - 3] = spaceRow;            
        }
    }
    
    /**
     * Uses the provided example file on the LMS page.
     */
    public Aquarium()
    {
        this("Examples/a6_1.txt");
    }

    /**
     * Returns an array containing the ints in s, 
     * each of which is separated by one space. 
     * e.g. if s = "1 299 34 5", it will return {1,299,34,5} 
     */
    public static int[] parseLine(String s)
    {
        String[] numberStrings = s.split(" ");
        
        int[] numbers = new int[numberStrings.length];
        
        for (int numberIndex = 0; numberIndex < numberStrings.length; ++numberIndex) {
            numbers[numberIndex] = Integer.parseInt(numberStrings[numberIndex]);
        }
        
        return numbers;
    }
    
    /**
     * Returns the size of the puzzle.
     */
    public int getSize()
    {
        return size;
    }
    
    /**
     * Returns the column totals.
     */
    public int[] getColumnTotals()
    {
        return columnTotals;
    }
    
    /**
     * Returns the row totals.
     */
    public int[] getRowTotals()
    {
        return rowTotals;
    }
    
    /**
     * Returns the board in aquariums.
     */
    public int[][] getAquariums()
    {
        return aquariums;
    }
    
    /**
     * Returns the board in spaces.
     */
    public Space[][] getSpaces()
    {
        return spaces;
    }
    
    /**
     * Performs a left click on Square r,c if the indices are legal, o/w does nothing. 
     * A water space becomes empty; other spaces become water. 
     */
    public void leftClick(int r, int c)
    {        
        if (c >= 0 && c < size && r >= 0 && r < size) {
            if (spaces[r][c] == Space.WATER) {
                spaces[r][c] = Space.EMPTY;
            }
            else {
                spaces[r][c] = Space.WATER;
            }
        }
    }
    
    /**
     * Performs a right click on Square r,c if the indices are legal, o/w does nothing. 
     * An air space becomes empty; other spaces become air. 
     */
    public void rightClick(int r, int c)
    {
        if (c >= 0 && c < size && r >= 0 && r < size) {
            if (spaces[r][c] == Space.AIR) {
                spaces[r][c] = Space.EMPTY;
            }
            else {
                spaces[r][c] = Space.AIR;
            }
        }
    }
    
    /**
     * Empties all of the spaces.
     */
    public void clear()
    {
        // TODO 6
    }
}
