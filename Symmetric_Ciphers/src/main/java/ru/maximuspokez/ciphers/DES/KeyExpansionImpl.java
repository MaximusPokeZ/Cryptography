package ru.maximuspokez.ciphers.DES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.crypto.PermuteBits;
import ru.maximuspokez.constants.DES.DesConstants;
import ru.maximuspokez.interfaces.KeyExpansion;

import static ru.maximuspokez.utils.HexUtil.bytesToHex;


public class KeyExpansionImpl implements KeyExpansion {

  private static final Logger log = LoggerFactory.getLogger(KeyExpansionImpl.class);

  @Override
  public byte[][] generateRoundKeys(byte[] key) {
    if (key.length != 8) {
      throw new IllegalArgumentException("Key must be 8 bytes (64 bits)");
    }

    log.info("Generating round keys");
    byte[] permutedKey = PermuteBits.permute(key, DesConstants.PC1, false, true); // 64 --> 56
    log.info("Key after PC1: {}", bytesToHex(key));

    int C = 0, D;

    for (int i = 0; i < 3; i++) {
      C = (C << 8) | (permutedKey[i] & 0xFF);
    }

    C = (C << 4) | ((permutedKey[3] & 0xF0) >> 4);

    D = (permutedKey[3] & 0x0F);
    for (int i = 4; i < 7; i++) {
      D = (D << 8) | (permutedKey[i] & 0xFF);
    }

    byte[][] roundKeys = new byte[16][];

    for (int i = 0; i < 16; i++) {
      int shift = DesConstants.SHIFT_SCHEDULE[i];
      C = leftShift28(C, shift);
      D = leftShift28(D, shift);

      long combined = (((long) C) << 28) | (D & 0x0FFFFFFF);
      byte[] combinedKey = new byte[7];
      for (int j = 0; j < 7; j++) {
        combinedKey[j] = (byte) ((combined >> ((6 - j) * 8)) & 0xFF);
      }

      roundKeys[i] = PermuteBits.permute(combinedKey, DesConstants.PC2, false, true); // 56 --> 48

      log.info("Key{}: {}",i + 1, bytesToHex(roundKeys[i]));
    }

    return roundKeys;
  }

  private int leftShift28(int value, int shift) {
    return ((value << shift) | (value >> (28 - shift))) & 0x0FFFFFFF;
  }
}
