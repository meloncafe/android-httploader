
package net.side5.httploader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static final int NETWORK_CONNECTED = 1;
    public static final int NETWORK_NONE = 2;

    private static ConnectivityManager sConnManager = null;

    public static int CheckStatus(Context context) {
        if (isConnect(context)) {
            return NETWORK_CONNECTED;
        } else {
            return NETWORK_NONE;
        }
    }

    public static boolean isConnect(Context context) {
        sConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = sConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetwork != null && isNetworkCheck(wifiNetwork)) {
            Trace.v("connected wifi");
            return true;
        }

        NetworkInfo mobileNetwork = sConnManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && isNetworkCheck(mobileNetwork)) {
            Trace.v("connected mobile");
            return true;
        }

        NetworkInfo activeNetwork = sConnManager.getActiveNetworkInfo();
        if (activeNetwork != null && isNetworkCheck(activeNetwork)) {
            Trace.v("connected other");
            return true;
        }

        Trace.v("not connected");
        return false;
    }

    private static boolean isNetworkCheck(NetworkInfo info) {
        if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
            return true;
        } else
            return false;
    }

    public static Boolean CheckStatusMessege(Context context) {
        if (!isConnect(context)) {
            if (context instanceof Activity) {
                AlertDialog errorDialog = new AlertDialog.Builder(context)
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

                errorDialog.show();
            }
            return false;
        }
        return true;
    }
}
