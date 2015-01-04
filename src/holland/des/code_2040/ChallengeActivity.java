package holland.des.code_2040;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ChallengeActivity extends Activity implements ASyncResponse {
	public static final String CODE_REVERSAL = "REVERSAL";
	public static final String CODE_HAYSTACK = "HAYSTACK";
	public static final String CODE_PREFIX = "PREFIX";
	public static final String CODE_TIME = "TIME";
	
	Context con;
	String token;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_challenge);
		con = this;
		MyAsync.delegate = this; //reset the delegate to use this class's finished()
		
		getActionBar().hide();
		
		token = getIntent().getExtras().getString("token");
		Log.d("debug", "Token received: " + token);
		
		Button stringReversalButton = (Button) findViewById(R.id.button1);
		stringReversalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONWrapper[] params = new JSONWrapper[1];
				params[0] = new JSONWrapper("token", token);
				new MyAsync("http://challenge.code2040.org/api/getstring").execute(params);
			}
		});
		
		Button haystackButton = (Button) findViewById(R.id.button2);
		haystackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONWrapper[] params = new JSONWrapper[1];
				params[0] = new JSONWrapper("token", token);
				new MyAsync("http://challenge.code2040.org/api/haystack").execute(params);
			}
		});
		
		Button prefixButton = (Button) findViewById(R.id.button3);
		prefixButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONWrapper[] params = new JSONWrapper[1];
				params[0] = new JSONWrapper("token", token);
				new MyAsync("http://challenge.code2040.org/api/prefix").execute(params);
			}
		});
		 
		Button timeButton = (Button) findViewById(R.id.button4);
		timeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONWrapper[] params = new JSONWrapper[1];
				params[0] = new JSONWrapper("token", token);
				new MyAsync("http://challenge.code2040.org/api/time").execute(params);
			}
		});
	}
	
	@Override
	public void finished(String code, JSONObject response) {
		if(code.equals(CODE_REVERSAL)) {
			String stringToReverse;
			try {
				stringToReverse = response.getString("result");
				
				JSONWrapper[] params = new JSONWrapper[2];
				params[0] = new JSONWrapper("token", token);
				params[1] = new JSONWrapper("string", reverseString(stringToReverse));
				new MyAsync("http://challenge.code2040.org/api/validatestring").execute(params);
				Toast.makeText(con, "Success!", Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(con, "Failed", Toast.LENGTH_LONG).show();
			}
		}
		
		if(code.equals(CODE_HAYSTACK)) {
			try {
				JSONObject obj = response.getJSONObject("result");
				String needle = obj.getString("needle");
				JSONArray hay = obj.getJSONArray("haystack");
				ArrayList<String> hayArrList = new ArrayList<String>();     
				for(int i=0; i < hay.length(); i++)
					hayArrList.add(hay.get(i).toString());
				
				JSONWrapper[] params = new JSONWrapper[2];
				params[0] = new JSONWrapper("token", token);
				params[1] = new JSONWrapper("needle", String.valueOf(searchHaystack(needle, hayArrList)));
				new MyAsync("http://challenge.code2040.org/api/validateneedle").execute(params);
				Toast.makeText(con, "Success!", Toast.LENGTH_LONG).show();
			} catch(Exception e) {
				e.printStackTrace();
				Toast.makeText(con, "Failed", Toast.LENGTH_LONG).show();
			}
		}
		
		if(code.equals(CODE_PREFIX)) {
			try {
				JSONObject obj = response.getJSONObject("result");
				String prefix = obj.getString("prefix");
				JSONArray array = obj.getJSONArray("array");
				
				JSONWrapper[] params = new JSONWrapper[2];
				params[0] = new JSONWrapper("token", token);
				params[1] = new JSONWrapper("array", "array", removeElementsStartingWith(prefix, array).toString());
				new MyAsync("http://challenge.code2040.org/api/validateprefix").execute(params);
				Toast.makeText(con, "Success!", Toast.LENGTH_LONG).show();
			} catch(Exception e) {
				e.printStackTrace();
				Toast.makeText(con, "Failed", Toast.LENGTH_LONG).show();
			}
		}
		
		if(code.equals(CODE_TIME)) {
			try {
				JSONObject obj = response.getJSONObject("result");
				String datestamp = obj.getString("datestamp");
				long interval = obj.getLong("interval");
				JSONWrapper[] params = new JSONWrapper[2];
				params[0] = new JSONWrapper("token", token);
				params[1] = new JSONWrapper("datestamp", processDate(datestamp, interval).toString());
				new MyAsync("http://challenge.code2040.org/api/validatetime").execute(params);
				Toast.makeText(con, "Success!", Toast.LENGTH_LONG).show();
			} catch(Exception e) {
				e.printStackTrace();
				Toast.makeText(con, "Failed", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * Reverses a given String
	 * @param input String to reverse
	 * @return input, reversed
	 */
	public String reverseString(String input) {
		String ans = "";
		for(char c : input.toCharArray()) 
			ans = c + ans;
		Log.d("debug", "reverseString input: " + input + "|answer: " + ans);
		return ans;
	}
	
	/**
	 * Searches hay for the first location of needle
	 * @param needle The String to search for
	 * @param hay The list of Strings to search
	 * @return The index of needle in hay, starting from 0
	 */
	public int searchHaystack(String needle, ArrayList<String> hay) {
		return hay.indexOf(needle);
	}
	
	/**
	 * Removes all instances of String in arr that start with prefix
	 * @param prefix
	 * @param arr
	 * @return A JSONArray of elements of arr that do not start with prefix
	 * @throws JSONException
	 */
	public JSONArray removeElementsStartingWith(String prefix, JSONArray arr) throws JSONException {
		JSONArray ans = new JSONArray();
		for(int i = 0; i < arr.length(); i++) 
			if(((String) arr.get(i)).startsWith(prefix) == false){
				ans.put(arr.get(i));
			}
		return ans;
	}
	
	/**
	 * Adds a number of seconds to a given timestamp. Instead of importing
	 * and using Joda time, I found some date hacks on StackOverflow
	 * @param date A String representing the ISO8601 date to handle
	 * @param interval The number of seconds to add to date
	 * @return The ISO8601 timestamp of the new date
	 */
	public String processDate(String date, long interval) {
		Calendar c = toCalendar(date);
		c.setTime(new Date((long) ((long) (c.getTimeInMillis() / 1000. + interval) * 1000.))); //interval is in seconds so divide millis by 1000
		return fromCalendar(c);
	}
	
	/**
	 * Found this clever implementation at:
	 * http://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
	 * (I did not write the following method, just modified it to meet the specs)
	 */
	/** Transform Calendar to ISO 8601 string. */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(date);
        return formatted;
    }
    
	/**
	 * Found this clever implementation at:
	 * http://stackoverflow.com/questions/2201925/converting-iso-8601-compliant-string-to-java-util-date
	 * (I did not write the following method, just modified it to meet the specs)
	 */
	/** Transform ISO 8601 string to Calendar. */
	public static Calendar toCalendar(final String iso8601string) {
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		Calendar calendar = GregorianCalendar.getInstance(timeZone);
		String s = iso8601string.replace("Z", "+00:00");
		s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
		Date date;
		try {
			calendar.setTimeZone(timeZone);
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(s);
			calendar.setTime(date);
			calendar.setTimeZone(timeZone);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		
		return calendar;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.challenge, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
