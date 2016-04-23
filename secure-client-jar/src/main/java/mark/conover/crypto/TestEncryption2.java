package mark.conover.crypto;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;

public class TestEncryption2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			// Print out AES key
//			String str = new String(fileEncryption.aesKey, StandardCharsets.UTF_8);
//			System.out.println("AES key is: " + str);
//			
			// Encrypt AES key file w/ RSA public key
			//File encryptedKeyFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\encrypted-file.key");
//			File publicKeyFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\public_1024.der");
			//File publicKeyFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\public.der");
			
			// Decrypt AES key file w/ RSA private key
			//File unencryptedFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\unencrypted-file.key");
//			File privateKeyFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\private_1024.der");
			
			PublicKey publicKey = FileEncryption2.readPublicKey("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\public.der");
			PrivateKey privateKey = 
				FileEncryption2.readPrivateKey("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\private.der");
			byte[] message = "Hello World".getBytes("UTF8");
			byte[] secret = FileEncryption2.encrypt(publicKey, message);
			byte[] recovered_message = FileEncryption2.decrypt(privateKey, 
				secret);
			System.out.println("Recovered message is: " + 
				new String(recovered_message, "UTF8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
