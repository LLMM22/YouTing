package com.example.testvolley;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyMessageActivity extends Activity {
	public static boolean hasmessage=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_message);
	}
	
	public void ReturnToMain(View view){  
		this.finish();
	}
}

