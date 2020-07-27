package ir.mahdi2001h.bluetoothIOT;

import android.app.Application;

import in.myinnos.customfontlibrary.TypefaceUtil;


public class AppBaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/behdad-regular.ttf");
    }
}
