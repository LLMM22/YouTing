package com.example.testvolley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageRequest;
import com.example.request.JsonObjectRequest;
import com.example.request.StringRequest;
import com.example.testvolley.MainActivity;
import com.example.myview.Tools;
import com.example.myview.Tools.*;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import de.greenrobot.daoexample.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserInfoSettingActivity extends Activity {
	private ListView lv_userinfo;
	private Button iv_return;
	private ImageView faceImage;
	private ProgressDialog mDialog;
	private MyApplication application;
	private SharedPreferences sp;
	private RequestQueue mQueue;
	private String token,key;
	private User user;
	private Drawable drawable;
	private final String TAG = "UserInfoSettingActivity";
	private final String token_url = "http://121.42.164.7/index.php/Home/index/getToken";
	private final String changeAvatar_url = "http://121.42.164.7/index.php/Home/index/change_avatar";
	private final String qiniu_url = "http://7xi2lw.com1.z0.glb.clouddn.com/";
	private String[] items = new String[] { "选择本地图片", "拍照" };
	Map<String,String> uploadParams = new HashMap<String,String>();
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_setting);
//		initViews();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v(TAG,"onresume");
		initViews();
	}

	private void initViews() {
		application =(MyApplication) this.getApplicationContext();
		mQueue = application.getRequestQueue();
		sp = getSharedPreferences("youting",MODE_PRIVATE);		
		user = application.getLoginUser();
		String temp_name="账户名称 "+user.getName();
		String temp_mood="个性签名 "+user.getMood(); 
		String mood = user.getMood();
		String avatar = user.getAvatar();
		iv_return = (Button) findViewById(R.id.my_userinfo_return);
		faceImage = (ImageView) findViewById(R.id.iv_face);
		if(mood != "null"){
			temp_mood = "个性签名 "+"~暂无心情~";
		}
		if(avatar.equals("null")){
			ImageRequest avatarRequest = new ImageRequest(avatar,new Response.Listener<Bitmap>() {

				@Override
				public void onResponse(Bitmap response) {
					// TODO Auto-generated method stub
					faceImage.setImageBitmap(response);
				}
			},300,200,Config.ARGB_8888,new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					
				}
			});
			mQueue.add(avatarRequest);
		}


		lv_userinfo = (ListView) findViewById(R.id.lv_userinfo);
		lv_userinfo.setAdapter(new ArrayAdapter<String>(UserInfoSettingActivity.this,
				R.layout.userinfo_basic, new String[] { temp_name, temp_mood,"设置头像"}));
		
		//List不同item的点击响应函数
		lv_userinfo.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				switch(position){
				case 0:
					break;
				case 1:
	                Intent intent = new Intent();
	                intent.setClass(UserInfoSettingActivity.this, MyMoodActivity.class);
	                startActivity(intent);
//	                finish();
	                break;
				case 2:
					showDialog();
				}
			}
		});
		
		iv_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.getMainActivityCallBack().setUserInfo();
                finish();
                
			}
		});
	}
	
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(UserInfoSettingActivity.this, "未找到存储卡，无法存储照片！",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					//
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			drawable = new BitmapDrawable(photo);
			byte[] img = Bitmap2Bytes(photo);
			
			mDialog = new ProgressDialog(UserInfoSettingActivity.this);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("正在上传图片");
            mDialog.show();
			uploadPic(img);
			//成功上传后再设置
			Log.v(TAG,"succ set");

			/** 保存到SD卡 */
//			  File f = new File("/sdcard/DCIM/", "zzzzz");
//			  if (f.exists()) {
//			   f.delete();
//			  }
//			  try {
//			   FileOutputStream out = new FileOutputStream(f);
//			   photo.compress(Bitmap.CompressFormat.PNG, 90, out);
//			   out.flush();
//			   out.close();
//			  } catch (FileNotFoundException e) {
//			   e.printStackTrace();
//			  } catch (IOException e) {
//			   e.printStackTrace();
//			  }
		}
	}
	//上传图片到云端
	public void uploadPic(byte[] i){
		final byte[] img = i;
		key = "avatar-"+user.getUid()+"v0.png";
		if(user.getAvatar() != "null"){
			int start = user.getAvatar().lastIndexOf("v")+1;
			int end = user.getAvatar().lastIndexOf(".");
			int version = Integer.parseInt(user.getAvatar().substring(start, end))+1;
			Log.v(TAG,"version:"+version);
			key = "avatar-"+user.getUid()+"v"+version+".png";
		}
//		uploadParams.put("x:scope", "youting:"+key);
//		uploadParams.put("x:deadline", ""+3600);
//		uploadParams.put("x:mimeLimit", "image/jpeg");
		
		Log.v(TAG,uploadParams.toString());
		//获取token
		StringRequest tokenRequest = new StringRequest(Method.POST,token_url,null,new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				token = response;
				Log.v(TAG,token);
				//upload
				UploadManager uploadManager = new UploadManager();
				uploadManager.put(img, key, token,
				new UpCompletionHandler() {
					@Override
					public void complete(String arg0, ResponseInfo arg1,
							JSONObject arg2) {
						// 更新数据库
						Log.v(TAG,"upload succ"+arg0+"response:"+arg1);
						String avatar = qiniu_url+key;
						user.setAvatar(avatar);
						application.setLoginUser(user);
						changeAvatar();
						}					
				}, new UploadOptions(null, null, false,
				        new UpProgressHandler(){
		            public void progress(String key, double percent){
		            	int progress = (int)percent*100;
		            	mDialog.setProgress(progress);
		                Log.i("qiniu", key + ": " + percent);
		            }
		        }, null));
			}
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.v(TAG,error.toString());
			}
		});
		mQueue.add(tokenRequest);
	}
	public void changeAvatar(){
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("avatar", qiniu_url+key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObjectRequest changeAvatar = new JsonObjectRequest(Method.POST,changeAvatar_url,jObject,null,new Response.Listener<JSONObject>() {

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
					mDialog.dismiss();
					
					faceImage.setImageDrawable(drawable);
					Toast.makeText(getApplicationContext(), "修改头像成功", Toast.LENGTH_SHORT).show();
				}
			}
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		mQueue.add(changeAvatar);
	}
	private byte[] Bitmap2Bytes(Bitmap bm){  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();   
	    bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
	    return baos.toByteArray();  
	}  

}


