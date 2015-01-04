package holland.des.code_2040;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * @author descartesholland
 *
 */
public class MyAsync extends AsyncTask<JSONWrapper, Void, String> {
	public static ASyncResponse delegate = null;
	String url;
	
	
	public MyAsync(String url) {
		this.url = url;
	}
	
	@Override
	protected String doInBackground(JSONWrapper... v) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost post = new HttpPost(url);
		
		JSONObject json = new JSONObject();
		try {
			for(JSONWrapper wrapper : v) {
				if(wrapper.type.equals("array"))
					json.put(wrapper.name, new JSONArray(wrapper.data));
				else if(wrapper.type.equals("object"))
					json.put(wrapper.name, new JSONObject(wrapper.data));
				else
					json.put(wrapper.name, wrapper.data);
			}
			
			StringEntity se = new StringEntity(json.toString());
			se.setContentType("application/json;charset=UTF-8");
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
			post.setEntity(se);
			
			HttpResponse response = httpClient.execute(post, localContext);
			HttpEntity responseEntity = response.getEntity();
			Log.d("debug", "Status code " + response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 400) 
				return convertStreamToString(responseEntity.getContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "failure";
	}
	
	
	protected void onPostExecute(String result) { 
		Log.d("debug", "Result: " + result);
		String code = "";
		if(url.equals("http://challenge.code2040.org/api/register"))
			code = Login.CODE_LOGIN;
		else if(url.equals("http://challenge.code2040.org/api/getstring"))
			code = ChallengeActivity.CODE_REVERSAL;
		else if(url.equals("http://challenge.code2040.org/api/haystack"))
			code = ChallengeActivity.CODE_HAYSTACK;
		else if(url.equals("http://challenge.code2040.org/api/prefix"))
			code = ChallengeActivity.CODE_PREFIX;
		else if(url.equals("http://challenge.code2040.org/api/time"))
			code = ChallengeActivity.CODE_TIME;
		
		try {
			JSONObject response = new JSONObject(result);
			delegate.finished(code, response);
		} catch (JSONException e) {
			e.printStackTrace();
			delegate.finished(code, null);
		}
	}
	
	private String convertStreamToString(InputStream in) {
		
		String builder = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			String line = reader.readLine();
			do {
				builder = builder + line;
				line = reader.readLine();
			} while (line != null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
	
}
