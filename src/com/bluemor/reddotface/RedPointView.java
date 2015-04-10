package com.bluemor.reddotface;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Function: 鏈娑堟伅鎺т欢锛屽彲浠ヨ嚜鐢辫缃ぇ灏忋�棰滆壊銆佷綅缃� * @author qizhenghao
 *
 */
public class RedPointView extends TextView {
	//璁剧疆榛樿鐨勫榻愭帓鍒楁柟寮�	
	private static final int DEFAULT_MARGIN_DIP = 3;
	private static final int DEFAULT_PADDING_DIP = 3;
	private int pointMargin;
	private int paddingPixels;
	
	//鐢ㄤ簬淇濆瓨鑳屾櫙鍥�	
	private ShapeDrawable pointBg;

	// 鏄剧ず鏈鏉℃暟
	private int content = 0;
	// 鑳屾櫙棰滆壊
	private int colorBg = Color.RED;
	// 鍐呭棰滆壊
	private int colorContent = Color.WHITE;
	// 鏄剧ず宸﹀彸浣嶇疆
	private int left_right = Gravity.RIGHT;
	// 鏄剧ず涓婁笅浣嶇疆
	private int top_bottom = Gravity.TOP;
	// 鏄剧ず澶у皬
	private int sizeContent = 15;
	// 鑳屾櫙澶у皬
	private int sizeBg = (int) (sizeContent * 1.5);
	// 鏄惁鏄剧ず
	private boolean isShown;

	private Context context;
	private View orginView;

	public RedPointView(Context context, View target) {
		super(context);
		this.context = context; 
		this.orginView = target;
		init();
	}

	/**
	 * Fuction: 璁剧疆鏈鏉℃暟
	 * 
	 * @param content
	 *            锛岄粯璁や负 0
	 * @author qizhenghao
	 */
	public void setContent(int content) {
		this.content = content;
		setText(content + "");
	}

	/**
	 * Fuction: 璁剧疆鍐呭瀛椾綋棰滆壊
	 * 
	 * @param colorContent
	 *            锛�榛樿涓�Color.WHITE
	 * @author qizhenghao
	 */

	public void setColorContent(int colorContent) {
		this.colorContent = colorContent;
		setTextColor(colorContent);
	}

	/**
	 * Fuction: 璁剧疆鑳屾櫙棰滆壊
	 * 
	 * @param colorBg
	 *            锛岄粯璁や负 Color.RED
	 * @author qizhenghao
	 */

	public void setColorBg(int colorBg) {
		this.colorBg = colorBg;
		pointBg = getDefaultBackground();
		setBackgroundDrawable(pointBg);
	}

	/**
	 * Fuction: 璁剧疆鏄剧ず浣嶇疆
	 * 
	 * @param left_right
	 *            锛岄粯璁や负 Gravity.RIGHT
	 * @param top_bottom
	 *            锛岄粯璁や负 Gravity.TOP
	 * @author qizhenghao
	 */

	public void setPosition(int left_right, int top_bottom) {
		this.left_right = left_right;
		this.top_bottom = top_bottom;
		setPositionParams(left_right, top_bottom);
	}

	/**
	 * Fuction: 璁剧疆鍐呭瀛椾綋澶у皬
	 * 
	 * @param sizeContent
	 *            锛岄粯璁や负 15锛屽崟浣嶉粯璁や负 sp锛岃儗鏅殢涔嬫墿鍏�	 * @author qizhenghao
	 */

	public void setSizeContent(int sizeContent) {
		this.sizeContent = sizeContent;
		setTextSize(sizeContent);
		this.sizeBg = (int) (sizeContent * 1.5);
	}

	/**
	 * Function: 鏄剧ず灏忕孩鐐�	 * 
	 * @author qizhenghao
	 */
	public void show() {
		this.setVisibility(View.VISIBLE);
		isShown = true;
	}

	/**
	 * Function: 闅愯棌灏忕孩鐐�	 * 
	 * @author qizhenghao
	 */
	public void hide() {
		this.setVisibility(View.GONE);
		isShown = false;
	}

	// 鐢讳竴涓儗鏅�	
	private ShapeDrawable getDefaultBackground() {
		int r = sizeBg;
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
		ShapeDrawable shap = new ShapeDrawable(rectShape);
		shap.getPaint().setColor(colorBg);

		return shap;
	}

	// 璁剧疆鏄剧ず浣嶇疆鍙傛暟
	private void setPositionParams(int left_right, int top_bottom) {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = left_right | top_bottom;

		switch (left_right) {
		case Gravity.LEFT:
			switch (top_bottom) {
			case Gravity.TOP:
				params.setMargins(pointMargin, pointMargin, 0, 0);
				break;
			case Gravity.BOTTOM:
				params.setMargins(pointMargin, 0, 0, pointMargin);
			default:
				break;
			}
		case Gravity.RIGHT:
			switch (top_bottom) {
			case Gravity.TOP:
				params.setMargins(0, pointMargin, pointMargin, 0);
				break;
			case Gravity.BOTTOM:
				params.setMargins(0, 0, pointMargin, pointMargin);
			default:
				break;
			}
			break;
		default:
			break;
		}

		setLayoutParams(params);
	}

	/*
	 * 鍒濆鍖�	 */
	private void init() {
		pointMargin = dipToPixels(DEFAULT_MARGIN_DIP);

		setTypeface(Typeface.DEFAULT_BOLD);
		paddingPixels = dipToPixels(DEFAULT_PADDING_DIP);
		setPadding(paddingPixels, 0, paddingPixels, 0);

		setContent(content);
		setColorContent(colorContent);
		setSizeContent(sizeContent);
		setPosition(left_right, top_bottom);
		setColorBg(colorBg);

		isShown = false;

		if (this.orginView != null) {
			restartDraw(this.orginView);
		}
	}

	// 灏唗arget浠庣埗view涓幓鎺夛紝鍙栬�浠ｄ箣涓轰竴涓寘鍚玹arget鍜宲oint鐨刦ramLayout
	private void restartDraw(View target) {
		LayoutParams lp = target.getLayoutParams();
		ViewParent parent = target.getParent();
		FrameLayout framLayout = new FrameLayout(context);

		ViewGroup viewGroup = (ViewGroup) parent;
		int index = viewGroup.indexOfChild(target);

		viewGroup.removeView(target);
		viewGroup.addView(framLayout, index, lp);
		framLayout.addView(target);
		framLayout.addView(this);

		viewGroup.invalidate();
	}

	private int dipToPixels(int dip) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				r.getDisplayMetrics());
		return (int) px;
	}

}
