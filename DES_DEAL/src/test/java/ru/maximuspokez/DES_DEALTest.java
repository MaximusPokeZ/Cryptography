package ru.maximuspokez;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.DEAL.DEAL;
import ru.maximuspokez.DES.DES;
import ru.maximuspokez.constants.CipherMode;
import ru.maximuspokez.constants.DealKeySize;
import ru.maximuspokez.constants.PaddingMode;
import ru.maximuspokez.context.SymmetricCipherContext;
import ru.maximuspokez.interfaces.SymmetricCipher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DES_DEALTest {
  private static byte[] keyDES;
  private static byte[] keyDEAL;
  private final int BUFFER_SIZE = 4096;

  private static final String ENCRYPTED_PATH = "src/test/resources/encrypted.dat";
  private static final String VIDEO_PATH = "src/test/resources/antonina.mp4";
  private static final String IMAGE_PATH = "src/test/resources/Tux.jpg";
  private static final String AUDIO_PATH = "src/test/resources/helicopter.mp3";
  private static final String DECRYPTED_VIDEO_PATH = "src/test/resources/decrypted_video.mp4";
  private static final String DECRYPTED_IMAGE_PATH = "src/test/resources/decrypted_image.jpg";
  private static final String DECRYPTED_AUDIO_PATH = "src/test/resources/decrypted_audio.mp3";

  @BeforeAll
  public static void setUp() {

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.getLogger("root").setLevel(Level.OFF);

    keyDES = KeyGenerator.generateKey(8);
    keyDEAL = KeyGenerator.generateKey(16);
  }

  private void testCipherWithFiles(SymmetricCipher cipher, byte[] key, String inputPath, String encPath, String decPath) throws IOException {
    cipher.setSymmetricKey(key);
    SymmetricCipherContext context = new SymmetricCipherContext(
            cipher, CipherMode.OFB, PaddingMode.ISO_10126, null, (Object) null, 10);

    encryptFile(context, inputPath, encPath);
    decryptFile(context, encPath, decPath);

    assertTrue(compareFiles(inputPath, decPath), "Decrypted file should match original");
  }

  private void encryptFile(SymmetricCipherContext context, String inputPath, String outputPath) throws IOException {
    try (InputStream is = Files.newInputStream(Paths.get(inputPath));
         OutputStream os = Files.newOutputStream(Paths.get(outputPath))) {

      byte[] buffer = new byte[BUFFER_SIZE];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) != -1) {
        byte[] chunk = Arrays.copyOfRange(buffer, 0, bytesRead);
        byte[] encrypted = context.encrypt(chunk);
        os.write(encrypted);
      }

    }
  }

  private void decryptFile(SymmetricCipherContext context, String inputPath, String outputPath) throws IOException {
    try (InputStream is = Files.newInputStream(Paths.get(inputPath));
         OutputStream os = Files.newOutputStream(Paths.get(outputPath))) {

      byte[] buffer = new byte[BUFFER_SIZE + context.getBlockSize()];
      int bytesRead;
      while ((bytesRead = is.read(buffer)) != -1) {
        byte[] chunk = Arrays.copyOfRange(buffer, 0, bytesRead);
        byte[] decrypted = context.decrypt(chunk);
        os.write(decrypted);
      }

    }
  }

  private boolean compareFiles(String path1, String path2) throws IOException {
    byte[] file1 = Files.readAllBytes(Paths.get(path1));
    byte[] file2 = Files.readAllBytes(Paths.get(path2));
    return java.util.Arrays.equals(file1, file2);
  }

  @Test
  void testDesEncryptionDecryption() {
    DES des = new DES();
    byte[] desKey = KeyGenerator.generateKey(8);
    des.setSymmetricKey(desKey);

    byte[] message = "HelloDES".getBytes();
    message = Arrays.copyOf(message, 8);

    byte[] ciphertext = des.encrypt(message);
    byte[] decrypted = des.decrypt(ciphertext);

    assertNotNull(ciphertext, "Ciphertext should not be null");
    assertArrayEquals(message, decrypted, "DES: decrypted data should match original");
  }

  @Test
  void testDealEncryptionDecryption() {
    byte[] keyDES = KeyGenerator.generateKey(8);
    DEAL deal = new DEAL(DealKeySize.KEY_128, keyDES);
    byte[] keyDEAL = KeyGenerator.generateKey(16);
    deal.setSymmetricKey(keyDEAL);

    byte[] message = KeyGenerator.generateKey(16);

    byte[] ciphertext = deal.encrypt(message);
    byte[] decrypted = deal.decrypt(ciphertext);

    assertNotNull(ciphertext, "Ciphertext should not be null");
    assertArrayEquals(message, decrypted, "DES: decrypted data should match original");
  }

  @Test
  void testDESWithFiles() throws IOException {
    SymmetricCipher des = new DES();
    testCipherWithFiles(des, keyDES, VIDEO_PATH, ENCRYPTED_PATH, DECRYPTED_VIDEO_PATH);
    testCipherWithFiles(des, keyDES, IMAGE_PATH, ENCRYPTED_PATH, DECRYPTED_IMAGE_PATH);
    testCipherWithFiles(des, keyDES, AUDIO_PATH, ENCRYPTED_PATH, DECRYPTED_AUDIO_PATH);
  }

  @Test
  void testDEALWithFiles() throws IOException {
    byte[] keyDES = KeyGenerator.generateKey(8);
    SymmetricCipher deal = new DEAL(DealKeySize.KEY_128, keyDES);
    testCipherWithFiles(deal, keyDEAL, VIDEO_PATH, ENCRYPTED_PATH, DECRYPTED_VIDEO_PATH);
    testCipherWithFiles(deal, keyDEAL, IMAGE_PATH, ENCRYPTED_PATH, DECRYPTED_IMAGE_PATH);
    testCipherWithFiles(deal, keyDEAL, AUDIO_PATH, ENCRYPTED_PATH, DECRYPTED_AUDIO_PATH);
  }
}
