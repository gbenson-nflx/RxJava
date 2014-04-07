package rx.android.sample.util;

import android.util.Log;

public class LogUtil {
	
	private static final String APP_TAG = "RxAndroid";

	public static void handleException(String tag, Throwable th) {
		Log.e(APP_TAG, tag + " - " + Thread.currentThread().getName() + ": " + th.getMessage(), th);
	}

	public static void v(String tag, String msg) {
		Log.v(APP_TAG, tag + " - " + Thread.currentThread().getName() + ": " + msg);
	}
}
