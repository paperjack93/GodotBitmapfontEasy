package com.dashur.integration.extw.connectors;

import com.dashur.integration.commons.exception.ApplicationException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtil {
  static final String ALGORITHM_SHA256 = "HmacSHA256";

  public static final String hash(final String base64Key, final String message) {
    try {
      return new String(hash(Base64.getDecoder().decode(base64Key), message.getBytes("UTF8")));
    } catch (UnsupportedEncodingException e) {
      throw new ApplicationException("Unsupported encoding [%s]", e.getMessage());
    }
  }

  private static final byte[] hash(final String base64Key, final byte[] message) {
    return hash(Base64.getDecoder().decode(base64Key), message);
  }

  private static final byte[] hash(final byte[] key, final byte[] message) {
    try {
      Mac mac = Mac.getInstance(ALGORITHM_SHA256);
      mac.init(new SecretKeySpec(key, ALGORITHM_SHA256));

      return Base64.getEncoder().encode(mac.doFinal(message));
    } catch (NoSuchAlgorithmException e) {
      throw new ApplicationException("No such algorithm [%s]", e.getMessage());
    } catch (InvalidKeyException e) {
      throw new ApplicationException("Invalid key [%s]", e.getMessage());
    }
  }
}
