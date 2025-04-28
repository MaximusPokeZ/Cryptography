package ru.maximuspokez.feistel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.constants.DesConstants;
import ru.maximuspokez.crypto.PermuteBits;
import ru.maximuspokez.interfaces.EncryptionTransformation;

import ru.maximuspokez.utils.HexUtil;

import java.util.Arrays;

public class Feistel {

  private static final Logger log = LoggerFactory.getLogger(Feistel.class);

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

    byte[] permutedMessage = PermuteBits.permute(input, DesConstants.IP, false, true);
    log.info("Message after IP: {}", permutedMessage);

    int halfSize = permutedMessage.length / 2;
    byte[] leftSide = Arrays.copyOfRange(permutedMessage, 0, halfSize);
    byte[] rightSide = Arrays.copyOfRange(permutedMessage, halfSize, input.length);

    for (int i = 0; i < roundCount; i++) {
      int roundIndex = reverseRounds ? roundCount - 1 - i : i;
      byte[] fResult = roundFunction.transform(rightSide, roundKeys[roundIndex]);
      byte[] newRight = xor(leftSide, fResult);
      leftSide = rightSide;
      rightSide = newRight;
    }

    byte[] result = new byte[input.length];

    // swap
    System.arraycopy(rightSide, 0, result, 0, halfSize);
    System.arraycopy(leftSide, 0, result, halfSize, halfSize);

    return PermuteBits.permute(result, DesConstants.IP_INV, false, true);
  }

  private byte[] xor(byte[] a, byte[] b) {
    if (a.length != b.length) {
      throw new IllegalArgumentException("Input length must be equal to output length.");
    }
    byte[] result = new byte[b.length];
    for (int i = 0; i < b.length; i++) {
      result[i] = (byte) (a[i] ^ b[i]);
    }
    return result;
  }
}

