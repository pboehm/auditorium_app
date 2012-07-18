package de.i77i.auditorium;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.ConnectivityManager;

public class InternetStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final ConnectivityManager connMgr = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);

		final android.net.NetworkInfo wifi = 
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = 
				connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if( wifi.isAvailable() || mobile.isAvailable()){
			Intent i = new Intent(context, ServiceStarterReceiver.class);
			context.sendBroadcast(i);
		}
	}
}
 