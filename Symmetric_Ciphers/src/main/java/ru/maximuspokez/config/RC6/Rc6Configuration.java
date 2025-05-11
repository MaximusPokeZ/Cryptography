package ru.maximuspokez.config.RC6;

public class Rc6Configuration {
  private final int w;
  private final int r;
  private final int b;
  private final int P;
  private final int Q;
  private final int logW;

  public Rc6Configuration(int w, int r, int b) {
    if (w != 32) {
      throw new IllegalArgumentException("w (block length) must be 32 bytes");
    }
    this.w = w;
    this.logW = 5;
    this.r = r;
    this.b = b;
    P = 0xB7E15163;
    Q = 0x9E3779B9;
  }

  public int getW() {
    return w;
  }

  public int getR() {
    return r;
  }

  public int getB() {
    return b;
  }

  public int getP() {
    return P;
  }

  public int getQ() {
    return Q;
  }

  public int getLogW() {
    return logW;
  }

  // по 2 элемента массива S для каждого раунда и 4 элемента для начальной и финальной обработки
  public int getKeyLength() {
    return 2 * r + 4;
  }

  @Override
  public String toString() {
    return String.format("RC6 Configuration: w=%d, r=%d, b=%d, P=0x%08X, Q=0x%08X", w, r, b, P, Q);
  }
}
