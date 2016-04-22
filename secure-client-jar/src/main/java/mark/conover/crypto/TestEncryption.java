package mark.conover.crypto;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class TestEncryption {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			FileEncryption fileEncryption = new FileEncryption();
			fileEncryption.makeKey();
			
			// Encrypt AES key file w/ RSA public key
			File encryptedKeyFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\encrypted-file.key");
			File publicKeyFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\public.der");
			fileEncryption.saveKey(encryptedKeyFile, publicKeyFile);
			
			// Decrypt AES key file w/ RSA private key
			File unencryptedFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\unencrypted-file.key");
			File privateKeyFile = new File("C:\\Users\\Mark-Desktop\\git\\secure-client-and-server\\secure-client-jar\\src\\main\\resources\\rsa-keys\\private.der");
			fileEncryption.loadKey(encryptedKeyFile, privateKeyFile);
			fileEncryption.decrypt(encryptedKeyFile, unencryptedFile);
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Done!");
		
//		To use the code, you need corresponding public and private RSA keys. RSA keys can be generated using the open source tool OpenSSL. However, you have to be careful to generate them in the format required by the Java encryption libraries. To generate a private key of length 2048 bits:
//
//			openssl genrsa -out private.pem 2048
//			To get it into the required (PKCS#8, DER) format:
//
//			openssl pkcs8 -topk8 -in private.pem -outform DER -out private.der -nocrypt
//			To generate a public key from the private key:
//
//			openssl rsa -in private.pem -pubout -outform DER -out public.der
//			An example of how to use the code:
//
//			FileEncryption secure = new FileEncryption();
//
//			// to encrypt a file
//			secure.makeKey();
//			secure.saveKey(encryptedKeyFile, publicKeyFile);
//			secure.encrypt(fileToEncrypt, encryptedFile);
//
//			// to decrypt it again
//			secure.loadKey(encryptedKeyFile, privateKeyFile);
//			secure.decrypt(encryptedFile, unencryptedFile);
		
	}

}
