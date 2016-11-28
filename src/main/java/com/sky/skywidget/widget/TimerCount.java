package com.sky.skywidget.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sky.skywidget.R;


public class TimerCount extends View {
    /* 默认圆形背景颜色 */
    private static int DEFAULT_CIRCLE_COLOR = 0x20162A3B;
    /* 最外层进度条宽度 */
    private static int DEFAULT_CIRCLE_STROKE_WIDTH = 60;
    /* 默认半径 */
    private static int DEFAULT_CIRCLE_RADIUS = 300;
    /* 默认进度颜色 */
    private static int PROGRESS_BAR_COLOR = Color.BLACK;
    /* 默认进度图标宽度 */
    private static int PROGRESS_WIDTH = 84;
    /* 默认倒计时时间字体颜色 */
    private static int textColor = Color.BLACK;
    /* 默认字体大小px */
    private static float textSize = 100;
    /* 计时文字刷新周期 */
    private static int REFRESH_TIME_CYCLE = 1000;

    private Paint defaultCirclePaint;
    private Paint whiteLineCirclePaint;
    private Paint progressPaint;
    private Paint smallCirclePaint;
    private Paint smallCircleSolidPaint;
    private Paint textPaint;
    private Paint backgroundPaint;
    private Paint panelPaint;

    private float currentAngle;
    private int countdownTime = 0;


    Bitmap cursor;
    Bitmap panel;
    /* bitmap抗锯齿 */
    PaintFlagsDrawFilter mSetfil;

    private static int ARROW_ICON = R.drawable.timer_count_arrow;
    private static final int PANEL_ICON = R.drawable.timer_count_clock_bac;

    private static int START_COLOR = 0x50000000;
    private static int END_COLOR = 0xFF000000;


    public TimerCount(Context context) {
        super(context);
        setPaint();
    }

    public TimerCount(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttrs(context, attrs);
        setPaint();
    }

    public TimerCount(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        readAttrs(context, attrs);
        setPaint();
    }


    private void readAttrs(Context context, AttributeSet attrs) {
        TypedArray types = context.obtainStyledAttributes(attrs,
                R.styleable.TimerCount);
        for (int i = 0; i < types.getIndexCount(); i++) {
            switch (types.getIndex(i)) {
                case R.styleable.TimerCount_trackballImage:
                    ARROW_ICON = types.getResourceId(R.styleable.TimerCount_trackballImage, ARROW_ICON);
                    break;
                case R.styleable.TimerCount_trackballRadius:
                    PROGRESS_WIDTH = types.getDimensionPixelOffset(R.styleable.TimerCount_trackballRadius, PROGRESS_WIDTH);
                    break;
                case R.styleable.TimerCount_trackStartColor:
                    START_COLOR = types.getColor(R.styleable.TimerCount_trackStartColor, START_COLOR);
                    break;
                case R.styleable.TimerCount_trackEndColor:
                    END_COLOR = types.getColor(R.styleable.TimerCount_trackEndColor, END_COLOR);
                    break;
                case R.styleable.TimerCount_trackWidth:
                    DEFAULT_CIRCLE_STROKE_WIDTH = types.getDimensionPixelOffset(R.styleable.TimerCount_trackWidth, DEFAULT_CIRCLE_STROKE_WIDTH);
                    break;
            }

        }
        types.recycle();
    }

    private void setPaint() {
        //默认圆
        defaultCirclePaint = new Paint();
        defaultCirclePaint.setAntiAlias(true);//抗锯齿
        defaultCirclePaint.setDither(true);//防抖动
        defaultCirclePaint.setStyle(Paint.Style.STROKE);
        defaultCirclePaint.setStrokeWidth(DEFAULT_CIRCLE_STROKE_WIDTH);
        defaultCirclePaint.setColor(DEFAULT_CIRCLE_COLOR);//这里先画边框的颜色，后续再添加画笔画实心的颜色
        // 默认圆线
        whiteLineCirclePaint = new Paint();
        whiteLineCirclePaint.setAntiAlias(true);
        whiteLineCirclePaint.setDither(true);//防抖动
        whiteLineCirclePaint.setStyle(Paint.Style.STROKE);
        whiteLineCirclePaint.setStrokeWidth(2);
        whiteLineCirclePaint.setColor(0xFF4BE6A4);
        //默认圆上面的进度弧度
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setDither(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(DEFAULT_CIRCLE_STROKE_WIDTH);
        progressPaint.setColor(PROGRESS_BAR_COLOR);
        progressPaint.setAlpha(255);
        progressPaint.setStrokeCap(Paint.Cap.BUTT);//设置画笔笔刷样式
        // 进度上面的小圆
        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setDither(true);
        smallCirclePaint.setStyle(Paint.Style.STROKE);
        // 画进度上面的小圆的实心画笔（主要是将小圆的实心颜色设置成白、色）
        smallCircleSolidPaint = new Paint();
        smallCircleSolidPaint.setAntiAlias(true);
        smallCircleSolidPaint.setDither(true);
        smallCircleSolidPaint.setFilterBitmap(true);
        smallCircleSolidPaint.setStyle(Paint.Style.FILL);
        // 背景白色的圆圈
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setDither(true);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(Color.WHITE);
        // 时钟刻度样子的圈圈
        panelPaint = new Paint();
        panelPaint.setAntiAlias(true);
        panelPaint.setDither(true);
        panelPaint.setStyle(Paint.Style.STROKE);
        // 中央倒计时数字
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        if (cursor == null) {
            cursor = zoomImage(BitmapFactory.decodeResource(this.getContext().getResources(), ARROW_ICON), PROGRESS_WIDTH, PROGRESS_WIDTH);
        }
        panel = zoomImage(BitmapFactory.decodeResource(this.getContext().getResources(), PANEL_ICON), DEFAULT_CIRCLE_RADIUS * 2 - DEFAULT_CIRCLE_STROKE_WIDTH / 2 - 50, DEFAULT_CIRCLE_RADIUS * 2 - DEFAULT_CIRCLE_STROKE_WIDTH / 2 - 50);
        mSetfil = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        //画默认白圆
        canvas.drawCircle(DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS, whiteLineCirclePaint);
        // 画透明黑色圆圈与安全
        canvas.drawCircle(DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS, defaultCirclePaint);

        //画进度圆弧
        //currentAngle = getProgress()*1.0f/getMax()*360;

        /* 默认Shader */
        SweepGradient mShader = new SweepGradient(DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, new int[]{START_COLOR + (int) (0.25 * END_COLOR), END_COLOR, START_COLOR, START_COLOR + (int) (0.25 * END_COLOR)}, new float[]{0, 0.749f, 0.75f, 1f});
        progressPaint.setShader(mShader);
        canvas.drawArc(new RectF(PROGRESS_WIDTH / 2, PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS * 2 + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS * 2 + PROGRESS_WIDTH / 2), -90, 360 * currentAngle, false, progressPaint);
        // 画白色背景
        canvas.drawCircle(DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2, DEFAULT_CIRCLE_RADIUS - DEFAULT_CIRCLE_STROKE_WIDTH / 2, backgroundPaint);
        // 画panel
        canvas.drawBitmap(panel, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2 - panel.getWidth() / 2, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2 - panel.getHeight() / 2, panelPaint);
        // 画中间文字
        String text = getTimeCount();
        float textWidth = textPaint.measureText(text);
        float textHeight = (textPaint.descent() + textPaint.ascent()) / 2;
        canvas.drawText(text, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2 - textWidth / 2, DEFAULT_CIRCLE_RADIUS + PROGRESS_WIDTH / 2 - textHeight, textPaint);
        //画小圆
        canvas.setDrawFilter(mSetfil);
        float currentDegreeFlag = 360 * currentAngle;
        float smallCircleX = 0, smallCircleY = 0;
        float hudu = (float) Math.abs(Math.PI * currentDegreeFlag / 180);//Math.abs：绝对值 ，Math.PI：表示π ， 弧度 = 度*π / 180
        smallCircleX = (float) Math.abs(Math.sin(hudu) * DEFAULT_CIRCLE_RADIUS + DEFAULT_CIRCLE_RADIUS);
        smallCircleY = (float) Math.abs(DEFAULT_CIRCLE_RADIUS - Math.cos(hudu) * DEFAULT_CIRCLE_RADIUS);
        canvas.rotate(currentDegreeFlag, smallCircleX + PROGRESS_WIDTH / 2, smallCircleY + PROGRESS_WIDTH / 2);
        canvas.drawBitmap(cursor, smallCircleX, smallCircleY, smallCirclePaint);

        canvas.restore();
    }

    public void restart() {
        if (animator != null) {
            animator.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        currentAngle = 0;
        setTime(0);
    }

    /**
     * 暂停
     */
    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        invalidate();
    }

    private ITimerText timerText;

    public void setTimerText(ITimerText timerText) {
        this.timerText = timerText;
    }

    public interface ITimerText {
        String getTimerText(int time);
    }

    private String getTimeCount() {
        if (timerText != null) {
            return timerText.getTimerText(countdownTime);
        }
        long min = countdownTime / 60;
        long sec = countdownTime % 60;
        String minStr, secStr;
        minStr = min < 10 ? "0" + min : String.valueOf(min);
        secStr = sec < 10 ? "0" + sec : String.valueOf(sec);
        return minStr + ":" + secStr;
    }

    public void setTime(int time) {
        this.countdownTime = time;
        invalidate();
    }

    /**
     * 返回当前倒计时所剩秒数
     */
    public int getTime() {
        return countdownTime;
    }

    private Bitmap zoomImage(Bitmap bgimage, double newWidth,
                             double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * Y
     * 如果该View布局的宽高开发者没有精确的告诉，则需要进行测量，如果给出了精确的宽高则我们就不管了
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize;
        int heightSize;
        int strokeWidth = Math.max(DEFAULT_CIRCLE_STROKE_WIDTH, PROGRESS_WIDTH);
        if (widthMode != MeasureSpec.UNSPECIFIED) {

            int delta = PROGRESS_WIDTH - DEFAULT_CIRCLE_STROKE_WIDTH > 0 ? PROGRESS_WIDTH - DEFAULT_CIRCLE_STROKE_WIDTH : 0;
            widthSize = MeasureSpec.getSize(widthMeasureSpec);
            DEFAULT_CIRCLE_RADIUS = (widthSize - delta) / 2;

//            widthSize = getPaddingLeft() + DEFAULT_CIRCLE_RADIUS * 2 + strokeWidth + getPaddingRight();
//            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
//        if (heightMode != MeasureSpec.UNSPECIFIED) {
//            heightSize = getPaddingTop() + DEFAULT_CIRCLE_RADIUS * 2 + strokeWidth + getPaddingBottom();
//            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
//        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    ValueAnimator animator;

    /**
     * 启动计时器动画，并启动本地倒计时
     */
    public void start(float startAngle) {
        setClickable(false);
        animator = ValueAnimator.ofFloat(startAngle, 1.0f);
        animator.setDuration(countdownTime * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setClickable(true);
                if (timerCountDoneCallback != null) {
                    timerCountDoneCallback.onTimerCountEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        //调用倒计时操作
        countdownMethod();
    }

    private OnTimerCountEnd timerCountDoneCallback;

    public void setOnTimerCountEnd(OnTimerCountEnd timerCountEnd) {
        timerCountDoneCallback = timerCountEnd;
    }

    public interface OnTimerCountEnd {
        void onTimerCountEnd();
    }

    /**
     * 启动计时器动画，并启动本地倒计时
     */
    public void start() {
        start(0);
    }

    CountDownTimer countDownTimer;

    //倒计时的方法
    private void countdownMethod() {
        countDownTimer = new CountDownTimer(countdownTime * 1000 + 1000, REFRESH_TIME_CYCLE) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownTime--;
                invalidate();
            }

            @Override
            public void onFinish() {
                countdownTime = 0;
                invalidate();
            }
        };
        countDownTimer.start();
    }

}


