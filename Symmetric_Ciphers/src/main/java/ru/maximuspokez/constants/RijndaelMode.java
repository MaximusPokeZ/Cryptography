package ru.maximuspokez.constants;

public enum RijndaelMode {
  AES_128(4, 4),
  AES_192(4, 6),
  AES_256(4, 8),
  RIJNDAEL_192_BLOCK(6, 4),
  RIJNDAEL_256_BLOCK(8, 4),
  FULL_256_256(8, 8);

  private final int nb;
  private final int nk;
  private final int nr;

  RijndaelMode(int nb, int nk) {
    this.nb = nb;
    this.nk = nk;
    this.nr = Math.max(nb, nk) + 6;
  }

  public int getNb() {
    return nb;
  }

  public int getNk() {
    return nk;
  }

  public int getNr() {
    return nr;
  }
}

