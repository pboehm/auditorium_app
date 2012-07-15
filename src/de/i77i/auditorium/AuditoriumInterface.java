/**
 * 
 */
package de.i77i.auditorium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.i77i.auditorium.exceptions.HttpNotFoundException;
import de.i77i.auditorium.exceptions.NoChangesException;


/**
 * @author philipp
 * 
 */
public class AuditoriumInterface {

	private String auditoriumURL, auditoriumAuthToken;

	public AuditoriumInterface(String url, String token) {
		auditoriumURL = url;
		auditoriumAuthToken = token;
	}

	private String buildUrl() {
		return auditoriumURL + "/remote/" + auditoriumAuthToken;
	}

	/**
	 * gets the changed posts from auditorium
	 * 
	 * @return
	 * @throws IOException
	 *             if there are problems with HTTP
	 * @throws JSONException
	 *             if there are problems with JSON processing
	 * @throws NoChangesException
	 *             if there are no changed posts
	 * @throws HttpNotFoundException 
	 */
	public AuditoriumResponse[] getUnreadContent() throws IOException,
			JSONException, NoChangesException, HttpNotFoundException {

		String json_content = getHttpResponse(buildUrl());

		JSONObject response = new JSONObject(json_content);
		JSONArray keys = response.names(); // these are the post ids

		if (keys == null)
			throw new NoChangesException();

		AuditoriumResponse[] responses = new AuditoriumResponse[keys.length()];

		for (int i = 0; i < keys.length(); i++) {
			String key = keys.getString(i);

			JSONObject post_data = response.getJSONObject(key);

			String message = post_data.getString("post_message");
			int comments = post_data.getInt("new_comments");
			boolean isNewPost = post_data.getBoolean("is_new_post");

			AuditoriumResponse r = new AuditoriumResponse(
					Integer.parseInt(key), message, isNewPost, comments);
			responses[i] = r;

		}
		return responses;
	}

	public String getHttpResponse(String url) throws IOException, HttpNotFoundException {

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);

		//try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else if (statusCode == 404) {
				throw new HttpNotFoundException();
			}
		//} catch (Exception e) {
		//	throw new IOException("Could not get the HTTP Response (" + e.toString() + "): "
		//			+ e.getMessage());
		//}

		return builder.toString();
	}
}
