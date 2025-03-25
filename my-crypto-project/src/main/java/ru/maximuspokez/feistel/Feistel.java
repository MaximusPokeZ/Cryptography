package ru.maximuspokez.feistel;

import ru.maximuspokez.interfaces.EncryptionTransformation;

import java.util.Arrays;

public class Feistel {
  private final EncryptionTransformation roundFunction;
  private final int roundCount;

  public Feistel(EncryptionTransformation roundFunction, int roundCount) {
    this.roundFunction = roundFunction;
    this.roundCount = roundCount;
  }

  public byte[] encrypt(byte[] message, byte[][] roundKeys) {
    return processFeistel(message, roundKeys, false);
  }

  public byte[] decrypt(byte[] ciphertext, byte[][] roundKeys) {
    return processFeistel(ciphertext, roundKeys, true);
  }

  private byte[] processFeistel(byte[] input, byte[][] roundKeys, boolean reverseRounds) {
    if (input.length % 2 != 0) {
      throw new IllegalArgumentException("Input length must be even.");
    }

    int halfSize = input.length / 2;
    byte[] leftSide = Arrays.copyOfRange(input, 0, halfSize);
    byte[] rightSide = Arrays.copyOfRange(input, halfSize, input.length);

    for (int i = 0; i < roundCount; i++) {
      int roundIndex = reverseRounds ? roundCount - 1 - i : i;
      byte[] fResult = roundFunction.transform(rightSide, roundKeys[roundIndex]);
      byte[] newRight = xor(leftSide, fResult);
      leftSide = rightSide;
      rightSide = newRight;
    }

    byte[] result = new byte[input.length];
    System.arraycopy(leftSide, 0, result, 0, halfSize);
    System.arraycopy(rightSide, 0, result, halfSize, halfSize);
    return result;
  }

  private byte[] xor(byte[] a, byte[] b) {
    byte[] result = new byte[b.length];
    for (int i = 0; i < b.length; i++) {
      result[i] = (byte) (a[i] ^ b[i]);
    }
    return result;
  }
}
