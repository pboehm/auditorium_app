package de.i77i.auditorium;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import de.i77i.auditorium.exceptions.HttpNotFoundException;
import de.i77i.auditorium.exceptions.NoChangesException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class WatchdogService extends Service {

	private final String TAG = WatchdogService.class.toString();
	final Handler handler = new Handler();
	private TimerTask checkForNewDataTask;
	private Timer timer = new Timer();

	private static final int NOTICICATION_ID = 123456789;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (checkForNewDataTask != null) {
			return super.onStartCommand(intent, flags, startId);
		}

		Toast.makeText(this, "Auditorium Service started", Toast.LENGTH_SHORT).show();

		final String token = intent.getStringExtra("token");
		final String host = intent.getStringExtra("host");
		final int interval = intent.getIntExtra("interval", 10);

		Log.d(TAG, "Token: " + token);
		Log.d(TAG, "Host: " + host);
		Log.d(TAG, "Interval: " + interval);

		checkForNewDataTask = new TimerTask() {

			@Override
			public void run() {

				if (!isOnline()) {
					Log.d(TAG, "There is currently no internet connection");
					return;
				}

				AuditoriumInterface i = new AuditoriumInterface("http://"
						+ host, token);

				AuditoriumResponse[] responses;
				try {
					int newPosts = 0;
					int newComments = 0;

					responses = i.getUnreadContent();
					for (AuditoriumResponse r : responses) {
						if (r.isNewPost())
							newPosts++;

						newComments += r.getComments();
					}

					String message = getNotificationMessage(newPosts,
							newComments);

					Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
					notificationIntent.setData(Uri.parse("http://" + host));

					notify(message, notificationIntent);

					Log.d(TAG, message);

				} catch (IOException e) {
					Log.e(TAG, "There was an Error getting the JSON Response: "
							+ e.getMessage());
				} catch (JSONException e) {
					Log.e(TAG, "There was an Error parsing the JSON Response: "
							+ e.getMessage());
				} catch (NoChangesException e) {
					Log.d(TAG, "There are no Changes");
				} catch (HttpNotFoundException e) {
					notify("Authentifikationstoken falsch", new Intent(
							getApplicationContext(), MainActivity.class));
					Log.e(TAG, "The token is possibly wrong");
				}
			}

			/**
			 * @todo refactor creation of Notifications
			 * @param text
			 */
			private void notify(final String text, final Intent intent) {
				handler.post(new Runnable() {

					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						String ns = Context.NOTIFICATION_SERVICE;
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

						int icon = R.drawable.ic_launcher;
						CharSequence tickerText = text;
						long when = System.currentTimeMillis();

						Notification notification = new Notification(icon,
								tickerText, when);
						// notification.defaults |= Notification.DEFAULT_SOUND;

						notification.flags |= Notification.FLAG_AUTO_CANCEL;

						Context context = getApplicationContext();
						CharSequence contentTitle = "Auditorium Ticker";
						CharSequence contentText = text;

						PendingIntent contentIntent = PendingIntent
								.getActivity(getApplicationContext(), 0,
										intent, 0);

						notification.setLatestEventInfo(context, contentTitle,
								contentText, contentIntent);

						mNotificationManager.notify(NOTICICATION_ID,
								notification);
					}
				});
			}

			/**
			 * build the notification
			 * 
			 * @param newPosts
			 * @param newComments
			 * @return
			 */
			public String getNotificationMessage(final int newPosts,
					final int newComments) {
				String message = "";
				String postMessage = "";
				String commentMessage = "";

				if (newComments != 0) {
					if (newComments == 1) {
						commentMessage = newComments + " neuer Kommentar";
					} else {
						commentMessage = newComments + " neue Kommentare";
					}

				}

				if (newPosts != 0) {
					if (newPosts == 1) {
						postMessage = newPosts + " neue Diskussion";
					} else {
						postMessage = newPosts + " neue Diskussionen";
					}
				}

				if (newPosts != 0 && newComments != 0)
					message = postMessage + " und " + commentMessage;
				else if (newPosts != 0 && newComments == 0)
					message = postMessage;
				else if (newPosts == 0 && newComments != 0)
					message = commentMessage;

				return message;
			}

			private boolean isOnline() {
				/**
				 * check for an internet connection
				 */
				ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = cm.getActiveNetworkInfo();
				if (netInfo != null && netInfo.isConnected()) {
					return true;
				}
				return false;
			}

		};

		timer.scheduleAtFixedRate(checkForNewDataTask, 300, interval * 60000);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		checkForNewDataTask.cancel();
		timer.cancel();

		Toast.makeText(this, "Auditorium Service stopped", Toast.LENGTH_SHORT)
				.show();
		Log.d(TAG, "Auditorium Service stopped (OnDestroy)");

	}

}
