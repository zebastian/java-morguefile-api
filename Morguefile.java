package utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Morguefile {
	private String MF_API_ID = "";
	private String MF_API_SECRET = ""; 
	
	public final static int MF_API_JSON = 1001; 
	public final static int MF_API_XML = 1002;
	
	public Morguefile(String api_id, String api_secret) {
		MF_API_ID = api_id;
		MF_API_SECRET = api_secret;
	}
	
	public String call(String slug, int type){
		// clean up slug
		slug = slug.toLowerCase(Locale.getDefault()).trim();
		String str = slug.replaceAll("/", "");
		
		// create the signature
		String sig = hmacDigest(str, MF_API_SECRET, "HmacSHA256");

		// make api call
		return getMorgueResponse(slug, sig, type);
	}
	
	
	private String getMorgueResponse(String slug, String sig, int type){
		String urlParameters = null;
		String out = "";
		HttpURLConnection connection = null;
		// send request
		try {
			urlParameters = "key=" + MF_API_ID
					 	+ "&sig=" + sig;
			URL url = null;
			switch(type){
				case MF_API_JSON: 
					url = new URL( "https://morguefile.com/api/" + slug + ".json"); 
					break;
				case MF_API_XML: 
					url = new URL( "https://morguefile.com/api/" + slug + ".xml"); 
					break;
				default: 
					url = new URL( "https://morguefile.com/api/" + slug + ".json"); 
					break;
			}
			connection = (HttpURLConnection) url.openConnection();           
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			// default java User Agent gets denied! -> override
			connection.setRequestProperty( "User-Agent", ""); 
			DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
			writer.writeBytes(urlParameters);
			writer.flush();
			writer.close();

		} catch(Exception e){
			System.out.println("Error in Morguefile API request");
			e.printStackTrace();
		}
		
		// get response
		try {	
			out = streamToString(connection.getInputStream());
			connection.disconnect();
		} catch(Exception e){
			System.out.println("Error in Morguefile API response");
			e.printStackTrace();
		}
		return out;
	}
	
	private static String streamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        return sb.toString();
    }

	// http://www.supermind.org/blog/1102/generating-hmac-md5-sha1-sha256-etc-in-java modified
	private static String hmacDigest(String msg, String keyString, String algo) {
	    try {
	      SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), algo);
	      Mac mac = Mac.getInstance(algo);
	      mac.init(key);

	      byte[] bytes = mac.doFinal(msg.getBytes("UTF-8"));

	      StringBuffer hash = new StringBuffer();
	      for (int i = 0; i < bytes.length; i++) {
	        String hex = Integer.toHexString(0xFF & bytes[i]);
	        if (hex.length() == 1) {
	          hash.append('0');
	        }
	        hash.append(hex);
	      }
	      return hash.toString();
	    } catch (Exception e) {
	    	System.out.println("Error in Morguefile API hmacDigest error");
	    	e.printStackTrace();
	    }
	    return null;
	  }
}
