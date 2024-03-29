//https://stackoverflow.com/questions/39355241/compute-hmac-sha512-with-secret-key-in-java


public class HMAC {
	

public static String bytesToHex(byte[] bytes) {
    final  char[] hexArray = "0123456789ABCDEF".toCharArray();
    char[] hexChars = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hexChars[j * 2] = hexArray[v >>> 4];
        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
}


/*
public static void main(String[] args) {//just a sample how to used it not used in this project
    Mac sha512_HMAC = null;
    String result = null;
    String key =  "Welcome1";

    try{
        byte [] byteKey = key.getBytes("UTF-8");
        final String HMAC_SHA512 = "HmacSHA512";
        sha512_HMAC = Mac.getInstance(HMAC_SHA512);      
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
        sha512_HMAC.init(keySpec);
        byte [] mac_data = sha512_HMAC.
         doFinal("My message".getBytes("UTF-8"));
        //result = Base64.encode(mac_data);
        result = bytesToHex(mac_data);
        System.out.println("HMAC "+result);
    } catch (UnsupportedEncodingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (InvalidKeyException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }finally{
        System.out.println("Done");
    }
}
*/


}