/*
https://gist.github.com/ishikawa/88599/3195bdeecabeb38aa62872ab61877aefa6aef89e
Java Sample Code for Calculating HMAC-SHA1 Signatures 
shikawa/gist:88599
Created 9 years ago 
<script src="https://gist.github.com/ishikawa/88599.js"></script>


*/

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class HmacSha1Signature {
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String f=formatter.toString();
		formatter.close();
		return f;
	}

	public static String calculateRFC2104HMAC(String data, String key)
		throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
		
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		if(DGW_Main.iflog)System.out.println(signingKey.toString());
		mac.init(signingKey);
		return toHexString(mac.doFinal(data.getBytes()));
	}

	/*public static void main(String[] args) throws Exception {
		String hmac = calculateRFC2104HMAC("data", "key");

		System.out.println(hmac);
		assert hmac.equals("104152c5bfdca07bc633eebd46199f0255c9f49d");
	}*/
}