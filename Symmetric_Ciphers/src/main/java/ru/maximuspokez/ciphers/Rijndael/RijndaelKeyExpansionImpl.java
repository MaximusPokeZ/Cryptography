package ru.maximuspokez.ciphers.Rijndael;

import ru.maximuspokez.config.RijndaelConfiguration;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.sbox.Sbox;

public class RijndaelKeyExpansionImpl implements KeyExpansion {

  private final RijndaelConfiguration configuration;

  public RijndaelKeyExpansionImpl(RijndaelConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public byte[][] generateRoundKeys(byte[] key) {
    int Nb = configuration.getNb();
    int Nk = configuration.getNk();
    int Nr = configuration.getNr();

    int keySize = Nk * 4;
    if (keySize != key.length) {
      throw new IllegalArgumentException("Invalid key size: expected " + keySize + " but got " + key.length);
    }

    int totalWords = (Nr + 1) * Nb;
    byte[][] w = new byte[totalWords][4]; // общее кол-во слов
    for (int i = 0; i < Nk; i++) {
      System.arraycopy(key, i * 4, w[i], 0, 4);
    }

    // генерируем остальные слова
    for (int i = Nk; i < totalWords; i++) {
      byte[] temp = new byte[4];
      System.arraycopy(w[i - 1], 0, temp, 0, 4);

      if (i % Nk == 0) {
        temp = subWord(rotWord(temp));
        temp[0] ^= configuration.getRcon()[(i / Nk) - 1];
      } else if (Nk > 6 && i % Nk == 4) {
        temp = subWord(temp);
      }
      w[i] = xor(w[i - Nk], temp);
    }

    byte[][] roundKeys = new byte[Nr + 1][Nb * 4];
    for (int round = 0; round <= Nr; round++) {
      for (int column = 0; column < Nb; column++) {
        System.arraycopy(w[round * Nb + column], 0, roundKeys[round], column * 4, 4);
      }
    }

    return roundKeys;
  }

  private byte[] rotWord(byte[] word) {
    byte temp = word[0];
    word[0] = word[1];
    word[1] = word[2];
    word[2] = word[3];
    word[3] = temp;
    return word;
  }

  private byte[] subWord(byte[] word) {
    Sbox sBox = configuration.getSBox();
    byte[] result = new byte[4];
    for (int i = 0; i < 4; i++) {
      result[i] = sBox.get(word[i]);
    }
    return result;
  }

  private byte[] xor(byte[] a, byte[] b) {
    byte[] result = new byte[a.length];
    for (int i = 0; i < a.length; i++) {
      result[i] = (byte) (a[i] ^ b[i]);
    }
    return result;
  }

}
