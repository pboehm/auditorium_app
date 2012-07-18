package de.i77i.auditorium;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final int PREFERENCE_ACTIVITY_RESULT = 123456789;
	private final String TAG = MainActivity.class.toString();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		printToTextView("\nDie Hauptaufgabe dieser App leistet ein Service im "
				+ "Hintergrund. Hier wird dieser bloß konfiguriert. Wenn neue "
				+ "Diskussionen und Kommentare existieren erzeugt dieser Service "
				+ "eine Notification.");

		if (checkConfig()) {
			installService();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivityForResult(new Intent(this, PreferencesActivity.class),
					PREFERENCE_ACTIVITY_RESULT);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PREFERENCE_ACTIVITY_RESULT) {
			if (checkConfig()) {
				printToTextView("Service wurde neu gestartet");
				installService();
			}
		}
	}

	public void buttonClick(View v) {

		switch (v.getId()) {
		case R.id.btn_preferences:
			startActivityForResult(new Intent(this, PreferencesActivity.class),
					PREFERENCE_ACTIVITY_RESULT);
			break;
		default:
			break;
		}
	}

	public void installService() {
		Intent i = new Intent(this, ServiceSetupReceiver.class);
		sendBroadcast(i);
	}

	public boolean checkConfig() {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		String token = prefs.getString("auth_token", "");
		if (token.equals("")) {
			printToTextView("\nTODO: Es muss das Authentifizierungstoken konfiguriert werden,"
					+ "um diese App nutzen zu können.");
			return false;
		} else {
			printToTextView("\nAuthentifikationstoken: " + token);
		}

		String host = prefs.getString("auditorium_host", "");
		if (token.equals("")) {
			printToTextView("\nTODO: Es muss ein Auditorium-Host eingetragen werden (IP-Adresse oder Hostname).");
			return false;
		} else {
			printToTextView("\nAuditorium-Host: " + host);
		}

		return true;
	}

	public void printToTextView(String text) {
		TextView view = (TextView) findViewById(R.id.main_textoutput);
		view.setText(view.getText() + "\n" + text);
	}
}
