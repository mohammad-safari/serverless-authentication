package ir.aut.ce.cloud.serverlessauthentication.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.SneakyThrows;

@Configuration
public class Encryption {
    @Value("${encryption.secret}")
    private String SECRET_KEY;
    @Value("${encryption.salt}")
    private String SALT;
    private SecretKey secretKey;

    @SneakyThrows
    public String encrypt(String inputString) {
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        var spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
        secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

        var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        var encryptedBytes = cipher.doFinal(inputString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @SneakyThrows
    public String decrypt(String encryptedString) {

        var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        var encryptedBytes = Base64.getDecoder().decode(encryptedString);
        var decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);

    }
}