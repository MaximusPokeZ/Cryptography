package ru.maximuspokez.ciphers.RC6;

import ru.maximuspokez.config.RC6.Rc6Configuration;
import ru.maximuspokez.interfaces.KeyExpansion;

public class RC6KeyExpansion implements KeyExpansion {

  private final Rc6Configuration config;

  public RC6KeyExpansion(Rc6Configuration config) {
    this.config = config;
  }

  @Override
  public byte[][] generateRoundKeys(byte[] key) {
    int w = config.getW();
    int u = w / 8;
    int c = Math.max(1, key.length / u);
    int[] L = new int[c];

    for (int i = 0; i < key.length; i++) {
      L[i / u] |= (key[i] & 0xFF) << (8 * (i % u));
    }

    int lenS = config.getKeyLength();
    int[] S = new int[lenS];
    S[0] = config.getP();
    for (int i = 1; i < lenS; i++) {
      S[i] = S[i - 1] + config.getQ();
    }

    int A = 0, B = 0, i = 0, j = 0;
    int iter = 3 * Math.max(lenS, c);

    for (int k = 0; k < iter; k++) {
      A = S[i] = Integer.rotateLeft((S[i] + A + B), 3);
      B = L[j] = Integer.rotateLeft((L[j] + A + B), (A + B) & 0x1F);
      i = (i + 1) % lenS;
      j = (j + 1) % c;
    }

    byte[][] roundKeys = new byte[lenS][w];
    for (int k = 0; k < lenS; k++) {
      roundKeys[k][0] = (byte) (S[k]);
      roundKeys[k][1] = (byte) (S[k] >>> 8);
      roundKeys[k][2] = (byte) (S[k] >>> 16);
      roundKeys[k][3] = (byte) (S[k] >>> 24);
    }

    return roundKeys;
  }
}
