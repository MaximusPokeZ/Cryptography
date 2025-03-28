package ru.maximuspokez.utils;

public class HexUtil {
  private HexUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String bytesToHex(byte[] bytes) {
    StringBuilder hexString = new StringBuilder();
    for (byte b : bytes) {
      hexString.append(String.format("%02X ", b));
    }
    return hexString.toString().trim();
  }

  public static String bytesToBinary(byte[] bytes) {
    StringBuilder bString = new StringBuilder();
    for (byte b : bytes) {
      bString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0')).append(" ");
    }
    return bString.toString().trim();
  }

}
