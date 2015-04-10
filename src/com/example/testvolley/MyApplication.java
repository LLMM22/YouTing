package com.example.testvolley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.receiver.PlayerReceiver;
import com.example.service.*;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoMaster.OpenHelper;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.User;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.util.Log;

public class MyApplication extends Application {
	
		private static final String SET_COOKIE_KEY = "Set-Cookie";
		private static final String COOKIE_KEY = "Cookie";
		private static final String SESSION_COOKIE = "PHPSESSID";
	    private static final String TAG = "application";
	    private static MyApplication mInstance;
	    private static DaoMaster daoMaster;
	    private static DaoSession daoSession;
	    private RequestQueue mQueue;
	    private RemoteControlClient myRemoteControlClient;
		private SharedPreferences preferences;
		private SharedPreferences.Editor editor;
	    private ArrayList<Music> myMusicList = new ArrayList<Music>();
	    private ArrayList<Music> friendMusicList = new ArrayList<Music>();
	    private ArrayList<Music> localMusicList = new ArrayList<Music>();
	    private ArrayList<User> friendList = new ArrayList<User>();
	    private Map<String,String> header = new HashMap<String,String>();
	    private User loginUser;
	    private boolean isLogin = false;
	    private boolean hasMessage = false;
	    private NotificationManager notificationManager;
		private Notification notification;
		private PlayerReceiver receiver;
		private PlayerService  service;
		
	    @Override
	    public void onCreate() {    	     
	    	 Log.d(TAG, "[ExampleApplication] onCreate");
	         super.onCreate();
	         if(mInstance == null){
	        	 mInstance = this;
	         }
	         preferences = getSharedPreferences("youting",MODE_PRIVATE);	
	         mQueue = Volley.newRequestQueue(getApplicationContext());
	         initList();
	         Log.v(TAG,"mqueue");
	         JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
	         JPushInterface.init(this);     		// 初始化 JPush
	         // 锁屏测试！！！
	         ComponentName myEventReceiver = new ComponentName(getPackageName(), MediaButtonReceiver.class.getName());
	         // MyRemoteControlEventReceiver是一个接收系统控件请求的BoardCastReceiver，需要在manifest中进行注册
	              /* <receiver android:name=".receiver.MediaButtonIntentReceiver">
	                 <intent-filter >
	                    <action android:name="android.intent.action.MEDIA_BUTTON" />
	                 </intent-filter>
	                 </receiver>*/
	        AudioManager myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	         // 需要向AudioManager注册
	        myAudioManager.registerMediaButtonEventReceiver(myEventReceiver);
	        // build the PendingIntent for the remote control client
	        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
	        mediaButtonIntent.setComponent(myEventReceiver);
	        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
	        // create and register the remote control client
	        myRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
	         // 在AudioManager中注册RemoteControlClient
	        myAudioManager.registerRemoteControlClient(myRemoteControlClient);
//	        myRemoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);

	        int flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
	                | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
	                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY
	                | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
	                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE;
	        myRemoteControlClient.setTransportControlFlags(flags);
	        myAudioManager.requestAudioFocus(new OnAudioFocusChangeListener() {  
	            
	            @Override  
	            public void onAudioFocusChange(int focusChange) {  
	                System.out.println("focusChange = " + focusChange);  
	            }  
	        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);  
////	        myRemoteControlClient.setOnGetPlaybackPositionListener(new OnGetPlaybackPositionListener() {
//	            @Override
//	            public long onGetPlaybackPosition() {
//	                return 5;
//	            }
//	        });
	    }
	    public static MyApplication get(){
	    	return mInstance;
	    }
	    public void initList(){
	    	
	    	friendList.clear();
	    	myMusicList.clear();
	    	friendMusicList.clear();
	    }
	    public static DaoMaster getDaoMaster(Context context){
	    	if (daoMaster == null){
	    		OpenHelper helper = new DaoMaster.DevOpenHelper(context, "my_db", null);
	    		daoMaster = new DaoMaster(helper.getWritableDatabase());
	    		
	    	}
	    	return daoMaster;
	    }
	    public static DaoSession getDaoSession(Context context) {  
	        if (daoSession == null) {  
	            if (daoMaster == null) {  
	                daoMaster = getDaoMaster(context);  
	            }  
	            daoSession = daoMaster.newSession();  
	        }  
	        return daoSession;  
	    } 
	    public RequestQueue getRequestQueue(){
	    	
	    	return mQueue;
	    }
	    public RemoteControlClient getRemoteControlClient(){
	    	return myRemoteControlClient;
	    }
	    public ArrayList<Music> getMyMusicList(){
			return this.myMusicList;
	    	
	    }
	    public void setMyMusicList(ArrayList<Music> list){
	    	this.myMusicList = list ;
	    }
	    public void addToMyMusicList(Music music){
	    	this.myMusicList.add(music);
	    }
	    public void RemoveFromMyMusicList(Music music){
	    	this.myMusicList.remove(music);
	    }
	    public void setFriendList(ArrayList<User> list){
	    	this.friendList = list;
	    }
	    public void addToFriendList(User user){
	    	this.friendList.add(user);
	    }
	    public void removeFromFriendList(User user){
	    	this.friendList.remove(user);
	    }
	    public ArrayList<User> getFriendList(){
	    	return this.friendList;
	    }
	    public void setFriendMusicList(){
	    	
	    }
	    
	    public ArrayList<Music> getFriendMusicList(){
			return null;	    	
	    }
		public ArrayList<Music>  getLocalMusicList(){
			return this.localMusicList;
		}
		public void setLocalMusicList(ArrayList<Music> list){
			this.localMusicList = list;
		}
		public User getLoginUser(){
			
			return this.loginUser;
		}
		public void setLoginUser(User user){
			this.loginUser = user;
		}
	    /**
	     * Checks the response headers for session cookie and saves it
	     * if it finds it.
	     * @param headers Response Headers.
	     */
	    public final void checkSessionCookie(Map<String, String> headers) {
	    	Log.v(TAG,"check begin");
	    	Log.v(TAG,"headers:"+headers.toString());
	        if (headers.containsKey(SET_COOKIE_KEY)
	                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
	        	Log.v(TAG,"check succ");
	                String cookie = headers.get(SET_COOKIE_KEY);
	                if (cookie.length() > 0) {
	                    String[] splitCookie = cookie.split(";");
	                    String[] splitSessionId = splitCookie[0].split("=");
	                    cookie = splitSessionId[1];
	                    editor = preferences.edit();
	                    editor.putString(SESSION_COOKIE, cookie);
	                    editor.commit();

	                }
	            }
	    }

	    /**
	     * Adds session cookie to headers if exists.
	     * @param headers
	     */
	    public final void addSessionCookie(Map<String, String> headers) {
	        String sessionId = preferences.getString(SESSION_COOKIE, "");
	        Log.v(TAG,"add sessionCookie");
	        if (sessionId.length() > 0) {
	            StringBuilder builder = new StringBuilder();
	            builder.append(SESSION_COOKIE);
	            builder.append("=");
	            builder.append(sessionId);
	            if (headers.containsKey(COOKIE_KEY)) {
	                builder.append("; ");
	                builder.append(headers.get(COOKIE_KEY));
	            }
	            headers.put(COOKIE_KEY, builder.toString());
	        }
	    }
	    
	    public Notification getNotification() {
			return notification;
		}
		public void setNotification(Notification notification) {
			this.notification = notification;
		}
		public NotificationManager getNotificationManager() {
			return notificationManager;
		}
		public void setNotificationManager(NotificationManager notificationManager) {
			this.notificationManager = notificationManager;
		}
		public PlayerReceiver getReceiver() {
			return receiver;
		}
		public void setReceiver(PlayerReceiver receiver) {
			this.receiver = receiver;
		}
		public PlayerService getService() {
			return service;
		}
		public void setService(PlayerService service) {
			this.service = service;
		}
		public boolean isLogin(){
			return this.isLogin;
		}
		public void setIsLogin(boolean b){
			this.isLogin = b;
		}
		public boolean hasMessage(){
			return hasMessage;
		}
		public void setMessage(boolean b){
			this.hasMessage = b;
		}
}
