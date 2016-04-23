package mark.conover.crypto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class FileEncryption2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			PublicKey publicKey = readPublicKey("public.der");
			PrivateKey privateKey = readPrivateKey("private.der");
			byte[] message = "Hello World".getBytes("UTF8");
			byte[] secret = encrypt(publicKey, message);
			byte[] recovered_message = decrypt(privateKey, secret);
			System.out.println(new String(recovered_message, "UTF8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static byte[] readFileBytes(String filename) throws IOException {
		Path path = Paths.get(filename);
		return Files.readAllBytes(path);
	}

	public static PublicKey readPublicKey(String filename) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(
				readFileBytes(filename));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(publicSpec);
	}

	public static PrivateKey readPrivateKey(String filename) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(
				readFileBytes(filename));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

	public static byte[] encrypt(PublicKey key, byte[] plaintext)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher
				.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(plaintext);
	}

	public static byte[] decrypt(PrivateKey key, byte[] ciphertext)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher
				.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(ciphertext);
	}

}
