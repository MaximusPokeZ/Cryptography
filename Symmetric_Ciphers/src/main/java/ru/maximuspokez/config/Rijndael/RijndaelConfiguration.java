package ru.maximuspokez.config.Rijndael;

import ru.maximuspokez.exceptions.ReducedModuleException;
import ru.maximuspokez.galois.GaloisFieldService;
import ru.maximuspokez.sbox.Rijndael.InverseSBoxRijndael;
import ru.maximuspokez.sbox.Rijndael.SBoxRijndael;
import ru.maximuspokez.utils.PolynomialUtils;

public class RijndaelConfiguration {
  private final int nb;
  private final int nk;
  private final int nr;
  private final int modulus;

  private final SBoxRijndael sBox;
  private final InverseSBoxRijndael invSBox;

  private final byte[] rcon;

  public RijndaelConfiguration(int nb, int nk, int modulus) {
    this.nb = nb;
    this.nk = nk;
    this.nr = Math.max(nb, nk) + 6;
    if (!PolynomialUtils.isIrreducible(modulus))
      throw new ReducedModuleException("The module must be irreducible!");
    this.modulus = modulus;
    this.sBox = new SBoxRijndael(modulus);
    this.invSBox = new InverseSBoxRijndael(sBox);
    this.rcon = generateRcon((nr + 1) * nb);
  }

  public RijndaelConfiguration(int nb, int nk) {
    this(nb, nk, GaloisFieldService.getRandomIrreduciblePolynomial());
  }

  private byte[] generateRcon(int size) {
    byte[] res = new byte[size];
    res[0] = (byte) 0x01;
    for (int i = 1; i < size; i++) {
      res[i] = GaloisFieldService.multiply(res[i - 1], (byte) 0x02, modulus);
    }

    return res;
  }

  public int getNb() { return nb; }
  public int getNk() { return nk; }
  public int getNr() { return nr; }
  public int getModulus() { return modulus; }
  public SBoxRijndael getSBox() { return sBox; }
  public InverseSBoxRijndael getInvSBox() { return invSBox; }
  public byte[] getRcon() { return rcon; }

  @Override
  public String toString() {
    return String.format("Rijndael Configuration: Nb=%d, Nk=%d, Nr=%d, Modulus=0x%02X", nb, nk, nr, modulus);
  }
}