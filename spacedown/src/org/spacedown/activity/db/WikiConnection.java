package org.spacedown.activity.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import android.util.Log;

public class WikiConnection {

	private static final String TAG = WikiConnection.class.getSimpleName();

	// time to open a connection
	private static final int CONNECTION_CONNECT_TIMEOUT_MSEC = 30000; // 30
																		// seconds
	// time for the read to take place. (needs to be longer, some connections
	// are slow and the data volume is large!)
	private static final int CONNECTION_READ_TIMEOUT_MSEC = 180000; // 180 seconds
	
	// Max lag: 5 seconds
	private static final int CONNECTION_MAX_LAG_MSEC = 5000; 

	/**
	 * A generic URL content fetcher. This is only useful for GET requests,
	 * which is almost everything that doesn't modify the wiki. Might be useful
	 * for subclasses.
	 * 
	 * Here we also check the database lag and wait if it exceeds
	 * <tt>maxlag</tt>, see <a
	 * href="https://mediawiki.org/wiki/Manual:Maxlag_parameter"> here</a> for
	 * how this works.
	 * 
	 * @param url
	 *            the url to fetch
	 * @param caller
	 *            the caller of this method
	 * @return the content of the fetched URL
	 * @throws IOException
	 *             if a network error occurs
	 * @throws AssertionError
	 *             if assert=user|bot fails
	 * @since 0.18
	 */
	protected String fetch(String url, String caller) throws IOException {
		// connect
		// logurl(url, caller);
		Log.v(TAG, "Caller: " + caller + ", URI: " + url);
		URLConnection connection = makeConnection(url);
		connection.setConnectTimeout(CONNECTION_CONNECT_TIMEOUT_MSEC);
		connection.setReadTimeout(CONNECTION_READ_TIMEOUT_MSEC);
		// setCookies(connection);
		connection.connect();
		// grabCookies(connection);

		// check lag
		int lag = connection.getHeaderFieldInt("X-Database-Lag", -5);
		int maxlag = CONNECTION_MAX_LAG_MSEC;
		if (lag > maxlag) {
			try {
				synchronized (this) {
					int time = connection.getHeaderFieldInt("Retry-After", 10);
					// log(Level.WARNING, caller,
					Log.w(TAG, "Caller: " + caller + ", current database lag " + lag + " s exceeds " + maxlag
							+ " s, waiting " + time + " s.");
					Thread.sleep(time * 1000);
				}
			} catch (InterruptedException ex) {
				// nobody cares
			}
			return fetch(url, caller); // retry the request
		}

		// get the text
		String temp = "";
		boolean zipped = false;
		BufferedReader in = new BufferedReader(new InputStreamReader(zipped ? new GZIPInputStream(
				connection.getInputStream()) : connection.getInputStream(), "UTF-8"));

		try {
			String line;
			StringBuilder text = new StringBuilder(100000);
			while ((line = in.readLine()) != null) {
				text.append(line);
				text.append("\n");
			}
			temp = text.toString();
		} catch (IOException e) {

		}
		if (temp.contains("<error code=")) {
			// assertions
			// if ((assertion & ASSERT_BOT) == ASSERT_BOT &&
			// temp.contains("error code=\"assertbotfailed\""))
			// // assert !temp.contains("error code=\"assertbotfailed\"") :
			// "Bot privileges missing or revoked, or session expired.";
			// throw new
			// AssertionError("Bot privileges missing or revoked, or session expired.");
			// if ((assertion & ASSERT_USER) == ASSERT_USER &&
			// temp.contains("error code=\"assertuserfailed\""))
			// // assert !temp.contains("error code=\"assertuserfailed\"") :
			// "Session expired.";
			// throw new AssertionError("Session expired.");
			// // Something *really* bad happened. Most of these are
			// self-explanatory
			// // and are indicative of bugs (not necessarily in this framework)
			// or
			// // can be avoided entirely.
			// if (!temp.matches("code=\"(rvnosuchsection)")) // list "good"
			// errors here
			// throw new UnknownError("MW API error. Server response was: " +
			// temp);
		}
		return temp;
	}

	/**
	 * Creates a new URL connection. Override to change SSL handling, use a
	 * proxy, etc.
	 * 
	 * @param url
	 *            a URL string
	 * @return a connection to that URL
	 * @throws IOException
	 *             if a network error occurs
	 * @since 0.31
	 */
	protected URLConnection makeConnection(String url) throws IOException {
		return new URL(url).openConnection();
	}

}
