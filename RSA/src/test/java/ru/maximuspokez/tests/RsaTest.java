package ru.maximuspokez.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.maximuspokez.rsa.RsaService;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RsaTest {
  private static final Path MESSAGE_PATH;
  private static final Path ENCRYPTED_PATH;

  static {
    try {
      MESSAGE_PATH = Path.of(Objects.requireNonNull(RsaTest.class.getClassLoader().getResource("message.txt")).toURI());
      ENCRYPTED_PATH = Path.of(Objects.requireNonNull(RsaTest.class.getClassLoader().getResource("encrypted.txt")).toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private RsaService rsaService;

  @BeforeEach
  void setUp() {
    rsaService = new RsaService(RsaService.PrimaryTestType.MILLER_RABIN, 0.99, 1024);
  }

  @Test
  void testRsa() throws IOException {
    byte[] originalBytes = Files.readAllBytes(MESSAGE_PATH);

    String message = new String(originalBytes);

    BigInteger m = new BigInteger(1, originalBytes);
    BigInteger c = rsaService.encrypt(m);

    Files.write(ENCRYPTED_PATH, c.toByteArray());

    BigInteger encryptedFromFile = new BigInteger(1, Files.readAllBytes(ENCRYPTED_PATH));
    BigInteger decrypted = rsaService.decrypt(encryptedFromFile);
    byte[] decryptedBytes = decrypted.toByteArray();

    String decryptedString = new String(decryptedBytes);

    assertEquals(message, decryptedString, "Decrypted text should be similar");
  }
}
