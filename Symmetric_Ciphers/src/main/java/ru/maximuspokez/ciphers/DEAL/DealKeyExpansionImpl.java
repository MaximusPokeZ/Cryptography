package ru.maximuspokez.ciphers.DEAL;

import ru.maximuspokez.ciphers.DES.DES;
import ru.maximuspokez.constants.DealKeySize;
import ru.maximuspokez.interfaces.KeyExpansion;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class DealKeyExpansionImpl implements KeyExpansion {
  private final DES des;
  private final DealKeySize keySize;

  public DealKeyExpansionImpl(DealKeySize keySize, DES des) {
    this.des = des;
    this.keySize = keySize;
  }

  @Override
  public byte[][] generateRoundKeys(byte[] key) {
    int numKeys = keySize.getNumKeys();
    if (key.length != numKeys * 8) {
      throw new IllegalArgumentException("Key size must be " + numKeys + " bytes");
    }
    byte[][] keys = new byte[numKeys][];
    for (int i = 0; i < numKeys; i++) {
      keys[i] = Arrays.copyOfRange(key, i * 8, (i + 1) * 8);
    }

    int numRounds = keySize.getNumRounds();
    byte[][] roundKeys = new byte[numRounds][];

    roundKeys[0] = des.encrypt(keys[0]);
    roundKeys[1] = des.encrypt(xor(keys[1], roundKeys[0]));

    for (int i = 2; i < numRounds; i++) {
      long I = 1L << (64 - (1 << (i - 2)));
      byte[] crntI = longToBytes(I);
      roundKeys[i] = des.encrypt(xor(keys[i % numKeys], xor(crntI, roundKeys[i - 1])));
    }

    return roundKeys;
  }

  private byte[] xor(byte[] a, byte[] b) {
    byte[] result = new byte[a.length];
    for (int i = 0; i < a.length; i++) {
      result[i] = (byte) (a[i] ^ b[i]);
    }
    return result;
  }

  private byte[] longToBytes(long value) {
    return ByteBuffer.allocate(8)
            .putLong(value)
            .array();
  }

}
