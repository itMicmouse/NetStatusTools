package cn.com.zhiyihealth.tools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by android on 2017/12/20.
 */

/**
 * 主要检测服务器的状态。
 */
public class NetConnectServer extends Service {

    private Subscription subscribe;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        System.out.println("onCreate invoke");
        super.onCreate();


        subscribe = Observable.interval(3, 3, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                //.take(count) //设置循环5次
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        boolean ping = ping("app.yunzhenshi.com", 1, new StringBuffer());
                        System.out.println("状态" + ping);
                    }
                });

    }

    /**
     * 每次通过startService()方法启动Service时都会被回调。
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand invoke");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 服务销毁时的回调
     */
    @Override
    public void onDestroy() {
        System.out.println("onDestroy invoke");
        super.onDestroy();
        subscribe.unsubscribe();
    }

    public static boolean ping(String host, int pingCount, StringBuffer stringBuffer) {
        String line = null;
        Process process = null;
        BufferedReader successReader = null;
//        String command = "ping -c " + pingCount + " -w 5 " + host;
        String command = "ping -c " + pingCount + " -W 3 " + host;
        boolean isSuccess = false;
        try {
            process = Runtime.getRuntime().exec(command);
            if (process == null) {
                Log.i("tag", "ping fail:process is null.");
                append(stringBuffer, "ping fail:process is null.");
                return false;
            }
            successReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = successReader.readLine()) != null) {
                Log.i("tag", line);
                append(stringBuffer, line);
            }
            int status = process.waitFor();
            if (status == 0) {
                Log.i("tag", "exec cmd success:" + command);
                append(stringBuffer, "exec cmd success:" + command);
                isSuccess = true;
            } else {
                Log.i("tag", "exec cmd fail.");
                append(stringBuffer, "exec cmd fail.");
                isSuccess = false;
            }
            Log.i("tag", "exec finished.");
            append(stringBuffer, "exec finished.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Log.i("tag", "ping exit.");
            if (process != null) {
                process.destroy();
            }
            if (successReader != null) {
                try {
                    successReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    private static void append(StringBuffer stringBuffer, String text) {
        if (stringBuffer != null) {
            stringBuffer.append(text + "\n");
        }
    }
}
