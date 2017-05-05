package org.komparator.security;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

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

    public byte[] getDigest(byte[] bytesToDigest) throws NoSuchAlgorithmException {
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

    public static PrivateKey getPrivateKeyFromKeyStoreFile(String keyStoreFilePath, char[] keyStorePassword,
	    String keyAlias, char[] keyPassword)
	    throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
	return getPrivateKeyFromKeyStoreFile(new File(keyStoreFilePath), keyStorePassword, keyAlias, keyPassword);
    }

    public static PrivateKey getPrivateKeyFromKeyStoreFile(File keyStoreFile, char[] keyStorePassword, String keyAlias,
	    char[] keyPassword) throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
	KeyStore keystore = readKeystoreFromFile(keyStoreFile, keyStorePassword);
	return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
    }

    public static PrivateKey getPrivateKeyFromKeyStoreResource(String keyStoreResourcePath, char[] keyStorePassword,
	    String keyAlias, char[] keyPassword)
	    throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
	KeyStore keystore = readKeystoreFromResource(keyStoreResourcePath, keyStorePassword);
	return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
    }

    public static KeyStore readKeystoreFromResource(String keyStoreResourcePath, char[] keyStorePassword)
	    throws KeyStoreException {
	InputStream is = getResourceAsStream(keyStoreResourcePath);
	return readKeystoreFromStream(is, keyStorePassword);
    }

    private static InputStream getResourceAsStream(String resourcePath) {
	// uses current thread's class loader to also work correctly inside
	// application servers
	// reference: http://stackoverflow.com/a/676273/129497
	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
	return is;
    }

    private static KeyStore readKeystoreFromFile(File keyStoreFile, char[] keyStorePassword)
	    throws FileNotFoundException, KeyStoreException {
	FileInputStream fis = new FileInputStream(keyStoreFile);
	return readKeystoreFromStream(fis, keyStorePassword);
    }

    private static KeyStore readKeystoreFromStream(InputStream keyStoreInputStream, char[] keyStorePassword)
	    throws KeyStoreException {
	KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
	try {
	    keystore.load(keyStoreInputStream, keyStorePassword);
	} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
	    throw new KeyStoreException("Could not load key store", e);
	} finally {
	    closeStream(keyStoreInputStream);
	}
	return keystore;
    }

    private static void closeStream(InputStream in) {
	try {
	    if (in != null)
		in.close();
	} catch (IOException e) {
	    // ignore
	}
    }

    private static PrivateKey getPrivateKeyFromKeyStore(String keyAlias, char[] keyPassword, KeyStore keystore)
	    throws KeyStoreException, UnrecoverableKeyException {
	PrivateKey key;
	try {
	    key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);
	} catch (NoSuchAlgorithmException e) {
	    throw new KeyStoreException(e);
	}
	return key;
    }

    public static PublicKey getPublicKeyFromCertificate(Certificate certificate) {
	return certificate.getPublicKey();
    }

    public static Certificate certificateStringToObject(String certificateString) {
	try {
	    byte[] bytes = certificateString.getBytes(StandardCharsets.UTF_8);
	    InputStream in = new ByteArrayInputStream(bytes);
	    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
	    Certificate cert;
	    cert = certFactory.generateCertificate(in);
	    return cert;
	} catch (CertificateException e) {
	    return null;
	}
    }

}
