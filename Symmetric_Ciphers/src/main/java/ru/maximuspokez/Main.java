package ru.maximuspokez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.maximuspokez.ciphers.RC6.RC6;
import ru.maximuspokez.ciphers.Rijndael.Rijndael;
import ru.maximuspokez.ciphers.Rijndael.RijndaelKeyExpansionImpl;
import ru.maximuspokez.ciphers.Serpent.Serpent;
import ru.maximuspokez.config.RC6.Rc6ConfigurationFactory;
import ru.maximuspokez.config.Rijndael.RijndaelConfigFactory;
import ru.maximuspokez.config.Rijndael.RijndaelConfiguration;
import ru.maximuspokez.config.Serpent.SerpentConfigurationFactory;
import ru.maximuspokez.constants.Serpent.SerpentConstants;
import ru.maximuspokez.constants.SymmetricCipher.CipherMode;
import ru.maximuspokez.constants.SymmetricCipher.PaddingMode;
import ru.maximuspokez.context.SymmetricCipherContext;
import ru.maximuspokez.crypto.PermuteBits;
import ru.maximuspokez.interfaces.KeyExpansion;
import ru.maximuspokez.interfaces.SymmetricCipher;
import ru.maximuspokez.utils.HexUtil;

import java.io.IOException;
import java.util.Arrays;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;


public class Main {
  private static final Logger log = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws IOException {

//    byte[] key = {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0xC8, (byte)0xC0, (byte)0xCD, (byte)0xC0};
//    byte[] iv = {(byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
//
//    InputStream path = Main.class.getClassLoader().getResourceAsStream("Tux.jpg");
//
//    byte[] message = path.readAllBytes();
//
//
//    byte[] KEY = { (byte)0xD4, (byte)0xDE, (byte)0x6A, (byte)0xA8, (byte)0x94, (byte)0x55, (byte)0xD3, (byte)0x89,
//            (byte)0x03, (byte)0xF6, (byte)0xD7, (byte)0x8C, (byte)0x45, (byte)0xC4, (byte)0xC5, (byte)0xD8 };
//
//    byte[] MESS = { (byte)0xC7, (byte)0x22, (byte)0x7F, (byte)0x94, (byte)0xBB, (byte)0xA7, (byte)0xCB, (byte)0xA6,
//            (byte)0xD6, (byte)0x4C, (byte)0x55, (byte)0xAC, (byte)0x06, (byte)0xB2, (byte)0x8D, (byte)0x6D, (byte)0xB2 };
//
//    SymmetricCipher des = new DES();
//    des.setSymmetricKey(key);
//    DEAL deal = new DEAL(DealKeySize.KEY_128, key);
//    deal.setSymmetricKey(KEY);
//
//
//    SymmetricCipherContext context = new SymmetricCipherContext(deal, CipherMode.RANDOM_DELTA, PaddingMode.ISO_10126, null, (Object) null, 10);
//
//    byte[] ciphertext = context.encrypt(message);
//    byte[] decrypted = context.decrypt(ciphertext);
//
////    System.out.println("Start message: " + HexUtil.bytesToHex(MESS));
//    System.out.println("MEss: " + HexUtil.bytesToHex(message));
//    System.out.println("\n\n\nDecrypted: " + HexUtil.bytesToHex(decrypted));
//    System.out.println("\n\n\nRESULT: " + (message.length == decrypted.length));
//
//    String outputDir = System.getProperty("user.home") + "/Downloads/okkk.jpg";
//    Files.write(Paths.get(outputDir), decrypted);


    int modulus =  0x11b; // стандартный AES модуль


    RijndaelConfiguration configuration = RijndaelConfigFactory.rijndael192Block();

//    System.out.println(HexUtil.bytesToHex(configuration.getRcon()));

    KeyExpansion keyExpansion = new RijndaelKeyExpansionImpl(configuration);

    byte[] key = hexStringToByteArray("2b7e151628aed2a6abf7158809cf4f3c");
    byte[][] roundKeys = keyExpansion.generateRoundKeys(key);
//
//    System.out.println(HexUtil.bytesToHex(roundKeys[1]));
//    System.out.println(Arrays.equals(hexStringToByteArray("a0fafe1788542cb123a339392a6c7605"), roundKeys[1]));


    SymmetricCipher rijndael = new Rijndael(configuration);
    rijndael.setSymmetricKey(key);

    SymmetricCipherContext context = new SymmetricCipherContext(rijndael, CipherMode.RANDOM_DELTA, PaddingMode.ISO_10126, null, (Object) null, 10);

    byte[] plaintextBlock = hexStringToByteArray("ffffffffffffffffffffffffffffffff");
    System.out.println(HexUtil.bytesToHex(plaintextBlock));

    byte[] ciphertext = context.encrypt(plaintextBlock);
    System.out.println("Ciphertext: " + HexUtil.bytesToHex(ciphertext));

    byte[] decryptedBlock = context.decrypt(ciphertext);
    System.out.println("Decrypted: " + HexUtil.bytesToHex(decryptedBlock));


//
//    byte[] testBlock = {0x1f, 0x33, 0x21, 0x12, 0x1f, 0x33, 0x21, 0x12, 0x1f, 0x33, 0x21, 0x12, 0x1f, 0x33, 0x21, 0x12};
//    byte[] afterIP = PermuteBits.permute(testBlock, SerpentConstants.IP_TABLE, false, false);
//    byte[] afterFP = PermuteBits.permute(afterIP, SerpentConstants.FP_TABLE, false, false);
//    System.out.println(Arrays.equals(testBlock, afterFP));
//
//    byte[] Key = hexStringToByteArray("80000000000000000000000000000000");
//    SymmetricCipher rc6 = new RC6(Rc6ConfigurationFactory.rc6_128());
//    rc6.setSymmetricKey(Key);
//
//    byte[] pB = hexStringToByteArray("ffffffffffffffffffffffffffffffff");
//    System.out.println(HexUtil.bytesToHex(pB));
//
//    byte[] ciB = rc6.encrypt(pB);
//    System.out.println("Ciphertext: " + HexUtil.bytesToHex(ciB));
//
//    byte[] dB = rc6.decrypt(ciB);
//    System.out.println("Decrypted: " + HexUtil.bytesToHex(dB));

  }

}