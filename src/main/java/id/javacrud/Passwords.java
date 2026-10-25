package id.javacrud;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Passwords {
  private Passwords() {}

  public static String hash(String password) {
    Validation.password(password);
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 600_000, 256);
    try {
      byte[] result =
          SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
      return "pbkdf2-sha256$600000$"
          + Base64.getEncoder().encodeToString(salt)
          + "$"
          + Base64.getEncoder().encodeToString(result);
    } catch (java.security.GeneralSecurityException error) {
      throw new IllegalStateException("Hash password tidak tersedia.", error);
    } finally {
      spec.clearPassword();
    }
  }
}
