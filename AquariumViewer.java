
/**
 * AquariumViewer represents an interface for playing a game of Aquarium.
 *
 * @author Lyndon While
 * @version 2020
 */
import java.awt.*;
import java.awt.event.*; 
import javax.swing.SwingUtilities;

public class AquariumViewer implements MouseListener
{
    private final int BOXSIZE = 40;          // the size of each square
    private final int OFFSET  = BOXSIZE * 2; // the gap around the board
    private       int WINDOWSIZE;            // set this in the constructor 

    private Aquarium puzzle; // the internal representation of the puzzle
    private int        size; // the puzzle is size x size
    private SimpleCanvas sc; // the display window
    
    private String     lastSolvedStatus;

    private int        buttonContainerY1;
    private int        buttonContainerY2;
    private int        solvedButtonX1;
    private int        solvedButtonX2;
    private int        resetButtonX1;
    private int        resetButtonX2;

    private Color bgColour = Color.white;
    private Color gridColour = Color.black;
    private Color incorrectNumberColour = Color.red;
    private Color correctNumberColour = Color.green;
    private Color aquariumColour = Color.red;
    private Color solvedButtonColour = Color.red;
    private Color resetButtonColour = Color.blue;
    private Color waterColour = Color.cyan;
    private Color airColour = Color.pink;
    private Color textColour = Color.black;
        
    /**
     * Main constructor for objects of class AquariumViewer.
     * Sets all fields, and displays the initial puzzle.
     */
    public AquariumViewer(Aquarium puzzle)
    {
        lastSolvedStatus = "";
        this.puzzle = puzzle;
        size = puzzle.getSize();
        WINDOWSIZE = size * BOXSIZE + 2*OFFSET;
        
        sc = new SimpleCanvas("Aquarium Game", WINDOWSIZE, WINDOWSIZE, bgColour);
        sc.addMouseListener(this);
        
        Font primaryFont = new Font("Arial", Font.BOLD, BOXSIZE / 3);
        sc.setFont(primaryFont);
        
        displayPuzzle();
    }

    /**
     * Selects from among the provided files in folder Examples. 
     * xyz selects axy_z.txt. 
     */
    public AquariumViewer(int n)
    {
        this(new Aquarium("Examples/a" + n / 10 + "_" + n % 10 + ".txt"));
    }

    /**
     * Uses the provided example file on the LMS page.
     */
    public AquariumViewer()
    {
        this(61);
    }

    /**
     * Returns the current state of the puzzle.
     */
    public Aquarium getPuzzle()
    {
        return puzzle;
    }

    /**
     * Returns the size of the puzzle.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Returns the current state of the canvas.
     */
    public SimpleCanvas getCanvas()
    {
        return sc;
    }

    /**
     * Displays the initial puzzle; see the LMS page for the format.
     */
    private void displayPuzzle()
    {
        sc.drawRectangle(0, 0, WINDOWSIZE, WINDOWSIZE, bgColour);
        
        for (int r = 0; r < size; ++r) {
            for (int c = 0; c < size; ++c) {
                updateSquare(r, c);
            }
        }
        displayGrid();
        displayNumbers();
        displayAquariums();
        displayButtons();
    }

    /**
     * Displays the grid in the middle of the window.
     */
    public void displayGrid()
    {
        for (int column = 0; column < size + 1; ++column) {
            int xBegin = OFFSET + BOXSIZE * column;
            int yBegin = OFFSET;

            int xEnd = xBegin;
            int yEnd = WINDOWSIZE - OFFSET;

            sc.drawLine(xBegin, yBegin, xEnd, yEnd, gridColour);
        }

        for (int row = 0; row < size + 1; ++row) {

            int xBegin = OFFSET;
            int yBegin = OFFSET + BOXSIZE * row;

            int xEnd = WINDOWSIZE - OFFSET;
            int yEnd = yBegin;

            sc.drawLine(xBegin, yBegin, xEnd, yEnd, gridColour);
        }
    }

    /**
     * Displays the numbers around the grid.
     */
    public void displayNumbers()
    {
        int[] columnCounts = CheckSolution.columnCounts(puzzle);
        for (int column = 0; column < size; ++column) {
            int x = OFFSET + BOXSIZE * column + BOXSIZE / 2;
            int y = OFFSET - 10;
            
            int desiredColumnCount = puzzle.getColumnTotals()[column];
            Color c;
            if (desiredColumnCount != columnCounts[column])  c = incorrectNumberColour;
            else                                             c = correctNumberColour;
                
            sc.drawString(desiredColumnCount, x, y, c);
        }

        int[] rowCounts = CheckSolution.rowCounts(puzzle);
        for (int row = 0; row < size; ++row) {
            int x = OFFSET - 20;
            int y = OFFSET + BOXSIZE * row + BOXSIZE / 2;
            
            int desiredRowCount = puzzle.getRowTotals()[row];
            Color c;
            if (desiredRowCount != rowCounts[row])  c = incorrectNumberColour;
            else                                    c = correctNumberColour;
            
            sc.drawString(puzzle.getRowTotals()[row], x, y, c);
        }
    }
    
    
    /**
     * Displays a horizontal or vertical line (won't work with slanted lines) with a fixed width w.
     */
    public void drawLineAsRectangle(int x1, int y1, int x2, int y2, int width, Color c)
    {   
        sc.drawRectangle(x1 - width / 2, y1 - width / 2, x2 + width / 2, y2 + width / 2, c);
    }
    
    /**
     * Displays the aquariums.
     */
    public void displayAquariums()
    {
        int[][] aquariums = puzzle.getAquariums();

        drawLineAsRectangle(OFFSET, OFFSET, OFFSET, WINDOWSIZE - OFFSET, 4, aquariumColour);
        drawLineAsRectangle(OFFSET, OFFSET, WINDOWSIZE - OFFSET, OFFSET, 4, aquariumColour);
        drawLineAsRectangle(WINDOWSIZE - OFFSET, OFFSET, WINDOWSIZE - OFFSET, WINDOWSIZE - OFFSET, 4, aquariumColour);
        drawLineAsRectangle(OFFSET, WINDOWSIZE - OFFSET, WINDOWSIZE - OFFSET, WINDOWSIZE - OFFSET, 4, aquariumColour);
        
        for (int row = 0; row < aquariums.length; ++row) {
            for (int column = 0; column < aquariums.length; ++column) {
                if (column + 1 < size) {
                    
                    if (aquariums[row][column + 1] != aquariums[row][column])
                        drawLineAsRectangle(OFFSET + (column + 1) * BOXSIZE, OFFSET + row * BOXSIZE, OFFSET + (column + 1) * BOXSIZE, OFFSET + (row + 1) * BOXSIZE, 4, aquariumColour);
                    
                }    
                
                if (row + 1 < size) {
                    if (aquariums[row + 1][column] != aquariums[row][column])
                        drawLineAsRectangle(OFFSET + column * BOXSIZE, OFFSET + (row + 1) * BOXSIZE, OFFSET + (column + 1) * BOXSIZE, OFFSET + (row + 1) * BOXSIZE, 4, aquariumColour);
                }
            } 
        }
    }
    
    private void displayButton(String text, int x1, int y1, int x2, int y2, Color colour) {
        sc.drawRectangle(x1, y1, x2, y2, colour);
        int buttonWidth = x2 - x1;
        int buttonHeight = y2 - y1;
        sc.drawString(text, x1 + (buttonWidth / 4), y1 + (buttonHeight / 2), textColour);
    }
    
    /**
     * Displays the buttons below the grid.
     */
    public void displayButtons()
    {
        int buttonGap = BOXSIZE / 2;

        buttonContainerY1 = WINDOWSIZE - (OFFSET - buttonGap);
        buttonContainerY2 = WINDOWSIZE - buttonGap;

        int buttonContainerLeft = OFFSET;
        int buttonContainerRight = WINDOWSIZE - OFFSET;

        int buttonContainerWidth = buttonContainerRight - buttonContainerLeft;
        int buttonContainerHeight = buttonContainerY1 - buttonContainerY2;

        int buttonWidth = (buttonContainerWidth - buttonGap) / 2;

        // Solved?? button
        solvedButtonX1 = buttonContainerLeft;
        solvedButtonX2 = buttonContainerLeft + buttonWidth;
        displayButton("SOLVED?", solvedButtonX1, buttonContainerY1, solvedButtonX2, buttonContainerY2, solvedButtonColour);
        
        sc.drawString(lastSolvedStatus, solvedButtonX1, buttonContainerY2 + 15, textColour);
        
        // reset button
        resetButtonX1 = solvedButtonX2 + buttonGap;
        resetButtonX2 = resetButtonX1 + buttonWidth;
        displayButton("CLEAR", resetButtonX1, buttonContainerY1, resetButtonX2, buttonContainerY2, resetButtonColour);
    }

    /**
     * Updates the display of Square r,c.  
     * Sets the display of this square to whatever is in the squares array. 
     */
    public void updateSquare(int r, int c)
    {
        Space space = puzzle.getSpaces()[r][c];

        int y = OFFSET + r * BOXSIZE;
        int x = OFFSET + c * BOXSIZE;
        
        switch(space) {
            case WATER:
            sc.drawRectangle(x, y, x + BOXSIZE, y + BOXSIZE, waterColour);
            break;
            case AIR:
            sc.drawCircle(x + BOXSIZE/2 , y + BOXSIZE/2, BOXSIZE/4, airColour);
            break;
            case EMPTY:
            sc.drawRectangle(x, y, x + BOXSIZE, y + BOXSIZE, bgColour);
            break;
        }
    }

    /**
     * Responds to a mouse click. 
     * If it's on the board, make the appropriate move and update the screen display. 
     * If it's on SOLVED?,   check the solution and display the result. 
     * If it's on CLEAR,     clear the puzzle and update the screen display. 
     */
    public void mousePressed(MouseEvent e) 
    {
        int x = e.getX();
        int y = e.getY();

        if (x > OFFSET && y > OFFSET && x < WINDOWSIZE - OFFSET && y < WINDOWSIZE - OFFSET) {
            // We're in the grid
            int row = (y - OFFSET) / BOXSIZE;
            int col = (x - OFFSET) / BOXSIZE;

            if (e.getButton() == 1) puzzle.leftClick(row, col);
            else if (e.getButton() == 3) puzzle.rightClick(row, col);
        }
        else if (y > buttonContainerY1 && y < buttonContainerY2) {

            if (x > solvedButtonX1 && x < solvedButtonX2) {
                lastSolvedStatus = CheckSolution.isSolution(puzzle);
            }
            else if (x > resetButtonX1 && x < resetButtonX2) {
                puzzle.clear();
                lastSolvedStatus = "";
            }
        }
        
        displayPuzzle();
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
