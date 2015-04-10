package com.example.testvolley;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MyFriendsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_friends);
	}
	
	public void ReturnToMain(View view){  
		this.finish();
	}
}

