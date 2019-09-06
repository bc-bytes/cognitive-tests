package com.bc.memorytest;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bc.memorytest.Globals.AppState;

/**
 * Class to handle the main application states.
 * 
 * @author Bill Cassidy
 */
public class Main implements ApplicationListener
{
    private Globals globals;
    private SpriteBatch spriteBatch;
    public ShapeRenderer shapeRenderer;
    private Menu menu;
    private Test1 test1;
    private Test2 test2;
    private Test3 test3;
    private Options options;

    /** Method that is called when the app is first loaded. */
    public void create()
    {
        globals = new Globals();
        Gdx.input.setCatchMenuKey(true); // disable Android Menu button
        Gdx.input.setCatchBackKey(true); // disable Android Back button

        spriteBatch = new SpriteBatch();

        Gdx.graphics.setVSync(true);
        shapeRenderer = new ShapeRenderer();
        Gdx.gl20.glLineWidth(3 / globals.camera.zoom);
        menu = new Menu(globals);
        test1 = new Test1(globals, shapeRenderer);
        test2 = new Test2(globals);
        test3 = new Test3(globals);
        options = new Options(globals);
        globals.appState = AppState.MENU;
    }

    /**
     * Method that is called every frame. Logic updates should be called first
     * followed by all drawing calls.
     */
    public void render()
    {
        // Clear the screen.		
        Gdx.graphics.getGL20().glClearColor(globals.glClearColourR, globals.glClearColourG, globals.glClearColourB, 1);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Tell the camera to update its matrices.
        globals.camera.update();

        // Tell SpriteBatch & ShapeRenderer to render in the coordinate system specified by the camera.
        spriteBatch.setProjectionMatrix(globals.camera.combined);
        shapeRenderer.setProjectionMatrix(globals.camera.combined);

        switch (globals.appState)
        {
            case MENU:
                // update...
                menu.Update();

                // draw...				
                spriteBatch.begin();
                spriteBatch.setColor(Color.WHITE);
                menu.Draw();
                spriteBatch.end();
                break;

            case TEST1:
                // update...
                test1.Update();

                // draw...
                spriteBatch.begin();
                spriteBatch.setColor(Color.WHITE);
                test1.Draw(spriteBatch);
                spriteBatch.end();
                
                if (globals.doScreenshot)
                {
                    ScreenshotFactory.saveScreenshot(globals.sbCurrentParticipantID.toString() + "_" + test1.testTypes[test1.testTypeTakenIndex]);
                    globals.doScreenshot = false;
                }
                break;

            case TEST2:
                // update...
                test2.Update();

                // draw...
                spriteBatch.begin();
                spriteBatch.setColor(Color.WHITE);
                test2.Draw(spriteBatch);
                spriteBatch.end();
                
                // Set responseTimeStart if indicated to do so by test2 update. This value is set here as this is the point at which the 
                // X has just been drawn to screen.
                if (test2.doGetTimeStart)
                {
                    test2.responseTimeStart = System.nanoTime(); // System.currentTimeMillis(); //Calendar.getInstance().getTimeInMillis();
                    test2.doGetTimeStart = false;
                }
                break;

            case TEST3:
                // update...
                test3.Update();

                // draw...
                spriteBatch.begin();
                spriteBatch.setColor(Color.WHITE);
                test3.Draw(spriteBatch);
                spriteBatch.end();
                break;
                
            case OPTIONS:
                // update...
                options.Update();

                // draw...
                spriteBatch.begin();
                spriteBatch.setColor(Color.WHITE);
                options.Draw();
                spriteBatch.end();
                break;
        }
    }

    /**
     * Method called when the app is resized. For apps with fixed orientation,
     * this is only called when the app is started or resumed. Aspect ratio is
     * maintained by scaling and cropping with black borders if running on a
     * device that does not match our native resolution (defined by
     * VIRTUAL_WIDTH and VIRTUAL_HEIGHT in Globals class).
     * 
     * @param width
     *            The pixel width of the resized screen.
     * 
     * @param height
     *            The pixel height of the resized screen.
     */
    public void resize(int width, int height)
    {
        //		Vector2 size = Scaling.fit.apply(globals.VIRTUAL_WIDTH, globals.VIRTUAL_HEIGHT, width, height);
        //		globals.resizeViewport.x = (int)(width - size.x) / 2;
        //		globals.resizeViewport.y = (int)(height - size.y) / 2;
        //		globals.resizeViewport.width = (int)size.x;
        //		globals.resizeViewport.height = (int)size.y;
        //		
        //        Gdx.gl.glViewport(
        //        		(int)globals.resizeViewport.x, 
        //        		(int)globals.resizeViewport.y, 
        //        		(int)globals.resizeViewport.width, 
        //        		(int)globals.resizeViewport.height);
        //        
        //        menu.Resize();
        //        test1.Resize();
    }

    /**
     * Method called when the application is interrupted and paused by an
     * external event such as an incoming call or when the user presses the home
     * button.
     */
    public void pause()
    {
    }

    /**
     * Method called once at start-up and everytime the application returns from
     * a paused state.
     */
    public void resume()
    {
        Gdx.gl20.glLineWidth(3 / globals.camera.zoom);
    }

    /** Dispose of all disposable objects to free up memory when app is closed. */
    public void dispose()
    {
        globals.Dispose();
        shapeRenderer.dispose();
        menu.Dispose();
        test1.Dispose();
        test2.Dispose();
        test3.Dispose();
        options.Dispose();
    }
}