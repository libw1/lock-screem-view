package com.example.libowen.lockscreenviewdemo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by libowen on 18-5-17.
 */

public class LockScreenView extends View {

    private int mSmallRadius;
    private int mBigRadius;
    private int mNormalColor;
    private int mRightColor;
    private int mWrongColor;
    private int mChooseColor;
    private State state = State.STATE_NORMAL;
    private Paint mPaint;
    private boolean isChoose = false;

    public enum State{
        STATE_NORMAL,STATE_CHOOSE,STATE_RIGHT,STATE_WRONG
    }
    public LockScreenView(Context context, int mSmallRadius, int mNormalColor,int mBigRadius, int mRightColor,int mWrongColor, int mChooseColor) {
        super(context);
        this.mSmallRadius = mSmallRadius;
        this.mBigRadius = mBigRadius;
        this.mRightColor = mRightColor;
        this.mWrongColor = mWrongColor;
        this.mChooseColor = mChooseColor;
        this.mNormalColor = mNormalColor;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setState(State state){
        this.state = state;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST){
            widthSize = mBigRadius * 2;
        }
        if (heightMode == MeasureSpec.AT_MOST){
            heightSize = mBigRadius * 2;
        }
        setMeasuredDimension(widthSize,heightSize);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (state){
            case STATE_NORMAL:
                mPaint.setColor(mNormalColor);
                mPaint.setAlpha(255);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2,mSmallRadius,mPaint);
                if (isChoose){
                    zoomIn();
                }
                isChoose = false;
                break;
            case STATE_CHOOSE:
                mPaint.setColor(mChooseColor);
                mPaint.setAlpha(255);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2,mSmallRadius,mPaint);
                mPaint.setColor(mChooseColor);
                mPaint.setAlpha(50);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2,mBigRadius,mPaint);
                if (!isChoose){
                    zoomUp();
                }
                isChoose = true;
                break;
            case STATE_RIGHT:
                mPaint.setColor(mRightColor);
                mPaint.setAlpha(255);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2,mSmallRadius,mPaint);
                mPaint.setColor(mRightColor);
                mPaint.setAlpha(50);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2,mBigRadius,mPaint);
                break;
            case STATE_WRONG:
                mPaint.setColor(mWrongColor);
                mPaint.setAlpha(255);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2,mSmallRadius,mPaint);
                mPaint.setColor(mWrongColor);
                mPaint.setAlpha(50);
                canvas.drawCircle(getWidth() / 2,getHeight() / 2,mBigRadius,mPaint);
                break;
        }
    }

    private void zoomUp() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1, 1.2f);
        animatorX.setDuration(50);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1, 1.2f);
        animatorY.setDuration(50);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        set.start();
    }

    private void zoomIn() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this, "scaleX", 1, 1f);
        animatorX.setDuration(0);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this, "scaleY", 1, 1f);
        animatorY.setDuration(0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        set.start();
    }
}
