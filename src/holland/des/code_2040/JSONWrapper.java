package holland.des.code_2040;

/**
 * This class allows me to wrap JSON or key/value pairs together
 * with a type attribute that specifies whether the information being
 * wrapped is a JSONObject, a JSONArray, or simply a key/value pair
 * @author Descartes Holland
 *
 */
public class JSONWrapper {
	String type;
	String data;
	String name;
	
	public JSONWrapper(String type, String name, String data) {
		this.type = type;
		this.name = name;
		this.data = data;
	}

	public JSONWrapper(String name, String data) {
		this.type = "";
		this.name = name;
		this.data = data;
	}
}
