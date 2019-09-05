package com.bc.memorytest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.bc.memorytest.Globals.AppState;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.StringBuilder;

/**
 * Class to handle all Trail Making Test logic.
 * 
 * @author Bill Cassidy
 */
public final class Test1 implements InputProcessor
{
	private final Array<Sprite> numberSprites;
	private final Array<Sprite> letterSprites;
	private final Array<Sprite> numLetSprites; // alternating numbers & letters
	private Array<Sprite> currentSprites;
	private final Array<Vector2> positionsABSample;
	private final Array<Vector2> positionsAFull;
	private final Array<Vector2> positionsBFull;
	private final Globals globals;
	private final Sprite hand;
	private State state;
	private State nextState;
	private State oldState;
	private TestSize testSize;
	private TestType testType;
	private final Vector3 touchPos;
	private int correctCirclesTouched;
	private int currentlyTouchedCircle;
	private final ShapeRenderer shapeRenderer;
	private final Array<Vector2> drawPoints;
	private int demoState;
	private int handPosIndex;
	private final Vector2 beginPos;
	private final Vector2 endPos;
	private final Vector2 handStartPos;
	private final Vector2 handCurrentPos;
	private final Vector2 handEndPos;
	private final int handOffsetX;
	private final int handOffsetY;
	private final Tween tween;
	private Vector2 vect2A; // re-usable Vector2
	private Vector2 vect2B; // re-usable Vector2
	private final int demoPointOffset;
	private final Stage stage;
	private final Label lblTrailMaking;
	private final Label lblTestComplete;
	private final TextButton btnMainMenu;
	private final TextButton btnDemoA;
	private final TextButton btnDemoB;
	private final TextButton btnPartASample;
	private final TextButton btnPartAFull;		
	private final TextButton btnPartBSample;
	private final TextButton btnPartBFull;
	private final Table tblMenu;
	private final Table tblTestComplete;
	private final TextButton btnBackToTrailMakingMenu;
	private float handWidth;
	private float handHeight;
	private float waitElapsed;
    private final float waitInterval;
    private final String demoAInstructions;
    private final String demoBInstructions;
    private final String partASampleInstructions;
    private final String partAFullInstructions;
    private final String partBSampleInstructions;
    private final String partBFullInstructions;
    private final String strBegin;
    private final String strEnd;
    private final String reason1ErrorMsg;
    private final String reason2ErrorMsg;
    private final String reason3ErrorMsg;
    private final String reason4ErrorMsg;
    private final String testCompleteMsg1;
    private final String testCompleteMsg2;
    private final String testCompleteMsg3;
    private final String testCompleteMsg4;
    private final String strFileName;
    private final String strQuote;
    private final String strQuoteStop;
    private final String strDemoComplete;
    private final StringBuilder sbErrorMsg;
    private float handInterval;
    private float handElapsed;
    private long dateTime;
    private float timer;
    private float finalTestTime;
    public String[] testTypes;
    public int testTypeTakenIndex;
    private int reason1ErrorCount;
    private int reason2ErrorCount;
    private int reason3ErrorCount;
    private int reason4ErrorCount;
    private boolean testStarted;
    private final String[] typeBChars;
    private int lastCorrectCircleTouched;
    private int lastIncorrectCircleTouched;
    private int nextCorrectCircle;
    //private boolean doDrawPoints;
    private boolean firstCircleTouched;
    private boolean touchErrorFlag;
    private final float beginStringPosXOffset;
	private final float beginStringPosYOffset;
	private final float endStringPosXOffset;
	private final float endStringPosYOffset;
	private final int maxDrawPoints;
	private int drawPointsCounter;
    
	/** Enumerator used to indicate the test type. */
	private enum TestType
	{
		TEST_A_SAMPLE,
        TEST_A_FULL,
        TEST_B_SAMPLE,
        TEST_B_FULL
	}
	
	/** Enumerator used to indicate the number of circles the test type requires. */
	private enum TestSize
	{
		SAMPLE(8), //8 circles
		FULL(25);  //25 circles
		
		private final int val;
		private TestSize(int v) { val = v; }
		public int getVal() { return val; }
	}
	
	/** Enumerator used to indicate the state of the test. */
	private enum State
    {
        MENU,
        DEMO,
        TEST_A_SAMPLE,
        TEST_A_FULL,
        TEST_B_SAMPLE,
        TEST_B_FULL,
        WAIT,
        TEST_COMPLETE,
        TEST_ERROR,
        SAVE,
        END
    }
	
	/** Enumerator used to indicate the type of touch event that has occured. */
	private enum TouchType
	{
		TOUCH_DOWN,
		DRAG
	}
	
	/**
	 * Test1 constructor. All class objects and variables are initialised here.
	 * 
	 * @param globals
	 *   An initialised Globals object.
	 *   
	 * @param shapeRenderer
	 *   An initialised ShapeRenderer object.
	 */
	public Test1(final Globals globals, ShapeRenderer shapeRenderer)
	{
		this.globals = globals;	
		this.shapeRenderer = shapeRenderer;
		stage = new Stage();
		
		// Test type codes used for save file.
		testTypes = new String[4];
		testTypes[0] = "AS"; // Part A Sample.
		testTypes[1] = "AF"; // Part A Full.
		testTypes[2] = "BS"; // Part B Sample.
		testTypes[3] = "BF"; // Part B Full.
		
		drawPoints = new Array<Vector2>();
		
		numberSprites = new Array<Sprite>();
		letterSprites = new Array<Sprite>();
		numLetSprites = new Array<Sprite>();
		currentSprites = new Array<Sprite>();
		
		// Set positions for sample tests (A & B).
		float yOffset = 135;
		positionsABSample = new Array<Vector2>();
		positionsABSample.add(new Vector2(587, 408 + yOffset)); // 1 | 1
		positionsABSample.add(new Vector2(720, 222 + yOffset)); // 2 | A
		positionsABSample.add(new Vector2(852, 385 + yOffset)); // 3 | 2
		positionsABSample.add(new Vector2(719, 384 + yOffset)); // 4 | B
		positionsABSample.add(new Vector2(746, 546 + yOffset)); // 5 | 3
		positionsABSample.add(new Vector2(285, 488 + yOffset)); // 6 | C
		positionsABSample.add(new Vector2(339, 263 + yOffset)); // 7 | 4
		positionsABSample.add(new Vector2(525, 211 + yOffset)); // 8 | D
		
		// Set positions for Part A full test.
		positionsAFull = new Array<Vector2>();
		positionsAFull.add(new Vector2(780, 223 + yOffset));  // 1
		positionsAFull.add(new Vector2(885, 394 + yOffset));  // 2
		positionsAFull.add(new Vector2(989, 204 + yOffset));  // 3
		positionsAFull.add(new Vector2(584, 139 + yOffset));  // 4
		positionsAFull.add(new Vector2(620, 434 + yOffset));  // 5
		positionsAFull.add(new Vector2(695, 312 + yOffset));  // 6
		positionsAFull.add(new Vector2(780, 509 + yOffset));  // 7
		positionsAFull.add(new Vector2(932, 610 + yOffset));  // 8
		positionsAFull.add(new Vector2(1070, 550 + yOffset)); // 9
		positionsAFull.add(new Vector2(950, 497 + yOffset));  // 10
		positionsAFull.add(new Vector2(1185, 272 + yOffset)); // 11
		positionsAFull.add(new Vector2(1204, 689 + yOffset)); // 12
		positionsAFull.add(new Vector2(634, 645 + yOffset));  // 13
		positionsAFull.add(new Vector2(822, 730 + yOffset));  // 14
		positionsAFull.add(new Vector2(62, 725 + yOffset));   // 15
		positionsAFull.add(new Vector2(290, 610 + yOffset));  // 16
		positionsAFull.add(new Vector2(5, 359 + yOffset));    // 17
		positionsAFull.add(new Vector2(369, 384 + yOffset));  // 18
		positionsAFull.add(new Vector2(244, 142 + yOffset));  // 19
		positionsAFull.add(new Vector2(170, 265 + yOffset));  // 20
		positionsAFull.add(new Vector2(77, 44 + yOffset));    // 21
		positionsAFull.add(new Vector2(480, 39 + yOffset));   // 22
		positionsAFull.add(new Vector2(1020, 25 + yOffset));  // 23
		positionsAFull.add(new Vector2(885, 104 + yOffset));  // 24
		positionsAFull.add(new Vector2(1157, 141 + yOffset)); // 25
		
		// Set positions for Part B full test.
		positionsBFull = new Array<Vector2>();
		positionsBFull.add(new Vector2(498, 420 + yOffset));  // 1
		positionsBFull.add(new Vector2(770, 305 + yOffset));  // A
		positionsBFull.add(new Vector2(836, 511 + yOffset));  // 2
		positionsBFull.add(new Vector2(287, 487 + yOffset));  // B
		positionsBFull.add(new Vector2(379, 399 + yOffset));  // 3
		positionsBFull.add(new Vector2(518, 301 + yOffset));  // C
		positionsBFull.add(new Vector2(262, 333 + yOffset));  // 4
		positionsBFull.add(new Vector2(257, 131 + yOffset));  // D
		positionsBFull.add(new Vector2(480, 133 + yOffset));  // 5
		positionsBFull.add(new Vector2(960, 147 + yOffset));  // E
		positionsBFull.add(new Vector2(939, 373 + yOffset));  // 6
		positionsBFull.add(new Vector2(995, 553 + yOffset));  // F
		positionsBFull.add(new Vector2(484, 600 + yOffset));  // 7
		positionsBFull.add(new Vector2(300, 730 + yOffset));  // G
		positionsBFull.add(new Vector2(70, 704 + yOffset));   // 8
		positionsBFull.add(new Vector2(277, 625 + yOffset));  // H
		positionsBFull.add(new Vector2(108, 540 + yOffset));  // 9
		positionsBFull.add(new Vector2(142, 276 + yOffset));  // I
		positionsBFull.add(new Vector2(46, 43 + yOffset));    // 10
		positionsBFull.add(new Vector2(790, 34 + yOffset));   // J
		positionsBFull.add(new Vector2(1185, 56 + yOffset));  // 11
		positionsBFull.add(new Vector2(1203, 730 + yOffset)); // K
		positionsBFull.add(new Vector2(904, 680 + yOffset));  // 12
		positionsBFull.add(new Vector2(619, 731 + yOffset));  // L
		positionsBFull.add(new Vector2(420, 730 + yOffset));  // 13
		
		// Set characters used for displaying errors to examinee.
		typeBChars = new String[positionsBFull.size];
		typeBChars[0] = "1";
		typeBChars[1] = "A";
		typeBChars[2] = "2";
		typeBChars[3] = "B";
		typeBChars[4] = "3";
		typeBChars[5] = "C";
		typeBChars[6] = "4";
		typeBChars[7] = "D";
		typeBChars[8] = "5";
		typeBChars[9] = "E";
		typeBChars[10] = "6";
		typeBChars[11] = "F";
		typeBChars[12] = "7";
		typeBChars[13] = "G";
		typeBChars[14] = "8";
		typeBChars[15] = "H";
		typeBChars[16] = "9";
		typeBChars[17] = "I";
		typeBChars[18] = "10";
		typeBChars[19] = "J";
		typeBChars[20] = "11";
		typeBChars[21] = "K";
		typeBChars[22] = "12";
		typeBChars[23] = "L";
		typeBChars[24] = "13";
				
		beginPos = new Vector2(0, 0);
		endPos = new Vector2(0, 0);
		touchPos = new Vector3(0, 0, 0);
		handStartPos = new Vector2(0, 0);
		handCurrentPos = new Vector2(0, 0);		
		handEndPos = new Vector2(0, 0);
		vect2A = new Vector2(0, 0);
		vect2B = new Vector2(0, 0);
		handOffsetX = 116;
		handOffsetY = 20;
		tween = new Tween(2);
		demoPointOffset = 150;
		waitInterval = 0.35f;
		handInterval = 0.89f;
		sbErrorMsg = new StringBuilder();
		maxDrawPoints = 10000;
		
		beginStringPosXOffset = 8;
		beginStringPosYOffset = 34;
		endStringPosXOffset = 5;
		endStringPosYOffset = 34;
		
		// Set Sprites for letters.
		int count = 12;
		for (int i = 0; i < count; i++)
		{
			letterSprites.add(new Sprite(globals.spritesAtlas.createSprite("Circle" + (char)(65 + i))));
			letterSprites.get(i).setSize(70, 70);
			letterSprites.get(i).flip(false, true);
			letterSprites.get(i).getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		// Set Sprites for numbers.
		count = 25;
		for (int i = 0; i < count; i++)
		{
			numberSprites.add(new Sprite(globals.spritesAtlas.createSprite("Circle" + (i + 1))));
			numberSprites.get(i).setSize(70, 70);
			numberSprites.get(i).flip(false, true);
			numberSprites.get(i).getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		// Set Sprites for numbers & letters.
		for (int i = 0; i < count; i++)
		{
			numLetSprites.add(new Sprite(globals.spritesAtlas.createSprite("Circle" + (i + 1))));
			
			if (i < 12)
				numLetSprites.add(new Sprite(globals.spritesAtlas.createSprite("Circle" + (char)(65 + i))));
		}
		
		// Sprites are loaded upside down, so flip them all. Set linear filter so scaling across screen sizes is smooth. 
		for (Sprite sprite : numLetSprites)
		{			
			sprite.flip(false, true);
			sprite.setSize(70, 70);
			sprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		// Set demo hand Sprite.
		hand = new Sprite(globals.spritesAtlas.createSprite("Hand"));
		hand.flip(false, true);
		hand.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		// Create widgets.
		lblTrailMaking = new Label("Trail Making Menu", globals.skin);
		lblTrailMaking.setAlignment(Align.center);
		btnDemoA = new TextButton("Watch Part A Demo", globals.skin);
		btnDemoB = new TextButton("Watch Part B Demo", globals.skin);
		btnPartASample = new TextButton("Part A Sample", globals.skin);
		btnPartAFull = new TextButton("Part A Full", globals.skin);		
		btnPartBSample = new TextButton("Part B Sample", globals.skin);
		btnPartBFull = new TextButton("Part B Full", globals.skin);
		btnMainMenu = new TextButton("Main Menu", globals.skin);
		btnBackToTrailMakingMenu = new TextButton("Back to Trail Making Menu", globals.skin);
		
		lblTestComplete = new Label("", globals.skin);
		lblTestComplete.setAlignment(Align.center);
		
		testCompleteMsg1 = "Thank you, part A sample test completed.";
	    testCompleteMsg2 = "Thank you, part A test completed.";
	    testCompleteMsg3 = "Thank you, part B sample test completed.";
	    testCompleteMsg4 = "Thank you, part B test completed.";
	    
	    reason1ErrorMsg = "You started with the wrong circle.\nYou must start at the circle marked '1'.";
	    reason2ErrorMsg = "The last circle touched was not the next in sequence.\nContinue from circle marked ";
	    reason3ErrorMsg = "Please keep the pen on the screen, and continue from circle marked ";
	    reason4ErrorMsg = "No circle touched. Continue from circle marked ";
	    
	    strBegin = "Begin";
	    strEnd = "End";
	    strFileName = "tmt.csv";
	    strQuote = "'";
	    strQuoteStop = "'.";
	    strDemoComplete = "Demonstration completed. Tap screen to return to menu.";
		
		// Create Menu table.
		tblMenu = new Table(globals.skin);
		tblMenu.setFillParent(true);
				
		// Create Test Complete table.
		tblTestComplete = new Table(globals.skin);
		tblTestComplete.setFillParent(true);
		
		demoAInstructions = 
				"You are about to view a demonstration that will show you how\n" +
				"to complete the Part A tests. On the screen you will see some\n" +
				"circles with numbers in them. You will see a hand drawing a line\n"+
				"between the circles. The hand starts at the circle marked 1,\n" +
				"and moves between each circle in numerical order. The correct\n" +
				"order is 1 to 2, 2 to 3, 3 to 4, and so on. The demo ends when\n" +
				"all circles have been touched in the correct order.\n\n" +
				"Don't worry if you don't understand how the test works at first.\n" +
				"You will be able to watch the demonstration as many times as you\n" +
				"want before starting the actual tests.";
		
		demoBInstructions = 		
				"You will now view a demonstration that will show you how to\n" +
				"complete the Part B tests. On the screen you will see some\n" +
				"circles with numbers and letters in them. You will see a hand\n" + 
				"drawing a line between the circles. The finger starts at the circle\n" +
				"marked 1, and moves between each circle in the following order:\n" +
				"1 to A, A to 2, 2 to B, B to 3, 3 to C, and so on. The demo ends\n" +
				"when all circles have been touched in the correct order.\n\n" +
				"Don't worry if you don't understand how the test works at first.\n" +
				"You will be able to watch the demonstration as many times as you\n" +
				"want before starting the actual tests.";
		
	    partASampleInstructions = 
	    		"When the sample test starts, you will be shown some circles with\n" +
	    		"numbers in them. Begin at number 1 and draw a line from 1 to 2,\n" +
	    		"2 to 3, 3 to 4, and so on, in order, until you reach the end (the\n" +
	    		"circle marked 'END').\n\n"+
	    		"Draw the lines as fast as you can. Do not lift the pen from the\n" +
	    		"screen. If you make a mistake, a message will appear at the bottom\n" +
	    		"of the screen telling you what you did wrong and how to continue.";
	    
	    partAFullInstructions =
	    		"On this test are numbers from 1 to 25. Do this test the same way\n" +
	    		"as the sample test you have just completed. Begin at number 1 and\n" +
	    		"draw a line from 1 to 2, 2 to 3, 3 to 4, and so on, in order until\n" +
	    		"you reach the end. Remember, work as fast as you can.";
	    
	    partBSampleInstructions = 
	    		"In this test there are some numbers and letters. Begin at number 1 and\n" +
	    		"draw a line from 1 to A, A to 2, 2 to B, B to 3, 3 to C, and so on,\n" +
	    		"in order until you reach the end. Remember, first you have a number\n" +
	    		"(1) then a letter (A), then a number (2), then a letter (B), and so\n" +
	    		"on. Draw the lines as fast as you can.";
	    
	    partBFullInstructions = 
	    		"In this final test there are both numbers and letters. Do this test\n" +
	    		"the same way as the previous sample test. Begin at number 1 and draw\n" +
	    		"a line from 1 to A, A to 2, 2 to B, B to 3, 3 to C, and so on, in\n" +
	    		"order, until you reach the end (the circle marked 'END')\n\n" +
	    		"Remember, first you have a number (1), then a letter (B), and so on.\n" +
	    		"Do not skip around, but go from one circle to the next in the proper\n" +
	    		"order. Draw the lines as fast as you can.";
		
		// Add listeners.
		btnDemoA.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{				
				new Dialog("Demonstration Part A Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetTestType(TestType.TEST_A_SAMPLE, true);
                    }
				}.text(demoAInstructions)
				 .button("Cancel", false) 
				 .button("Start Demo", true)
                 .show(stage);
			}
		});
		
		btnDemoB.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				new Dialog("Demonstration Part B Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetTestType(TestType.TEST_B_SAMPLE, true);
                    }
				}.text(demoBInstructions)
				 .button("Cancel", false) 
				 .button("Start Demo", true)
                 .show(stage);				
			}
		});
		
		btnPartASample.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				new Dialog("Part A Sample Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetTestType(TestType.TEST_A_SAMPLE, false);
                    }
				}.text(partASampleInstructions)
				 .button("Cancel", false) 
				 .button("Start Part A Sample", true)
                 .show(stage);				
			}
		});
		
		btnPartAFull.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				new Dialog("Part A Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetTestType(TestType.TEST_A_FULL, false);
                    }
				}.text(partAFullInstructions)
				 .button("Cancel", false) 
				 .button("Start Part A", true)
                 .show(stage);				
			}
		});
		
		btnPartBSample.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				new Dialog("Part B Sample Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetTestType(TestType.TEST_B_SAMPLE, false);
                    }
				}.text(partBSampleInstructions)
				 .button("Cancel", false) 
				 .button("Start Part B Sample", true)
                 .show(stage);				
			}
		});
		
		btnPartBFull.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				new Dialog("Part B Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetTestType(TestType.TEST_B_FULL, false);
                    }
				}.text(partBFullInstructions)
				 .button("Cancel", false) 
				 .button("Start Part B", true)
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
		
		btnBackToTrailMakingMenu.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				Setup();
			}
		});
	}
	
	/** Release all resources used by objects that are disposable. */
	public void Dispose()
	{
		stage.dispose();
	}
	
	/** Method to setup a stage when menus are being displayed. The state should be set before calling this method.
	 * 
	 * @param actor
	 *   An initialised Table object.
	 */
	private void SetStage(Table table)
	{		
		tblMenu.clear();
		tblTestComplete.clear();
		
		// Add relevant widgets to table.
		switch (state)
		{
			case MENU: // Add widgets to Menu table.
				tblMenu.row().height(150).colspan(2);
				tblMenu.add(lblTrailMaking).width(1000);
				tblMenu.row().height(150);		
				tblMenu.add(btnDemoA).width(500);
				tblMenu.add(btnDemoB).width(500);
				
				tblMenu.row().height(150);
				tblMenu.add(btnPartASample).width(500);
				tblMenu.add(btnPartBSample).width(500);
				
				tblMenu.row().height(150);
				tblMenu.add(btnPartAFull).width(500);
				tblMenu.add(btnPartBFull).width(500);
				
				tblMenu.row().height(150).colspan(2);
				tblMenu.add(btnMainMenu).width(1000);
				break;
				
			case TEST_COMPLETE: // Add widgets to Test Complete table.				
				tblTestComplete.row().height(90);
				tblTestComplete.add(lblTestComplete).width(700);
				tblTestComplete.row().height(150);
				tblTestComplete.add(btnBackToTrailMakingMenu).width(700);
				break;
		}	
		
		// Setup stage.
		stage.clear();
		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	/** Reset states and variables. To be called before Menu is shown. */
	private void Setup()
	{		
		state = State.MENU;
		correctCirclesTouched = 0;
		currentlyTouchedCircle = 0;
		shapeRenderer.setColor(Color.BLACK);
		demoState = 1;	
		SetStage(tblMenu);		
		//drawPoints.size = 0;
		hand.setPosition(2000, 2000);
		Gdx.input.setInputProcessor(stage);
		waitElapsed = 0;
		nextCorrectCircle = 0;
		lastCorrectCircleTouched = -1;
		//doDrawPoints = false;
		firstCircleTouched = false;
		touchErrorFlag = true;
		globals.doScreenshot = false;
	}
	
	/** Sets the sprites and menu state for the selected test type. 
	 *  
	 * @param testType
	 *   A valid TestType enum value indicating the type of test that will be shown.
	 *   
	 * @param isDemo
	 *   Boolean indicating if the next State should be set to DEMO.
	 */
	private void SetTestType(TestType testType, boolean isDemo)
	{
		this.testType = testType;
		SetPositions(testType);
		globals.Resize();
		waitElapsed = 0;
		handElapsed = 0;
		handInterval = 0.89f;
		reason1ErrorCount = 0;
		reason2ErrorCount = 0;
		reason3ErrorCount = 0;
		reason4ErrorCount = 0;
		testStarted = false;		
		drawPoints.clear();
		
		// Pre-fill drawPoints array so we don't need to create a new Vector2 every time a new draw point is added.
		for (int i = 0; i < maxDrawPoints; i++)
		{
			drawPoints.add(new Vector2(-1, -1));
		}
		
		drawPointsCounter = 0;
		
		if (isDemo)
			state = State.DEMO;
		
		switch (testType)
		{
			case TEST_A_SAMPLE:
				testTypeTakenIndex = 0;
				currentSprites = numberSprites;
				if (!isDemo)
					state = State.TEST_A_SAMPLE;
				break;
				
			case TEST_A_FULL:
				testTypeTakenIndex = 1;
				currentSprites = numberSprites;
				if (!isDemo)
					state = State.TEST_A_FULL;
				break;
				
			case TEST_B_SAMPLE:
				testTypeTakenIndex = 2;
				currentSprites = numLetSprites;
				if (!isDemo)
					state = State.TEST_B_SAMPLE;
				break;
				
			case TEST_B_FULL:
				testTypeTakenIndex = 3;
				currentSprites = numLetSprites;
				if (!isDemo)
					state = State.TEST_B_FULL;
				break;
		}
		
		SetBEStringPos(testType);
		
		for (Sprite sprite : currentSprites)
			sprite.setColor(1, 1, 1, 1);
		
		if (!isDemo)
		{
			Gdx.input.setInputProcessor(this);
		}
		
		dateTime = System.currentTimeMillis(); // Calendar.getInstance().getTimeInMillis();
	}
	
	/**
	 * Set the positions of the Begin & End strings.
	 * 
	 * @param testType
	 *   A valid TestType enum value indicating the type of test that will be shown.
	 */
	private void SetBEStringPos(TestType testType)
	{
		switch (testType)
		{
			case TEST_A_SAMPLE:
			case TEST_B_SAMPLE:
				beginPos.x = positionsABSample.get(0).x - beginStringPosXOffset;
				beginPos.y = positionsABSample.get(0).y - beginStringPosYOffset;
				endPos.x = positionsABSample.get(positionsABSample.size - 1).x + endStringPosXOffset;
				endPos.y = positionsABSample.get(positionsABSample.size - 1).y - endStringPosYOffset;
				break;
				
			case TEST_A_FULL:
				beginPos.x = positionsAFull.get(0).x - beginStringPosXOffset;
				beginPos.y = positionsAFull.get(0).y - beginStringPosYOffset;
				endPos.x = positionsAFull.get(positionsAFull.size - 1).x + endStringPosXOffset;
				endPos.y = positionsAFull.get(positionsAFull.size - 1).y - endStringPosYOffset;
				break;
				
			case TEST_B_FULL:
				beginPos.x = positionsBFull.get(0).x - beginStringPosXOffset;
				beginPos.y = positionsBFull.get(0).y - beginStringPosYOffset;
				endPos.x = positionsBFull.get(positionsBFull.size - 1).x + endStringPosXOffset;
				endPos.y = positionsBFull.get(positionsBFull.size - 1).y - endStringPosYOffset;
				break;
		}
	}
	
	/**
	 * Set the circle Sprite positions for each test type.
	 * 
	 * @param testType
	 *   TestType enumerator value indicating the type of test used.
	 */
	private void SetPositions(TestType testType)
	{
		switch (testType)
		{
			case TEST_A_SAMPLE:
				testSize = TestSize.SAMPLE;
				for (int i = 0; i < TestSize.SAMPLE.getVal(); i++)		
					numberSprites.get(i).setPosition(positionsABSample.get(i).x, positionsABSample.get(i).y);
				break;
			
			case TEST_A_FULL:
				testSize = TestSize.FULL;
				for (int i = 0; i < TestSize.FULL.getVal(); i++)		
					numberSprites.get(i).setPosition(positionsAFull.get(i).x, positionsAFull.get(i).y);
				break;
				
			case TEST_B_SAMPLE:
				testSize = TestSize.SAMPLE;
				for (int i = 0; i < TestSize.SAMPLE.getVal(); i++)	
					numLetSprites.get(i).setPosition(positionsABSample.get(i).x, positionsABSample.get(i).y);
				break;
				
			case TEST_B_FULL:
				testSize = TestSize.FULL;
				for (int i = 0; i < TestSize.FULL.getVal(); i++)		
					numLetSprites.get(i).setPosition(positionsBFull.get(i).x, positionsBFull.get(i).y);
				break;
		}
	}
	
	/** Update method called once per frame. Updates all logic before each draw call. */
	public void Update()
	{		
		if (state == null)
			Setup();
		
		if (Gdx.input.getInputProcessor() == null)
			Gdx.input.setInputProcessor(stage);
		
		switch (state)
		{
			case MENU:
				stage.act(Gdx.graphics.getDeltaTime());
				break;
				
			case DEMO:  // Display a demonstration showing how to do the test.
				
				if (Gdx.input.isTouched())
					Setup();
				
				PlotPoints();
				
				switch (demoState)
				{
					case 1: // Initialise...
						handPosIndex = 0;
						handWidth = 630;
						handHeight = 891;
						hand.setSize(handWidth, handHeight);
						demoState = 2;
						break;
						
					case 2: // Scale hand sprite towards first circle.
						handWidth -= 2.90f;
						handHeight -= 2.90f;
						
						hand.setSize(handWidth, handHeight);						
						hand.setPosition(
								currentSprites.get(handPosIndex).getX() - handOffsetX, 
								currentSprites.get(handPosIndex).getY() + handOffsetY);
						
						if (handWidth <= 420)
							demoState = 3;
						break;
						
					case 3: // Set Start & End position vectors.
						handStartPos.x = currentSprites.get(handPosIndex).getX() - handOffsetX;
						handStartPos.y = currentSprites.get(handPosIndex).getY() + handOffsetY;
						hand.setPosition(handStartPos.x, handStartPos.y);
						
						handEndPos.x = currentSprites.get(handPosIndex + 1).getX() - handOffsetX;
						handEndPos.y = currentSprites.get(handPosIndex + 1).getY() + handOffsetY;
						
						tween.time = 0;
						demoState = 4;	
						
						currentSprites.get(handPosIndex).setColor(180, 233, 252, 0.10f);
						break;
						
					case 4: // Pause hand for "handInterval" seconds.
						handElapsed += Gdx.graphics.getDeltaTime();
						if (handElapsed > handInterval)
		                {
							handElapsed = 0;
							demoState = 5;
							handInterval = 0.05f;
		                }
						break;
						
					case 5: // Move hand from current "Start" to "End" point.
						handCurrentPos.x = tween.apply(handStartPos.x, handEndPos.x, Interpolation.fade);
						handCurrentPos.y = tween.apply(handStartPos.y, handEndPos.y, Interpolation.fade);
						tween.update(Gdx.graphics.getDeltaTime());						
						hand.setPosition(handCurrentPos.x, handCurrentPos.y);						
						//drawPoints.add(new Vector2(handCurrentPos.x + demoPointOffset, handCurrentPos.y));
						if (drawPointsCounter < maxDrawPoints)
						{
							drawPoints.get(drawPointsCounter).x = handCurrentPos.x + demoPointOffset;
							drawPoints.get(drawPointsCounter).y = handCurrentPos.y;
							drawPointsCounter++;
						}
						
						// Repeat sequence until all points have been traversed.
						if (handPosIndex == testSize.getVal() - 1)
						{
							demoState = 6; // display demo complete message
						}
						else
						{
							if (handCurrentPos.x == handEndPos.x && handCurrentPos.y == handEndPos.y)
							{
								currentSprites.get(handPosIndex).setColor(180, 233, 252, 0.10f);
								handPosIndex++;
								demoState = 3;
							}
						}
						break;
				}				
				break;
				
			case TEST_A_SAMPLE: // Running sample test using just numbers.
			case TEST_A_FULL:   // Running full test using just numbers.
			case TEST_B_SAMPLE: // Running sample test using alphbetical letters & numbers.
			case TEST_B_FULL:   // Running full test using alphbetical letters & numbers.
			case TEST_ERROR:
				PlotPoints();
				timer += Gdx.graphics.getDeltaTime();
				break;
				
			case WAIT: // Test completed or failed, pause for "waitInterval" seconds then proceed to appropriate State.
				timer += Gdx.graphics.getDeltaTime();
				PlotPoints();
				waitElapsed += Gdx.graphics.getDeltaTime();
				if (waitElapsed > waitInterval)
                {
					waitElapsed = 0;
					
					if (nextState == State.TEST_COMPLETE)
					{						
						switch (testType)
						{
							case TEST_A_SAMPLE:
								lblTestComplete.setText(testCompleteMsg1);
								break;
								
							case TEST_A_FULL:
								lblTestComplete.setText(testCompleteMsg2);
								break;
								
							case TEST_B_SAMPLE:
								lblTestComplete.setText(testCompleteMsg3);
								break;
								
							case TEST_B_FULL:
								lblTestComplete.setText(testCompleteMsg4);
								break;
						}
							
						finalTestTime = timer;
						state = State.SAVE;
						
						Save();
						globals.doScreenshot = true;
						state = State.SAVE;
						
						//state = State.TEST_COMPLETE;
						//SetStage(tblTestComplete);						
					}
					else if (nextState == State.TEST_ERROR)
					{
						state = State.TEST_ERROR;
						//SetStage(tblTestFailed);
						//lblFailedReason.setText(failedReasonStrings[failedReasonIndex]);    	        		
					}
                }
				break;
				
			case SAVE:
				state = State.END;
				break;
				
			case END:
				state = State.TEST_COMPLETE;
				SetStage(tblTestComplete);
				break;
		}
	}
	
	/** Plot all user-painted lines. */
	private void PlotPoints()
	{
		shapeRenderer.begin(ShapeType.Line);
		//int count = drawPoints.size - 1;		
		for (int i = 0; i < drawPointsCounter - 1; i++)
		{
			vect2A = drawPoints.get(i);
			vect2B = drawPoints.get(i + 1);
			
			// Skip to next point if there's a break (specified by -1).
			if (vect2A.x == -1 || vect2B.x == -1)
				continue;
			
			shapeRenderer.line(vect2A.x, vect2A.y, vect2B.x, vect2B.y);
		}
		shapeRenderer.end();
	}
	
	/**
	 * Draw method, called once per frame after logic updates.
	 * 
	 * @param spriteBatch
	 *   An initialised SpriteBatch object.
	 */
	public void Draw(SpriteBatch spriteBatch)
	{
		// Debug info.
		if (state != State.MENU)
		{
//			globals.wascoSans30.draw(spriteBatch, "Correct circles touched: " + (correctCirclesTouched), 5, 10);
//			globals.wascoSans30.draw(spriteBatch, "Next correct circle: " + (nextCorrectCircle), 410, 10);
//			globals.wascoSans30.draw(spriteBatch, "Last correct circle touched: " + (lastCorrectCircleTouched), 730, 10);
//			globals.wascoSans30.draw(spriteBatch, "Current circle touched: " + (currentlyTouchedCircle + 1), 10, 10);
//			globals.wascoSans30.draw(spriteBatch, "Test size: " + testSize.getVal(), 400, 10);
//			globals.wascoSans30.draw(spriteBatch, "drawPoints: " + drawPointsCounter, 650, 10);
		}
		
		switch (state)
		{
			case MENU:							
			case TEST_COMPLETE:
				stage.draw();
				break;
				
			case DEMO:
				globals.wascoSans30.setColor(Color.GRAY);
				globals.wascoSans30.draw(spriteBatch, strBegin, beginPos.x, beginPos.y);
				globals.wascoSans30.draw(spriteBatch, strEnd, endPos.x, endPos.y);
				
				for (int i = 0; i < testSize.getVal(); i++)
					currentSprites.get(i).draw(spriteBatch);
				
				hand.draw(spriteBatch, 0.15f);
				
				if (demoState == 6)
				{
					globals.wascoSans30.setColor(Color.BLACK);
					globals.wascoSans30.drawWrapped(spriteBatch, strDemoComplete, 0, 25, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
				}
				break;
				
			case TEST_ERROR:				
				globals.wascoSans30.setColor(Color.RED);
				globals.wascoSans30.drawWrapped(spriteBatch, sbErrorMsg, 0, 25, globals.VIRTUAL_WIDTH, HAlignment.CENTER);				
				
			case SAVE:				
			case TEST_A_SAMPLE:
			case TEST_A_FULL:
			case TEST_B_SAMPLE:
			case TEST_B_FULL:			
			case WAIT:
			case END:
				globals.wascoSans30.setColor(Color.GRAY);
				globals.wascoSans30.draw(spriteBatch, strBegin, beginPos.x, beginPos.y);
				globals.wascoSans30.draw(spriteBatch, strEnd, endPos.x, endPos.y);
				
				for (int i = 0; i < testSize.getVal(); i++)
					currentSprites.get(i).draw(spriteBatch);				
				break;
		}
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

		globals.camera.unproject(touchPos, globals.resizeViewport.x, globals.resizeViewport.y, 
				globals.resizeViewport.width, globals.resizeViewport.height);
		
		switch (state)
		{				
			case TEST_ERROR:
				
				state = oldState;
				
				// Restore circle colours when user resumes test after error.
				if (lastCorrectCircleTouched == -1)
				{
					// No circles correctly clicked yet.
					for (int i = 0; i < testSize.getVal(); i++)
					{
						currentSprites.get(i).setColor(Color.WHITE);							
					}
				}
				else
				{
					for (int i = 0; i < testSize.getVal(); i++)
					{
						if (i < nextCorrectCircle + 1)
							currentSprites.get(i).setColor(180, 233, 252, 0.10f);
						else
							currentSprites.get(i).setColor(Color.WHITE);
					}
				}
				//break;
				
			case TEST_A_SAMPLE:
			case TEST_A_FULL:
			case TEST_B_SAMPLE:
			case TEST_B_FULL:
				
				// Test begins (and timer starts) when screen is first touched.
				if (!testStarted)
				{
					testStarted = true;
					timer = 0; // Reset timer.
					
					// Get start time in separate thread so it doesn't interfere with other test logic events.
//					new Thread(new Runnable() 
//					{
//						@Override
//						public void run() 
//						{
//							// Do asynchronous task here.
//							dateTime = Calendar.getInstance().getTimeInMillis(); // Set start date & time.
//							// post a Runnable to the rendering thread that processes the result
//							Gdx.app.postRunnable(new Runnable() 
//							{
//								@Override
//								public void run() 
//								{
//									// process the result, e.g. add it to an Array<Result> field of the ApplicationListener.
//									//results.add(result);
//								}
//							});
//						}
//					}).start();
				}
				
				touchUpdate(TouchType.TOUCH_DOWN);
				return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// Disable multi-touch.
		if (pointer > 0 || button > 0)
			return false;
		
		switch (state)
		{				
			case TEST_A_SAMPLE:
			case TEST_A_FULL:
			case TEST_B_SAMPLE:
			case TEST_B_FULL:
				
				//doDrawPoints = false;
				
				// Test error if all circles have not been touched when the user lifts finger/pen from screen.
				if (correctCirclesTouched != testSize.getVal())
				{
					// Break draw points so that a line will not be drawn from last error point to next start point.
					//drawPoints.add(new Vector2(-1, -1));
					if (drawPointsCounter < maxDrawPoints)
					{
						drawPoints.get(drawPointsCounter).x = -1;
						drawPoints.get(drawPointsCounter).y = -1;
						drawPointsCounter++;
					}					
					
					sbErrorMsg.setLength(0);
					sbErrorMsg.append(reason3ErrorMsg);
					sbErrorMsg.append(strQuote);
					
					if (state == State.TEST_A_SAMPLE || state == State.TEST_A_FULL)
					{
						sbErrorMsg.append(currentlyTouchedCircle + 1);
					}
					else
					{
						sbErrorMsg.append(typeBChars[currentlyTouchedCircle]);
					}
					
					sbErrorMsg.append(strQuoteStop);
					
					oldState = state;
					reason3ErrorCount++;
					nextState = State.TEST_ERROR;
					state = State.WAIT;
					
					// Stop the next & last correct circle values being altered if there is an outstanding touch error.
					if (!touchErrorFlag)
					{
						int temp = nextCorrectCircle;
						nextCorrectCircle = lastCorrectCircleTouched;
						lastCorrectCircleTouched = temp - 2;
						touchErrorFlag = true;
					}
					return true;
				}
				else
					state = State.TEST_COMPLETE;
		}
		return false;
	}
	
	/**
	 * Update logic for touch events during testing.
	 * 
	 * @param type
	 *   A TouchType indicating if event is touch down or drag.
	 */
	private void touchUpdate(TouchType type)
	{
		for (int i = 0; i < testSize.getVal(); i++)
		{
			// Has a circle been touched?
			if (currentSprites.get(i).getBoundingRectangle().contains(touchPos.x, touchPos.y))
			{
				//doDrawPoints = true;
				currentlyTouchedCircle = i;
				
				// Do nothing if user is still touching the last circle touched.
				if (i == lastCorrectCircleTouched && !touchErrorFlag)
					return;
								
				// Is the currently touched circle the next circle in the sequence?
				if (i == nextCorrectCircle)
				{
					touchErrorFlag = false;
					
					if (i == 0)
						firstCircleTouched = true;
						
					// Correct circle was touched - set circle colour to blue.
					currentSprites.get(i).setColor(180, 233, 252, 0.10f);
					correctCirclesTouched++;
					nextCorrectCircle++;
					lastCorrectCircleTouched = i;
					
					// Test completed if all circles touched.
					if (i == testSize.getVal() - 1)
					{
						state = State.WAIT;
						nextState = State.TEST_COMPLETE;
					}								
					return;
				}				
				else if (!firstCircleTouched) // Has the first circle been touched?
				{
					// Break draw points so that a line will not be drawn from last error point to next start point.
					//drawPoints.add(new Vector2(-1, -1));
					if (drawPointsCounter < maxDrawPoints)
					{
						drawPoints.get(drawPointsCounter).x = -1;
						drawPoints.get(drawPointsCounter).y = -1;
						drawPointsCounter++;
					}					
					
					// Show "cirlce 1 to be touched first" error.
					oldState = state;
					nextState = State.TEST_ERROR;
					state = State.WAIT;
					reason1ErrorCount++;
					sbErrorMsg.setLength(0);
					sbErrorMsg.append(reason1ErrorMsg);
					currentSprites.get(i).setColor(Color.RED);
					return;
				}
				else // Incorrect circle touched.
				{
					// Break draw points so that a line will not be drawn from last error point to next start point.
					//drawPoints.add(new Vector2(-1, -1));
					if (drawPointsCounter < maxDrawPoints)
					{
						drawPoints.get(drawPointsCounter).x = -1;
						drawPoints.get(drawPointsCounter).y = -1;
						drawPointsCounter++;					
					}
					
					sbErrorMsg.setLength(0);
					sbErrorMsg.append(reason2ErrorMsg);
					sbErrorMsg.append(strQuote);
					
					// Set error message.
					if (state == State.TEST_A_SAMPLE || state == State.TEST_A_FULL)
					{
						if (touchErrorFlag)
							sbErrorMsg.append(nextCorrectCircle + 1);
						else
							sbErrorMsg.append(nextCorrectCircle);
					}
					else if (state == State.TEST_B_SAMPLE || state == State.TEST_B_FULL)
					{
						if (touchErrorFlag)
							sbErrorMsg.append(typeBChars[nextCorrectCircle]);
						else
							sbErrorMsg.append(typeBChars[nextCorrectCircle - 1]);
					}
					
					currentSprites.get(i).setColor(Color.RED);
					
					sbErrorMsg.append(strQuoteStop);					
					oldState = state;
					nextState = State.TEST_ERROR;
					state = State.WAIT;
					reason2ErrorCount++;
					
					// Stop the next & last correct circle values being altered if there is an outstanding touch error.
					if (!touchErrorFlag)
					{
						int temp = nextCorrectCircle;
						nextCorrectCircle = lastCorrectCircleTouched;
						lastCorrectCircleTouched = temp - 2;
						touchErrorFlag = true;
					}
					return;
				}
			}
		}
		
		// No circle touched - display error if touch (not drag).
		if (type == TouchType.TOUCH_DOWN)
		{
			// Break draw points so that a line will not be drawn from last error point to next start point.
			//drawPoints.add(new Vector2(-1, -1));
			if (drawPointsCounter < maxDrawPoints)
			{
				drawPoints.get(drawPointsCounter).x = -1;
				drawPoints.get(drawPointsCounter).y = -1;
				drawPointsCounter++;
			}			
			
			//doDrawPoints = false;
			
			sbErrorMsg.setLength(0);
			sbErrorMsg.append(reason4ErrorMsg);
			sbErrorMsg.append(strQuote);
						
			// Set error message.
			if (state == State.TEST_A_SAMPLE || state == State.TEST_A_FULL)
			{
				if (touchErrorFlag)
					sbErrorMsg.append(nextCorrectCircle + 1);
				else
					sbErrorMsg.append(nextCorrectCircle);
			}
			else if (state == State.TEST_B_SAMPLE || state == State.TEST_B_FULL)
			{
				if (touchErrorFlag)
					sbErrorMsg.append(typeBChars[nextCorrectCircle]);
				else
					sbErrorMsg.append(typeBChars[nextCorrectCircle - 1]);
			}
			
			sbErrorMsg.append(strQuoteStop);
			oldState = state;
			nextState = State.TEST_ERROR;
			state = State.WAIT;
			reason4ErrorCount++;
			return;
		}
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		// Disable multi-touch.
		if (pointer > 0)
			return false;
		
		switch (state)
		{
			case TEST_A_SAMPLE:
			case TEST_A_FULL:
			case TEST_B_SAMPLE:
			case TEST_B_FULL:
				
				// Translate touch co-ords to virtual screen co-ords.
				touchPos.x = screenX;
				touchPos.y = screenY;
				touchPos.z = 0;
				
				globals.camera.unproject(touchPos, globals.resizeViewport.x, globals.resizeViewport.y, 
						globals.resizeViewport.width, globals.resizeViewport.height);
				
//				if (doDrawPoints)
//					drawPoints.add(new Vector2(touchPos.x, touchPos.y));
				
				if (drawPointsCounter < maxDrawPoints)
				{
					drawPoints.get(drawPointsCounter).x = touchPos.x;
					drawPoints.get(drawPointsCounter).y = touchPos.y;
					drawPointsCounter++;
				}				
				
				touchUpdate(TouchType.DRAG);
				return true;
		}
		
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
	
	/** Append data for last test to tmt.csv save file. */
	private void Save()
	{
		if (Gdx.files.isLocalStorageAvailable())
		{			
			BufferedWriter out = null;
			try
			{
				out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strFileName).write(true)));
				
				out.write(globals.sbCurrentParticipantID.toString());
				out.write(globals.strComma);
				out.write(Long.toString(dateTime));
				out.write(globals.strComma);
				out.write(testTypes[testTypeTakenIndex]);
				out.write(globals.strComma);
				out.write(Float.toString(finalTestTime));
				out.write(globals.strComma);
				out.write(String.valueOf(reason1ErrorCount));
				out.write(globals.strComma);
				out.write(String.valueOf(reason2ErrorCount));
				out.write(globals.strComma);
				out.write(String.valueOf(reason3ErrorCount));
				out.write(globals.strComma);
				out.write(String.valueOf(reason4ErrorCount));
				
				out.write(globals.strNewLine);
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