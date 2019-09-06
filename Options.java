package com.bc.memorytest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//import org.apache.commons.codec.binary.Base64;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.bc.memorytest.Globals.AppState;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class Options //implements HttpResponseListener
{
    //private final Array<TrailMakingSave> saveItems;
    private final Label lblOptions;
    private final Label lblParticipantID;
    private final Label lblSubjectID;
    private final Label lblStudyID;
    private final TextButton btnSyncData;
    private final TextButton btnMainMenu;
    private final TextField txtSubjectID;
    private final TextField txtStudyID;
    private final Table table;
    private final Globals globals;
    private final Stage stage;
    private final SelectBox participants;
    private final Array<String> pidArray;
    private final String strPIDsCSVFileName;
    
    /**
     * Options constructor. All class objects and variables are initialised here.
     * 
     * @param globals
     *   An initialised Globals object.
     */
    public Options(final Globals globals)
    {		
        this.globals = globals;	
        stage = new Stage();
        strPIDsCSVFileName = "pids.csv";
        pidArray = new Array<String>();		
        
        // TODO: remove when I get server auth sorted.
        String[] participantsList = {"TRIALX10001", "TRIALX10002", "TRIALX10003", "TRIALX10004", "TRIALX10005", "TRIALX10006", 
                "TRIALX10007", "TRIALX10008", "TRIALX10009", "TRIALX10010", "TRIALX10011", "TRIALX10012", "TRIALX10013",
                "TRIALX10014", "TRIALX10015", "TRIALX10016", "TRIALX10017", "TRIALX10018", "TRIALX10019", "TRIALX10020", 
                "TRIALX10021", "TRIALX10022", "TRIALX10023", "TRIALX10024", "TRIALX10025", "TRIALX10026", "TRIALX10027",
                "TRIALX10028", "TRIALX10029", "TRIALX10030", "TRIALX10031", "TRIALX10032", "TRIALX10033", "TRIALX10034",
                "TRIALX10035", "TRIALX10036", "TRIALX10037", "TRIALX10038", "TRIALX10039", "TRIALX10040", "TRIALX10041",
                "TRIALX10042", "TRIALX10043", "TRIALX10044", "TRIALX10045", "TRIALX10046", "TRIALX10047", "TRIALX10048",
                "TRIALX10049", "TRIALX10050", "TRIALX10051", "TRIALX10052", "TRIALX10053", "TRIALX10054", "TRIALX10055"};
        
        // TODO: remove when I get server auth sorted.
        globals.sbCurrentParticipantID.setLength(0);
        globals.sbCurrentParticipantID.append(participantsList[0]);
        
        // Create widgets.
        lblOptions = new Label("Options", globals.skin);
        lblOptions.setAlignment(Align.center);
        lblParticipantID = new Label("Participant ID:", globals.skin);
        lblSubjectID = new Label("Subject ID:", globals.skin);
        lblStudyID = new Label("Study ID:", globals.skin);
        btnSyncData = new TextButton("Sync Data", globals.skin);
        btnMainMenu = new TextButton("Main Menu", globals.skin);
        txtSubjectID = new TextField("", globals.skin);
        txtStudyID = new TextField("", globals.skin);
        //participants = new SelectBox(new String[0], globals.skin); // initially contains no items
        participants = new SelectBox(participantsList, globals.skin);
        
        //LoadPIDs();
        
        // Add listeners.
        txtSubjectID.addListener(new ClickListener() 
        {
            public void clicked(InputEvent event, float x, float y) 
            {
                Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });
        
        txtStudyID.addListener(new ClickListener() 
        {
            public void clicked(InputEvent event, float x, float y) 
            {
                Gdx.input.setOnscreenKeyboardVisible(true);
            }
        });
        
        participants.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                globals.sbCurrentParticipantID.setLength(0);
                globals.sbCurrentParticipantID.append(participants.getSelection());
            }
        });
        
        btnSyncData.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                // Check that there is an internet connection available.
                
                
                // Get PIDs from server.
                //getServerPIDs();
                
                // Send Trail Making data to server.
                
                
                // Send Reaction Time data to server.
                
                
                // Send Paired Associates Learning data to server.
                
            }
        });
        
        btnMainMenu.addListener(new ChangeListener() 
        {
            public void changed(ChangeEvent event, Actor actor) 
            {
                Gdx.input.setInputProcessor(null);
                globals.appState = AppState.MENU;
            }
        });
        
        // Create table for each menu.
        table = new Table(globals.skin);
        table.setFillParent(true);
        
        // Add widgets to table.
        table.row().height(50);
        table.add(lblOptions).width(700).colspan(2);
        table.row().height(150);
        table.add(lblParticipantID).width(300).colspan(1);	
        table.add(participants).width(400).colspan(1);		
        table.row().height(150);
        table.add(btnSyncData).width(700).colspan(2);
        table.row().height(150);
        table.add(btnMainMenu).width(700).colspan(2);
        
        stage.addActor(table);
        
        //stage.setViewport(1280, 800, false);
    }
    
    /** Update local PID csv with data from server. */
//	private void getServerPIDs()
//	{
//		try
//	    {
//			//I use Jackson for JSON serialisation; you'd need to change this for however you generate your JSON
//	        //String json = mapper.writeValueAsString(mapData);
//	         
//	        //Create a POST request to get JSON data from server.
//	        HttpRequest request = new HttpRequest(HttpMethods.GET);
//	        request.setUrl("http://mudfoot.doc.stu.mmu.ac.uk/students/cassidyb/mem-test/process.php?type=PIDs");
//	        
//	        //request.setHeader("Content-Type", "application/json");
//	        request.setHeader("Content-Length", "0");
//	        //request.setHeader("type", "PIDs");
//
//	        //Assuming you're using HTTP Basic.
//	        String userCredentials = "cassidyb:cerflonJ2";
//	        String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
//	        request.setHeader("Authorization", basicAuth);
//	         
//	        //Send the request, and define a callback to execute when we get a response
//	        Gdx.net.sendHttpRequest(request, new HttpResponseListener()
//	        {
//	        	//This is executed on successful response
//	            @Override
//	            public void handleHttpResponse(HttpResponse httpResponse)
//	            {
//	               Gdx.app.log("Net", "response code was " + httpResponse.getStatus().getStatusCode());
//	               Gdx.app.log("Net", "response code was " + httpResponse.getResultAsString());
//	            }
//	            
//	            //This happens if it goes wrong
//	            @Override
//	            public void failed(Throwable t)
//	            {
//	               Gdx.app.log("Net", "Failed");
//	            }
//	        });
//	    }
//		finally
//		{
//			
//		}
//	    //catch(IOException e)
//	    //{
//	    //   throw new GdxRuntimeException("JSON b0rk");
//	    //}
//	}
    
    /** Populate participants SelectBox with contents of local PID file */
    private void LoadPIDs()
    {				
        // Check if the file exists.
        FileHandle handle = Gdx.files.local(strPIDsCSVFileName);
        if (!handle.exists())
        {
            return;
        }		
        
        // Close the file in case it was previously left open.
        try
        {
            InputStream in = Gdx.files.local(strPIDsCSVFileName).read();
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            //message += "Open local success\n";
        }
        catch (Throwable e)
        {
            //message += "Couldn't open localstorage/test.txt\n" + e.getMessage() + "\n";
        }
 
        // Read PIDs into pidArray.
        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(Gdx.files.local(strPIDsCSVFileName).read()));
 
            String pid = "";
            while ((pid = in.readLine()) != null)
            {
                pidArray.add(pid);
            }
        }
        catch (GdxRuntimeException ex)
        {
            //message += "Couldn't open localstorage/test.txt\n";
            ex.printStackTrace();
        }
        catch (IOException e)
        {
            //message += "Couldn't read localstorage/test.txt\n";
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        if (pidArray.size > 0)
        {
            // Set current PID to first PID in array.
            globals.sbCurrentParticipantID.setLength(0);
            globals.sbCurrentParticipantID.append(pidArray.get(0));
            
            // Convert pidArray to a String[] so we can populate participants SelectBox.
            String[] pidArr = new String[pidArray.size];
            pidArr = pidArray.toArray(String.class);
            
            // Populate participants SelectBox.
            participants.setItems(pidArr);
        }
    }
    
    /** Update method called once per frame. Updates all logic before each draw call. */
    public void Update()
    {
        if (Gdx.input.getInputProcessor() == null)
            Gdx.input.setInputProcessor(stage);
            
        stage.act(Gdx.graphics.getDeltaTime());
    }
    
    /** Draw method, called once per frame after logic updates. */
    public void Draw()
    {
        stage.draw();
    }
    
    /** Release all resources used by objects that are disposable. */
    public void Dispose()
    {
        stage.dispose();
    }
    
    /** Update local PID list with list from server. */
    private void UpdatePIDs()
    {	
        // Save server data to local file.
        if (Gdx.files.isLocalStorageAvailable())
        {
            BufferedWriter out = null;
            try
            {
                //out = new BufferedWriter(new OutputStreamWriter(Gdx.files.local(strFileName).write(true)));
                
                out.write(globals.sbCurrentParticipantID.toString());
                out.write(globals.strComma);
                //out.write(Long.toString(dateTime));
                out.write(globals.strComma);
                
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