package testing.example.changenightdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    /*
     * commit和apply的总结：
     *    1. apply没有返回值而commit返回boolean表明修改是否提交成功 ；
     *    2. commit是把内容同步提交到硬盘的，而apply先立即把修改提交到内存，然后开启一个异步的线程提交到硬盘，
     *       并且如果提交失败，你不会收到任何通知。
     *    3. 所有commit提交是同步过程，效率会比apply异步提交的速度慢，在不关心提交结果是否成功的情况下，优先考虑apply方法。
     *    4. apply是使用异步线程写入磁盘，commit是同步写入磁盘。所以我们在主线程使用的commit的时候，
     *       需要考虑是否会出现ANR问题。（不适合大量数据存储）
     * */

    private SharedPreferences sharedPreferences;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = findViewById(R.id.sw);
        sharedPreferences = getSharedPreferences("app_night_mode", MODE_PRIVATE);
        final boolean isNightMode = sharedPreferences.getBoolean("night_mode", false);
        if (isNightMode) {
            aSwitch.setChecked(true);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            aSwitch.setChecked(false);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        /*
        设置 当前的模式
        AppCompatDelegate.setDefaultNightMode(int mode);
        它有四个可选值,分别是:
        MODE_NIGHT_NO: 使用亮色(light)主题,不使用夜间模式
        MODE_NIGHT_YES:使用暗色(dark)主题,使用夜间模式
        MODE_NIGHT_AUTO:根据当前时间自动切换 亮色(light)/暗色(dark)主题
        MODE_NIGHT_FOLLOW_SYSTEM(默认选项):设置为跟随系统,通常为 MODE_NIGHT_NO*/
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !isNightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //SharedPreferences.Editor editor=sharedPreferences.edit();
                    sharedPreferences.edit().putBoolean("night_mode", true).apply();
                } else if (!isChecked && isNightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferences.edit().putBoolean("night_mode", false).apply();
                }
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.night_mode_open_anim, R.anim.night_mode_close_anim);
                finish();
            }
        });

    }
}
