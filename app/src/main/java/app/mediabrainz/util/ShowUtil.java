package app.mediabrainz.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public class ShowUtil {

    public static void showError(Context context, Throwable t) {
        if (t != null) {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void showMessage(Activity activity, final String msg) {
        if (TextUtils.isEmpty(msg)) return;
        activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show());
    }

    public static void showToast(Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, final int resId) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show();
    }

}
