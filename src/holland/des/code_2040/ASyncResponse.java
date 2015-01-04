package holland.des.code_2040;

import org.json.JSONObject;

/**
 * Allows the main (UI) thread to receive the results of an ASyncTask 
 * defined in a separate class. Used to determine if/when an upload completes.
 */
public interface ASyncResponse {
	/**
	 * Call when the ASyncTask is finished processing the server's response.
	 * @param code The type of Challenge being executed
	 * @param response The server's response, as a JSON
	 */
	void finished(String code, JSONObject response);

}
