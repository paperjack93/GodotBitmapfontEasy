package com.dashur.integration.commons.utils;

import com.dashur.integration.commons.exception.ApplicationException;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/** A simple common encryption utilities */
@Slf4j
public class EncryptionUtils {

  private static final HashFunction secureHashFunc = Hashing.sha256();
  private static final HashFunction fastHashFunc = Hashing.murmur3_128();
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final int hashTimeWarningMs = 50;

  public static String secureHash(String str) {
    long start = System.currentTimeMillis();
    String hash = secureHashFunc.hashString(str, StandardCharsets.UTF_8).toString();
    long elapsed = System.currentTimeMillis() - start;
    // if the hashing take more than hashTimeWarningMs ms, put a warning logging
    if (elapsed > hashTimeWarningMs) {
      log.warn("secure hash of {} takes {} ms", str, elapsed);
    }
    return hash;
  }

  public static String fastHash(String str) {
    long start = System.currentTimeMillis();
    String hash = fastHashFunc.hashString(str, StandardCharsets.UTF_8).toString();
    long elapsed = System.currentTimeMillis() - start;
    // if the hashing take more than hashTimeWarningMs ms, put a warning logging
    if (elapsed > hashTimeWarningMs) {
      log.warn("fast hash of {} takes {} ms", str, elapsed);
    }
    return hash;
  }

  public static String randomHash(int size) {
    long start = System.currentTimeMillis();
    byte[] bytes = new byte[size];
    secureRandom.nextBytes(bytes);
    String hash = DigestUtils.md5Hex(bytes);
    long elapsed = System.currentTimeMillis() - start;
    // if the hashing take more than hashTimeWarningMs ms, put a warning logging
    if (elapsed > hashTimeWarningMs) {
      log.warn("random hash of [{}] takes {} ms", size, elapsed);
    }
    return hash;
  }

  public static PrivateKey getPrivateKey(URI uri) throws Exception {
    byte[] keyBytes = Files.readAllBytes(Paths.get(uri));
    String keyContent =
        new String(keyBytes)
            .replaceAll("\\n", "")
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "");
    keyBytes = Base64.getDecoder().decode(keyContent);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(spec);
  }

  public static PublicKey getPublicKey(URI uri) throws Exception {
    byte[] keyBytes = Files.readAllBytes(Paths.get(uri));
    String keyContent =
        new String(keyBytes)
            .replaceAll("\\n", "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "");
    keyBytes = Base64.getDecoder().decode(keyContent);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(spec);
  }

  public static String sign(String message, PrivateKey key) {
    try {
      Signature privateSignature = Signature.getInstance("SHA256withRSA");
      privateSignature.initSign(key);
      privateSignature.update(message.getBytes(StandardCharsets.UTF_8));
      byte[] signature = privateSignature.sign();
      return Base64.getEncoder().encodeToString(signature);
    } catch (Exception e) {
      throw new ApplicationException("Unable to sign message. %s", e.getMessage());
    }
  }

  public static boolean verify(String message, String signature, PublicKey key) {
    try {
      Signature publicSignature = Signature.getInstance("SHA256withRSA");
      publicSignature.initVerify(key);
      publicSignature.update(message.getBytes(StandardCharsets.UTF_8));
      byte[] signatureBytes = Base64.getDecoder().decode(signature);
      return publicSignature.verify(signatureBytes);
    } catch (Exception e) {
      log.error("Error verifying message: {}, signature: {}", message, signature);
      throw new ApplicationException("Unable to verify message. %s", e.getMessage());
    }
  }
}
