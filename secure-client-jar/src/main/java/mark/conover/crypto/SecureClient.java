package mark.conover.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SecureClient {

	private String cookies;
	private HttpClient client = HttpClientBuilder.create().build();
	private static final String USER_AGENT = "Mozilla/5.0";
	private static String PUBLIC_KEY = null;

	public static void main(String[] args) throws Exception {
		String url = "http://localhost:8080/secure-server/SecureServlet";
		//String gmail = "https://mail.google.com/mail/";
		
		// TODO: Switch to client's public key once you have it!
		PUBLIC_KEY = 
	        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCf55mXwWwNkVEKRu3WC5ApR2JR" +
            "W3V6s9a4Rdl5EVj3BOzxPVGkp9MN5lLuPtFLIvH2iqRq4iHEZAc0BNxyY6V8LoxE" +
            "4/OilsjAxShw9HZLqR6nv5obZGGnCY4ot1mTntxmDYf0oUjCXCdntT3VhoeVuIUu" +
            "jV6vhAG6ZtpO17ZVfwIDAQAB";

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		SecureClient http = new SecureClient();

		//String page = http.GetPageContent(url);
		
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("publicKey", PUBLIC_KEY));

		String responseText = http.sendPost(url, postParams);
		String serverPublicKey = responseText.split(":") [1].trim();
		
		System.out.println("Server's public key is: " + serverPublicKey);

		//String result = http.GetPageContent(gmail);
		//System.out.println(result);

		System.out.println("Done");
	}

	private String sendPost(String url, List<NameValuePair> postParams) 
			throws ClientProtocolException, IOException {
		
		HttpPost post = new HttpPost(url);

		// add header
//		post.setHeader("Host", "accounts.google.com");
//		post.setHeader("User-Agent", USER_AGENT);
//		post.setHeader("Accept", 
//	             "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//		post.setHeader("Accept-Language", "en-US,en;q=0.5");
//		post.setHeader("Cookie", getCookies());
//		post.setHeader("Connection", "keep-alive");
//		post.setHeader("Referer", "https://accounts.google.com/ServiceLoginAuth");
//		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		post.setEntity(new UrlEncodedFormEntity(postParams));

		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + postParams);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(
	                new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		String responseText = result.toString();
		System.out.println("Response from server:" + responseText);
		
		return responseText;
	}

	private String GetPageContent(String url) throws Exception {

		HttpGet request = new HttpGet(url);

		request.setHeader("User-Agent", USER_AGENT);
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		request.setHeader("Accept-Language", "en-US,en;q=0.5");

		HttpResponse response = client.execute(request);
		int responseCode = response.getStatusLine().getStatusCode();

		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}

		// set cookies
		setCookies(response.getFirstHeader("Set-Cookie") == null ? ""
				: response.getFirstHeader("Set-Cookie").toString());

		return result.toString();

	}

	public List<NameValuePair> getFormParams(String html, String username,
			String password) throws UnsupportedEncodingException {

		System.out.println("Extracting form's data...");

		Document doc = Jsoup.parse(html);

		// Google form id
		Element loginform = doc.getElementById("gaia_loginform");
		Elements inputElements = loginform.getElementsByTag("input");

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();

		for (Element inputElement : inputElements) {
			String key = inputElement.attr("name");
			String value = inputElement.attr("value");

			if (key.equals("Email"))
				value = username;
			else if (key.equals("Passwd"))
				value = password;

			paramList.add(new BasicNameValuePair(key, value));

		}

		return paramList;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

}
