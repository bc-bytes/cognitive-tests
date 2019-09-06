package com.bc.memorytest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StringBuilder;
import com.bc.memorytest.Globals.AppState;

public class Test3 implements InputProcessor
{
    private Globals globals;
    private Stage stage;
    private Vector3 touchPos;
    private final String strIntroTextA;
    private final String strIntroTextB;
    private final String strTouchText;
    private final String strWellDone;
    private final String strPatterns;
    private final String strTestComplete;
    private final String strRetry;
    private final String strHeaderFileName;
    private final String strDetailFileName;
    private final Sprite whiteSquare1Sprite;
    private final Sprite whiteSquare2Sprite;
    private final Sprite shape1Sprite;
    private final Sprite shape2Sprite;
    private final Sprite shape3Sprite;
    private final Sprite shape4Sprite;
    private final Sprite shape5Sprite;
    private final Sprite shape6Sprite;
    private final Sprite shape7Sprite;
    private final Sprite shape8Sprite;
    private final Sprite shape9Sprite;
    private final Sprite shape10Sprite;
    private final Array<Sprite> shapes;
    private final Label lblPAL;
    private final TextButton btnBeginTest;
    private final TextButton btnMainMenu;
    private final StringBuilder sbIntroText;
    private final StringBuilder sbPattern;
    private final Table tblMenu;
    private final Array<Vector2> eightBoxPositions;
    private final Array<Vector2> sixBoxPositions;
    private State state;
    private final Array<Integer> shapesForCurrentSet;
    private final Array<Integer> shapeBoxes;
    private final Array<Integer> revealOrder;
    private final Array<Integer> shapeIndexes;
    private int testState;
    private int numberOfShapes;
    private float introElapsed;
    private final float introInterval;
    private float revealElapsed;
    private final float revealInterval;
    private float waitElapsed;
    private final float waitInterval;
    private int numberOfBoxes;
    private int currentShape;
    private final PALStage[] stages;
    private int currentSet;
    private int currentStage;
    private int numberOfSets;
    private final Rectangle rect;
    private Array<PALBox> boxes;
    private int currentBox;
    private int correctShapeCount;
    private int incorrectShapeCount;
    private int setFailCount;
    private Calendar startTime;
    private Calendar endTime;
    private final Array<PALDetail> palDetails;
    
    /** Enumerator used to indicate the current state of the PAL component. */
    private enum State
    {
        MENU,
        OPTIONS_MENU,
        PRACTICE,
        FULL
    }
    
    /** Enumerator used to indicate the current background colour. */
    private enum BGColour
    {
        WHITE,
        BLACK
    }
    
    /**
     * Test3 constructor. All class objects and variables are initialised here.
     * 
     * @param globals
     *   An initialised Globals object.
     */
    public Test3(final Globals globals)
    {
        this.globals = globals;
        stage = new Stage();
        touchPos = new Vector3(0, 0, 0);
        introInterval = 4.5f;
        revealInterval = 2.70f; // How long a box will be open for.
        waitInterval = 0.85f;
        rect = new Rectangle(0, 0, 0, 0);
        
        palDetails = new Array<PALDetail>();
        
        // Set strings.
        strIntroTextA = "In this test you will see ";
        strIntroTextB = " white boxes and they will\n" +
                        "open in a random order. There will be a pattern in\n" +
                        "one of the boxes and you have to remember which\n" +
                        "box it is in.\n\n" +
                        "Don't worry if you find it difficult or if you make a\n" +
                        "mistake. The test will take about 10 minues to\n" +
                        "complete.";
        strTouchText = "Touch the box where you saw this pattern appear.";
        strWellDone = "Well done, this time there will be ";
        strPatterns = " patterns.";
        strTestComplete = "Well done for completing the PAL memory test.";
        strRetry = "That was not quite right, let's look at the boxes again.";
        strHeaderFileName = "pal_header.csv";
        strDetailFileName = "pal_detail.csv";
        
        // Set StringBuilders.
        sbIntroText = new StringBuilder();
        sbPattern = new StringBuilder();
        
        // Set sprites.
        float scaleFactor = 1.85f;
        
        whiteSquare1Sprite = new Sprite(globals.spritesAtlas.createSprite("WhiteSquare1"));
        whiteSquare1Sprite.setSize(whiteSquare1Sprite.getWidth() * scaleFactor, 
                whiteSquare1Sprite.getHeight() * scaleFactor);
        whiteSquare2Sprite = new Sprite(globals.spritesAtlas.createSprite("WhiteSquare2"));
        whiteSquare2Sprite.setSize(whiteSquare2Sprite.getWidth() * scaleFactor, 
                whiteSquare2Sprite.getHeight() * scaleFactor);
        
        shape1Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape1"));
        shape2Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape2"));
        shape3Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape3"));
        shape4Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape4"));
        shape5Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape5"));
        shape6Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape6"));
        shape7Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape7"));
        shape8Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape8"));
        shape9Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape9"));
        shape10Sprite = new Sprite(globals.spritesAtlas.createSprite("PALShape10"));
        
        // Add sprites to array.
        shapes = new Array<Sprite>();
        shapes.add(shape1Sprite);
        shapes.add(shape2Sprite);
        shapes.add(shape3Sprite);
        shapes.add(shape4Sprite);
        shapes.add(shape5Sprite);
        shapes.add(shape6Sprite);
        shapes.add(shape7Sprite);
        shapes.add(shape8Sprite);
        shapes.add(shape9Sprite);
        shapes.add(shape10Sprite);
        
        // Flip and resize all sprites.
        scaleFactor = 2.5f;
        for (Sprite shape : shapes)
        {
            shape.flip(false, true);
            shape.setSize(shape.getWidth() * scaleFactor, shape.getHeight() * scaleFactor);
        }
        
        // Positions array for 6 boxes.
        sixBoxPositions = new Array<Vector2>();
        // Top middle.
        sixBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH / 2 - whiteSquare1Sprite.getWidth() / 2, 40));
        // Left upper.
        sixBoxPositions.add(new Vector2(40, globals.VIRTUAL_HEIGHT / 2 - whiteSquare1Sprite.getHeight() - 60));		
        // Right upper.
        sixBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH - whiteSquare1Sprite.getWidth() - 40, 
                globals.VIRTUAL_HEIGHT / 2 - whiteSquare1Sprite.getHeight() - 60));		
        // Left lower.
        sixBoxPositions.add(new Vector2(40, globals.VIRTUAL_HEIGHT / 2 + 60));
        // Right lower.
        sixBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH - whiteSquare1Sprite.getWidth() - 40, 
                globals.VIRTUAL_HEIGHT / 2 + 60));
        // Bottom middle.
        sixBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH / 2 - whiteSquare1Sprite.getWidth() / 2, 
                globals.VIRTUAL_HEIGHT - whiteSquare1Sprite.getHeight() - 40));		
        
        // Positions array for 8 boxes.
        eightBoxPositions = new Array<Vector2>();
        // Top left.
        eightBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH / 2 - whiteSquare1Sprite.getWidth() / 2 - 150, 40));
        // Top right.
        eightBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH / 2 - whiteSquare1Sprite.getWidth() / 2 + 150, 40));
        // Left upper.
        eightBoxPositions.add(new Vector2(40, 180));
        // Right upper.
        eightBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH - whiteSquare1Sprite.getWidth() - 40, 180));
        // Left lower.
        eightBoxPositions.add(new Vector2(40, 440));
        // Right lower.
        eightBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH - whiteSquare1Sprite.getWidth() - 40, 440));
        // Bottom left.
        eightBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH / 2 - whiteSquare1Sprite.getWidth() / 2 - 150, 
                globals.VIRTUAL_HEIGHT - whiteSquare1Sprite.getHeight() - 40));
        // Bottom right.
        eightBoxPositions.add(new Vector2(globals.VIRTUAL_WIDTH / 2 - whiteSquare1Sprite.getWidth() / 2 + 150, 
                globals.VIRTUAL_HEIGHT - whiteSquare1Sprite.getHeight() - 40));		
        
        // Array to store shapes used in current set. 
        shapesForCurrentSet = new Array<Integer>();
        
        // Array to store which boxes contain shapes.
        shapeBoxes = new Array<Integer>();
        
        // Array to store reveal order.
        revealOrder = new Array<Integer>();
        
        // Array to store the index of each shape stored in shapeBoxes array.
        shapeIndexes = new Array<Integer>();
        
        // Define number of shapes per set for each stage (for 8 boxes).
        // 1. Three sets with 1 shape.
        // 2. Two sets with 2 shapes.
        // 3. Two sets with 3 shapes.
        // 4. One set with 6 shapes.
        // 5. One set with 8 shapes.
//		stages = new PALStage[5];
//		stages[0] = new PALStage(3, 1);
//		stages[1] = new PALStage(2, 2);
//		stages[2] = new PALStage(2, 3);
//		stages[3] = new PALStage(1, 6);
//		stages[4] = new PALStage(1, 8);
        
        //stage 1: 1 pattern
        //stage 2: 2 patterns
        //stage 3: 3 patterns
        //stage 4: 6 patterns
        
        // Define number of shapes per set for each stage (for 6 boxes).
        // 1. One set with 1 shape.
        // 2. One set with 2 shapes.
        // 3. One set with 3 shapes.
        // 4. One set with 6 shapes.
        stages = new PALStage[4];
        stages[0] = new PALStage(1, 1);
        stages[1] = new PALStage(1, 2);
        stages[2] = new PALStage(1, 3);
        stages[3] = new PALStage(1, 6);
        
        // Array of boxes used for this test.
        boxes = new Array<PALBox>();
        
        // Create widgets.
        lblPAL = new Label("Paired Associates Learning Menu", globals.skin);
        lblPAL.setAlignment(Align.center);
        btnBeginTest = new TextButton("Begin Test", globals.skin);
        btnMainMenu = new TextButton("Main Menu", globals.skin);
        
        // Create listeners.
        btnBeginTest.addListener(new ChangeListener() 
        {			
            public void changed(ChangeEvent event, Actor actor) 
            {
                sbIntroText.setLength(0);
                sbIntroText.append(strIntroTextA);
                sbIntroText.append(numberOfBoxes = 6);
                sbIntroText.append(strIntroTextB);
                
                new Dialog("Test Instructions", globals.skin, "dialog")
                {
                    protected void result (Object object) 
                    {
                        //System.out.println("Chosen: " + object);
                        if (object.equals(true))
                        {
                            state = State.FULL; 
                            SetBGColour(BGColour.BLACK);                    		
                            testState = 1;
                        }
                    }
                }.text(sbIntroText.toString())
                 .button("Cancel", false) 
                 .button("Start", true)
                 .show(stage);
            }
        });
        
        btnMainMenu.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                Gdx.input.setInputProcessor(null);
                state = null;
                globals.appState = AppState.MENU; 
            }
        });
        
        // Create Menu table.
        tblMenu = new Table(globals.skin);
        AddWidgets();
    }
    
    /** Clear stage and menu then add widgets to table. */
    private void AddWidgets()
    {
        stage.clear();
        tblMenu.clear();
        tblMenu.setFillParent(true);
        tblMenu.row().height(150);
        tblMenu.add(lblPAL).width(700);
        tblMenu.row().height(150);
        tblMenu.add(btnBeginTest).align(Align.center).width(700);
        tblMenu.row().height(150);
        tblMenu.add(btnMainMenu).align(Align.center).width(700);
        stage.addActor(tblMenu);
    }
    
    /**
     * Method to switch colours of background.
     * 
     * @param colour
     *   A valid BGColour enum value.
     */
    private void SetBGColour(BGColour colour)
    {
        if (colour == BGColour.BLACK) // black for test
        {
            globals.glClearColourR = 0;
            globals.glClearColourG = 0;
            globals.glClearColourB = 0;
        }
        else // white for menus
        {
            globals.glClearColourR = 1;
            globals.glClearColourG = 1;
            globals.glClearColourB = 1;
        }
    }
    
    /**
     * Method to be called before the start of each set.
     * 
     * @param numberOfShapes
     *   An int indicating the number of shapes to place in the boxes for the current set.
     */
    private void SetupSet(int numberOfShapes)
    {
        // Add random shapes used for current set.
        shapesForCurrentSet.clear();
        for (int i = 0; i < shapes.size; i++)
        {
            shapesForCurrentSet.add(i);
        }
        shapesForCurrentSet.shuffle();
        shapesForCurrentSet.truncate(numberOfShapes);

        // Add all current shapes to shapeBoxes array.
        shapeBoxes.clear();
        for (int i = 0; i < numberOfBoxes; i++)
        {
            // If no more shapes to add, then add "9999" marker - indicates that no shape is present in current box.
            if (i < numberOfShapes)
                shapeBoxes.add(shapesForCurrentSet.get(i));
            else
                shapeBoxes.add(9999);
        }		
        // Randomise.
        shapeBoxes.shuffle();
        
        // Add index of each shape in shapeBoxes to shapeIndexes array.
        shapeIndexes.clear();
        for (int i = 0; i < numberOfBoxes; i++)
        {
            if (shapeBoxes.get(i) != 9999)
                shapeIndexes.add(shapeBoxes.get(i));
        }
        
        SetRevealOrder();
        
        // Populate box objects.
        boxes.clear();
        for (int i = 0; i < numberOfBoxes; i++)
        {
            boxes.add(new PALBox(i, shapeBoxes.get(i), false));
        }
    }
    
    /** Method to set the random box reveal order. */
    private void SetRevealOrder()
    {
        // Set random reveal order for current set.
        revealOrder.clear();
        for (int i = 0; i < numberOfBoxes; i++)
        {
            revealOrder.add(i);
        }
        revealOrder.shuffle();
    }
    
    /** Method to update the message displayed when either one or more shapes are in the current set. */
    private void SetPatternMessage()
    {
        sbPattern.setLength(0);		
        sbPattern.append(strWellDone);
        sbPattern.append(numberOfShapes);
        sbPattern.append(strPatterns);
    }
    
    /** Update method called once per frame. Updates all logic before each draw call. */
    public void Update()
    {
        if (state == null)
        {
            state = State.MENU;
            Gdx.input.setInputProcessor(stage);
            //SetTable(tblMenu);
        }
        
        // Practice test sequence?		
        
        // Full test sequence (Straus et al., 2006):
        // 1. Three sets with 1 shape.
        // 2. Two sets with 2 shapes.
        // 3. Two sets with 3 shapes.
        // 4. One set with 6 shapes.
        // 5. One set with 8 shapes.
        
        // 8 layout:
        // 
        //   O O
        // O     O
        // O     O
        //   O O
        //
        // 8 layout indexes:
        //
        //   0 1
        // 2     3
        // 4     5
        //   6 7
        
        // 6 layout (as per cantab):
        // 
        //    O 
        // O     O
        // O     O
        //    O 
        //
        // 6 layout indexes:
        //
        //    0 
        // 1     2
        // 3     4
        //    5
        
        switch (state)
        {
            case MENU: // Update listeners.
                stage.act(Gdx.graphics.getDeltaTime());
                break;
                
            case FULL:				
                switch (testState)
                {
                    case 1: // Randomly set positions of shape(s) for current test (1st stage).
                        numberOfSets = stages[0].getNumberOfSets();
                        numberOfShapes = stages[0].getNumberOfShapes();
                        SetupSet(numberOfShapes);
                        introElapsed = 0;
                        revealElapsed = 0;
                        testState = 2;
                        currentStage = 0;
                        currentShape = 0;
                        currentSet = 0;
                        currentBox = 0;
                        correctShapeCount = 0;
                        incorrectShapeCount = 0;
                        setFailCount = 0;
                        SetPatternMessage();
                        Gdx.input.setInputProcessor(this);
                        startTime = Calendar.getInstance();						
                        palDetails.clear();
                        break;
                        
                    case 2: // Display "x Patterns" message.
                        if (currentStage == 0 && currentSet == 0)
                        {
                            testState = 3;
                            return;
                        }
                        
                        introElapsed += Gdx.graphics.getDeltaTime();
                        if (introElapsed > introInterval)
                        {
                            introElapsed = 0;
                            revealElapsed = 0;
                            testState = 3;
                            waitElapsed = 0;
                        }
                        break;
                        
                    case 3: // Wait for x seconds.
                        waitElapsed += Gdx.graphics.getDeltaTime();
                        if (waitElapsed > waitInterval)
                        {
                            waitElapsed = 0;
                            testState = 4;
                        }
                        break;
                        
                    case 4: // Reveal each box, 1 at a time, in random order.						
                        revealElapsed += Gdx.graphics.getDeltaTime();
                        if (revealElapsed > revealInterval)
                        {
                            revealElapsed = 0;
                            
                            // Open next box specified by revealOrder array.
                            //currentOpenBox = revealOrder.get(currentBox);							
                            currentBox++;
                                                        
                            if (currentBox == numberOfBoxes)
                                testState = 5;
                            else
                                testState = 3;
                        } 
                        break;
                        
                    case 5: // Wait for x seconds.
                        waitElapsed += Gdx.graphics.getDeltaTime();
                        if (waitElapsed > waitInterval)
                        {
                            waitElapsed = 0;
                            testState = 6;
                        }
                        break;
                        
                    case 6: // Display all shapes in sequence, await user input.
                        
                        break;
                        
                    case 7: // Process next Stage/Set/Shape after user input.
                        introElapsed = 0;
                        currentShape++;
                        
                        // Have all shapes for current set been completed?
                        if (currentShape == numberOfShapes)
                        {
                            // Repeat set if user didn't correctly find all shapes.
                            if (numberOfShapes != correctShapeCount)
                            {
                                // End test if 3 consecutive sets in a stage have been failed.								
                                if (setFailCount == 3)
                                {
                                    endTime = Calendar.getInstance();
                                    AddDetailRecord();
                                    Save();
                                    testState = 8;
                                    return;
                                }
                                
                                SetRevealOrder(); // Set new reveal order.
                                //correctShapeCount = 0;
                                //incorrectShapeCount = 0;
                                currentShape = 0;
                                currentBox = 0;
                                testState = 9;
                                return;
                            }
                            
                            // All shapes for current set completed - save detail record for last completed set.
                            AddDetailRecord();
                                                        
                            currentSet++;
                            currentBox = 0;
                            
                            // Have all sets been completed for current stage?
                            if (currentSet == numberOfSets)
                            {								
                                // All sets for current stage completed.								
                                currentSet = 0;
                                currentStage++;
                                
                                // Have all stages been completed?
                                if (currentStage == stages.length)
                                {
                                    // All stages of full test complete.
                                    endTime = Calendar.getInstance();
                                    Save();
                                    testState = 8;
                                }
                                else
                                {
                                    // All stages not completed, goto next stage.
                                    currentShape = 0;
                                    correctShapeCount = 0;
                                    incorrectShapeCount = 0;
                                    setFailCount = 0;
                                    numberOfSets = stages[currentStage].getNumberOfSets();
                                    numberOfShapes = stages[currentStage].getNumberOfShapes();
                                    SetupSet(numberOfShapes);
                                    SetPatternMessage();									
                                    testState = 2;
                                }
                            }
                            else // All sets not completed for current stage, goto next set.
                            {
                                // Save detail record for last completed set.
                                AddDetailRecord();
                                
                                currentShape = 0;
                                correctShapeCount = 0;
                                incorrectShapeCount = 0;
                                setFailCount = 0;
                                numberOfSets = stages[currentStage].getNumberOfSets();
                                numberOfShapes = stages[currentStage].getNumberOfShapes();
                                SetupSet(numberOfShapes);
                                SetPatternMessage();
                                testState = 2;
                            }
                        }
                        else
                        {
                            // Await input for next shape.								
                            testState = 6;
                            return;
                        }
                        break;
                        
                    case 8: // All stages of test completed, or 3 consecutive sets failed in current stage.
                        
                        break;
                        
                    case 9: // Set failed, displaying "not quite right" message.
                        introElapsed += Gdx.graphics.getDeltaTime();
                        if (introElapsed > introInterval)
                        {
                            introElapsed = 0;
                            revealElapsed = 0;
                            testState = 3;
                            waitElapsed = 0;
                            setFailCount++;
                        }
                        break;
                }
                break;
        }
    }
    
    /** Adds a new detail record to palDetails Array. To be called when a set or stage has been completed. */
    private void AddDetailRecord()
    {
        // Save detail record for last completed set.
        PALDetail detail = new PALDetail();
        detail.stageNumber = currentStage + 1;
        detail.setNumber = currentSet + 1;
        detail.patternCount = stages[currentStage].getNumberOfShapes();
        detail.correctCount = correctShapeCount;
        detail.wrongCount = incorrectShapeCount; 								
        palDetails.add(detail);
    }
    
    /**
     * Draw method, called once per frame after logic updates.
     * 
     * @param spriteBatch
     *   An initialised SpriteBatch object.
     */
    public void Draw(SpriteBatch spriteBatch)
    {
        switch (state)
        {
            case MENU:
            case OPTIONS_MENU:
                stage.draw();
                break;
                
            case PRACTICE:		
                
                break;
                
            case FULL:
                globals.wascoSans20.setColor(Color.WHITE);
                globals.wascoSans30.setColor(Color.WHITE);
                
                // Draw closed boxes.	
                if (testState > 2 && testState < 7)
                {
                    for (Vector2 pos : sixBoxPositions)
                    {
                        whiteSquare1Sprite.setPosition(pos.x, pos.y);
                        whiteSquare1Sprite.draw(spriteBatch);
                    }
                    
                    // Display debug info.
//					globals.wascoSans30.draw(spriteBatch, "Current Pattern: " + (currentShape + 1), 0, 0);
//					globals.wascoSans30.draw(spriteBatch, "Current Set: " + (currentSet + 1), 350, 0);
//					globals.wascoSans30.draw(spriteBatch, "Current Stage: " + (currentStage + 1) + " / " + 
//							stages.length, 600, 0);
//					globals.wascoSans30.draw(spriteBatch, "Fingers: " + fingersOnScreen, 910, 0);					
                }
                
                switch (testState)
                {
                    case 2: // Display stage/set intro message.						
                        globals.wascoSans30.drawWrapped(spriteBatch, sbPattern, 0, 
                                390, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
                        break;

                    case 4: // Draw each opened box & shape if present in box.
                        
                        // Draw open box.
                        int shapeIndex = 0;
                        boolean showShape = false;
                        for (PALBox box : boxes)
                        {
                            if (box.getBoxIndex() == revealOrder.get(currentBox))
                            {
                                whiteSquare2Sprite.setPosition(
                                        sixBoxPositions.get(box.getBoxIndex()).x, 
                                        sixBoxPositions.get(box.getBoxIndex()).y);
                                whiteSquare2Sprite.draw(spriteBatch);
                                
                                // Draw shape, if present.
                                if (box.getShapeIndex() != 9999)
                                {
                                    showShape = true;
                                    shapeIndex = box.getShapeIndex();
                                }
                                break;
                            }
                        }
                        
                        // Is a shape currently being shown?
                        if (showShape)
                        {
                            // Draw current shape in center of open box.
                            shapes.get(shapeIndex).setPosition(
                                    whiteSquare2Sprite.getX() + whiteSquare2Sprite.getWidth() / 2 - 
                                    shapes.get(shapeIndex).getWidth() / 2, 
                                    whiteSquare2Sprite.getY() + whiteSquare2Sprite.getHeight() / 2 - 
                                    shapes.get(shapeIndex).getHeight() / 2);
                            shapes.get(shapeIndex).draw(spriteBatch);
                        }											
                        break;
                        
                    case 6: // Draw current shape in middle of screen (awaiting input from user).
                        whiteSquare2Sprite.setPosition(globals.VIRTUAL_WIDTH / 2 - whiteSquare2Sprite.getWidth() / 2, 
                                globals.VIRTUAL_HEIGHT / 2 - whiteSquare2Sprite.getHeight() / 2);
                        whiteSquare2Sprite.draw(spriteBatch);
                        shapes.get(shapeIndexes.get(currentShape)).setPosition(
                                globals.VIRTUAL_WIDTH / 2 - shapes.get(shapeIndexes.get(currentShape)).getWidth() / 2, 
                                globals.VIRTUAL_HEIGHT / 2 - shapes.get(shapeIndexes.get(currentShape)).getHeight() / 2);
                        shapes.get(shapeIndexes.get(currentShape)).draw(spriteBatch);						
                        
                        // Draw hint text for 1st pattern of 1st set of 1st stage only.
                        if (currentShape == 0 && currentSet == 0 && currentStage == 0)
                        {
                            globals.wascoSans20.drawWrapped(spriteBatch, strTouchText, 0, 
                                    whiteSquare2Sprite.getY() + whiteSquare2Sprite.getHeight() + 50, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
                        }
                        break;
                        
                    case 8: // Display "test complete" message.
                        globals.wascoSans30.drawWrapped(spriteBatch, strTestComplete, 0, 
                                390, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
                        break;
                        
                    case 9: // Displaying "not quite right" message.
                        globals.wascoSans30.drawWrapped(spriteBatch, strRetry, 0, 
                                390, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
                        break;
                }
                
                break;
        }
    }
    
    /** Release all resources used by objects that are disposable. */
    public void Dispose()
    {
        stage.dispose();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        // Disable multi-touch.
        if (pointer > 0 || button > 0)
            return false;
        
        // Translate touch co-ords to virtual screen co-ords.
        touchPos.x = screenX;
        touchPos.y = screenY;
        touchPos.z = 0;
        
        // Need this line only for desktop build when window size != VIRTUAL sizes in globals.
        globals.camera.unproject(touchPos, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        if (state == State.FULL)
        {
            switch (testState)
            {
                case 6: // Construct a rectangle around each box and test if user touched it.
                    int boxIndex = 0;					
                    for (Vector2 pos : sixBoxPositions)
                    {
                        rect.x = pos.x;
                        rect.y = pos.y;
                        rect.width = whiteSquare1Sprite.getWidth();
                        rect.height = whiteSquare1Sprite.getHeight();
                        
                        // Was the current box touched?
                        if (rect.contains(touchPos.x, touchPos.y))
                        {
                            // Does the touched box contain the current pattern?
                            if (shapeIndexes.get(currentShape) == shapeBoxes.get(boxIndex))
                            {
                                // Increment score to go here...
                                correctShapeCount++;
                                testState = 7;
                                return true;
                            }
                            else // Box touched is either empty or does not contain the current pattern.
                            {
                                // Decrement score to go here...
                                incorrectShapeCount++;
                                testState = 7;
                                return true;
                            }
                        }
                        boxIndex++;
                    }
                    
                    // No
                    break;
            }
        }
        
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        // Disable multi-touch.
        if (pointer > 0 || button > 0)
            return false;
        
        // Quit test if two fingers touched the screen.
//		if (state == State.FULL && fingersOnScreen == 2)
//		{
//			SetBGColour(BGColour.WHITE);
//			AddWidgets();
//			Gdx.input.setInputProcessor(stage);			
//			state = State.MENU;
//		}
//		fingersOnScreen--;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
    
    /** Append data for last test to PAL csv files. */
    private void Save()
    {		
        if (Gdx.files.isLocalStorageAvailable())
        {
            BufferedWriter out = null;
            boolean error = false;
            
            // Header
            try
            {
                out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strHeaderFileName).write(true)));
                
                out.write(globals.sbCurrentParticipantID.toString());
                out.write(globals.strComma);
                out.write(Long.toString(startTime.getTimeInMillis()));
                out.write(globals.strComma);
                out.write(String.valueOf(boxes.size)); // box count
                out.write(globals.strComma);
                out.write(Long.toString(endTime.getTimeInMillis()));
                out.write(globals.strNewLine);
            }
            catch (GdxRuntimeException gre)
            {
                gre.printStackTrace();
                //message += "Couldn't open localstorage/test.txt\n";
                error = true;
            }
            catch (IOException ioE1) 
            {
                ioE1.printStackTrace();
                error = true;
            }
            finally
            {
                if (out != null)
                {
                    try
                    {
                        out.close();
                    }
                    catch (IOException ioE2)
                    {
                        ioE2.printStackTrace();
                    }
                }
            }
            
            if (error)
                return;
            
            // Detail
            try
            {
                out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strDetailFileName).write(true)));
                
                for (int i = 0; i < palDetails.size; i++)
                {
                    out.write(globals.sbCurrentParticipantID.toString());
                    out.write(globals.strComma);
                    out.write(String.valueOf(palDetails.get(i).stageNumber));
                    out.write(globals.strComma);
                    out.write(String.valueOf(palDetails.get(i).setNumber));
                    out.write(globals.strComma);
                    out.write(String.valueOf(palDetails.get(i).patternCount));
                    out.write(globals.strComma);
                    out.write(String.valueOf(palDetails.get(i).correctCount));
                    out.write(globals.strComma);
                    out.write(String.valueOf(palDetails.get(i).wrongCount));
                    out.write(globals.strNewLine);
                }
            }
            catch (GdxRuntimeException gre)
            {
                gre.printStackTrace();
                //message += "Couldn't open localstorage/test.txt\n";
            }
            catch (IOException ioE1) 
            {
                ioE1.printStackTrace();
            }
            finally
            {
                if (out != null)
                {
                    try
                    {
                        out.close();
                    }
                    catch (IOException ioE2)
                    {
                        ioE2.printStackTrace();
                    }
                }
            }
        }
    }
}