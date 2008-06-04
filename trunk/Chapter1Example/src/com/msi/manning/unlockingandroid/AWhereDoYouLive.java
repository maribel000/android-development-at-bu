package com.msi.manning.unlockingandroid;

import com.msi.manning.unlockingandroid.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AWhereDoYouLive extends Activity 
{
    @Override
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        

        final EditText addressfield = (EditText) findViewById(R.id.address);
        
        final Button button = (Button) findViewById(R.id.launchmap);
        button.setOnClickListener(new Button.OnClickListener() 
        { 
            public void onClick(View v) 
            {
            	try
            	{
                // Perform action on click
            		String address = addressfield.getText().toString();
            		address = address.replace(' ', '+');
            		Intent myIntent = new Intent(android.content.Intent.VIEW_ACTION,Uri.parse("geo:0,0?q=" + address));
           	     	startActivity(myIntent);
            	}           	     
            	catch (Exception e)
            	{
            		showAlert("failed to launch",0,e.getMessage(),"OK",false);
            	}
        	}
        }
        );                
        
        
    }
}