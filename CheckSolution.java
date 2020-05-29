

/**
 * CheckSolution is a utility class which can check if
 * a board position in an Aquarium puzzle is a solution.
 *
 * @author Michael Nefiodovas (22969312) & Lisa Chen (22855669)
 * @version 1.0
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
     * Convert a list of lists into a list of ints based on sublist size
     */
    public static ArrayList<Integer> condenseList(ArrayList<ArrayList<Integer>> list) {
        ArrayList<Integer> condensed = new ArrayList<Integer>();
        for (ArrayList<Integer> sublist : list)
            condensed.add(sublist.size());

        return condensed;
    }

    /*
     * AUTOSOLVER COMPONENT
     * returns a partition of the row based on contiguous aquarium segments
     */
    public static ArrayList<ArrayList<Integer>> aquariumRowPartition(Aquarium p, int row) {
        ArrayList<ArrayList<Integer>> partition = new ArrayList<ArrayList<Integer>>();
        int[] aquariumRow = p.getAquariums()[row];
        int previousAquarium = aquariumRow[0];
        ArrayList<Integer> contiguousSegment = new ArrayList<Integer>();
        for (int column = 0; column < p.getSize(); ++column) {
            int aquarium = aquariumRow[column];
            if (aquarium != previousAquarium) {
                partition.add(contiguousSegment);
                previousAquarium = aquarium;
                contiguousSegment = new ArrayList<Integer>();
            } 
            contiguousSegment.add(column);

            if (column == p.getSize() - 1) {
                partition.add(contiguousSegment);
            }
        }
        return partition;
    }

    /*
     * AUTOSOLVER COMPONENT
     * returns all subsets from values which sum to sumTarget
     */
    public static ArrayList<ArrayList<Integer>> subsetSums(int sumTarget, ArrayList<Integer> values) {
        ArrayList<ArrayList<Integer>> subsetSums = new ArrayList<ArrayList<Integer>>();

        int maxComparison = (int)Math.pow(2, values.size());
        // maxComparison looks like: 000000011111

        for (int i = 0; i < maxComparison; ++i) {
            int currentSet = i;
            int sum = 0;
            ArrayList<Integer> subset = new ArrayList<Integer>();
            for (int counter = 0; counter < values.size(); ++counter) {
                if (((currentSet >> counter) & 1) == 1) {
                    sum += values.get(counter);
                    subset.add(counter);
                }
            }
            if (sum == sumTarget)
                subsetSums.add(subset);
        }
        return subsetSums;
    }

    /*
     * AUTOSOLVER COMPONENT
     * (In theory) should return an aquarium in the solved state [not completed]
     */
    public static Aquarium solve(Aquarium p, int maxSteps) {
        p.clear();

        for (int row = 0; row < p.getSize(); ++row) {
            ArrayList<ArrayList<Integer>> partition = aquariumRowPartition(p, row);
            ArrayList<ArrayList<Integer>> subsetSums = subsetSums(p.getRowTotals()[row], condenseList(partition));
            if (subsetSums.size() == 1) {
                for (Integer index : subsetSums.get(0))
                    for (Integer col : partition.get(index))
                        p.leftClick(row, col);
            }
        }

        for (int row = 0; row < p.getSize(); ++row) {
            ArrayList<Integer> nextRowFills = new ArrayList<Integer>();
            for (int col = 0; col < p.getSize(); ++col) {
                if (p.getSpaces()[row][col] == Space.WATER) {
                    if (!nextRowFills.contains(p.getAquariums()[row][col]))
                        nextRowFills.add(p.getAquariums()[row][col]);
                }
            }
            
            if (row < p.getSize() - 1)
                for (int col = 0; col < p.getSize(); ++col) {
                    if (nextRowFills.contains(p.getAquariums()[row + 1][col]) && p.getSpaces()[row + 1][col] != Space.WATER)
                        p.leftClick(row + 1, col);
                }
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
