package ru.maximuspokez.DES;

import ru.maximuspokez.crypto.PermuteBits;
import ru.maximuspokez.constants.DesConstants;

import ru.maximuspokez.interfaces.KeyExpansion;

public class KeyExpansionImpl implements KeyExpansion {

  @Override
  public byte[][] generateRoundKeys(byte[] key) {
    if (key.length != 8) {
      throw new IllegalArgumentException("Key must be 8 bytes (64 bits)");
    }

    byte[] permutedKey = PermuteBits.permute(key, DesConstants.PC1, false, false); // Получили 56 бит

    // делим на 2 по 28 бит
    byte[] C = new byte[4];
    byte[] D = new byte[4];

    for (int i = 0; i < 4; i++) {
      C[i] = permutedKey[i];
      D[i] = permutedKey[i + 4];
    }

    byte[][] roundKeys = new byte[16][];

    for (int i = 0; i < 16; i++) {
      C = leftShift(C, DesConstants.SHIFT_SCHEDULE[i]);
      D = leftShift(D, DesConstants.SHIFT_SCHEDULE[i]);

      byte[] combinedKey = new byte[7];

      System.arraycopy(C, 0, combinedKey, 0, 3); // Первые 24 бита - ок
      combinedKey[3] = (byte)((C[3] & 0xF0) | ((D[0] & 0xF0) >> 4)); // граничный случай (4 бита из C и 4 бита из D)
      System.arraycopy(C, 0, combinedKey, 0, 3); // Последние 24 бита - ок

      roundKeys[i] = PermuteBits.permute(combinedKey, DesConstants.PC2, false, false); // 48
    }

    return roundKeys;
  }

  private byte[] leftShift(byte[] input, int shift) {
    int len = input.length * 8;
    byte[] result = new byte[input.length];

    for (int i = 0; i < len; i++) {
      int currBitIndx = (i + shift) % len;
      int currByteIndx = currBitIndx / 8;
      int currBit = (input[currByteIndx] >> (7 - currBitIndx)) & 1;

      result[i / 8] |= (byte) (currBit << (7 - (i % 8)));
    }
    return result;
  }

}
