/*
授权声明：
本源码系《Java多线程编程实战指南（核心篇）》一书（ISBN：978-7-121-31065-2，以下称之为“原书”）的配套源码，
欲了解本代码的更多细节，请参考原书。
本代码仅为原书的配套说明之用，并不附带任何承诺（如质量保证和收益）。
以任何形式将本代码之部分或者全部用于营利性用途需经版权人书面同意。
将本代码之部分或者全部用于非营利性用途需要在代码中保留本声明。
任何对本代码的修改需在代码中以注释的形式注明修改人、修改时间以及修改内容。
本代码可以从以下网址下载：
https://github.com/Viscent/javamtia
http://www.broadview.com.cn/31065
*/
package io.github.viscent.mtia.util;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.net.util.Base64;

public class DESEncryption {
  public static class CryptoException extends Exception {
    private static final long serialVersionUID = 1L;

    public CryptoException(Exception cause) {
      super(cause);
    }
  }

  /**
   * @param args
   * @throws CryptoException
   */
  public static void main(String[] args) throws CryptoException {
    String content = "secret content中文";
    // 密码长度必须是8的倍数
    String password = "12345678";
    System.out.println("密钥：" + password);
    System.out.println("加密前：" + content);
    String result = encryptAsString(content, password);
    System.out.println("加密后：" + result);
    String decryResult = decryptString(result, password);
    System.out.println("解密后：" + decryResult);
  }

  /**
   * 加密
   *
   * @param content
   *          待加密内容
   * @param key
   *          加密的密钥
   * @return
   * @throws CryptoException
   */
  public static byte[] encrypt(String content, String key)
      throws CryptoException {
    byte[] result = null;
    try {
      DESKeySpec desKey = new DESKeySpec(key.getBytes());
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      SecretKey secretKey = keyFactory.generateSecret(desKey);
      Cipher cipher = Cipher.getInstance("DES");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey);
      result = cipher.doFinal(content.getBytes());
    } catch (Exception e) {
      throw new CryptoException(e);
    }
    return result;
  }

  public static String encryptAsString(String content, String key)
      throws CryptoException {
    byte[] encryptedBytes = encrypt(content, key);
    byte[] bytesEncoded = new Base64().encode(encryptedBytes);
    try {
      return new String(bytesEncoded, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new CryptoException(e);
    }
  }
  /**
   * 解密
   *
   * @param content
   *          待解密内容
   * @param key
   *          解密的密钥
   * @return
   */
  public static String decrypt(byte[] content, String key)
      throws CryptoException {
    try {
      DESKeySpec desKey = new DESKeySpec(key.getBytes());
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      SecretKey secretKey = keyFactory.generateSecret(desKey);
      Cipher cipher = Cipher.getInstance("DES");
      cipher.init(Cipher.DECRYPT_MODE, secretKey);
      byte[] result = cipher.doFinal(content);
      return new String(result, "UTF-8");
    } catch (Exception e) {
      throw new CryptoException(e);
    }
  }

  public static String decryptString(String content, String key)
      throws CryptoException {
    byte[] bytesDecoded;
    try {
      bytesDecoded = new Base64().decode(content.getBytes("UTF-8"));
      return decrypt(bytesDecoded, key);
    } catch (UnsupportedEncodingException e) {
      throw new CryptoException(e);
    }
  }
}