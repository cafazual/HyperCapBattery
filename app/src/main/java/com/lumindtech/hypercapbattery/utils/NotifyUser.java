package com.lumindtech.hypercapbattery.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created by Carlos Zuluaga on 10/10/2016.
 */

public class NotifyUser {

    public static void toastMessage(final Context context, final String msg){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        final Toast T=new Toast(context);
                        T.makeText(context, msg , Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
