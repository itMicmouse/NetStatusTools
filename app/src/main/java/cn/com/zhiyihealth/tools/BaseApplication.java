package cn.com.zhiyihealth.tools;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

/**
 * Created by android on 2017/12/20.
 */

public class BaseApplication extends Application {
    private boolean isBackground = true;
    private Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();

        listenForForeground();
        listenForScreenTurningOff();
        startNetListener();
    }
    private void startNetListener(){
        this.intent = new Intent(this, NetConnectServer.class);
        startService(intent);
    }

    /**
     * 注册Activity.registerLifeStyleCallback监听应用切换至前台运行
     */
    private void listenForForeground() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (isBackground) {
                    isBackground = false;
                    notifyForeground();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }

        });
    }

    /**
     * 通过INTENT.ACTION_SCREEN_OFF注册广播接受器监听屏幕熄灭
     */
    private void listenForScreenTurningOff() {
        IntentFilter screenStateFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isBackground = true;
                notifyBackground();
            }
        }, screenStateFilter);
    }

    /**
     * 用Application.onTrimLevel(int level)和TRIM_MEMORY_UI_HIDDEN判断应用是否切换至后台运行。
     *
     * @param level 逻辑等级
     */
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true;
            notifyBackground();
        }

    }

    private void notifyForeground() {
        startService(intent);
    }

    private void notifyBackground() {
        stopService(intent);
    }

    public boolean isBackground() {
        return isBackground;
    }
}
