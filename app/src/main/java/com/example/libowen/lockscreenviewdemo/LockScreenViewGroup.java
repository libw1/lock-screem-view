package com.example.libowen.lockscreenviewdemo;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libowen on 18-5-17.
 */

public class LockScreenViewGroup extends RelativeLayout {

    private int itemCount;
    private int mSmallRadius;
    private int mBigRadius;
    private int mNormalColor;
    private int mRightColor;
    private int mWrongColor;
    private int mChooseColor;
    private Paint mPaint;
    private int mStartX = -1;
    private int mStartY = -1;

    private int mX;
    private int mY;
    private LockScreenView[] lockScreenViews;
    private List<Integer> mCountViews;
    private Path mPath;
    private static final String TAG = "LockScreenViewGroup";
    private int[] answer = {1,2,3,4,5,6};
    private Vibrator mVibrator;

    public LockScreenViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LockScreenViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("CustomViewStyleable") TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.lock_screen);
        this.itemCount = array.getInt(R.styleable.lock_screen_itemCount,3);
        this.mSmallRadius = (int) array.getDimension(R.styleable.lock_screen_mSmallRadius,20);
        this.mBigRadius = (int) array.getDimension(R.styleable.lock_screen_mBigRadius,40);
        this.mNormalColor = array.getColor(R.styleable.lock_screen_mNormalColor,0x666666);
        this.mRightColor = array.getColor(R.styleable.lock_screen_mRightColor,0x00ff00);
        this.mWrongColor = array.getColor(R.styleable.lock_screen_mWrongColor,0x0000ff);
        this.mChooseColor = array.getColor(R.styleable.lock_screen_mChooseColor,0x000000);
        array.recycle();

        mCountViews = new ArrayList<>();
        mPath = new Path();

        mPaint = new Paint();
        // 画path时，paint要设置为stroke模式，path会化成一个填充区域
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setColor(mNormalColor);
        mPaint.setAlpha(5);

        mVibrator = (Vibrator) getContext().getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width,width);
        if (lockScreenViews == null){
            lockScreenViews = new LockScreenView[itemCount * itemCount];
            for (int i = 0; i < itemCount * itemCount; i++){
                lockScreenViews[i] = new LockScreenView(getContext(),mSmallRadius,mNormalColor,mBigRadius,mRightColor,mWrongColor,mChooseColor);
                lockScreenViews[i].setId(i + 1);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);

                int marginWidth = (getMeasuredWidth() - mBigRadius * 2 * itemCount) / (itemCount + 1);

                if (i >= itemCount){
                    params.addRule(BELOW,lockScreenViews[i - itemCount].getId());
                }

                if (i % itemCount > 0){
                    params.addRule(RIGHT_OF, lockScreenViews[i - 1].getId());
                }

                int top = marginWidth;
                int bottom = 0;
                int left = marginWidth;
                int right = 0;
                params.setMargins(left,top,right,bottom);
                lockScreenViews[i].setState(LockScreenView.State.STATE_NORMAL);
                addView(lockScreenViews[i],params);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                resetView();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(mChooseColor);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                LockScreenView view = findLockScreen(x,y);
                Log.d(TAG, "onTouchEvent: " + x + " " + y);
                if (view != null) {
                    int id = view.getId();
                    if (!mCountViews.contains(id)) {
                        mCountViews.add(id);
                        view.setState(LockScreenView.State.STATE_CHOOSE);
                        mStartX = (view.getLeft() + view.getRight()) / 2;
                        mStartY = (view.getTop() + view.getBottom()) / 2;

                        // path中线段的添加
                        if (mCountViews.size() == 1) {
                            mPath.moveTo(mStartX, mStartY);
                        } else {
                            mPath.lineTo(mStartX, mStartY);
                        }
                        mVibrator.vibrate(50);
                    }
                }
                mX = x;
                mY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (checkAnswer()){
                    setState(LockScreenView.State.STATE_RIGHT);
                    mPaint.setColor(mRightColor);
                }else {
                    setState(LockScreenView.State.STATE_WRONG);
                    mPaint.setColor(mWrongColor);
                }
                mStartX = -1;
                mStartY = -1;
                mVibrator.cancel();
                break;
        }
        invalidate();
        return true;
    }

    private void setState(LockScreenView.State state) {
        for (int i = 0; i < mCountViews.size(); i++){
            lockScreenViews[mCountViews.get(i) - 1].setState(state);
        }
    }

    private boolean checkAnswer() {
        if (mCountViews.size() != answer.length){
            return false;
        }
        for (int i = 0; i < mCountViews.size(); i++){
            if (mCountViews.get(i) != answer[i]){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制点之间的线
        if (!mPath.isEmpty()) {
            canvas.drawPath(mPath, mPaint);
        }

        //绘制点与手指的连线
        if (mStartX != -1) {
            canvas.drawLine(mStartX, mStartY, mX, mY, mPaint);
        }
    }

    //检查当前手指点的点
    private LockScreenView findLockScreen(int x, int y) {
        for (int i = 0; i < itemCount * itemCount; i++){
            if (isChooseView(x,y,lockScreenViews[i])){
                return lockScreenViews[i];
            }
        }
        return null;
    }

    private boolean isChooseView(int x, int y, LockScreenView lockScreenView) {
        if (x > lockScreenView.getLeft() - 5 && x < lockScreenView.getRight() + 5
                && y > lockScreenView.getTop() - 5 && y < lockScreenView.getBottom() + 5){
            return true;
        }
        return false;
    }

    private void resetView() {
        if (!mCountViews.isEmpty()){
            mCountViews.clear();
        }
        if (!mPath.isEmpty()){
            mPath.reset();
        }
        for (int i = 0; i < itemCount * itemCount; i++){
            lockScreenViews[i].setState(LockScreenView.State.STATE_NORMAL);
        }
        mStartX = -1;
        mStartY = -1;
    }

    public void setAnswer(int[] answer){
        this.answer = answer;
    }
}
