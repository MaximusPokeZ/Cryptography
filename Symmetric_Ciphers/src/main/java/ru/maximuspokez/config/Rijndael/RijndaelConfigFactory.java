package ru.maximuspokez.config.Rijndael;

public class RijndaelConfigFactory {

  public static RijndaelConfiguration aes128() {
    return new RijndaelConfiguration(4, 4, 0x11B);
  }

  public static RijndaelConfiguration aes192() {
    return new RijndaelConfiguration(4, 6, 0x11B);
  }

  public static RijndaelConfiguration aes256() {
    return new RijndaelConfiguration(4, 8, 0x11B);
  }

  public static RijndaelConfiguration rijndael192Block(int modulus) {
    return new RijndaelConfiguration(6, 4, modulus);
  }

  public static RijndaelConfiguration rijndael256Block(int modulus) {
    return new RijndaelConfiguration(8, 4, modulus);
  }

  public static RijndaelConfiguration rijndael192Block() {
    return new RijndaelConfiguration(6, 4);
  }

  public static RijndaelConfiguration rijndael256Block() {
    return new RijndaelConfiguration(8, 4);
  }

  public static RijndaelConfiguration custom(int nb, int nk, int modulus) {
    return new RijndaelConfiguration(nb, nk, modulus);
  }
}
