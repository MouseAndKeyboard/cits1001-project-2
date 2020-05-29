
/**
 * AquariumViewer represents an interface for playing a game of Aquarium.
 *
 * @author Michael Nefiodovas (22969312) & Lisa Chen (22855669)
 * @version 1.0
 */
import java.awt.*;
import java.awt.event.*; 
import javax.swing.SwingUtilities;

public class AquariumViewer implements MouseListener
{
    // ALL UI scaling is relative to BOXSIZE [UI looks best when it's a multiple of 40]
    private final int BOXSIZE = 40;          // the size of each square 
    private final int OFFSET  = BOXSIZE * 2; // the gap around the board
    private final int aquariumBorderWidth = BOXSIZE/10;
    private final int columnNumberSpacer = BOXSIZE/4;

    private       int WINDOWSIZE;            // set this in the constructor 
    private       int FAROFFSET; // Distance along an axis to get to the offset at the other end

    private Aquarium puzzle; // the internal representation of the puzzle
    private int        size; // the puzzle is size x size
    private SimpleCanvas sc; // the display window

    private String lastSolvedStatus;

    private int solvedX1;
    private int solvedY1;
    private int solvedX2;
    private int solvedY2;

    private int resetX1;
    private int resetY1;
    private int resetX2;
    private int resetY2;

    private int restyleX1;
    private int restyleY1;
    private int restyleX2;
    private int restyleY2;

    private int solveX1;
    private int solveY1;
    private int solveX2;
    private int solveY2;

    private Color bgColour;
    private Color gridColour;
    private Color incorrectNumberColour;
    private Color correctNumberColour;
    private Color aquariumBadColour;
    private Color aquariumGoodColour;
    private Color solvedButtonColour;
    private Color resetButtonColour;
    private Color restyleButtonColour;
    private Color autoSolveButtonColour;
    private Color waterColour;
    private Color airColour;
    private Color textColour;

    private ColourTheme currentTheme;

    private static enum ColourTheme {
        DEFAULT,
        BLACKWHITE,
        RAINBOW;

        // From: https://stackoverflow.com/a/17006263
        private static ColourTheme[] allThemes = values();
        public ColourTheme next() {
            return allThemes[(this.ordinal() + 1) % allThemes.length];
        }

    }

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
        FAROFFSET = WINDOWSIZE - OFFSET;

        sc = new SimpleCanvas("Aquarium Game", WINDOWSIZE, WINDOWSIZE, bgColour);
        sc.addMouseListener(this);

        int fontScaleFactor = 3;        
        sc.setFont(new Font("Serif", Font.BOLD, BOXSIZE / fontScaleFactor)); 

        currentTheme = ColourTheme.DEFAULT;
        setStyle(currentTheme);

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
        for (int colrowIndex = 0; colrowIndex < size + 1; ++colrowIndex) {
            int xColBegin = OFFSET + BOXSIZE * colrowIndex;
            int yColBegin = OFFSET;

            int xColEnd = xColBegin;
            int yColEnd = FAROFFSET;

            sc.drawLine(xColBegin, yColBegin, xColEnd, yColEnd, gridColour);

            int xRowBegin = OFFSET;
            int yRowBegin = OFFSET + BOXSIZE * colrowIndex; // rows = column = size so OK to do

            int xRowEnd = FAROFFSET;
            int yRowEnd = yRowBegin;

            sc.drawLine(xRowBegin, yRowBegin, xRowEnd, yRowEnd, gridColour);
        }
    }

    /**
     * Displays the numbers around the grid.
     */
    public void displayNumbers()
    {
        // Distance from the border of the grid that the numbers appear
        int rowNumberSpacer = BOXSIZE/2;

        int[] columnCounts = CheckSolution.columnCounts(puzzle);
        for (int column = 0; column < size; ++column) {
            int x = OFFSET + BOXSIZE * column + BOXSIZE / 2;
            int y = OFFSET - columnNumberSpacer;

            int desiredColumnCount = puzzle.getColumnTotals()[column];
            Color c;
            if (desiredColumnCount != columnCounts[column])  c = incorrectNumberColour;
            else                                             c = correctNumberColour;

            sc.drawString(desiredColumnCount, x, y, c);
        }

        int[] rowCounts = CheckSolution.rowCounts(puzzle);
        for (int row = 0; row < size; ++row) {
            int x = OFFSET - rowNumberSpacer;
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
    public void drawLineAsRectangle(int x1, int y1, int x2, int y2, int width, Color c, boolean vertical)
    {   
        if (vertical)
            sc.drawRectangle(x1 - width / 2, y1, x2 + width / 2, y2, c);
        else
            sc.drawRectangle(x1, y1 - width / 2, x2, y2  + width / 2, c);
    }

    /**
     * Displays the aquariums.
     */
    public void displayAquariums()
    {
        int[][] aquariums = puzzle.getAquariums();

        for (int row = 0; row < aquariums.length; ++row) {
            for (int column = 0; column < aquariums.length; ++column) {

                Color borderColour;
                if (CheckSolution.isAquariumOK(puzzle, aquariums[row][column]).isEmpty())
                    borderColour = aquariumGoodColour;
                else
                    borderColour = aquariumBadColour;

                // Note to marker:
                //      Our aquariums have dynamic border colours depending on if they're valid.
                //      We therefore have to render UP, DOWN, LEFT and RIGHT border of each aquarium independently.

                // Right
                if (column + 1 == size || 
                aquariums[row][column] != aquariums[row][column + 1]) {
                    int x1 = OFFSET + (column + 1) * BOXSIZE;
                    int y1 = OFFSET + row * BOXSIZE;
                    int x2 = OFFSET + (column + 1) * BOXSIZE;
                    int y2 = OFFSET + (row + 1) * BOXSIZE;
                    int thisBorderWidth = aquariumBorderWidth;
                    if (column + 1 != size) {
                        x1 -= aquariumBorderWidth / 4;
                        x2 = x1;
                        thisBorderWidth = aquariumBorderWidth / 2;
                    } 

                    drawLineAsRectangle(x1, y1, x2, y2, thisBorderWidth, borderColour, true);
                }

                // Up
                if (row == 0 || 
                aquariums[row][column] != aquariums[row - 1][column]) {

                    int x1 = OFFSET + column * BOXSIZE;
                    int y1 = OFFSET + row * BOXSIZE;
                    int x2 = OFFSET + (column + 1) * BOXSIZE;
                    int y2 = OFFSET + row * BOXSIZE;
                    int thisBorderWidth = aquariumBorderWidth;
                    if (row != 0) {
                        y1 += aquariumBorderWidth / 4;
                        y2 = y1;
                        thisBorderWidth = aquariumBorderWidth / 2;
                    } 

                    drawLineAsRectangle(x1, y1, x2, y2, thisBorderWidth, borderColour, false);
                }

                // Left
                if (column == 0 || 
                aquariums[row][column] != aquariums[row][column - 1]) {
                    int x1 = OFFSET + column * BOXSIZE;
                    int y1 = OFFSET + row * BOXSIZE;
                    int x2 = OFFSET + column * BOXSIZE;
                    int y2 = OFFSET + (row + 1) * BOXSIZE;
                    int thisBorderWidth = aquariumBorderWidth;
                    if (column != 0) {
                        x1 += aquariumBorderWidth / 4;
                        x2 = x1;
                        thisBorderWidth = aquariumBorderWidth / 2;
                    } 

                    drawLineAsRectangle(x1, y1, x2, y2, thisBorderWidth, borderColour, true);
                }

                // Down
                if (row + 1 == size || 
                aquariums[row][column] != aquariums[row + 1][column]) {
                    int x1 = OFFSET + column * BOXSIZE;
                    int y1 = OFFSET + (row + 1) * BOXSIZE;
                    int x2 = OFFSET + (column + 1) * BOXSIZE;
                    int y2 = OFFSET + (row + 1) * BOXSIZE;
                    int thisBorderWidth = aquariumBorderWidth;
                    if (row + 1 != size) {
                        y1 -= aquariumBorderWidth / 4;
                        y2 = y1;
                        thisBorderWidth = aquariumBorderWidth / 2;
                    } 

                    drawLineAsRectangle(x1, y1, x2, y2, thisBorderWidth, borderColour, false);
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

        int buttonTop = FAROFFSET + buttonGap;
        int buttonBottom = WINDOWSIZE - buttonGap;

        int buttonWidth = (FAROFFSET - OFFSET - buttonGap) / 2;

        solvedX1 = OFFSET;
        solvedY1 = buttonTop;
        solvedX2 = OFFSET + buttonWidth;
        solvedY2 = buttonBottom;
        displayButton("SOLVED?", solvedX1, solvedY1, solvedX2, solvedY2, solvedButtonColour);

        int solvedStatusSpacer = 15;
        sc.drawString(lastSolvedStatus, solvedX1, buttonBottom + solvedStatusSpacer, textColour);

        resetX1 = solvedX2 + buttonGap;
        resetY1 = buttonTop;
        resetX2 = resetX1 + buttonWidth;
        resetY2 = buttonBottom;
        displayButton("CLEAR", resetX1, resetY1, resetX2, resetY2, resetButtonColour);

        buttonTop = buttonGap;
        buttonBottom = OFFSET - buttonGap - columnNumberSpacer;

        restyleX1 = OFFSET;
        restyleY1 = buttonTop;
        restyleX2 = OFFSET + buttonWidth;
        restyleY2 = buttonBottom;
        displayButton("RESTYLE", restyleX1, restyleY1, restyleX2, restyleY2, restyleButtonColour);

        solveX1 = restyleX2 + buttonGap;
        solveY1 = buttonTop;
        solveX2 = solveX1 + buttonWidth;
        solveY2 = buttonBottom;
        displayButton("AUTOSOLVE", solveX1, solveY1, solveX2, solveY2, autoSolveButtonColour);
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

        if (x > OFFSET && y > OFFSET && x < FAROFFSET && y < FAROFFSET) {
            // We're in the grid
            int row = (y - OFFSET) / BOXSIZE;
            int col = (x - OFFSET) / BOXSIZE;

            if (e.getButton() == 1) puzzle.leftClick(row, col);
            else if (e.getButton() == 3) puzzle.rightClick(row, col);
        }
        else if (x > solvedX1 && x < solvedX2 && y > solvedY1 && y < solvedY2) {
            lastSolvedStatus = CheckSolution.isSolution(puzzle);
        }
        else if (x > resetX1 && x < resetX2 && y > resetY1 && y < resetY2) {
            puzzle.clear();
            lastSolvedStatus = "";
        }
        else if (x > restyleX1 && x < restyleX2 && y > restyleY1 && y < restyleY2) {
            currentTheme = currentTheme.next();
            setStyle(currentTheme);
        }
        else if (x > solveX1 && x < solveX2 && y > solveY1 && y < solveY2) {
            puzzle = CheckSolution.solve(puzzle, 3000);
        }
        displayPuzzle();
    }

    public void setStyle(ColourTheme theme) {
        switch(theme) {
            case DEFAULT:
            bgColour = Color.white;
            gridColour = Color.black;
            incorrectNumberColour = Color.red;
            correctNumberColour = Color.green;
            aquariumBadColour = Color.red;
            aquariumGoodColour = Color.green;
            solvedButtonColour = Color.red;
            resetButtonColour = Color.blue;
            restyleButtonColour = Color.yellow;
            autoSolveButtonColour = Color.blue;
            waterColour = Color.cyan;
            airColour = Color.pink;
            textColour = Color.black;
            break;
            case BLACKWHITE:
            bgColour = Color.black;
            gridColour = Color.white;
            incorrectNumberColour = Color.gray;
            correctNumberColour = Color.white;
            aquariumBadColour = Color.gray;
            aquariumGoodColour = Color.white;
            solvedButtonColour = Color.white;
            resetButtonColour = Color.white;
            restyleButtonColour = Color.white;
            autoSolveButtonColour = Color.white;
            waterColour = Color.white;
            airColour = Color.white;
            textColour = Color.black;
            break;
            case RAINBOW:
            bgColour = Color.pink;
            gridColour = Color.red;
            incorrectNumberColour = Color.red;
            correctNumberColour = Color.green;
            aquariumBadColour = Color.blue;
            aquariumGoodColour = Color.green;
            solvedButtonColour = Color.orange;
            resetButtonColour = Color.red;
            restyleButtonColour = Color.yellow;
            autoSolveButtonColour = Color.green;
            waterColour = Color.cyan;
            airColour = Color.black;
            textColour = Color.white;
            break;
        }
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
