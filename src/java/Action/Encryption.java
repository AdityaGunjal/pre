package Action;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Encryption {

    public String encrypt(String text, SecretKey secretkey) {
        String plainData = null, cipherText = null;
        try {
            plainData = text;

            Cipher aesCipher = Cipher.getInstance("AES"); // getting AES instance
            aesCipher.init(Cipher.ENCRYPT_MODE, secretkey); // initiating cipher encryption using secretkey

            byte[] byteDataToEncrypt = plainData.getBytes();
            byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt); // encrypting data 

            cipherText = Base64.getEncoder().encodeToString(byteCipherText); // converting encrypted data to string 

            System.out.println("\n Given text : " + plainData + " \n Cipher Data : " + cipherText);

        } catch (Exception e) {
            System.out.println(e);
        }
        return cipherText;
    }
}
