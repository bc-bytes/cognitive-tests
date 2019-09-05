package com.bc.memorytest;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.utils.TimeUtils;
import com.bc.memorytest.Globals.AppState;

/**
 * Class to handle all Deary & Liewald Reaction Time Test logic.
 * 
 * @author Bill Cassidy
 */
public class Test2 implements InputProcessor
{
	private TestType testType;
	private State state;
	private final Globals globals;
	private final Stage stage;
	private final Vector3 touchPos;
	private final Table tblMenu;	
	private final Label lblDLMenu;	
	private final TextButton btnRunSRTPractice;
	private final TextButton btnRunSRTTest;
	private final TextButton btnRunCRTPractice;
	private final TextButton btnRunCRTTest;
	private final TextButton btnMainMenu;
	private final Sprite whiteSquareSprite;
	private final Sprite whiteSquareXSprite;
	private final StringBuilder sbTestIntro;
	private final String strTapScreenToStart;
	private final String strTestCompleted;
	private final String strTapScreenToReturnToMenu;
	private final String strSRTPracticeIntroA;
	private final String strSRTPracticeIntroB;
	private final String strSRTFullIntroA;
	private final String strSRTFullIntroB;
	private final String strCRTPracticeIntroA;
	private final String strCRTPracticeIntroB;
	private final String strCRTFullIntroA;
	private final String strCRTFullIntroB;
	private final String strSRTHeaderFileName;
	private final String strSRTDetailFileName;
	private final String strCRTHeaderFileName;
	private final String strCRTDetailFileName;
	private int noOfPracticeTrialsSRT;
	private int noOfPracticeTrialsCRT;
	private int noOfExperimentTrialsSRT;
	private int noOfExperimentTrialsCRT;
	private long responseRangeFromSRT;
	private long responseRangeFromCRT;
	private long responseRangeToSRT;
	private long responseRangeToCRT;
	private float interStimulusInvervalFromSRT;
	private float interStimulusInvervalFromCRT;
	private float interStimulusInvervalToSRT;
	private float interStimulusInvervalToCRT;
	private final Array<Sprite> whiteSquares;
	private boolean showingX;
	public boolean doGetTimeStart; // indicate to Main class if we should set responseTimeStart
	public long responseTimeStart;  // time when X shown 
	private long responseTimeEnd;   // time when user touches screen after X is shown
	private long currentResponse;   // difference between response start & end times
	private float interStimulusIntervalTimer;
	private float currentInterStimulusInterval;
	private int srtState;
	private int crtState;
	private int trialCounter;
	private final float waitInterval;
	private float waitElapsed;
	private int currentNoOfTrials;
	private float currentInterStimulusIntervalFrom;
	private float currentInterStimulusIntervalTo;
	private int xPosition;
	private final int whiteSquaresCount;
	private int touchedSquare;
	
	// Header fields.
	private int prematureResponses; // SRT & CRT
	private int anticipatedResponses; // SRT & CRT
	private int trialCount; // SRT (number of trials)
	private int correctCount; // CRT
	private int wrongCount; // CRT
	private long startTime; // SRT & CRT
	
	// Details records.
	private final Array<SRTDetail> srtDetails;
	private final Array<CRTDetail> crtDetails;
	
	/** Enumerator used to indicate the test type. */
	private enum TestType
	{
		SRT_PRACTICE,
        SRT_FULL,
        CRT_PRACTICE,
        CRT_FULL
	}
	
	/** Enumerator used to indicate the current state of the Reaction Time Test component. */
	private enum State
	{
		MENU,
		SRT_PRACTICE,
		SRT_FULL,
		CRT_PRACTICE,
		CRT_FULL
	}
	
	/**
	 * Test2 constructor. All class objects and variables are initialised here.
	 * 
	 * @param globals
	 *   An initialised Globals object.
	 */
	public Test2(final Globals globals)
	{
		this.globals = globals;
		stage = new Stage();
		touchPos = new Vector3(0, 0, 0);
		waitInterval = 4;
		whiteSquareSprite = new Sprite(globals.spritesAtlas.createSprite("WhiteSquare1"));
		whiteSquareSprite.flip(false, true);
		whiteSquareSprite.setPosition(globals.VIRTUAL_WIDTH / 2 - whiteSquareSprite.getWidth() / 2, 
				globals.VIRTUAL_HEIGHT / 2 - whiteSquareSprite.getHeight() / 2);
		whiteSquareXSprite = new Sprite(globals.spritesAtlas.createSprite("WhiteSquareX"));
		whiteSquareXSprite.flip(false, true);
		whiteSquares = new Array<Sprite>();
		
		srtDetails = new Array<SRTDetail>();
		crtDetails = new Array<CRTDetail>();
		
		// Create the 4 white squares used in the CRT test.
		for (int i = 0; i < 4; i++)
		{
			whiteSquares.add(new Sprite(globals.spritesAtlas.createSprite("WhiteSquare1")));
			whiteSquares.get(i).flip(false, true);
		}
		whiteSquaresCount = whiteSquares.size;
		
		// Center the 4 CRT square positions.
		int gap = 60;
		
		// Calculate positions relative to center of screen.
		// 2nd from left (- * - -)
		Vector2 pos2 = new Vector2(globals.VIRTUAL_WIDTH / 2 - (whiteSquareSprite.getWidth() + gap), 
				globals.VIRTUAL_HEIGHT / 2 - whiteSquareSprite.getHeight() / 2);
		// 1st from left (* - - -)
		Vector2 pos1 = new Vector2(pos2.x - (whiteSquareSprite.getWidth() + gap), 
				globals.VIRTUAL_HEIGHT / 2 - whiteSquareSprite.getHeight() / 2);
		// 3rd from left (- - * -)
		Vector2 pos3 = new Vector2(pos2.x + gap + whiteSquareSprite.getWidth(), 
				globals.VIRTUAL_HEIGHT / 2 - whiteSquareSprite.getHeight() / 2);
		// 4th from left (- - - *)
		Vector2 pos4 = new Vector2(pos3.x + gap + whiteSquareSprite.getWidth(), 
				globals.VIRTUAL_HEIGHT / 2 - whiteSquareSprite.getHeight() / 2);
		
		// Apply calculated positions to sprites.
		whiteSquares.get(0).setPosition(pos1.x, pos1.y);
		whiteSquares.get(1).setPosition(pos2.x, pos2.y);
		whiteSquares.get(2).setPosition(pos3.x, pos3.y);
		whiteSquares.get(3).setPosition(pos4.x, pos4.y);
		
		// Set default values.
		noOfPracticeTrialsSRT = 8;
		noOfExperimentTrialsSRT = 20;
		responseRangeFromSRT = 150; // milliseconds
		responseRangeToSRT = 1500;  // milliseconds
		interStimulusInvervalFromSRT = 1.0f; // seconds and milliseconds
		interStimulusInvervalToSRT = 3.0f;   // seconds and milliseconds
		
		noOfPracticeTrialsCRT = 8;		
		noOfExperimentTrialsCRT = 40;		
		responseRangeFromCRT = 200;	// milliseconds
		responseRangeToCRT = 1500;	// milliseconds	
		interStimulusInvervalFromCRT = 1.0f; // seconds and milliseconds
		interStimulusInvervalToCRT = 3.0f;   // seconds and milliseconds
		
		// Create strings.
		strTapScreenToStart = "Tap Screen to Start";
		strTestCompleted = "Test Completed - Thank You";
		strTapScreenToReturnToMenu = "Tap Screen to Return to Menu";
		strSRTPracticeIntroA =  
				"First of all you will have a practice session.\n" +
				"A cross will appear in the box on the screen\n";
		strSRTPracticeIntroB = 
			    " times and each time it appears you should\n" +
			    "tap the screen as quickly as you can. Don't\n" +
			    "hold your pen/finger on the screen, just touch\n" +
			    "and release it when the cross appears. When\n" +
			    "you are ready, tap the start button to begin.";
		strSRTFullIntroA =
				"Now a cross will appear another ";
		strSRTFullIntroB = 
				" times\n"+
				"and you should tap the screen as quickly as\n" +
				"you can, as in the practice. When you are ready,\n" +
				"tap the start button to start.";
		strCRTPracticeIntroA = 
				"In this test there will be four boxes on the screen.\n" +
				"A cross will appear in one of them and you have to\n" +
				"tap the box in which the cross appears as quickly\n" +
				"as you can. As before you will have a practice of\n";
		strCRTPracticeIntroB = 
				" crosses first. Remember, a cross can appear in\n" +
				"any of the four boxes. When you are ready, tap\n" +
				"the start button to start.";
		strCRTFullIntroA = 
				"You will now see another ";
		strCRTFullIntroB = 
				" crosses appear one after\n" +
				"another and you should respond as quickly as you\n" +
				"can by tapping the correct box, as in the practice.\n"+
				"When you are ready, tap the start button to start.";
		
		strSRTHeaderFileName = "srt_header.csv";
		strSRTDetailFileName = "srt_detail.csv";
		strCRTHeaderFileName = "crt_header.csv";
		strCRTDetailFileName = "crt_detail.csv";
		
		// Create StringBuilders.
		sbTestIntro = new StringBuilder();

		// Create widgets.
		lblDLMenu = new Label("Reaction Time Menu", globals.skin);
		btnRunSRTPractice = new TextButton("Run SRT Practice", globals.skin);
		btnRunSRTTest = new TextButton("Run SRT Test", globals.skin);
		btnRunCRTPractice = new TextButton("Run CRT Practice", globals.skin);
		btnRunCRTTest = new TextButton("Run CRT Test", globals.skin);
		btnMainMenu = new TextButton("Main Menu", globals.skin);	
		
		// Add listeners.		
		btnMainMenu.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				Gdx.input.setInputProcessor(null);
				state = null;
				globals.appState = AppState.MENU; 
			}
		});
		
		btnRunSRTPractice.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				sbTestIntro.setLength(0);
				sbTestIntro.append(strSRTPracticeIntroA);
				sbTestIntro.append(noOfPracticeTrialsSRT);
				sbTestIntro.append(strSRTPracticeIntroB);
				
				new Dialog("SRT Practice Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetupTest(TestType.SRT_PRACTICE);
                    }
				}.text(sbTestIntro.toString())
				 .button("Cancel", false) 
				 .button("Start", true)
                 .show(stage);
			}
		});
		
		btnRunSRTTest.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				sbTestIntro.setLength(0);
				sbTestIntro.append(strSRTFullIntroA);
				sbTestIntro.append(noOfExperimentTrialsSRT);
				sbTestIntro.append(strSRTFullIntroB);
				
				new Dialog("SRT Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetupTest(TestType.SRT_FULL);
                    }
				}.text(sbTestIntro.toString())
				 .button("Cancel", false) 
				 .button("Start", true)
                 .show(stage);
			}
		});
		
		btnRunCRTPractice.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				sbTestIntro.setLength(0);
				sbTestIntro.append(strCRTPracticeIntroA);
				sbTestIntro.append(noOfPracticeTrialsCRT);
				sbTestIntro.append(strCRTPracticeIntroB);
				
				new Dialog("CRT Practice Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetupTest(TestType.CRT_PRACTICE);
                    }
				}.text(sbTestIntro.toString())
				 .button("Cancel", false) 
				 .button("Start", true)
                 .show(stage);
			}
		});
		
		btnRunCRTTest.addListener(new ChangeListener() 
		{
			public void changed(ChangeEvent event, Actor actor) 
			{
				sbTestIntro.setLength(0);
				sbTestIntro.append(strCRTFullIntroA);
				sbTestIntro.append(noOfExperimentTrialsCRT);
				sbTestIntro.append(strCRTFullIntroB);
				
				new Dialog("CRT Instructions", globals.skin, "dialog")
				{
                    protected void result (Object object) 
                    {
                    	//System.out.println("Chosen: " + object);
                    	if (object.equals(true))
                    		SetupTest(TestType.CRT_FULL);
                    }
				}.text(sbTestIntro.toString())
				 .button("Cancel", false) 
				 .button("Start", true)
                 .show(stage);
			}
		});
		
		// Create tables for each menu.
		tblMenu = new Table(globals.skin);
		tblMenu.setFillParent(true);
	}
	
	/**
	 * Sets all values before each test type begins.
	 * 
	 * @param type
	 *   A valid TestType enum value indicating the test type to be run.
	 */
	private void SetupTest(TestType type)
	{
		doGetTimeStart = false;
		srtState = 1;
		crtState = 1;
		globals.glClearColourR = 0;
		globals.glClearColourG = 0;
		globals.glClearColourB = 128.0f / 255f;
		showingX = false;
		trialCounter = 1;
		globals.wascoSans30.setColor(Color.WHITE);
		waitElapsed = 0;
		prematureResponses = 0;
		anticipatedResponses = 0;
		trialCount = 0;
		correctCount = 0;
		wrongCount = 0;
		touchedSquare = -1;
		
		switch (type)
		{
			case SRT_PRACTICE:
				currentNoOfTrials = noOfPracticeTrialsSRT;
				currentInterStimulusIntervalFrom = interStimulusInvervalFromSRT;
				currentInterStimulusIntervalTo = interStimulusInvervalToSRT;
				whiteSquareXSprite.setPosition(whiteSquareSprite.getX(), whiteSquareSprite.getY());
				state = State.SRT_PRACTICE;
				break;
				
			case SRT_FULL:
				currentNoOfTrials = noOfExperimentTrialsSRT;
				currentInterStimulusIntervalFrom = interStimulusInvervalFromSRT;
				currentInterStimulusIntervalTo = interStimulusInvervalToSRT;
				whiteSquareXSprite.setPosition(whiteSquareSprite.getX(), whiteSquareSprite.getY());
				state = State.SRT_FULL;
				trialCount = noOfExperimentTrialsSRT;
				
				srtDetails.clear();
				for (int i = 0; i < noOfExperimentTrialsSRT; i++)
				{
					srtDetails.add(new SRTDetail());
				}
				break;
				
			case CRT_PRACTICE:
				currentNoOfTrials = noOfPracticeTrialsCRT;
				currentInterStimulusIntervalFrom = interStimulusInvervalFromCRT;
				currentInterStimulusIntervalTo = interStimulusInvervalToCRT;
				state = State.CRT_PRACTICE;
				break;
				
			case CRT_FULL:
				currentNoOfTrials = noOfExperimentTrialsCRT;
				currentInterStimulusIntervalFrom = interStimulusInvervalFromCRT;
				currentInterStimulusIntervalTo = interStimulusInvervalToCRT;
				state = State.CRT_FULL;
				trialCount = noOfExperimentTrialsCRT;
				
				crtDetails.clear();
				for (int i = 0; i < noOfExperimentTrialsCRT; i++)
				{
					crtDetails.add(new CRTDetail());
				}
				break;
		}
		
		Gdx.input.setInputProcessor(this);
	}
	
	/**
	 * Initialises the table to be displayed. 
	 * 
	 * @param table
	 *   An initialised Table object.
	 */
	private void SetTable(Table table)
	{
		tblMenu.clear();
		globals.glClearColourR = 1;
		globals.glClearColourG = 1;
		globals.glClearColourB = 1;
		
		switch (state) 
		{
			case MENU: // Add widgets to tblMenu.
				tblMenu.row().colspan(2).height(150);
				tblMenu.add(lblDLMenu).align(Align.center);
				
				tblMenu.row().colspan(2).height(150);
				tblMenu.add(btnRunSRTPractice).colspan(1).width(500);
				tblMenu.add(btnRunCRTPractice).colspan(1).width(500);
				
				tblMenu.row().colspan(2).height(150);
				tblMenu.add(btnRunSRTTest).colspan(1).width(500);
				tblMenu.add(btnRunCRTTest).colspan(1).width(500);
				
				tblMenu.row().colspan(2).height(150);
				tblMenu.add(btnMainMenu).width(1000).colspan(2);
				break;
		}
		
		// Setup stage.
		stage.clear();
		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
		//Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	/** Release all resources used by objects that are disposable. */
	public void Dispose()
	{
		stage.dispose();
	}
	
	/** Update method called once per frame. Updates all logic before each draw call. */
	public void Update()
	{
		if (state == null)
		{
			state = State.MENU;
			SetTable(tblMenu);
		}
		
		switch (state)
		{
			case SRT_PRACTICE:
			case SRT_FULL:
				switch (srtState)
				
				{
					case 1: // Show intro message, await user input.
						break;
						
					case 2: // Setup current "X".
						showingX = false;
						
						// End test if user has completed all the trials.
						if (trialCounter > currentNoOfTrials)
						{
							if (state == State.SRT_FULL)
								Save();
							
							srtState = 5;
							return;
						}
						
						// Set a random Inter Stimulus Interval.
						interStimulusIntervalTimer = 0;
						currentInterStimulusInterval = MathUtils.random(currentInterStimulusIntervalFrom, currentInterStimulusIntervalTo);
						
						srtState = 3;
						break;
						
					case 3: // Waiting for the currentInterStimulusInterval to elapse.
						interStimulusIntervalTimer += Gdx.graphics.getDeltaTime();
						if (interStimulusIntervalTimer >= currentInterStimulusInterval)
						{
							currentInterStimulusInterval = interStimulusIntervalTimer;
							srtState = 4;
							showingX = true;
							//responseTimeStart = Calendar.getInstance().getTimeInMillis();
							doGetTimeStart = true;
						}
						break;
						
					case 4: // Show "X" and wait for user to touch screen.
						break;
						
					case 5: // Display test completed message for 5 seconds.
						waitElapsed += Gdx.graphics.getDeltaTime();
						if (waitElapsed >= waitInterval)
						{
							srtState = 6;
						}
						break;
						
					case 6: // Display return to menu message.
						
						break;
				}				
				break;
				
			case CRT_PRACTICE:				
			case CRT_FULL:
				switch (crtState)
				{
					case 1: // Show intro message, await user input.						
						break;
						
					case 2: // Setup current "X".
						showingX = false;
						
						// End test if user has completed all the trials.
						if (trialCounter > currentNoOfTrials)
						{
							if (state == State.CRT_FULL)
								Save();
							
							crtState = 5;
							return;
						}
						
						// Set a random Inter Stimulus Interval.
						interStimulusIntervalTimer = 0;
						currentInterStimulusInterval = MathUtils.random(currentInterStimulusIntervalFrom, currentInterStimulusIntervalTo);
						
						// Randomly set the position of the "X".
						xPosition = MathUtils.random(0, 3);
						whiteSquareXSprite.setPosition(whiteSquares.get(xPosition).getX(), 
								whiteSquares.get(xPosition).getY());
						
						crtState = 3;
						break;
						
					case 3: // Waiting for the currentInterStimulusInterval to elapse.
						interStimulusIntervalTimer += Gdx.graphics.getDeltaTime();
						if (interStimulusIntervalTimer >= currentInterStimulusInterval)
						{
							currentInterStimulusInterval = interStimulusIntervalTimer;
							crtState = 4;
							showingX = true;
							//responseTimeStart = Calendar.getInstance().getTimeInMillis();
							doGetTimeStart = true;
						}
						break;
						
					case 4: // Show "X" and wait for user to touch a square.
						break;
						
					case 5: // Display test completed message for 5 seconds.
						waitElapsed += Gdx.graphics.getDeltaTime();
						if (waitElapsed >= waitInterval)
						{
							crtState = 6;
						}
						break;
						
					case 6: // Display return to menu message.
						
						break;
				}
				break;
				
			default: // Menus
				stage.act(Gdx.graphics.getDeltaTime());
				break;
		}
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
				stage.draw();
				break;
				
			case SRT_PRACTICE:
			case SRT_FULL:
				// Debug info.
				//globals.wascoSans30.draw(spriteBatch, "ISI: " + currentInterStimulusInterval, 5, 5);
				//globals.wascoSans30.draw(spriteBatch, "Response: " + currentResponse, 400, 5);
				//globals.wascoSans30.draw(spriteBatch, "trialCounter: " + trialCounter, 5, 5);
				//globals.wascoSans30.draw(spriteBatch, "currentNoOfTrials: " + currentNoOfTrials, 400, 5);
				
				if (srtState == 1)
				{
					globals.wascoSans30.drawWrapped(spriteBatch, strTapScreenToStart, 0, 
							100, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
				}
				else if (srtState == 5 || srtState == 6)
				{
					globals.wascoSans30.drawWrapped(spriteBatch, strTestCompleted, 0, 
							100, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
				}
				
				if (srtState == 6)
				{
					globals.wascoSans30.drawWrapped(spriteBatch, strTapScreenToReturnToMenu, 0, 
							150, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
				}
				
				if (showingX)
					whiteSquareXSprite.draw(spriteBatch);
				else
					whiteSquareSprite.draw(spriteBatch);
				break;
				
			case CRT_PRACTICE:
			case CRT_FULL:
				// Debug info.
				//globals.wascoSans30.draw(spriteBatch, "ISI: " + currentInterStimulusInterval, 5, 5);
				//globals.wascoSans30.draw(spriteBatch, "Response: " + currentResponse, 400, 5);
				//globals.wascoSans30.draw(spriteBatch, "Touched: " + String.valueOf(touchedSquare + 1), 900, 5);
				
				for (Sprite sprite : whiteSquares)
				{
					sprite.draw(spriteBatch);
				}
				
				// Debug info (square indexes).
//				for (int i = 0; i < whiteSquares.size; i++)
//				{
//					globals.wascoSans30.setColor(Color.CYAN);
//					globals.wascoSans30.draw(spriteBatch, String.valueOf(i), whiteSquares.get(i).getX(), whiteSquares.get(i).getY());				
//				}
				
				if (showingX)
					whiteSquareXSprite.draw(spriteBatch);
				
				if (crtState == 1)
				{
					globals.wascoSans30.drawWrapped(spriteBatch, strTapScreenToStart, 0, 
							100, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
				}
				else if (crtState == 5 || crtState == 6)
				{
					globals.wascoSans30.drawWrapped(spriteBatch, strTestCompleted, 0, 
							100, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
				}
				
				if (crtState == 6)
				{
					globals.wascoSans30.drawWrapped(spriteBatch, strTapScreenToReturnToMenu, 0, 
							150, globals.VIRTUAL_WIDTH, HAlignment.CENTER);
				}
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
		//responseTimeEnd = System.currentTimeMillis(); // Calendar.getInstance().getTimeInMillis();
		responseTimeEnd = TimeUtils.nanoTime();
		
		// Disable multi-touch.
		if (pointer > 0 || button > 0)
			return false;
		
		// Translate touch co-ords to virtual screen co-ords.
		touchPos.x = screenX;
		touchPos.y = screenY;
		touchPos.z = 0;

//		globals.camera.unproject(touchPos, globals.resizeViewport.x, globals.resizeViewport.y, 
//				globals.resizeViewport.width, globals.resizeViewport.height);
		
		// Need this line only window size != VIRTUAL sizes in globals.
		globals.camera.unproject(touchPos, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		switch (state)
		{
			case SRT_PRACTICE:
			case SRT_FULL:
				switch (srtState)
				{
					case 1: // User touched screen at start test prompt, so start the test.
						srtState = 2;						
						startTime = responseTimeEnd;
						return true;
						
					case 3: // User touched screen before "X" was displayed - log as a premature response.
						prematureResponses++;
						break;
						
					case 4: // User touched screen while "X" is displayed.
						
						// Set current response time.
						//responseTimeEnd = Calendar.getInstance().getTimeInMillis();
						currentResponse = responseTimeEnd - responseTimeStart;
						currentResponse = TimeUnit.MILLISECONDS.convert(currentResponse, TimeUnit.NANOSECONDS);
						
						// Did the screen touch occur within the response range?
						if (currentResponse >= responseRangeFromSRT && currentResponse <= responseRangeToSRT)
						{
							// Update details record if full test.
							if (state == State.SRT_FULL)
							{
								srtDetails.get(trialCounter - 1).runNumber = trialCounter;								
								srtDetails.get(trialCounter - 1).response = currentResponse;
								srtDetails.get(trialCounter - 1).interStimulusInterval = currentInterStimulusInterval;
							}
							
							trialCounter++;
							//srtState = 2;
							srtState = 7;
						}						
						else if (currentResponse < responseRangeFromSRT)
						{
							// Input was before "response range from" so trial must be repeated.
							anticipatedResponses++;
							//srtState = 2;
							srtState = 7;
						}	
						else if (currentResponse > responseRangeToSRT)
						{
							// Input was after "response range to" so trial must be repeated.
							//srtState = 2;
							srtState = 7;
						}
						return true;
						
					case 6: // Test completed - user touched screen to return to menu.
						state = State.MENU;
						SetTable(tblMenu);						
						break;
						
					case 7: // Waiting for finger/pen to be lifted from screen.
						
						break;
				}
				break;
				
			case CRT_PRACTICE:				
			case CRT_FULL:
				switch (crtState)
				{
					case 1: // User touched screen at start test prompt, so start the test.
						crtState = 2;
						startTime = responseTimeEnd;
						return true;
						
					case 3: // User touched screen before "X" was displayed - log as a premature response.
						prematureResponses++;
						break;
						
					case 4: // User touched screen while "X" is displayed.
						
						// Set current response time.
						//responseTimeEnd = Calendar.getInstance().getTimeInMillis();
						currentResponse = responseTimeEnd - responseTimeStart;
						currentResponse = TimeUnit.MILLISECONDS.convert(currentResponse, TimeUnit.NANOSECONDS);
																		
						// Did the screen touch occur within the response range?
						if (currentResponse >= responseRangeFromCRT && currentResponse <= responseRangeToCRT)
						{
							// Which square did the user touch?
							touchedSquare = -1; // Minus one means screen was touched but no square was touched.
							for (int i = 0; i < whiteSquaresCount; i++)
							{
								if (whiteSquares.get(i).getBoundingRectangle().contains(touchPos.x, touchPos.y))
								{
									touchedSquare = i;
									break;
								}
							}
							
							// Did user touch "X"?
							if (touchedSquare != -1 && xPosition == touchedSquare)
							{
								correctCount++;
							}
							else
							{
								wrongCount++; // Any other box or any other part of the screen was touched.
							}
							
							// Update details record if full test.
							if (state == State.CRT_FULL)
							{
								crtDetails.get(trialCounter - 1).runNumber = trialCounter;
								crtDetails.get(trialCounter - 1).correctResponse = xPosition;
								crtDetails.get(trialCounter - 1).subjectResponse = touchedSquare;
								crtDetails.get(trialCounter - 1).responseTime = currentResponse;
								crtDetails.get(trialCounter - 1).interStimulusInterval = currentInterStimulusInterval;
							}							
							
							trialCounter++;
							//crtState = 2;
							crtState = 7;
						}
						else if (currentResponse < responseRangeFromCRT)
						{
							// Input was after ISI but before "response range from" so trial must be repeated.
							anticipatedResponses++;
							//crtState = 2;
							crtState = 7;
						}	
						else if (currentResponse > responseRangeToCRT)
						{
							// Input was after "response range to" so trial must be repeated.
							//crtState = 2;
							crtState = 7;
						}	
						return true;
						
					case 6: // Test completed - user touched screen to return to menu.
						state = State.MENU;
						SetTable(tblMenu);						
						break;
						
					case 7: // Waiting for finger/pen to be lifted from screen.
						
						break;
				}
				break;
		}		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		// Disable multi-touch.
		if (pointer > 0 || button > 0)
			return false;
		
		// Resume test if user was holding finger/pen down while X was displayed.
		switch (state)
		{
			case SRT_PRACTICE:
			case SRT_FULL:
				if (srtState == 7)
					srtState = 2;
				break;
				
			case CRT_PRACTICE:
			case CRT_FULL:
				if (crtState == 7)
					crtState = 2;
				break;
		}
		
//		// Quit test if two fingers touched the screen.
//		if (state != State.MENU)
//		{
//			if (fingersOnScreen == 2)
//			{
//				fingersOnScreen = 0;
//				state = State.MENU;
//				SetTable(tblMenu);
//				return false;
//			}
//			fingersOnScreen--;
//		}		
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
	
	/** Append data for last test to csv files. */
	private void Save()
	{
		if (Gdx.files.isLocalStorageAvailable())
		{
			BufferedWriter out = null;
			boolean error = false;
			
			switch (state)
			{
				case SRT_FULL:
					
					// SRT Header
					try
					{
						out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strSRTHeaderFileName).write(true)));						
						out.write(globals.sbCurrentParticipantID.toString());
						out.write(globals.strComma);
						out.write(String.valueOf(prematureResponses));
						out.write(globals.strComma);
						out.write(String.valueOf(anticipatedResponses));
						out.write(globals.strComma);
						out.write(String.valueOf(trialCount));
						out.write(globals.strComma);
						out.write(Long.toString(startTime));
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
					
					// SRT Details
					try
					{
						out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strSRTDetailFileName).write(true)));						
						for (int i = 0; i < srtDetails.size; i++)
						{
							out.write(globals.sbCurrentParticipantID.toString());
							out.write(globals.strComma);
							out.write(String.valueOf(srtDetails.get(i).runNumber));
							out.write(globals.strComma);
							out.write(Long.toString(srtDetails.get(i).response));
							out.write(globals.strComma);
							out.write(Float.toString(srtDetails.get(i).interStimulusInterval));
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
					break;
				
				case CRT_FULL:
					
					// CRT Header
					try
					{
						out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strCRTHeaderFileName).write(true)));						
						out.write(globals.sbCurrentParticipantID.toString());
						out.write(globals.strComma);
						out.write(String.valueOf(prematureResponses));
						out.write(globals.strComma);
						out.write(String.valueOf(anticipatedResponses));
						out.write(globals.strComma);
						out.write(String.valueOf(correctCount));
						out.write(globals.strComma);
						out.write(String.valueOf(wrongCount));
						out.write(globals.strComma);
						out.write(Long.toString(startTime));
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
					
					// CRT Details
					try
					{
						out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strCRTDetailFileName).write(true)));						
						for (int i = 0; i < crtDetails.size; i++)
						{
							out.write(globals.sbCurrentParticipantID.toString());
							out.write(globals.strComma);
							out.write(String.valueOf(crtDetails.get(i).runNumber));
							out.write(globals.strComma);
							out.write(String.valueOf(crtDetails.get(i).correctResponse));
							out.write(globals.strComma);
							out.write(String.valueOf(crtDetails.get(i).subjectResponse));
							out.write(globals.strComma);
							out.write(Long.toString(crtDetails.get(i).responseTime));
							out.write(globals.strComma);
							out.write(Float.toString(crtDetails.get(i).interStimulusInterval));
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
					break;
			}
		}
	}
}