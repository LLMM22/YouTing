package com.example.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import com.example.activity.PlayerActivity;
import com.example.cacheplayer.HttpGetProxy;
import com.example.cacheplayer.MyPlayer;
import com.example.testvolley.MainActivity;
import com.example.testvolley.MyApplication;
import com.example.receiver.PlayerReceiver;
import com.example.testvolley.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.MusicDao;
import de.greenrobot.daoexample.MusicDao.Properties;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.SeekBar;

public class PlayerService extends IntentService {
	
	private static final String TAG = "service";
		 
	private static boolean playFlag = false;
	private  boolean firstFlag = true;
	private  int index;
	private  int duration;     //歌长
	private  ArrayList<Music> playList;  //歌曲列表
	private  int listSize;  //歌单长度
	private HttpGetProxy proxy;
	private String BuffPath = "/youting";
	private String ftplogin = "user", ftppass = "123";
	private Context context;
	static private final int BUFFER_SIZE= 1024;//Mb cache dir 
	static private final int NUM_FILES= 200;//Count files in cache dir
	private RemoteControlClient remoteControlClient; 
	private MyApplication application;
	private DaoSession daoSession; 
	private MusicDao musicDao;
	private QueryBuilder qb_music;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private ArrayList<Music> myMusicList,friendMusicList,localMusicList;
	public MyPlayer myPlayer;
	private Music music_pre; 
	private Music music_next;
	private Music music;
	private int progress;
	public int mPlayMode = 1;
	MyApplication myApplication=MyApplication.get();
	public boolean mHasLyric = false;
	
	public PlayerService(String name) {
		super(name);
	}

	public PlayerService() {
		super("player!");
	}

	private final IBinder mBinder = new LocalBinder();
	
	private Handler handler = new Handler();
	
	/**
	 * 播放模式<br>
	 * 0代表单曲循环，1代表列表循环，2代表顺序播放，3代表随机播放
	 */
	public class PlayMode {
		public static final int REPEAT_SINGLE = 0;
		public static final int REPEAT = 1;
		public static final int SEQUENTIAL = 2;
		public static final int SHUFFLE = 3;
	} 
	@Override
	public void onCreate() {
		
		
		super.onCreate();
        context = this.getApplicationContext();	
		myPlayer = new MyPlayer(context);
		application = (MyApplication)this.getApplicationContext();
		daoSession = application.getDaoSession(context);
		musicDao = daoSession.getMusicDao();
		remoteControlClient = application.getRemoteControlClient();
		myMusicList = application.getMyMusicList();
//		friendMusicList = application.getFriendMusicList();
		localMusicList = application.getLocalMusicList();
		//Log.v(TAG,"playList:"+myMusicList.get(1).getName());
		preferences = getSharedPreferences("youting",MODE_PRIVATE);		
		editor = preferences.edit();
		setIndex(preferences.getInt("INDEX", 0));
		if( preferences.getInt("PLAYLIST", 0)==0){
			setPlayList(myMusicList);
		}else{
			setPlayList(friendMusicList);
		}
		listSize=playList.size();
		if(listSize !=0){
			music_pre = (this.index==0)?playList.get(listSize-1):playList.get(index-1);
		    music_next =(index==(listSize-1))?playList.get(0):playList.get(index+1); 
		    music = playList.get(index);
		}
	}

	
public void play(){	
		Log.v(TAG,"play"+firstFlag);
		myPlayer.start();
		playFlag = true;
		MainActivity.getMainActivityCallBack().setProgress();		
		if(PlayerActivity.getPlayerActivityCallBack()!=null){
			PlayerActivity.getPlayerActivityCallBack().setProgress();
			
		}
		remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);		
		if(firstFlag)  MyApplication.get().setNotificationManager(MainActivity.getMainActivityCallBack().showCustomView());
		Intent i=new Intent(this,PlayerReceiver.class);
		i.putExtra("action", "play");
		sendBroadcast(i);
		if(firstFlag==true) firstFlag=false;
		
}

public void pause() {
	// TODO Auto-generated method stub
	Log.v(TAG,"pause");
	myPlayer.pause();
	playFlag = false;
	remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
	MainActivity.getMainActivityCallBack().setProgress();	
	Intent i=new Intent(this,PlayerReceiver.class);
	i.putExtra("action", "pause");
	sendBroadcast(i);
}

public void play_pause() {
	// TODO Auto-generated method stub
	Log.v(TAG,"play_pause");
	if(playFlag){
		pause();
	}else{
		play();
	}
}


public void playPrevious() {
	// TODO Auto-generated method stub
	if(index == 0){
		index = playList.size()-1;
		
	}else{
		index--;
	}
	myPlayer.release();
	Log.v(TAG,"playPrevious success");
	Music m = playList.get(index);
	playSong(m);
	
}


public void playNext() {
	// TODO Auto-generated method stub
	if (index+1 < playList.size()){
		index++;
		
	}else{
		index=0;
	}
	myPlayer.release();
	Log.v(TAG,"playnext success"+index);
	Music m = playList.get(index);
	playSong(m);

}
   public void playItems(int index){						
 
	myPlayer.release();
	Log.v(TAG,"play items"+playList.size()+"this.index:"+this.index);
	music = playList.get(index);
	listSize = playList.size();
	music_pre = (index==0)?playList.get(listSize-1):playList.get(index-1);
    music_next =(index==(listSize-1))?playList.get(0):playList.get(index+1);
	MainActivity.getMainActivityCallBack().setPage();
	playSong(music);	
}
   
   
   public void playSong(Music m){
		Log.v(TAG,"playSong");
		if (playList.contains(m)){
			index = playList.indexOf(m);
			Log.v(TAG,"index:"+index);
		}else{
			playList.add(m);
			index = playList.indexOf(m);
		}
		String url = m.getUrl();
		setMusicInfo(m);
		listSize = playList.size();
		music = m;
		music_pre = (index==0)?playList.get(listSize-1):playList.get(index-1);
	    music_next =(index==(listSize-1))?playList.get(0):playList.get(index+1); 
	    Log.v(TAG,"pre:"+music_pre.getName()+"next:"+music_next.getName());
		proxy = new HttpGetProxy();
		proxy.setPaths(BuffPath, url, BUFFER_SIZE, NUM_FILES, context, true, ftplogin, ftppass, false, false, "5FQTRE5AIPHN7K5Z4D3HTN653FXLCPH3VDVBU5A");
		//start player 
		String proxyUrl = proxy.getLocalURL();
		myPlayer.setPath(proxyUrl);
		myPlayer.setSeekListener(new MyPlayer.SeekListener() {
			@Override
			public void onSeek(int msec) {
				if (proxy!=null) {proxy.seek=true;} //more speed for seeking
			}

			@Override
			public void onSeekComplete(MediaPlayer mp) {
				
			}
       	
       });
		myPlayer.setOnPreparedListener(new OnPreparedListener(){

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				duration = myPlayer.getDuration();
				Log.v(TAG,"duration:"+duration);
				play();
				if(PlayerActivity.getPlayerActivityCallBack()!=null){
					PlayerActivity.getPlayerActivityCallBack().refreshview();}
			}
			
		});
		myPlayer.setOnCompletionListener(new OnCompletionListener(){

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				
				playNext();
				Log.v(TAG,music.getName());
				MainActivity.getMainActivityCallBack().setPage();
			}
			
		});
	}
   
   
   
   public void setMusicInfo(Music m){
		Music music = m;
		RemoteControlClient.MetadataEditor ed = remoteControlClient.editMetadata(true);
        ed.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, m.getName());
        ed.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM,m.getArtist());
//       ed.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, m.getArtist());
       ed.apply();
	}
   
	
   @Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	/**
	 * 自定义绑定Service类，通过这里的getService得到Service，之后就可调用Service这里的方法了
	 */
	public class LocalBinder extends Binder {
		public PlayerService getService() {
			Log.d("playerService", "getService");
			return PlayerService.this;
		}
	}

	/** 改变播放模式 */
	public void changePlayMode() {
		mPlayMode = (mPlayMode + 1) % 4;
		if (myPlayer != null) {
			// 如果正在播放歌曲
			switch (mPlayMode) {
			case PlayMode.REPEAT_SINGLE:
				// 如果是单曲循环，给MediaPlayer启动单曲播放
			//	myPlayer.set(true);
				break;
			default:
				// 如果不是单曲循环，取消MediaPlayer的单曲播放
			//	myPlayer.setLooping(false);
				break;
			}
		}
		
		PlayerActivity.getPlayerActivityCallBack().onPlayModeChanged(mPlayMode);
	}
	
	//跳到指定时间点
	public void seekToSpecifiedPosition(int milliSeconds){
		
		//if (playFlag) {
			myPlayer.seekTo(milliSeconds);
		//}
			
	}
	@Override
	public void onDestroy() {

	}

	protected void onHandleIntent(Intent intent) {
		String type = intent.getStringExtra("action");
		Log.d("intent", type);
		if (type.equals("next")) {
			int index = this.index;
			if (index+1 < playList.size()){
				index++;
				
			}else{
				index=0;
			}
			music = playList.get(index);
			listSize = playList.size();
			music_pre = (index==0)?playList.get(listSize-1):playList.get(index-1);
		    music_next =(index==(listSize-1))?playList.get(0):playList.get(index+1);
			MainActivity.getMainActivityCallBack().setPage();
			Log.v(TAG,"playnextsetpage");
			playNext();
			
		} else if (type.equals("pause")) {
			Intent i=new Intent(this,PlayerReceiver.class);
			if(isPlayFlag())
				i.putExtra("action", "play");
			else
				i.putExtra("action", "pause");
			sendBroadcast(i);
			play_pause();
		} 

	}
	

	/**
	 * 读取歌词文件
	 * @param path歌曲文件的路径            
	 */
	public void loadLyric(Music myMusic) {
		// 取得歌曲同目录下的歌词文件绝对路径
		File lyricfile = null;
		String lyricLocalPath = myMusic.getLrc_cache_url();

		if (lyricLocalPath!=null) {
			// 本地有歌词，直接读取
			Log.i(TAG, "loadLyric()--->本地有歌词，直接读取");
			 mHasLyric=true;
			 PlayerActivity.getPlayerActivityCallBack().showLrc();
		} else if(myMusic.getLrc_url().equals("null")){
			// 设置歌词为空
			mHasLyric = false;
			Log.i(TAG, "loadLyric()--->都木有歌词");
			PlayerActivity.getPlayerActivityCallBack().showLrc();					
			} else {	
				// 尝试网络获取歌词
					Log.i(TAG, "loadLyric()--->本地无歌词，尝试从网络获取");
					 getLrcFormNet(myMusic);
			}
		
		
		}
	
	
	//从网络上获得LRC
		public void getLrcFormNet(final Music myMusic){
			
			if (myMusic.getLrc_url().equals(null))   return;
			String lyricFilePath=null;
	
			AsyncHttpClient client = new AsyncHttpClient();		
			client.get(myMusic.getLrc_url(), new AsyncHttpResponseHandler() {

			    @Override
			    public void onStart() {
			        // called before request is started
			    }

			    @Override
			    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
			        // called when response HTTP status is "200 OK"
			    	 try {
						FileOutputStream fout = new FileOutputStream("sdcard/youting/"+myMusic.name+".lrc");
						String lyricFilePath="sdcard/youting/"+myMusic.name+".lrc";
						myMusic.setLrc_cache_url(lyricFilePath);
						Long uid = myMusic.getUid();
						//更改数据库
//						qb_music.where(Properties.Uid.eq(uid));
						musicDao.update(myMusic);
						fout.write(response);  
						fout.close();  
						mHasLyric=true;
					    PlayerActivity.getPlayerActivityCallBack().showLrc();		  
					    Log.i(TAG, "loadLyric()--->从网络获取成功");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	 
			    }

			    @Override
			    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
			        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
			    	
			    }

			    @Override
			    public void onRetry(int retryNo) {
			        // called when request is retried
				}
			
			});

		
		}
	
		
	public static boolean isPlayFlag() {
		return playFlag;
	}

	public static void setPlayFlag(boolean playFlag) {
		PlayerService.playFlag = playFlag;
	}

	
	public boolean isFirstFlag() {
		return firstFlag;
	}

	public void setFirstFlag(boolean firstFlag) {
		this.firstFlag = firstFlag;
	}

	public int getListSize() {
		return listSize;
	}

	public void setListSize(int listSize) {
		this.listSize = listSize;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public ArrayList<Music> getPlayList() {
		return playList;
	}

	public void setPlayList(ArrayList<Music> playList) {
		this.playList = playList;
	}

	public Music getMusic_pre() {
		return music_pre;
	}

	public void setMusic_pre(Music music_pre) {
		this.music_pre = music_pre;
	}

	public Music getMusic_next() {
		return music_next;
	}

	public void setMusic_next(Music music_next) {
		this.music_next = music_next;
	}

	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}
	public int getProgress(){
		return myPlayer.getCurrentPosition()*100/myPlayer.getDuration();
	}
	
	
}

