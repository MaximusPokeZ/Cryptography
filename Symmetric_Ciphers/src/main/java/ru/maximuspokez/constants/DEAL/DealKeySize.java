package ru.maximuspokez.constants.DEAL;

public enum DealKeySize {
  KEY_128(2),
  KEY_192(3),
  KEY_256(4);

  private final int numKeys;

  DealKeySize(int numKeys) {
    this.numKeys = numKeys;
  }
  
  public int getNumKeys() {
    return numKeys;
  }

  public int getNumRounds() {
    switch (this) {
      case KEY_128:
      case KEY_192:
        return 6;
      case KEY_256:
        return 8;
      default:
        throw new IllegalArgumentException("Unknown key size");
    }
  }
}