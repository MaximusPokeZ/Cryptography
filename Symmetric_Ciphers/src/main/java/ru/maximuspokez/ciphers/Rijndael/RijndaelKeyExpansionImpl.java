package ru.maximuspokez.ciphers.Rijndael;

import ru.maximuspokez.interfaces.KeyExpansion;

public class RijndaelKeyExpansionImpl implements KeyExpansion {
  @Override
  public byte[][] generateRoundKeys(byte[] key) {
    return new byte[0][];
  }
}
