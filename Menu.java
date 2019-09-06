package com.bc.memorytest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bc.memorytest.Globals.AppState;

/**
 * Class to handle Main Menu logic.
 * 
 * @author Bill Cassidy
 */
public class Menu
{
    private final Label lblTitle;
    private final Label lblMainMenu;
    private final TextButton btnTrailMakingTest;
    private final TextButton btnReactionTimeTest;
    private final TextButton btnPairedAssociatesLearningTest;
    private final TextButton btnOptions;
    private final Table table;
    private final Globals globals;
    private final Stage stage;
    
    /**
     * Menu constructor. All class objects and variables are initialised here.
     * 
     * @param globals
     *   An initialised Globals object.
     */
    public Menu(final Globals globals)
    {		
        this.globals = globals;	
        stage = new Stage();
        
        // Create widgets.
        lblTitle = new Label("MEMORY TEST", globals.skin);
        lblTitle.setAlignment(Align.center);
        lblMainMenu = new Label("Main Menu", globals.skin);
        lblMainMenu.setAlignment(Align.center);
        btnTrailMakingTest = new TextButton("Trail Making Test", globals.skin);
        btnReactionTimeTest = new TextButton("Reaction Time Test", globals.skin);		
        btnPairedAssociatesLearningTest = new TextButton("Paired Associates Learning Test", globals.skin);
        btnOptions = new TextButton("Options", globals.skin);

        // Add listeners.
        btnTrailMakingTest.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                globals.appState = AppState.TEST1;
            }
        });
        
        btnReactionTimeTest.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                globals.appState = AppState.TEST2;
            }
        });
        
        btnPairedAssociatesLearningTest.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                globals.appState = AppState.TEST3;
            }
        });
        
        btnOptions.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                Gdx.input.setInputProcessor(null);
                globals.appState = AppState.OPTIONS;
            }
        });

        // Create table.
        table = new Table(globals.skin);
        table.setFillParent(true);
//		table.setWidth(globals.resizeViewport.width);
//		table.debug();
        
        // Add widgets to table.
        table.row().height(50);
        table.add(lblTitle).width(700);
        table.row().height(50);
        table.add(lblMainMenu).width(700);
        table.row().height(150);
        table.add(btnTrailMakingTest).width(700);
        table.row().height(150);
        table.add(btnReactionTimeTest).width(700);
        table.row().height(150);
        table.add(btnPairedAssociatesLearningTest).width(700);
        table.row().height(150);
        table.add(btnOptions).width(700);
        
        stage.addActor(table);
    }
    
    /** Release all resources used by objects that are disposable. */
    public void Dispose()
    {
        stage.dispose();
    }

    /** Update method called once per frame. Updates all logic before each draw call. */
    public void Update()
    {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (Gdx.input.getInputProcessor() == null)
            Gdx.input.setInputProcessor(stage);
            
        stage.act(Gdx.graphics.getDeltaTime());
    }

    /** Draw method, called once per frame after logic updates. */
    public void Draw()
    {
        stage.draw();
//        Table.drawDebug(stage);
    }
}