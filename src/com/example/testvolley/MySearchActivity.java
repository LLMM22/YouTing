package com.example.testvolley;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MySearchActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_search);
		// 获取该Activity里面的TabHost组件
		TabHost tabHost = getTabHost();
		// 创建第一个Tab页
		TabSpec tab1 = tabHost.newTabSpec("tab1")
			.setIndicator("歌曲") // 设置标题
			.setContent(R.id.tab01); //设置内容
		// 添加第一个标签页
		tabHost.addTab(tab1);
		TabSpec tab2 = tabHost.newTabSpec("tab2")
			// 在标签标题上放置图标
			.setIndicator("用户")
			.setContent(R.id.tab02);
		// 添加第二个标签页
		tabHost.addTab(tab2);
	}
	
	public void ReturnToMain(View view){  
		//Intent intent=new Intent(MySearchActivity.this,MainActivity.class);
		//intent.putExtra(EXTRA_MESSAGE, "startaaa");
		//startActivity(intent);  
		finish();
	}
}

