package cn.jianke.humanbody.module;

import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jianke.humanbody.R;
import cn.jianke.humanbody.widget.IosButton;

/**
 * @className: HumanBodyActivity
 * @classDescription: 人体图
 * @author: leibing
 * @createTime: 2016/12/30
 */
public class HumanBodyActivity extends AppCompatActivity implements View.OnClickListener{
    // 点击部位
    public final static String CLICK_POS = "clickPos";
    // 性别
    public final static String SEX = "sex";
    // 跳到下一个activity
    final static int MSG_TO_NEXT_ACTIVITY = 1;
    // 允许触摸
    final static int MSG_CAN_TOUCH = 2;
    // 人体图容器
    private FrameLayout treatSelfBodyFly;
    // 性别tab add by leibing 2016/10/07
    private LinearLayout tabLy;
    private TextView tabLeftTv;
    private TextView tabRightTv;
    // 更改人体正反面 add by leibing 2016/10/07
    private IosButton exchangeIbtn;
    private TextView reverseTv;
    // 播放动画的点
    private ImageView ivPoint;
    private AnimationDrawable pointAnim;
    // 性别、面向标识
    public static final int WOMEN_FRONT = 1;
    public static final int WOMEN_BACK = 2;
    public static final int MAN_FRONT = 3;
    public static final int MAN_BACK = 4;
    // 当前性别和面向 (默认为男性正面)
    private int curSexFace = MAN_FRONT;
    // 屏幕被点击的坐标
    float[] touchScreenPos = new float[2];
    // 是否响应触摸
    private boolean canTouch = true;
    // 人体图页面
    private HumanBodyFragment fragmentHumanBody;
    // 自定义Handler（用于点击人体图的触发事件）
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TO_NEXT_ACTIVITY:
                    treatSelfBodyFly.removeView(ivPoint);
                    mHandler.sendEmptyMessageDelayed(MSG_CAN_TOUCH, 500);
                    break;
                case MSG_CAN_TOUCH:
                    // 停止点动画
                    pointAnim.stop();
                    setCanTouch(true);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        setContentView(R.layout.activity_main);
        // initView
        initView();
    }

   /**
    * init view
    * @author leibing
    * @createTime 2016/12/30
    * @lastModify 2016/12/30
    * @param
    * @return
    */
   public void initView() {
        // 初始化人体图容器
        treatSelfBodyFly = (FrameLayout) findViewById(R.id.fly_treat_self_body);
        // 初始化性别分页控件 add by leibing 2016/10/07
        tabLy = (LinearLayout) findViewById(R.id.ly_tab);
        tabLeftTv = (TextView) findViewById(R.id.tv_tab_left);
        tabRightTv = (TextView) findViewById(R.id.tv_tab_right);
        // 初始化人体正反面控件 add by leibing 2016/10/07
        exchangeIbtn = (IosButton) findViewById(R.id.ibtn_exchange);
        reverseTv = (TextView) findViewById(R.id.tv_reverse);
        // 初始化播放动画的点
        ivPoint = new ImageView(this.getApplicationContext());
        ivPoint.setLayoutParams(new FrameLayout.LayoutParams(50,50));
        ivPoint.setBackgroundResource(R.drawable.point_in_out);
        // 设置人体图 （默认为男性正面）
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fragmentHumanBody = HumanBodyFragment.newInstance(MAN_FRONT);
        fm.replace(R.id.ly_fragment_area, fragmentHumanBody, "FragmentHumanBody");
        fm.commit();
        // 设置退出点击事件
        findViewById(R.id.rly_btn_back).setOnClickListener(this);
        // 设置性别分页控件点击事件 add by leibing 2016/10/07
        tabRightTv.setOnClickListener(this);
        tabLeftTv.setOnClickListener(this);
        // 设置更改人体正反面点击事件 add by leibing 2016/10/07
        exchangeIbtn.setOnClickListener(this);
    }

    public boolean isCanTouch() {
        return canTouch;
    }

    public void setCanTouch(boolean canTouch) {
        this.canTouch = canTouch;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 获取屏幕被点击点的坐标
        touchScreenPos[0] = ev.getX();
        touchScreenPos[1] = ev.getY();
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 改变性别逻辑
     * @author leibing
     * @createTime 2016/10/07
     * @lastModify 2016/10/07
     * @param isMan 是否男性
     * @param isFront 是否正面
     * @return
     */
    private void changeSexLogic(boolean isMan, boolean isFront){
        if (isMan && isFront){
            // 男性&&正面
            curSexFace = MAN_FRONT;
        }else if (isMan && !isFront){
            // 男性&&反面
            curSexFace = MAN_BACK;
        }else if (!isMan && isFront){
            // 女性&&正面
            curSexFace = WOMEN_FRONT;
        }else {
            // 女性&&反面
            curSexFace = WOMEN_BACK;
        }

        if (curSexFace == MAN_FRONT || curSexFace == WOMEN_FRONT){
            // 当前面为男性正面或女性正面均设置为正面
            reverseTv.setText("正面");
        }else if (curSexFace == MAN_BACK || curSexFace == WOMEN_BACK){
            // 当前面为男性反面或女性反面均设置为反面
            reverseTv.setText("反面");
        }

        // 更新fragment内容
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fragmentHumanBody = HumanBodyFragment.newInstance(curSexFace);
        fm.replace(R.id.ly_fragment_area, fragmentHumanBody, "FragmentHumanBody");
        fm.commit();
    }

    /**
     * 将圆点add入，播放动画
     * @author leibing
     * @createTime 2016/10/07
     * @lastModify 2016/10/07
     * @param clickPos 点击部位
     * @return
     */
    public void addIVandPlay(int clickPos){
        setCanTouch(false);
        // 设置点X、Y坐标
        ivPoint.setX(touchScreenPos[0] - 15);
        ivPoint.setY(touchScreenPos[1] - getStatusBarHeight() - 80);
        // 将点添加到父类容器
        if(ivPoint.getParent() == null){
            treatSelfBodyFly.addView(ivPoint);
        }
        // 执行点动画
        pointAnim = (AnimationDrawable) ivPoint.getBackground();
        pointAnim.start();
        int duration = 0;
        for (int i = 0; i < pointAnim.getNumberOfFrames(); i++) {
            duration += pointAnim.getDuration(i);
        }
        // 将点击的部位发送到新启动的activity
        Message msg = Message.obtain();
        msg.what = MSG_TO_NEXT_ACTIVITY;
        Bundle bundle = new Bundle();
        bundle.putInt(CLICK_POS, clickPos);
        // 当前性别
        String curSex = (curSexFace == MAN_FRONT || curSexFace == MAN_BACK)?"男":"女";
        // 将性别值传给Handler
        bundle.putString(SEX, curSex);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, duration);
    }

    /**
     * 获取通知栏的高度
     * @author leibing
     * @createTime 2016/10/07
     * @lastModify 2016/10/07
     * @param
     * @return
     */
    public int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height",
                        "dimen", "android"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rly_btn_back:
                // 退出该页面
                HumanBodyActivity.this.finish();
                break;
            case R.id.ibtn_exchange:
                // 设置人体正反面
                setBodyFrontBack();
                break;
            case R.id.tv_tab_left:
                // 设置背景为男性
                tabLy.setBackgroundResource(R.drawable.man_new);
                // 更改字体颜色
                tabLeftTv.setTextColor(getResources().getColor(R.color.white));
                tabRightTv.setTextColor(getResources().
                        getColor(R.color.symptoms_self_diagnostics_tab_blue_text));
                // 选择性别分页男选项
                changeSexLogic(true, true);
                break;
            case R.id.tv_tab_right:
                // 设置背景为女性
                tabLy.setBackgroundResource(R.drawable.women_new);
                // 更改字体颜色
                tabRightTv.setTextColor(getResources().getColor(R.color.white));
                tabLeftTv.setTextColor(getResources().
                        getColor(R.color.symptoms_self_diagnostics_tab_blue_text));
                // 选择性别分页女选项
                changeSexLogic(false, true);
                break;
            default:
                break;
        }
    }

    /**
     * 设置人体正反面
     * @author leibing
     * @createTime 2016/10/07
     * @lastModify 2016/10/07
     * @param
     * @return
     */
    private void setBodyFrontBack() {
        switch (curSexFace){
            case MAN_FRONT:
                // 当为男性正面，则设置为男性反面
                curSexFace = MAN_BACK;
                break;
            case MAN_BACK:
                // 当为男性反面，则设置为男性正面
                curSexFace = MAN_FRONT;
                break;
            case WOMEN_FRONT:
                // 当为女性正面，则设置为女性反面
                curSexFace = WOMEN_BACK;
                break;
            case WOMEN_BACK:
                // 当为女性反面，则设置为女性正面
                curSexFace = WOMEN_FRONT;
                break;
        }

        if (curSexFace == MAN_FRONT || curSexFace == WOMEN_FRONT){
            // 当前面为男性正面或女性正面均设置为正面
            reverseTv.setText("正面");
        }else if (curSexFace == MAN_BACK || curSexFace == WOMEN_BACK){
            // 当前面为男性反面或女性反面均设置为反面
            reverseTv.setText("反面");
        }

        // 执行动画更新fragment内容
        FragmentTransaction fm = getFragmentManager().beginTransaction();
        fm.setCustomAnimations(R.anim.animator_two_enter, R.anim.animator_one_exit);
        fragmentHumanBody = HumanBodyFragment.newInstance(curSexFace);
        fm.replace(R.id.ly_fragment_area, fragmentHumanBody, "FragmentHumanBody");
        fm.commit();
    }
}
