package cn.xing.mypassword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

import cn.xing.mypassword.R;
import cn.xing.mypassword.app.BaseActivity;
import cn.xing.mypassword.view.LockPatternUtil;
import cn.xing.mypassword.view.LockPatternView;
import cn.xing.mypassword.view.LockPatternView.Cell;
import cn.xing.mypassword.view.LockPatternView.DisplayMode;
import cn.xing.mypassword.view.LockPatternView.OnPatternListener;
import cn.zdx.lib.annotation.FindViewById;

public class AuthorizationCheckActivity extends BaseActivity implements Callback, OnPatternListener{

    private final int MESSAGE_START_EDIT_PASSWORD = 1;
    private final int MESSAGE_CLEAR_LOCKPATTERNVIEW = 3;

    @FindViewById(R.id.entry_activity_iconview)
    private View iconView;
    private Handler handler;
    @FindViewById(R.id.entry_activity_bg)
    private View backgroundView;

    @FindViewById(R.id.entry_activity_lockPatternView)
    private LockPatternView lockPatternView;

    @FindViewById(R.id.entry_activity_tips)
    private TextView tipsView;

    //修改用户名或密码时，将用户名和密码等信息放在intent里传递过来。此变量用于接收这些信息
    private Intent authCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_check);

        handler = new Handler(this);
        lockPatternView.setOnPatternListener(this);
        authCheck = getIntent();

        List<Cell> cells = LockPatternUtil.getLocalCell(this);
        tipsView.setText("");
        initAnimation();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_START_EDIT_PASSWORD:
               // Intent intent = new Intent();
               // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_OK,authCheck);
                finish();
                break;

            case MESSAGE_CLEAR_LOCKPATTERNVIEW:
                lockPatternView.clearPattern();
                tipsView.setText("");
                break;

            default:
                break;
        }
        return true;
    }

    /**
     * 图标动画
     */
    private void initAnimation() {
        Animation iconAnimation = AnimationUtils.loadAnimation(this, R.anim.entry_animation_icon);
        iconView.startAnimation(iconAnimation);

        backgroundView.startAnimation(getAlpAnimation());
        lockPatternView.startAnimation(getAlpAnimation());
        tipsView.startAnimation(getAlpAnimation());
    }

    private Animation getAlpAnimation() {
        return AnimationUtils.loadAnimation(this, R.anim.entry_animation_alpha_from_0_to_1);
    }

    @Override
    public void onPatternStart() {
        handler.removeMessages(MESSAGE_CLEAR_LOCKPATTERNVIEW);
        tipsView.setText("");
    }

    @Override
    public void onPatternCleared() {
    }

    @Override
    public void onPatternCellAdded(List<Cell> pattern) {
    }

    @Override
    public void onPatternDetected(List<Cell> pattern) {
        if (LockPatternUtil.checkPatternCell(LockPatternUtil.getLocalCell(this), pattern)) {
            // 认证通过
            lockPatternView.setDisplayMode(DisplayMode.Correct);
            handler.sendEmptyMessage(MESSAGE_START_EDIT_PASSWORD);
        } else {
            // 认证失败
            lockPatternView.setDisplayMode(DisplayMode.Wrong);
            tipsView.setText(R.string.lock_pattern_error);
            handler.sendEmptyMessageDelayed(MESSAGE_CLEAR_LOCKPATTERNVIEW, 1000);
        }

    }
}
