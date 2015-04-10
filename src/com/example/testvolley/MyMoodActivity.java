package com.example.testvolley;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.example.request.JsonObjectRequest;
import com.example.testvolley.MainActivity;

import de.greenrobot.daoexample.User;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MyMoodActivity extends Activity {
	private EditText content_mood;
	private MyApplication application;
	private RequestQueue mQueue;
	private String mood;
	
	private final String TAG = "MyMood";
	private final String changeMood_url = "http://121.42.164.7/index.php/Home/Index/change_mood";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_mood);
		application = (MyApplication)this.getApplicationContext();
		mQueue = application.getRequestQueue();
		content_mood = (EditText)findViewById(R.id.mood_edit);
		mood = application.getLoginUser().getMood();
		if (mood != null){
			content_mood.setText(mood);
		}
		
		
	}
	
	public void ReturnToUserInfo(View view){  
		
		change_mood();
//		MainActivity.getMainActivityCallBack().initCallBack();
        
        //返回用户信息界面

//		this.finish();
	}
	public void change_mood(){
		mood = content_mood.getText().toString().trim();
		if (mood.equals("")){
			mood = "~暂无心情~";
		}
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("mood", mood);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObjectRequest changeMood = new JsonObjectRequest(Method.POST,changeMood_url,jObject,null,new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				String status = null;
				try {
					status = response.getString("status");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(status.equals("success")){
					User user = application.getLoginUser();
					user.setMood(mood);
					application.setLoginUser(user);
					onBackPressed();
				}
			}
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		mQueue.add(changeMood);
	}
}
