package de.i77i.auditorium;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ServiceSetupReceiver extends BroadcastReceiver {
	
	private final String TAG = ServiceSetupReceiver.class.toString();

	@Override
	public void onReceive(Context context, Intent intent) {

		// load the check intervall
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		int interval = Integer.parseInt(pref.getString("every_minute", "10"));
		int exec_interval = interval * 60000;

		// configure periodic execution
		Log.d(TAG, "SchedulerSetupReceiver.onReceive() called");
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Intent i = new Intent(context, ServiceStarterReceiver.class);

		PendingIntent intentExecuted = PendingIntent.getBroadcast(context, 0,
				i, PendingIntent.FLAG_CANCEL_CURRENT);
		
		alarmManager.cancel(intentExecuted);
		
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 3);
		
		alarmManager.setRepeating(AlarmManager.RTC,
				now.getTimeInMillis(), exec_interval, intentExecuted);

	}

}
