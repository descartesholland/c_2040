package holland.des.code_2040;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity implements OnClickListener, ASyncResponse {
	
	public static final String CODE_LOGIN = "login";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getActionBar().hide();
		
		MyAsync.delegate = this;
		Button login_btn = (Button) findViewById(R.id.login_btn);
		login_btn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
			return true;
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		JSONWrapper[] params = new JSONWrapper[2];
		String email = ((EditText) findViewById(R.id.email_et)).getText().toString();
		String github = ((EditText) findViewById(R.id.github_et)).getText().toString();
		params[0] = new JSONWrapper("email", email);
		params[1] = new JSONWrapper("github", github);
		
		new MyAsync("http://challenge.code2040.org/api/register").execute(params);
	}
	
	@Override
	public void finished(String code, JSONObject response) {
		try {
			Intent i = new Intent(this, ChallengeActivity.class);
			i.putExtra("token", response.getString("result"));
			startActivity(i);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
