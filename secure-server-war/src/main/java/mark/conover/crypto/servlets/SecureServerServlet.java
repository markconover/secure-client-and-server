package mark.conover.crypto.servlets;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mark.conover.crypto.AES128;
import mark.conover.crypto.FileEncryption2;
import mark.conover.crypto.config.SecureServerConfig;
import mark.conover.crypto.util.SecureServerUtil;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecureServerServlet extends HttpServlet {
	private static final long serialVersionUID = -7682825864889092242L;

	private String message = null;
	
	private static final Logger LOG = LoggerFactory.getLogger(
		SecureServerServlet.class);

	public void init() throws ServletException {
		// Do required initialization
		message = "Hello World";
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set response content type
		response.setContentType("text/html");

		// Actual logic goes here.
		PrintWriter out = response.getWriter();
		out.println("<h1>" + message + "</h1>"); 
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		LOG.debug("doPost() method has been called.");
		String clientPublicKey = req.getParameter("publicKey");
		String aesParam = req.getParameter("aesSymmetricKeyIncluded");
		String aesEncryptionEnabled = req.getParameter("aesEncryptionEnabled");
		
		LOG.debug("Client's publicKey is: " + clientPublicKey);	
		
		
		
		
		
		
		
		
		
		
		
		
		// Determine if the client sent over the AES symmetric key
		InputStream is = req.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] encryptedBytes = null;
        if (is != null) {

            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            byte[] aByte = new byte[1];
            int bytesRead;
            try {
                fos = new FileOutputStream(
                    "/etc/opt/secure-server/aes-encrypted-bytes.bin");
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(aByte, 0, aByte.length);

                do {
                    baos.write(aByte);
                    bytesRead = is.read(aByte);
                } while (bytesRead != -1);

                encryptedBytes = baos.toByteArray();
                bos.write(baos.toByteArray());
                bos.flush();
            } catch (IOException e) {
                // Do exception handling
                LOG.debug("Unable to read encrypted aes key bytes from client's post request.", e);
            } finally {
                SecureServerUtil.safeClose(bos);
                SecureServerUtil.safeClose(baos);
                SecureServerUtil.safeClose(is);
                SecureServerUtil.safeClose(fos);
            }
        }
//		StringBuilder stringBuilder = new StringBuilder();
//		BufferedReader bufferedReader = null;
//		try {
//			InputStream inputStream = req.getInputStream();
//			if (inputStream != null) {
//				bufferedReader = new BufferedReader(new InputStreamReader(
//						inputStream));
//				char[] charBuffer = new char[128];
//				int bytesRead = -1;
//				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//					stringBuilder.append(charBuffer, 0, bytesRead);
//				}
//			} else {
//				stringBuilder.append("");
//			}
//		} catch (IOException e) {
//			LOG.error("Unable to read doPost body content.", e);
//		} finally {
//			SecureServerUtil.safeClose(bufferedReader);
//		}
//		String body = stringBuilder.toString();
		
		
		
		
		
		
		
		
		
		
		
		
		
		//LOG.debug("Received the following doPost body content: {}", body);
		
		if (aesParam != null && aesParam.equals("yes")) {
			
//			LOG.debug("Encrypted AES Symmetric Key from client is: '{}'", 
//				body);
//			byte[] encryptedAesKeyBytes = body.getBytes(
//				Charsets.UTF_8);
			
			PrivateKey serverPrivateKey = null;
			try {
				serverPrivateKey = FileEncryption2.readPrivateKey(
				SecureServerConfig.SECURE_SERVER_PRIVATE_KEY_FILE_PATH);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				LOG.error("Unable to read server's private key from file " +
					"path '{}'", 
					SecureServerConfig.SECURE_SERVER_PRIVATE_KEY_FILE_PATH, e);
			}
			
			byte[] aesKeyBytes = null;
			try {
				aesKeyBytes = FileEncryption2.decrypt(serverPrivateKey, 
					encryptedBytes);
			} catch (InvalidKeyException | NoSuchAlgorithmException
					| NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {

				LOG.error("Unable to decrypt the encrypted AES key using the " +
					"server's private key.", e);
			}

			String aesKey = null;
			if (aesKeyBytes != null) {
				aesKey = new String(aesKeyBytes, Charsets.UTF_8);
				
				LOG.debug("The AES Symmetric key from client is: '{}'", aesKey);
			}
			
			if (aesKey != null) {
				// Tell the client the AES symmetric key was successfully 
				// received
		        BufferedOutputStream bos = new BufferedOutputStream(
		                resp.getOutputStream());
		        IOUtils.write("Got the AES symmetric key.", bos, 
		            Charsets.UTF_8);
		        bos.flush();
		        
		        SecureServerUtil.safeClose(bos);
			}

		} else if (aesEncryptionEnabled != null 
	        && aesEncryptionEnabled.equals("yes")) {
		          
            // Start sending messages to client but encrypting them first
            // with the AES symmetric key
		    String decryptedText = null;
		    try {
		        decryptedText = 
	                AES128.decrypt(new String(encryptedBytes, 
                        Charsets.UTF_8));
            } catch (Exception e) {
                LOG.error("Unable to decrypt the encrypted aes text.", e);
            }
		    
		    if (decryptedText != null) {
		        LOG.debug("The AES decrypted text sent by client is: '{}'", 
	                decryptedText);
		        
                // Send client the answer to the client's question "What up?"
                BufferedOutputStream bos = new BufferedOutputStream(
                        resp.getOutputStream());
                String aesEncryptedText = null;
                try {
                    aesEncryptedText = AES128.encrypt(
                        "Nothing.  Just Chillin.  Hbu?");
                } catch (Exception e) {
                    LOG.error("Unable to encrypt plaintext using AES.", e);
                }
                
                if (aesEncryptedText != null) {
                    IOUtils.write(aesEncryptedText, bos, 
                        Charsets.UTF_8);
                    bos.flush();
                    
                    SecureServerUtil.safeClose(bos);
                }
		    }
		} else {
		
			// Send the server's public key file back
	        resp.setHeader("Content-Disposition", 
	        		"attachment; filename=\"server-public-key_2048.der\""); 
			File serverPublicKeyFile = new File(
				SecureServerConfig.SECURE_SERVER_PUBLIC_KEY_FILE_PATH);
			FileInputStream fis = new FileInputStream(serverPublicKeyFile);
	        BufferedOutputStream bos = new BufferedOutputStream(
	                resp.getOutputStream());
	        
	        try {
		        IOUtils.copy(fis, bos);
	//	        IOUtils.write("server public key: " + 
	//	            SecureServerConfig.SECURE_SERVER_PUBLIC_KEY_FILE_PATH, bos, "UTF-8");
	        } catch (IOException e) {
	        	LOG.debug("Unable to copy server's public key file to HTTP " + 
        			"response output stream.", e);
	        } finally {
		        SecureServerUtil.safeClose(fis);
		        SecureServerUtil.safeClose(bos);
	        }
		}
	}

	public void destroy() {
		// do nothing.
	}
}
