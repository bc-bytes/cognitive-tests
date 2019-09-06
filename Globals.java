package com.bc.memorytest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.StringBuilder;

public final class Globals
{
    public final int VIRTUAL_WIDTH = 1280; //1280 (iOS: 1024)
    public final int VIRTUAL_HEIGHT = 980; //800  (iOS: 768)
    public final float ASPECT_RATIO = (float)VIRTUAL_WIDTH / (float)VIRTUAL_HEIGHT;
    
    public AppState appState;
    public final TextureAtlas spritesAtlas;
    //public BitmapFont wascoSans64;
    public final Rectangle viewportRect;
    public final Rectangle resizeViewport;
    public BitmapFont wascoSans20;
    public BitmapFont wascoSans30;
    private final FreeTypeFontGenerator generator;
    private final String FONT_CHARS;
    public final Skin skin;
    public final OrthographicCamera camera;
    public float glClearColourR;
    public float glClearColourG;
    public float glClearColourB;
    public StringBuilder sbCurrentParticipantID;
    public final String strNewLine;
    public final String strComma;
    public boolean doScreenshot;
    
    public Globals()
    {
        camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        camera.setToOrtho(true, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        glClearColourR = 1;
        glClearColourG = 1;
        glClearColourB = 1;
        
        strNewLine = "\n";
        strComma = ",";
        
        sbCurrentParticipantID = new StringBuilder();
        
        skin = new Skin(Gdx.files.internal("ui/Holo-light-xhdpi.json"));
        
        FONT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"`'<>";
        spritesAtlas = new TextureAtlas(Gdx.files.internal("sprite_pack.atlas"));
        viewportRect = new Rectangle(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        resizeViewport = new Rectangle(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        
        //Dispose of font(s) before loading. Need to do this in case they partially exist from a previous 
        //app state.
        if (wascoSans20 != null)
            wascoSans20.dispose();
        
        if (wascoSans30 != null)
            wascoSans30.dispose();
    
//		wascoSans64 = new BitmapFont(Gdx.files.internal("fonts/WascoSans64.fnt"), true);
        
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/WascoSans.ttf"));
        
        wascoSans20 = generator.generateFont(20, FONT_CHARS, true);
        wascoSans20.setColor(Color.BLACK); //object is unmanaged so might be best to set colour before each draw.
        
        wascoSans30 = generator.generateFont(30, FONT_CHARS, true);
        wascoSans30.setColor(Color.BLACK); //object is unmanaged so might be best to set colour before each draw.
        generator.dispose();
    }
    
    public void Resize()
    {
        Vector2 size = Scaling.fit.apply(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, Gdx.graphics.getWidth(), 
                Gdx.graphics.getHeight());
        resizeViewport.x = (int)(Gdx.graphics.getWidth() - size.x) / 2;
        resizeViewport.y = (int)(Gdx.graphics.getHeight() - size.y) / 2;
        resizeViewport.width = (int)size.x;
        resizeViewport.height = (int)size.y;
        
        Gdx.gl.glViewport(
                (int)resizeViewport.x, 
                (int)resizeViewport.y, 
                (int)resizeViewport.width, 
                (int)resizeViewport.height);
    }
    
    public enum AppState
    {
        MENU,
        TEST1,
        TEST2,
        TEST3,
        OPTIONS,
        EXIT
    }
    
    public void Dispose()
    {
        spritesAtlas.dispose();
        wascoSans20.dispose();
        wascoSans30.dispose();
        skin.dispose();
    }
}