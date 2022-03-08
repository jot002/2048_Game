/**
 * Sample Board
 * <p/>
 *     0   1   2   3
 * 0   -   -   -   -
 * 1   -   -   -   -
 * 2   -   -   -   -
 * 3   -   -   -   -
 * <p/>
 * The sample board shows the index values for the columns and rows
 * Remember that you access a 2D array by first specifying the row
 * and then the column: grid[row][column]
 */

/**
 * Creates an 2d array representation of the game 2048 where the board is 
 * tested with various methods.
 *
 * Bugs: None known
 *
 * @author   Jonathan Tran
 */

public class Board {
    /* Defined to avoid magic number */
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;

    /* Used to format number in the grid in boardToString methods */
    private static final String NUMBER_FORMAT = "%5d";

    /* Number of tiles showing when the game starts */
    public final int NUM_START_TILES = 2;

    /* The probability (times 100) that a randomly generated tile will be a 2 (vs a 4) */
    public final int TWO_PROBABILITY = 90;

    /* The size of the grid */
    public final int GRID_SIZE;

    /* The grid of tile values, its size being boardSize * boardSize */
    private final int[][] grid;


    /* Direction strings */
    public final String LEFT = "LEFT";
    public final String RIGHT = "RIGHT";
    public final String UP = "UP";
    public final String DOWN = "DOWN";

    /**
     * Constructor used to load boards for grading/testing
     *
     * @param random - the random generator for tile values
     * @param inputBoard - - the 2d board to assign
     */
    public Board(int[][] inputBoard) {
        this.GRID_SIZE = inputBoard.length;
        this.grid = new int[this.GRID_SIZE][this.GRID_SIZE];
        for (int r = 0; r < this.GRID_SIZE; r++) {
            for (int c = 0; c < this.GRID_SIZE; c++) {
                this.grid[r][c] = inputBoard[r][c];
            }
        }
    }

    /**
     * Return the current board as a 2D grid String.
     *
     * Use this format to construct board
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * int number = 16;
     * String cell = String.format(NUMBER_FORMAT, number);
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     * Then append "cell" to your output string.
     */
    public String boardToString() {
        // start off with empty string
        String stringOutput = "";
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[0].length; j++) {
                // only adds the values that aren't in the rightmost column
                if (j != this.grid[0].length-1) {
                    // adds the next number plus a space to stringOutput
                    stringOutput = stringOutput + 
                                    String.format(NUMBER_FORMAT, grid[i][j]);
                }
                // adds the value in the rightmost column
                else {
                    // adds the last number and "\n" to stringOutput
                    stringOutput = stringOutput + 
                            String.format(NUMBER_FORMAT, grid[i][j]) + "\n";
                }
            }
        }
        return stringOutput;
    }

    /**
     * Print the current board as a 2D grid.
     *
     * @param direction the tiles will move
     */
    //tips for future: can only use this.getGrid or this.grid
    public String boardToString(String direction) {
        // sets oldBoard to a copy of the 2D array
        int[][] oldBoard = this.getGrid();  
        // moves one direction and saves changes to the current grid
        this.move(direction);
        // saves string representation of grid
        String stringBoard = this.boardToString(); 
        // resets the board to the original board
        setGrid(oldBoard);
        // return the string representation
        return stringBoard;
    }
 
    /**
     * The purpose of this method is to check to see if the movement of
     * the tiles in any direction can actually take place. It does not move the
     * tiles, but at every index of the grid, checks to see if there is a tile
     * above, below, to the left or right that has the same value. If this is
     * the case, then that tile can be moved. It also checks if there is an
     * empty (0) tile at a specified index, as this also indicates that movement
     * can be possible. This method is called within move() so that that method
     * can determine whether or not tiles should be moved.
     *
     * @param direction the tiles will move (if possible)
     * @return true if the movement can be done and false if it cannot
     */
    public boolean canMove(String direction){
        // utilize helper methods to check if movement in a particular
        // direction is possible

        if (direction.equals(this.UP)) {
            return this.canMoveUp();
        }
        else if (direction.equals(this.RIGHT)) {
            return this.canMoveRight();
        }
        else if (direction.equals(this.DOWN)) {
            return this.canMoveDown();
        }
        else if (direction.equals(this.LEFT)) {
            return this.canMoveLeft();
        }
        else {
            return false;
        }
    }

    /**
     * Rotate 90 degrees clockwise
     *
     */
    private void rotate() {
        // 4 rotations to get to the original (4*90=360)
        int sideNum = 4;
        // this for loop iterates through only the right side of the
        // diagonal of the grid
        for (int i = 0; i < sideNum / 2; i++) {
            // for i=0, j=0,1,2; i=1, j=1,2; i=2, j=2,3; i=3, j=3
            for (int j = i; j < sideNum - i - 1; j++) {
                // temporary variable to hold the value at row i and column j
                int oldGridValue = this.grid[i][j];
                // starts rotating the board
                // Ex: this.grid[0][0] = this.grid[3][0] and 
                //     this.grid[1][0] = this.grid[3][1]
                // swaps the values 
                this.grid[i][j] = this.grid[sideNum - 1 - j][i];
                this.grid[sideNum - 1 - j][i] = 
                        this.grid[sideNum - 1 - i][sideNum - 1 - j];
                this.grid[sideNum - 1 - i][sideNum - 1 - j] = 
                        this.grid[j][sideNum - 1 - i];
                // puts new value at row i, column j
                this.grid[j][sideNum - 1 - i] = oldGridValue;
            }
        }
    }

    /**
     * Rotate 90 degrees clockwise given the number of times
     *
     * @param number The number of clockwise 90 degree rotations you want to do
     */
    public void rotate(int number) {
        if (number <= 0) return;
        for (int i = 0; i < number % 4; i++) this.rotate();
    }

    /**
     * determines if a move leftwards is possible
     *
     * @return true if such a move is possible, false if no such move is
     */
    private boolean canMoveLeft() {
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[0].length - 1; j++) {
                // used to check the column to the right
                int spot = j + 1;
                // checks if the original position is 0 and if the column to
                // the right is a number to see if it can move left
                if (this.grid[i][j] == 0 && this.grid[i][spot] != 0) {
                    return true;
                }
                // checks for pairs
                if (this.grid[i][j] == this.grid[i][spot] &&
                         this.grid[i][j]!=0) {
                    return true;
                }
            }
        }  
        // returns false if it can't move left
        return false;
    }

    /**
     * determines if a move downwards is possible
     *
     * @return true if such a move is possible, false if no such move is
     */
    private boolean canMoveDown() {
        for (int i = this.grid.length - 1; i > 0  ; i--) {
            for (int j = 0; j < this.grid[0].length; j++) {
                // used to check the row above
                int spot = i - 1;
                // checks if the current position is 0 and 
                // the row above is a number to see if it can move down
                if (this.grid[i][j] == 0 && this.grid[spot][j] != 0) {
                    return true;
                }
                // checks for pairs
                if (this.grid[i][j] == this.grid[spot][j] &&
                         this.grid[i][j]!=0) {
                    return true;
                }
            }
        } 
        // returns false if it can't move down
        return false;
    }

    /**
     * determines if a move rightward is possible
     *
     * @return true if such a move is possible, false if no such move is
     */
    private boolean canMoveRight() {
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = this.grid[0].length - 1; j > 0; j--) {
                // used to check the column to the left
                int spot = j - 1;
                // checks if the original position is 0 and if the column to
                // the left is a number to see if it can move right
                if (this.grid[i][j] == 0 && this.grid[i][spot] != 0) {
                    return true;
                }
                // checks for pairs
                if (this.grid[i][j] == this.grid[i][spot] && 
                        this.grid[i][j]!=0) {
                    return true;
                }
            }
        }   
        // returns false if it can't move right
        return false;
    }

    /**
     * determines if a move upwards is possible
     *
     * @return true if such a move is possible, false if no such move is
     */
    private boolean canMoveUp() {
        for (int i = 0; i < this.grid.length - 1  ; i--) {
            for (int j = 0; j < this.grid[0].length; j++) {
                // used to check the row below
                int spot = i + 1;
                // checks if the current position is 0 and 
                // the row below is a number to see if it can move up
                if (this.grid[i][j] == 0 && this.grid[spot][j] != 0) {
                    return true;
                }
                // checks for pairs
                if (this.grid[spot][j] == this.grid[i][j] && 
                                    this.grid[i][j]!=0) {
                    return true;
                }
            }
        }   
        // returns false if it can't move up
        return false;
    }


    /**
     * The purpose of this method is to move the tiles in the game
     * board by a specified direction passed in as a parameter. If the movement
     * cannot be done, the method returns false. If the movement can be done, it
     * moves the tiles and returns true. This method relies on the help of four
     * other helper methods to perform the game play.
     *
     * @param direction the tiles will move (if possible)
     * @return true if the movement can be done and false if it cannot
     */
    public boolean move(String direction) {
        /* if canMove is false, exit and don't move tiles */
        if (!this.canMove(direction)) return false;

        /* move in relationship to the direction passed in */
        if (direction.equals(this.UP)) {
            this.moveUp();
        }
        else if (direction.equals(this.RIGHT)) {
            this.moveRight();
        }
        else if (direction.equals(this.DOWN)) {
            this.moveDown();
        }
        else if (direction.equals(this.LEFT)) {
            this.moveLeft();
        }
        else {
            return false;
        }

        return true;
    }

    /**
     * performs a move left
     * Precondition: a left move is possible.
     * Postcondition: The board is modified to reflect the move
     */
    private void moveLeft() {
        // iterates through all the rows
        for (int i = 0; i < this.grid.length; i++) {
            // nextSpot is used to if the spot to the left is open 
            // -takes into account multiple spots
            int nextSpot = 0;
            // iterates through all the columns
            for (int j = 0; j < this.grid[0].length; j++) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value to the left
                    this.grid[i][nextSpot] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most left side
                    if (j != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // increments if a value is not equal to 0
                    nextSpot++;
                }
            }
        }
        // this for loop is for finding pairs and adding them
        for (int i = 0; i < this.grid.length; i++) {
            // only iterates in columns 0, 1, 2 because we are checking the
            // the column and the column to the right of it
            for (int j = 0; j < this.grid[0].length - 1; j++) {
                // checks if the value in the column and the column to the
                // right are the same
                if (this.grid[i][j] == this.grid[i][j+1]) {
                    // adds up the values of the pairs
                    int total = this.grid[i][j] + this.grid[i][j+1];
                    // sets the total value to the column to the left
                    this.grid[i][j] = total;
                    // sets the the column to the right equal to 0
                    this.grid[i][j+1]=0;
                } 
            }
        }
        // this third for loop is used to move everything to the left after
        // taking care of pairs
    
        // iterates through all the rows
        for (int i = 0; i < this.grid.length; i++) {
            // nextSpot is used to if the spot to the left is open 
            // -takes into account multiple spots
            int nextSpot = 0;
            // iterates through all the columns
            for (int j = 0; j < this.grid[0].length; j++) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value to the left
                    this.grid[i][nextSpot] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most left side
                    if (j != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // increments if a value is not equal to 0
                    nextSpot++;
                }
            }
        }     
    }

    /**
     * performs a move downward
     * Precondition: a downward move is possible.
     * Postcondition: The board is modified to reflect the move
     */
    private void moveDown() {
        for (int j = 0; j < this.grid[0].length; j++) {
            // nextSpot is used to if the spot below is open 
            // -takes into account multiple spots
            int nextSpot = 3;
            for (int i = this.grid.length - 1; i >= 0 ; i--) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value downward
                    this.grid[nextSpot][j] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most bottom side
                    if (i != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // decrements if a value is not equal to 0
                    nextSpot--;
                }
            }
        }
        // this for loop is for finding pairs and adding them
        for (int j = 0; j < this.grid[0].length ; j++) {
            // only iterates in rows 1, 2, 3 because we are checking the
            // the row and the row above of it
            for (int i = this.grid.length - 1; i > 0 ; i--) {
                // checks if the value in the row and the row above
                // are the same
                if (this.grid[i][j] == this.grid[i-1][j]) {
                    // adds up the values of the pairs
                    int total = this.grid[i][j] + this.grid[i-1][j];
                    // sets the total value to the original row
                    this.grid[i][j] = total;
                    // sets the the row above equal to 0
                    this.grid[i-1][j]=0;
                } 
            }
        }
        // this third for loop is used to move everything downward after
        // taking care of pairs
        for (int j = 0; j < this.grid[0].length; j++) {
            // nextSpot is used to if the spot below is open 
            // -takes into account multiple spots
            int nextSpot = 3;
            for (int i = this.grid.length - 1; i >= 0 ; i--) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value downward
                    this.grid[nextSpot][j] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most bottom side
                    if (i != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // decrements if a value is not equal to 0
                    nextSpot--;
                }
            }
        }  
    }

    /**
     * performs a move right
     * Precondition: a right move is possible.
     * Postcondition: The board is modified to reflect the move
     */
    private void moveRight() {
        // iterates through all the rows
        for (int i = 0; i < this.grid.length; i++) {
            // nextSpot is used to if the spot to the right is open 
            // -takes into account multiple spots
            int nextSpot = 3;
            // iterates through all the columns
            for (int j = this.grid[0].length - 1; j >= 0; j--) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value to the right
                    this.grid[i][nextSpot] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most right side
                    if (j != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // decrements if a value is not equal to 0
                    nextSpot--;
                }
            }
        }
        // this for loop is for finding pairs and adding them
        for (int i = 0; i < this.grid.length; i++) {
            // only iterates in columns 1, 2, 3 because we are checking the
            // the column and the column to the left of it
            for (int j = this.grid[0].length - 1; j > 0; j--) {
                // checks if the value in the column and the column to the
                // left are the same
                if (this.grid[i][j] == this.grid[i][j-1]) {
                    // adds up the values of the pairs
                    int total = this.grid[i][j] + this.grid[i][j-1];
                    // sets the total value to the column to the right
                    this.grid[i][j] = total;
                    // sets the the column to the left equal to 0
                    this.grid[i][j-1]=0;
                } 
            }
        }
        // this third for loop is used to move everything to the left after
        // taking care of pairs
        
        // iterates through all the rows
        for (int i = 0; i < this.grid.length; i++) {
            // nextSpot is used to if the spot to the right is open 
            // -takes into account multiple spots
            int nextSpot = 3;
            // iterates through all the columns
            for (int j = this.grid[0].length - 1; j >= 0; j--) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value to the right
                    this.grid[i][nextSpot] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most right side
                    if (j != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // decrements if a value is not equal to 0
                    nextSpot--;
                }
            }
        }
    }

    /**
     * performs a move upward
     *
     * Precondition: an upward move is possible.
     * Postcondition: The board is modified to reflect the move
     */
    private void moveUp() {
        for (int j = 0; j < this.grid[0].length; j++) {
            // nextSpot is used to if the spot above is open 
            // -takes into account multiple spots
            int nextSpot = 0;
            for (int i = 0; i < this.grid.length; i++) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value upward
                    this.grid[nextSpot][j] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most top side
                    if (i != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // increments if a value is not equal to 0
                    nextSpot++;
                }
            }
        }
        // this for loop is for finding pairs and adding them
        for (int j = 0; j < this.grid[0].length ; j++) {
            // only iterates in rows 0, 1, 2 because we are checking the
            // the row and the row below of it
            for (int i = 0; i < this.grid.length - 1 ; i++) {
                // checks if the value in the row and the row below
                // are the same
                if (this.grid[i][j] == this.grid[i+1][j]) {
                    // adds up the values of the pairs
                    int total = this.grid[i][j] + this.grid[i+1][j];
                    // sets the total value to the original row
                    this.grid[i][j] = total;
                    // sets the the row above equal to 0
                    this.grid[i+1][j]=0;
                } 
            }
        }
        // this third for loop is used to move everything upward after
        // taking care of pairs
        for (int j = 0; j < this.grid[0].length; j++) {
            // nextSpot is used to if the spot above is open 
            // -takes into account multiple spots
            int nextSpot = 0;
            for (int i = 0; i < this.grid.length; i++) {
                // checks if the value at row i and column j is not 0
                if (this.grid[i][j] != 0) {   
                    // moves the value upward
                    this.grid[nextSpot][j] = this.grid[i][j];
                    // makes the original spot equal to 0 
                    // also takes into account the most top side
                    if (i != nextSpot) {  
                        this.grid[i][j]=0;
                    }
                    // increments if a value is not equal to 0
                    nextSpot++;
                }
            }
        }  
    }

    /**
     * get a deep copy of the grid
     *
     * @return A copy of the grid
     */
    public int[][] getGrid() {
        int[][] gridCopy = new int[this.GRID_SIZE][this.GRID_SIZE];
        for (int r = 0; r < this.grid.length; r++) {
            for (int c = 0; c < this.grid[r].length; c++) {
                gridCopy[r][c] = this.grid[r][c];
            }
        }
        return gridCopy;
    }

    /**
     * set a deep copy of the grid
     *
     * @param newGrid the grid that you want to set to
     */
    public void setGrid(int[][] newGrid) {
        for (int r = 0; r < this.grid.length; r++) {
            for (int c = 0; c < this.grid[r].length; c++) {
                this.grid[r][c] = newGrid[r][c];
            }
        }
    }
}
