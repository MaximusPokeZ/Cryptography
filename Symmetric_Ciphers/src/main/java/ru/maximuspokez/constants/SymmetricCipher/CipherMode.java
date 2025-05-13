package ru.maximuspokez.constants.SymmetricCipher;

public enum CipherMode {
  ECB,    // Electronic Codebook
  CBC,    // Cipher Block Chaining
  PCBC,   // Propagating Cipher Block Chaining
  CFB,    // Cipher Feedback
  OFB,    // Output Feedback
  CTR,    // Counter Mode
  RANDOM_DELTA;
}
