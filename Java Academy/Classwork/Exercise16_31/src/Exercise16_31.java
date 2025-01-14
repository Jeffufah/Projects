/*
 * Course ID: EYF-649
 * Submission type: Assignment 16.31
 * Due Date: 2018/10/01
 * Author: Jeffrey McMullen II
 * Description: This program allows the user to play Connect Four. The user
 * will click empty circles to drop a color chip down the column clicked.
 * If four chips of the same color are alligned vertically, horizontally, or
 * diagonally, the owner of those color chips will win. If all slots are filled,
 * the game results in a tie. The users can click the reset button to start the
 * game over.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Exercise16_31 extends Application 
{   
    @Override
    public void start(Stage primaryStage) 
    {
        //Stores the fillable slots. playingGrid is 7 columns by 6 rows.
        PlayingSlot[][] playingGrid = new PlayingSlot[7][6];

        //Stores the locations of the indicators for each column.
        ArrowIndicator[] arrowIndicators = new ArrowIndicator[7];
        
        //Create a StackPane to hold all the images.
        StackPane root = new StackPane();
        
        //Set the scene to the StackPane with dimensions 1920 x 1080
        Scene scene = new Scene(root, 1920, 1080);
        
        //Create a Background object passing it parameters to create a
        //Rectangle with an event for OnMouseOver.
        GameBackground gameBackground = new GameBackground(1920,1080, arrowIndicators);
        root.getChildren().add(gameBackground.getBackground());
        
        //Create a PlayingBoard object passing dimensions, color value, and root
        //to setup its display.
        PlayingBoard playingBoard = new PlayingBoard(1200.0, 750.0, Color.color(214.0 / 255.0, 214.0 / 255.0, 79.0 / 255.0));  
        root.getChildren().add(playingBoard.getBoard());
        
        //Create a ResetButton object passign the playingGrid and root to it
        //for handling an event to reset the game when the button is clicked.
        ResetButton resetButton = new ResetButton(playingGrid);
        root.getChildren().add(resetButton.getButton());
        
        
        
        //Used to space the rows of chip slots.
        double rowOffset = 400.0;
        
        //Used to space the columns of chip slots.
        double columnOffset = -635.0;
        
        //Create an outer forloop to establish the placement of the columns from
        //left to right.
        for (int columnCounter = 0; columnCounter < playingGrid.length; columnCounter++)
        {
            //Increment columnOffset to move the column placement towards the right.
            columnOffset += 158;
            
            //Create an arrowIndicator and use the columnOffset for spacing and
            //the columnCounter for the column placement.
            ArrowIndicator arrowIndicator = new ArrowIndicator(columnOffset, columnCounter);
            
            //Assign the index arrowIndicators pointed to by the columnCounter
            //to the arrowIndicator object created.
            arrowIndicators[columnCounter] = arrowIndicator;
            
            root.getChildren().addAll(arrowIndicator.getIndicator(), 
                    arrowIndicator.getArrowStem(), arrowIndicator.getArrowHead());
            
            //Create an inner forloop to establish the placement of the rows from
            //bottom to top.
            for (int rowCounter = 0; rowCounter < playingGrid[0].length; rowCounter++)
            {
                //Decrement rowOffset to move the row placement upward.
                rowOffset -= 110;
                
                //Create a new playingSlot and use its constructor to make
                //contain a playing chip.
                PlayingSlot playingSlot = new PlayingSlot(columnOffset, rowOffset, arrowIndicators, playingGrid, resetButton);
                
                //Assign the playingSlot to the index values pointed to by
                //the row and column counters.
                playingGrid[columnCounter][rowCounter] = playingSlot;
                
                //Assign the playingSlot's chip id value to the value contained
                //in the column counter.
                playingSlot.getChip().setId(Integer.toString(columnCounter));
                
                root.getChildren().add(playingSlot.getBorder());
                root.getChildren().add(playingSlot.getChip());
            }
            
            //Reset rowOffset.
            rowOffset = 400.0;
        }
        
        primaryStage.setTitle("Connect Four");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }      

    /**
     * This method will first attempt to place a chip down the column that the 
     * player had clicked on, and if a chip was placed, then check all neighbors
     * of that chip to see if four chips are aligned.
     * @param slotId A String containing the columnIndex of the column clicked.
     * @param playingGrid A two dimensional array of type Circle containing the 
     * locations of every playable playingGrid slot.
     * @param arrowIndicators An array of type ArrowIndicator that contains
     * the location of each arrowIndicator at each column.
     * @param resetButton An object of type ResetButton that displays text within
     * when the game has ended.
     */
    public static void dropChip(String slotId, PlayingSlot[][] playingGrid, ArrowIndicator[] arrowIndicators, ResetButton resetButton)
    {        
        //Contains the column for the chip to be dropped down based on the value
        //stored in slotId.
        int columnIndex = Integer.parseInt(slotId);
        
        //Will determine the row that the chip is to appear at. -1 as a safety
        //feature if the row happens to be unassigned.
        int rowIndex = -1;
        
        //Create a circle object named slot to represent the slot that will
        //represent the dropped chip.
        PlayingSlot slot;
        
        //Working from the bottom up trying to find an empty slot to fill. At
        //the columnIndex passed as an argument to this method.
        for (int rowCounter = 0; rowCounter < playingGrid[columnIndex].length; rowCounter++)
        {
            slot = playingGrid[columnIndex][rowCounter];

            //If the slot color is White, and since we're working from the bottom
            //up, then the next available slot must have been reached and can
            //be safely changed to a color according to which player's turn it is.
            if (slot.getChip().getFill() == Color.WHITE)
            {
                if (GameManager.getTurnStatus() == 1)
                {
                    slot.getChip().setFill(Color.RED);
                }
                else
                {
                    slot.getChip().setFill(Color.BLACK);
                }
                
                rowIndex = rowCounter;
                
                break;
            }
        }

        //Provided that rowIndex was assigned a value that isn't -1, check to see if four chips are lined up by the same
        //color. Otherwise, increment the turn counter and turn over to the alternate
        //player color.
        if (rowIndex != -1)
        {
            //Create an array of type Circle that will either contain four
            //Circle objects, or the array will be assigned a null return value
            //from checkFour method instead.
            PlayingSlot[] slots = checkFour(columnIndex, rowIndex, playingGrid);
            
            //If the slots array is not null, change their color to green,
            //and also hide all chip indicators. A player can only win during their
            //turn, so the GameManager's getTurnStatus method can be used to determine
            //who was the winner of the game.
            if (slots != null)
            {
                for (PlayingSlot currentSlot : slots)
                {
                    currentSlot.getChip().setFill(Color.GREEN);
                }
                
                GameManager.hideChipIndicators(arrowIndicators);
                
                if (GameManager.getTurnStatus() == 1)
                {
                    resetButton.setText("Red Wins!: Click to reset.");
                }
                else
                {
                    resetButton.setText("Black Wins!: Click to reset.");
                }
                
                GameManager.setIsGameOver();
            }
            else
            {
                GameManager.incrementTurn();
                GameManager.showChipIndicator(arrowIndicators[columnIndex]);

                if (playingGrid[columnIndex][playingGrid[columnIndex].length - 1].getChip().getFill() != Color.WHITE)
                {
                    GameManager.hideChipIndicators(arrowIndicators);
                }

                if (GameManager.getTurnCount() == 42)
                {
                    resetButton.setText("Draw!: Click to reset.");
                    GameManager.setIsGameOver();
                }
            }
        }
    }
    
    /**
     * This method will utilize loops to check all directions relative to the 
     * most recently dropped chip to find four chips aligned with the same color.
     * @param columnIndex An integer pointing to the column that the chip was dropped
     * down.
     * @param rowIndex An integer pointing to the row the where the dropped chip
     * rests.
     * @param playingGrid A two dimensional array of type Circle containing the 
     * locations of every playable playingGrid slot.
     * @return Either an array of the four aligned, color matching chips, or a null
     * value because four aligned, color matching chips were not found.
     */
    public static PlayingSlot[] checkFour(int columnIndex, int rowIndex, PlayingSlot[][] playingGrid)
    {
        //Used to store the color of the chip most recently dropped and stored in
        //the playingGrid.
        Color chipColor;
        
        if (GameManager.getTurnStatus() == 1)
        {
            chipColor = Color.RED;
        }
        else
        {
            chipColor = Color.BLACK;
        }
        
        //Create a Circle array to be filled by four matching color chips.
        PlayingSlot[] slots = new PlayingSlot[4];
        
        //Create a slot counter integer for catching when four matching color
        //chips have been found.
        int slotCounter = 0;       
        
        //Vertical check from Bottom to Top
        for (int rowCounter = 0; rowCounter < playingGrid[columnIndex].length; rowCounter++)
        {
            if (playingGrid[columnIndex][rowCounter].getChip().getFill() == chipColor)
            {
                slots[slotCounter] = playingGrid[columnIndex][rowCounter];
                slotCounter++;
                if (slotCounter > 3)
                {
                    //FourConnected
                    return slots;
                }
            }
            else
            {
                slotCounter = 0;
            }
        }
        
        
        //Reset the slotCounter to zero.
        slotCounter = 0;
        
        //Horizontal check from Left to Right
        for (int columnCounter = 0; columnCounter < playingGrid.length; columnCounter++)
        {
            if (playingGrid[columnCounter][rowIndex].getChip().getFill() == chipColor)
            {
                slots[slotCounter] = playingGrid[columnCounter][rowIndex];
                slotCounter++;
                if (slotCounter > 3)
                {
                    //FourConnected
                    return slots;
                }
            }
            else
            {
                slotCounter = 0;
            }
        }
        
        
        
        //Start from the coordinate where most recent chip was placed.
        int xCoordinate = columnIndex;
        int yCoordinate = rowIndex;

        //Get the Top Left coordinate to start with.
        while(true)
        {
            if (xCoordinate > 0 && yCoordinate < playingGrid[rowIndex].length - 1)
            {
                xCoordinate--;
                yCoordinate++;
            }
            else
            {
                break;
            }
        }
        
        
        
        //Reset the slotCounter to zero.
        slotCounter = 0;
        
        //Diagonal check from Top Left to Bottom Right
        while(true)
        {
            if (xCoordinate < playingGrid.length && yCoordinate >= 0)
            {
                if (playingGrid[xCoordinate][yCoordinate].getChip().getFill() == chipColor)
                {
                    slots[slotCounter] = playingGrid[xCoordinate][yCoordinate];
                    slotCounter++;
                    if (slotCounter > 3)
                    {
                        //FourConnected
                        return slots;
                    }
                }
                else
                {
                    slotCounter = 0;
                }
                
                xCoordinate++;
                yCoordinate--;
            }
            else
            {
                break;
            }
        }

        
        
        //Restart from the coordinate where most recent chip was placed.
        xCoordinate = columnIndex;
        yCoordinate = rowIndex;
        
        //Get the Top Right Coordinate to start with.
        while(true)
        {
            if (xCoordinate < playingGrid.length - 1 && yCoordinate < playingGrid[columnIndex].length - 1)
            {
                xCoordinate++;
                yCoordinate++;
            }
            else
            {
                break;
            }
        }
        
        //Reset the slotCounter to zero.
        slotCounter = 0;
        
        //Diagonal check from Top Right to Bottom Left
        while(true)
        {
            if (xCoordinate >= 0 && yCoordinate >= 0)
            {
                if (playingGrid[xCoordinate][yCoordinate].getChip().getFill() == chipColor)
                {
                    slots[slotCounter] = playingGrid[xCoordinate][yCoordinate];
                    slotCounter++;
                    if (slotCounter > 3)
                    {
                        //FourConnected
                        return slots;
                    }
                }
                else
                {
                    slotCounter = 0;
                }
                
                xCoordinate--;
                yCoordinate--;
            }
            else
            {
                break;
            }
        }
        
        return null;
    }
}