package code;

/**	@author amurdock	**/

import java.io.UnsupportedEncodingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.commons.codec.binary.Base64;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

/**	The encryption/decryption class to be used for saving tokens.  I recommend changing the encryption key for your own use.  This class uses code available at http://www.exampledepot.com/egs/javax.crypto/PassKey.html.  This library uses a proprietary API developed by Sun that may be removed in future releases.	**/

public class DesEncrypter
{
	Cipher ecipher;
	Cipher dcipher;
	String ENCRYPTION_STR = "USGSngtoc";

       	//8-byte Salt
       	byte[] salt = new byte[]{ (byte)0xA9, (byte)0x9B, (byte)0xC8, (byte)0x32, (byte)0x56, (byte)0x35, (byte)0xE3, (byte)0x03};

	//Iteration count
	int iterationCount = 19;

	/**
	*	Class constructor.
	*	@param passPhrase	the passphrase to be used for encryption.
	**/
	DesEncrypter(String passPhrase)
	{
		passPhrase = passPhrase + ENCRYPTION_STR;
		try
		{
			//Create the key
			KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
			ecipher = Cipher.getInstance(key.getAlgorithm());
			dcipher = Cipher.getInstance(key.getAlgorithm());

			//Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			//Create the ciphers
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		}
		catch(java.security.InvalidAlgorithmParameterException e)
		{}
		catch(java.security.spec.InvalidKeySpecException e)
		{}
		catch(javax.crypto.NoSuchPaddingException e)
		{}
		catch(java.security.NoSuchAlgorithmException e)
		{}
		catch(java.security.InvalidKeyException e)
		{}
	}

	/**
	*	Encrypts the given string then returns the encrypted value as a string.
	*	@param str	String to be encrypted.
	*	@return		The encrypted version of the input string.
	**/
	public String encrypt(String str)
	{
		try
		{
			//Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");

			//Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			//Encode the bytes to base64 to get a string
			return new Base64().encodeBase64String(enc);//sun.misc.BASE64Encoder().encode(enc);
		}
		catch(javax.crypto.BadPaddingException e)
		{}
		catch(IllegalBlockSizeException e)
		{}
		catch(UnsupportedEncodingException e)
		{}
		catch(java.io.IOException e)
		{}
		return null;
	}

	/**
	*	Decrypts a given string then returns the decrypted value as a string.
	*	@param str	String to be decrypted.
	*	@return		The decrypted version of the input string.
	**/
	public String decrypt(String str)
	{
		try
		{
			//Decode base64 to get bytes
			byte[] dec = new Base64().decodeBase64(str);//sun.misc.BASE64Decoder().decodeBuffer(str);

			//Decrypt
			byte[] utf8 = dcipher.doFinal(dec);

			//Decode using utf-8
			return new String(utf8, "UTF8");
		}
		catch(javax.crypto.BadPaddingException e)
		{}
		catch(IllegalBlockSizeException e)
		{}
		catch(UnsupportedEncodingException e)
		{}
		catch(java.io.IOException e)
		{}
		return null;
	}
}
