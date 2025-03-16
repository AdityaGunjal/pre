package Action;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Decryption {

    public String decrypt(String txt, String skey) {
        String decryptedtext = null;
        try {
            // Converting string to SecretKey
            byte[] bs = Base64.getDecoder().decode(skey);
            SecretKey sec = new SecretKeySpec(bs, "AES");
            System.out.println("Converted string to secretkey:" + sec);

            System.out.println("Secret key:" + sec);

            Cipher aesCipher = Cipher.getInstance("AES");// Getting AES instance
            aesCipher.init(Cipher.DECRYPT_MODE, sec);// Initiating cipher decryption using secretkey

            byte[] byteCipherText = Base64.getDecoder().decode(txt); // Decrypting data

            aesCipher.init(Cipher.DECRYPT_MODE, sec, aesCipher.getParameters());// Initiating cipher decryption

            byte[] byteDecryptedText = aesCipher.doFinal(byteCipherText);
            decryptedtext = new String(byteDecryptedText);

            System.out.println("Decrypted Text:" + decryptedtext);
        } catch (Exception e) {
            System.out.println(e);
        }
        return decryptedtext;
    }
}
