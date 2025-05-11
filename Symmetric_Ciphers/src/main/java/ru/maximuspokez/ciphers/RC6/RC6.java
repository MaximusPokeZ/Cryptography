package ru.maximuspokez.ciphers.RC6;

import ru.maximuspokez.config.RC6.Rc6Configuration;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;

public class RC6 implements SymmetricCipher {

  private byte[][] roundKeys = null;
  private final KeyExpansion keyExpansion;
  private final Rc6Configuration config;

  public RC6(Rc6Configuration config) {
    this.config = config;
    this.keyExpansion = new RC6KeyExpansion(config);
  }

  @Override
  public void setSymmetricKey(byte[] symmetricKey) {
    if (symmetricKey.length != config.getB()) {
      throw new IllegalArgumentException("Symmetric key length must be " + config.getB() + " bytes");
    }
    this.roundKeys = keyExpansion.generateRoundKeys(symmetricKey);
  }

  @Override
  public byte[] encrypt(byte[] message) {
    if (message.length != getBlockSize()) {
      throw new IllegalArgumentException("Message length must be " + getBlockSize() + " bytes");
    }

    int A = bytesToWord(message, 0);
    int B = bytesToWord(message, 4);
    int C = bytesToWord(message, 8);
    int D = bytesToWord(message, 12);

    B += bytesToWord(roundKeys[0], 0);
    D += bytesToWord(roundKeys[1], 0);

    for (int i = 0; i < config.getR(); i++) {
      int t = Integer.rotateLeft(B * (2 * B + 1), config.getLogW());
      int u = Integer.rotateLeft(D * (2 * D + 1), config.getLogW());

      A = Integer.rotateLeft(A ^ t, u) + bytesToWord(roundKeys[2 * i], 0);
      C = Integer.rotateLeft(C ^ u, t) + bytesToWord(roundKeys[2 * i + 1], 0);

      int temp = A; A = B; B = C; C = D; D = temp;
    }

    A += bytesToWord(roundKeys[2 * config.getR() + 2], 0);
    C += bytesToWord(roundKeys[2 * config.getR() + 3], 0);

    byte[] result = new byte[getBlockSize()];
    wordToBytes(A, result, 0);
    wordToBytes(B, result, 4);
    wordToBytes(C, result, 8);
    wordToBytes(D, result, 12);

    return result;
  }

  @Override
  public byte[] decrypt(byte[] ciphertext) {
    if (ciphertext.length != getBlockSize()) {
      throw new IllegalArgumentException("Ciphertext length must be " + getBlockSize() + " bytes");
    }

    int A = bytesToWord(ciphertext, 0);
    int B = bytesToWord(ciphertext, 4);
    int C = bytesToWord(ciphertext, 8);
    int D = bytesToWord(ciphertext, 12);

    C -= bytesToWord(roundKeys[2 * config.getR() + 3], 0);
    A -= bytesToWord(roundKeys[2 * config.getR() + 2], 0);

    for (int i = config.getR() - 1; i >= 0; i--) {
      int tmp = D; D = C; C = B; B = A; A = tmp;

      int t = Integer.rotateLeft(B * (2 * B + 1), config.getLogW());
      int u = Integer.rotateLeft(D * (2 * D + 1), config.getLogW());

      C = Integer.rotateRight(C - bytesToWord(roundKeys[2 * i + 1], 0), t) ^ u;
      A = Integer.rotateRight(A - bytesToWord(roundKeys[2 * i], 0), u) ^ t;
    }

    D -= bytesToWord(roundKeys[1], 0);
    B -= bytesToWord(roundKeys[0], 0);

    byte[] result = new byte[getBlockSize()];
    wordToBytes(A, result, 0);
    wordToBytes(B, result, 4);
    wordToBytes(C, result, 8);
    wordToBytes(D, result, 12);

    return result;
  }

  private int bytesToWord(byte[] message, int i) {
    return (message[i] & 0xFF) | ((message[i + 1] & 0xFF) << 8) | ((message[i + 2] & 0xFF) << 16) | ((message[i + 3] & 0xFF) << 24);
  }

  private void wordToBytes(int word, byte[] out, int i) {
    out[i] = (byte) word;
    out[i + 1] = (byte) ((word >>> 8) & 0xFF);
    out[i + 2] = (byte) ((word >>> 16) & 0xFF);
    out[i + 3] = (byte) ((word >>> 24) & 0xFF);
  }

  @Override
  public int getBlockSize() {
    return 16;
  }
}
