package mark.conover.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import mark.conover.crypto.config.LoggingConfig;
import mark.conover.crypto.util.SecureClientUtil;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureClient {

	private String cookies;
	private HttpClient client = HttpClientBuilder.create().build();
	private static final String USER_AGENT = "Mozilla/5.0";
	private static String CLIENT_PUBLIC_KEY = null;
	private static String CLIENT_PUBLIC_KEY_FILE = null;
	private static String CLIENT_PRIVATE_KEY = null;
	private static String CLIENT_PRIVATE_KEY_FILE = null;
	private static final String AES_128_SYMMETRIC_KEY = "ASecureSecretKey";
	
    private static final Logger LOG = LoggerFactory.getLogger(
        SecureClient.class);

	public static void main(String[] args) throws Exception {
	    
	    LoggingConfig loggingConfig = new LoggingConfig();
	    loggingConfig.configure();
	    
		String url = "http://localhost:8080/secure-server/SecureServlet";
		//String gmail = "https://mail.google.com/mail/";
		
		// TODO: Switch to client's public key once you have it!
		CLIENT_PUBLIC_KEY = 
	        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCf55mXwWwNkVEKRu3WC5ApR2JR" +
            "W3V6s9a4Rdl5EVj3BOzxPVGkp9MN5lLuPtFLIvH2iqRq4iHEZAc0BNxyY6V8LoxE" +
            "4/OilsjAxShw9HZLqR6nv5obZGGnCY4ot1mTntxmDYf0oUjCXCdntT3VhoeVuIUu" +
            "jV6vhAG6ZtpO17ZVfwIDAQAB";
		
		CLIENT_PRIVATE_KEY = 
	        "lAnGxjoVHpOZt5hu0fxFzlCgUw18z/RYdxqNABcvt9wgCtVQ2Aeec9UzY/5E38+N" +
	        "naba4qdKVRbiuK0npVvFEW8FYcfZYBXbfd1c6A+XJMM21X87nw7smQuM7Cq2Wb0E" +
	        "bD7VpJWGcOMqULb1CwNjVW7OnJTcHvtROo4znaqIj+pFZZbmFz3szuY7GNaYlIVB" +
	        "6FHU8W5XwJYe2GY4FkdC3zK+YbsqbU3LilIzfAegQyI3ZAkK8kM69I8+A4HjWIN1" +
	        "0HqCqsTUc/iHwdzJEaORTrWWhwX8tzVwPj6ssENiC0xdeLRYo8d+oRT7sVm2K3ik" +
	        "aPR7d0cMlAbX7e7KNB1KwbQYr5xJR2QuEHnIlEX6ShIuaN+XARpkXmCxRPc8bRRY" +
	        "40c2u9i58Xz+Z8vQ0xoUmKsORfsYSVYIQ1+g4TmPr83aHwagbG/rQBLHHwQJjSmO" +
	        "0oWMXYigy/xH0DUAAs9VjbV++JrPnUmn/zt2lnrgZTfHcdtrPwy73cUPEtFw3MKl" +
	        "oxKM6nfj1ndVdMn6/otzH5q+0mAEaVx5N0qpO8BOtvAhhPB2cOZtG8r+BaNAMQzA" +
	        "BlOIADhsVKoIPVf4XhH53IjQmO5FmeSEwXuN0ziyCziZKTEoJXY6zjqPAg0nYJvI" +
	        "6ytt2EnBLqrY/HiJCL4r3zV+lGAVMmzwFLlJ8FEZRht1t+ISO8r9VJubCJFj81Vi" +
	        "IrgOWhtS4+2oo9r3RErZW60l91ys3UICi+NBvvB7AaKjQ0PDLsuUrvRo56rDcHrm" +
	        "V7HsqFS726QVUQyOwgnKy8eJaxpnxF9MvB+UBZ7bXVk=";

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		SecureClient http = new SecureClient();

		//String page = http.GetPageContent(url);
		
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		postParams.add(new BasicNameValuePair("publicKey", CLIENT_PUBLIC_KEY));

		String responseText = http.sendPost(url, postParams, null);
		String serverPublicKeyText = responseText.split(":") [1].trim();
		
		LOG.debug("Server's public key is: " + serverPublicKeyText);

		//---------------------------------------------------------------------
		
		// Write the server's public key to a temporary file so that it's 
		// easier to use RSAEncryptionUtil.java
		
		// Encrypt the string using the server's public key
		String prefix = "server-public-key";
	    String suffix = ".key";
	     
	    // this temporary file remains after the jvm exits
	    File serverPublicKeyFile = File.createTempFile(prefix, suffix);
	    FileOutputStream fos = new FileOutputStream(serverPublicKeyFile);
	    IOUtils.write(serverPublicKeyText.getBytes(), fos);
	    SecureClientUtil.safeClose(fos);
	    
	    ObjectInputStream inputStream = new ObjectInputStream(
            new FileInputStream(serverPublicKeyFile));
	    final PublicKey serverPublicKey = (PublicKey) inputStream.readObject();
	    final byte[] cipherTextBytes = RSAEncryptionUtil.encrypt(
            "AES Symmetric Key:" + AES_128_SYMMETRIC_KEY, serverPublicKey);
        SecureClientUtil.safeClose(inputStream);
        
        postParams = new ArrayList<NameValuePair>();
        postParams.add(new BasicNameValuePair("publicKey", CLIENT_PUBLIC_KEY));
        
        // Add symmetric key in HTTP POST content body
        
        String cipherText = new String(cipherTextBytes);
        responseText = http.sendPost(url, postParams, cipherText);
        if (responseText.contains("Got the AES symmetric key.")) {
            // TODO: Send messages to server now but encrypt them using 
            //       AES128.java and the AES symmetric key.
        } else {
            LOG.error("Server was unable to receive AES symmetric key");
        }

        LOG.debug("Done");
	}

	private String sendPost(String url, List<NameValuePair> postParams, 
        String bodyContent) 
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
		
		if (bodyContent != null) {
            HttpEntity entity = new ByteArrayEntity(
                bodyContent.getBytes("UTF-8"));
            post.setEntity(entity);
		}

		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		LOG.debug("\nSending 'POST' request to URL : " + url);
		LOG.debug("Post parameters : " + postParams);
		LOG.debug("Response Code : " + responseCode);

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
