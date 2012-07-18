package de.i77i.auditorium;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServiceStarterReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);

		String token = pref.getString("auth_token", "invalid");
		String host = pref.getString("auditorium_host", "auditorium.i77i.de");

		Intent serviceIntent = new Intent(context, WatchdogService.class);
		serviceIntent.putExtra("host", host);
		serviceIntent.putExtra("token", token);

		context.startService(serviceIntent);

	}
}
