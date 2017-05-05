package org.komparator.security;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtil {

    public static final String ASYM_ALGORITHM = "RSA/ECB/PKCS1Padding";
    private static final String ASYM_KEY = "RSA";
    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final int ASYM_KEY_SIZE = 2048;

    public String generateRandom() throws NoSuchAlgorithmException {
	SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	final byte array[] = new byte[32];
	random.nextBytes(array);
	return printHexBinary(array);
    }

    public KeyPair generateAsymKey() throws NoSuchAlgorithmException {
	KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_KEY);
	keyGen.initialize(ASYM_KEY_SIZE);
	KeyPair key = keyGen.generateKeyPair();
	return key;
    }

    public static byte[] cipher(int mode, Key key, byte[] bytes) throws NoSuchAlgorithmException,
	    NoSuchPaddingException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException {

	IvParameterSpec ips = new IvParameterSpec(new byte[16]);
	// Cipher.DECRYPT_MODE Cipher.ENCRYPT_MODE
	Cipher cipher = Cipher.getInstance(ASYM_ALGORITHM);
	cipher.init(mode, key);
	byte[] cipherBytes = cipher.doFinal(bytes);
	return cipherBytes;
    }

    public static byte[] getDigest(byte[] bytesToDigest) throws NoSuchAlgorithmException {
	MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
	messageDigest.update(bytesToDigest);
	byte[] digest = messageDigest.digest();
	return digest;
    }

    public boolean validateDigest(byte[] bytesToDigest, byte[] bytesToCompare) throws NoSuchAlgorithmException {
	return printHexBinary(getDigest(bytesToDigest)).equals(printHexBinary(bytesToCompare));
    }

    public void writeFile(String path, byte[] content) throws FileNotFoundException, IOException {
	FileOutputStream fos = new FileOutputStream(path);
	fos.write(content);
	fos.close();
    }

    public byte[] readFile(String path) throws FileNotFoundException, IOException {
	FileInputStream fis = new FileInputStream(path);
	byte[] content = new byte[fis.available()];
	fis.read(content);
	fis.close();
	return content;
    }

}
