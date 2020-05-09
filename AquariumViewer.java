    
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
    
        /**
         * Main constructor for objects of class AquariumViewer.
         * Sets all fields, and displays the initial puzzle.
         */
        public AquariumViewer(Aquarium puzzle)
        {
            this.puzzle = puzzle;
            size = puzzle.getSize();
            WINDOWSIZE = size * BOXSIZE + 2*OFFSET;
            sc = new SimpleCanvas("Aquarium Game", WINDOWSIZE, WINDOWSIZE, Color.white);
    
            displayGrid();
            displayNumbers();
            displayAquariums();
            displayButtons();
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
            // TODO 13
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
    
                sc.drawLine(xBegin, yBegin, xEnd, yEnd, Color.black);
            }
    
            for (int row = 0; row < size + 1; ++row) {
    
                int xBegin = OFFSET;
                int yBegin = OFFSET + BOXSIZE * row;
    
                int xEnd = WINDOWSIZE - OFFSET;
                int yEnd = yBegin;
    
                sc.drawLine(xBegin, yBegin, xEnd, yEnd, Color.black);
            }
        }
    
        /**
         * Displays the numbers around the grid.
         */
        public void displayNumbers()
        {
            for (int column = 0; column < size; ++column) {
                int x = OFFSET + BOXSIZE * column + BOXSIZE / 2;
                int y = OFFSET - 10;
                sc.drawString(column, x, y, Color.black);
            }
    
            for (int row = 0; row < size; ++row) {
                int x = OFFSET - 20;
                int y = OFFSET + BOXSIZE * row + BOXSIZE / 2;
                sc.drawString(row, x, y, Color.black);
            }
        }
    
        /**
         * Displays the aquariums.
         */
        public void displayAquariums()
        {
            int[][] aquariums = puzzle.getAquariums();
    
            for (int row = 0; row < aquariums.length; ++row) {
                for (int column = 0; column < aquariums.length; ++column) {
                    sc.drawString(aquariums[row][column], OFFSET + BOXSIZE * column + BOXSIZE / 2, OFFSET + BOXSIZE * row + BOXSIZE / 2, Color.black);
                } 
            }
        }
    
        /**
         * Displays the buttons below the grid.
         */
        public void displayButtons()
        {
            int buttonGap = BOXSIZE / 2;
    
            int buttonContainerBottom = WINDOWSIZE - buttonGap;
            int buttonContainerTop = WINDOWSIZE - (OFFSET - buttonGap);
    
            int buttonContainerLeft = OFFSET;
            int buttonContainerRight = WINDOWSIZE - OFFSET;
    
            int buttonContainerWidth = buttonContainerRight - buttonContainerLeft;
            int buttonContainerHeight = buttonContainerTop - buttonContainerBottom;
    
            int buttonWidth = (buttonContainerWidth - buttonGap) / 2;
    
            // Solved?? button
            int solvedX1 = buttonContainerLeft;
            int solvedX2 = buttonContainerLeft + buttonWidth;
            sc.drawRectangle(solvedX1, buttonContainerTop, solvedX2, buttonContainerBottom, Color.red);
            sc.drawString("Solved?", solvedX1 + (buttonWidth / 4), buttonContainerTop - (buttonContainerHeight / 2), Color.black);
    
            // reset button
            int resetX1 = solvedX2 + buttonGap;
            int resetX2 = resetX1 + buttonWidth;
            sc.drawRectangle(resetX1, buttonContainerTop, resetX2, buttonContainerBottom, Color.blue);
            sc.drawString("Clear", resetX1 + (buttonWidth / 4), buttonContainerTop - (buttonContainerHeight / 2), Color.black);
        }
    
        /**
         * Updates the display of Square r,c. 
         * Sets the display of this square to whatever is in the squares array. 
         */
        public void updateSquare(int r, int c)
        {
            Space space = puzzle.getSpaces()[r][c];
    
            int x = OFFSET + r * BOXSIZE;
            int y = OFFSET + c * BOXSIZE;
    
            Color col = Color.white;
            switch(space) {
                case WATER:
                    col = Color.blue;
                    break;
                case AIR:
                    col = Color.gray;
                    break;
                case EMPTY:
                    col = Color.white;
                    break;
                }
            sc.drawRectangle(x, y, x + BOXSIZE, y + BOXSIZE, col);
    }

    /**
     * Responds to a mouse click. 
     * If it's on the board, make the appropriate move and update the screen display. 
     * If it's on SOLVED?,   check the solution and display the result. 
     * If it's on CLEAR,     clear the puzzle and update the screen display. 
     */
    public void mousePressed(MouseEvent e) 
    {
        // TODO 15
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
