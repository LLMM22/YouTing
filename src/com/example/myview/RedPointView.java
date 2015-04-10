package com.example.myview;

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
 * Function: 未读消息控件，可以自由设置大小�?�颜色�?�位�?
 * @author qizhenghao
 *
 */
public class RedPointView extends TextView {
	//设置默认的对齐排列方�?
	private static final int DEFAULT_MARGIN_DIP = 3;
	private static final int DEFAULT_PADDING_DIP = 3;
	private int pointMargin;
	private int paddingPixels;
	
	//用于保存背景�?
	private ShapeDrawable pointBg;

	// 显示未读条数
	private int content = 0;
	// 背景颜色
	private int colorBg = Color.RED;
	// 内容颜色
	private int colorContent = Color.WHITE;
	// 显示左右位置
	private int left_right = Gravity.RIGHT;
	// 显示上下位置
	private int top_bottom = Gravity.TOP;
	// 显示大小
	private int sizeContent = 15;
	// 背景大小
	private int sizeBg = (int) (sizeContent * 1.5);
	// 是否显示
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
	 * Fuction: 设置未读条数
	 * 
	 * @param content
	 *            ，默认为 0
	 * @author qizhenghao
	 */
	public void setContent(int content) {
		this.content = content;
		setText(content + "");
	}

	/**
	 * Fuction: 设置内容字体颜色
	 * 
	 * @param colorContent
	 *            �? 默认�? Color.WHITE
	 * @author qizhenghao
	 */

	public void setColorContent(int colorContent) {
		this.colorContent = colorContent;
		setTextColor(colorContent);
	}

	/**
	 * Fuction: 设置背景颜色
	 * 
	 * @param colorBg
	 *            ，默认为 Color.RED
	 * @author qizhenghao
	 */

	public void setColorBg(int colorBg) {
		this.colorBg = colorBg;
		pointBg = getDefaultBackground();
		setBackgroundDrawable(pointBg);
	}

	/**
	 * Fuction: 设置显示位置
	 * 
	 * @param left_right
	 *            ，默认为 Gravity.RIGHT
	 * @param top_bottom
	 *            ，默认为 Gravity.TOP
	 * @author qizhenghao
	 */

	public void setPosition(int left_right, int top_bottom) {
		this.left_right = left_right;
		this.top_bottom = top_bottom;
		setPositionParams(left_right, top_bottom);
	}

	/**
	 * Fuction: 设置内容字体大小
	 * 
	 * @param sizeContent
	 *            ，默认为 15，单位默认为 sp，背景随之扩�?
	 * @author qizhenghao
	 */

	public void setSizeContent(int sizeContent) {
		this.sizeContent = sizeContent;
		setTextSize(sizeContent);
		this.sizeBg = (int) (sizeContent * 1.5);
	}

	/**
	 * Function: 显示小红�?
	 * 
	 * @author qizhenghao
	 */
	public void show() {
		this.setVisibility(View.VISIBLE);
		isShown = true;
	}

	/**
	 * Function: 隐藏小红�?
	 * 
	 * @author qizhenghao
	 */
	public void hide() {
		this.setVisibility(View.GONE);
		isShown = false;
	}

	// 画一个背�?
	private ShapeDrawable getDefaultBackground() {
		int r = sizeBg;
		float[] outerR = new float[] { r, r, r, r, r, r, r, r };
		RoundRectShape rectShape = new RoundRectShape(outerR, null, null);
		ShapeDrawable shap = new ShapeDrawable(rectShape);
		shap.getPaint().setColor(colorBg);

		return shap;
	}

	// 设置显示位置参数
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
	 * 初始�?
	 */
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

	// 将target从父view中去掉，取�?�代之为�?个包含target和point的framLayout
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
