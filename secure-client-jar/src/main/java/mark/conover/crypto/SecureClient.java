package mark.conover.crypto;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import mark.conover.crypto.config.LoggingConfig;
import mark.conover.crypto.util.SecureClientUtil;

import org.apache.commons.io.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
	private static String CLIENT_PUBLIC_KEY_FILE_PATH = null;
	private static String CLIENT_PRIVATE_KEY_FILE_PATH = null;
	private static final String AES_128_SYMMETRIC_KEY = "ASecureSecretKey";
	
    private static final Logger LOG = LoggerFactory.getLogger(
        SecureClient.class);

	public static void main(String[] args) throws Exception {
	    
	    LoggingConfig loggingConfig = new LoggingConfig();
	    loggingConfig.configure();
	    
		String url = "http://localhost:8080/secure-server/SecureServlet";
		
		CLIENT_PRIVATE_KEY_FILE_PATH = 
			"/etc/opt/secure-client/rsa-keys/client-private-key_2048.der";
		
		CLIENT_PUBLIC_KEY_FILE_PATH = 
			"/etc/opt/secure-client/rsa-keys/client-public-key_2048.der";

		// make sure cookies is turn on
		CookieHandler.setDefault(new CookieManager());

		SecureClient http = new SecureClient();
		
		List<NameValuePair> postParams = new ArrayList<NameValuePair>();
		
		// TODO: Add client's public key here
		//postParams.add(new BasicNameValuePair("publicKey", CLIENT_PUBLIC_KEY));
		postParams.add(new BasicNameValuePair("publicKey", "clientPublicKey"));
		
		String responseText = http.sendPost(url, postParams, "");
		
		String serverPublicKeyFilePath = null;
		if (responseText.contains("serverPublicKeyFile:")) {
			serverPublicKeyFilePath = responseText.split(":") [1].trim();
			LOG.debug("Server's public key is saved at: " + 
				serverPublicKeyFilePath);
		} else {
			LOG.error("The server did not send its public key file. " + 
				"Quitting client/server interaction.");
			return;
		}

		//---------------------------------------------------------------------
		
		// Read in the server's public key file   
	    PublicKey serverPublicKey = FileEncryption2.readPublicKey(
    		serverPublicKeyFilePath);
        
        postParams = new ArrayList<NameValuePair>();
		// TODO: Add client's public key here
		//postParams.add(new BasicNameValuePair("publicKey", CLIENT_PUBLIC_KEY));
		postParams.add(new BasicNameValuePair("aesSymmetricKeyIncluded", 
				"yes"));
		postParams.add(new BasicNameValuePair("publicKey", "clientPublicKey"));
        
        // Add symmetric key in HTTP POST content body
		byte[] message = (AES_128_SYMMETRIC_KEY
				).getBytes(Charsets.UTF_8);
		byte[] encryptedAesKey = FileEncryption2.encrypt(serverPublicKey, 
			message);
		String encryptedAesKeyString = new String(encryptedAesKey, 
			Charsets.UTF_8);
		LOG.debug("The enrypted AES shared symmetric key using the " 
				+ "server's public key is: '{}'", 
				encryptedAesKeyString);
//		
//		
//		
//		
//		
//		
//		// First verify that client can decrypt the encrypted aes key with the
//		// server's private key here.  If it can, then bytes may be being
//		// altered during transmission
//        PrivateKey serverPrivateKey = null;
//        try {
//            serverPrivateKey = FileEncryption2.readPrivateKey(
//                "/etc/opt/secure-server/rsa-keys/server-private-key_2048.der");
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            LOG.error("Unable to read server's private key from file " +
//                "path '{}'", 
//                "/etc/opt/secure-server/rsa-keys/server-private-key_2048.der", 
//                e);
//        }
//        
//        byte[] aesKeyBytes = null;
//        try {
//            aesKeyBytes = FileEncryption2.decrypt(serverPrivateKey, 
//                    encryptedAesKey);
//        } catch (InvalidKeyException | NoSuchAlgorithmException
//                | NoSuchPaddingException | IllegalBlockSizeException
//                | BadPaddingException e) {
//
//            LOG.error("Unable to decrypt the encrypted AES key using the " +
//                "server's private key.", e);
//        }
//        
//        String aesKey = null;
//        if (aesKeyBytes != null) {
//            aesKey = new String(aesKeyBytes, Charsets.UTF_8);
//            
//            LOG.debug("The AES Symmetric key from client is: '{}'", aesKey);
//        }
        
        
        
        
        
        
        
		
		// TODO: Add client's public key here
        responseText = http.sendPost(url + "?aesSymmetricKeyIncluded=yes&publicKey=clientPublicKey", postParams, encryptedAesKey);
        if (responseText.contains("Got the AES symmetric key.")) {
            // TODO: Send messages to server now but encrypt them using 
            //       AES128.java and the AES symmetric key.

			
//			responseText = http.sendPost(url, postParams, 
//					"Encrypted AES Symmetric Key:" + encryptedAesKeyString);		
			LOG.debug("Server's encrypted response is: " + responseText);
			
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
                bodyContent.getBytes(Charsets.UTF_8));
            post.setEntity(entity);
		}

		HttpResponse response = client.execute(post);

		int responseCode = response.getStatusLine().getStatusCode();

		LOG.debug("\nSending 'POST' request to URL : " + url);
		LOG.debug("Post parameters : " + postParams);
		LOG.debug("Response Code : " + responseCode);
		
		if (response.containsHeader("Content-Disposition")) {
			
			// Save server's public key file
			LOG.debug("Saving server's public key file...");
			InputStream is = response.getEntity().getContent();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (is != null) {
	
				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
			    byte[] aByte = new byte[1];
		        int bytesRead;
				try {
					fos = new FileOutputStream(
						"/etc/opt/secure-client/server-public-key_2048.der");
					bos = new BufferedOutputStream(fos);
					bytesRead = is.read(aByte, 0, aByte.length);
	
					do {
						baos.write(aByte);
						bytesRead = is.read(aByte);
					} while (bytesRead != -1);
	
					bos.write(baos.toByteArray());
					bos.flush();
				} catch (IOException e) {
					// Do exception handling
					LOG.debug("Unable to save server's public key file received " + 
						"by the server.", e);
				} finally {
					SecureClientUtil.safeClose(bos);
					SecureClientUtil.safeClose(baos);
					SecureClientUtil.safeClose(is);
					SecureClientUtil.safeClose(fos);
				}
			}
			
			return "serverPublicKeyFile:/etc/opt/secure-client/server-public-key_2048.der";
		}
		
		// Get the response text from the server
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
	
	private String sendPost(String url, List<NameValuePair> postParams, 
	        byte[] bodyContent) 
	        throws ClientProtocolException, IOException {
	        
	        HttpPost post = new HttpPost(url);

	        // add header
//	      post.setHeader("Host", "accounts.google.com");
//	      post.setHeader("User-Agent", USER_AGENT);
//	      post.setHeader("Accept", 
//	               "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//	      post.setHeader("Accept-Language", "en-US,en;q=0.5");
//	      post.setHeader("Cookie", getCookies());
//	      post.setHeader("Connection", "keep-alive");
//	      post.setHeader("Referer", "https://accounts.google.com/ServiceLoginAuth");
//	      post.setHeader("Content-Type", "application/x-www-form-urlencoded");

	        post.setEntity(new UrlEncodedFormEntity(postParams));
	        
	        if (bodyContent != null) {
	            HttpEntity entity = new ByteArrayEntity(
	                bodyContent);
	            post.setEntity(entity);
	        }

	        HttpResponse response = client.execute(post);

	        int responseCode = response.getStatusLine().getStatusCode();

	        LOG.debug("\nSending 'POST' request to URL : " + url);
	        LOG.debug("Post parameters : " + postParams);
	        LOG.debug("Response Code : " + responseCode);
	        
	        if (response.containsHeader("Content-Disposition")) {
	            
	            // Save server's public key file
	            LOG.debug("Saving server's public key file...");
	            InputStream is = response.getEntity().getContent();
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            if (is != null) {
	    
	                FileOutputStream fos = null;
	                BufferedOutputStream bos = null;
	                byte[] aByte = new byte[1];
	                int bytesRead;
	                try {
	                    fos = new FileOutputStream(
	                        "/etc/opt/secure-client/server-public-key_2048.der");
	                    bos = new BufferedOutputStream(fos);
	                    bytesRead = is.read(aByte, 0, aByte.length);
	    
	                    do {
	                        baos.write(aByte);
	                        bytesRead = is.read(aByte);
	                    } while (bytesRead != -1);
	    
	                    bos.write(baos.toByteArray());
	                    bos.flush();
	                } catch (IOException e) {
	                    // Do exception handling
	                    LOG.debug("Unable to save server's public key file received " + 
	                        "by the server.", e);
	                } finally {
	                    SecureClientUtil.safeClose(bos);
	                    SecureClientUtil.safeClose(baos);
	                    SecureClientUtil.safeClose(is);
	                    SecureClientUtil.safeClose(fos);
	                }
	            }
	            
	            return "serverPublicKeyFile:/etc/opt/secure-client/server-public-key_2048.der";
	        }
	        
	        // Get the response text from the server
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
