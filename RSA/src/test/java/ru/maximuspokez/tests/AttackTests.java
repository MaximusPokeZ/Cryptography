package ru.maximuspokez.tests;

import org.junit.jupiter.api.Test;
import ru.maximuspokez.attacks.FermatAttack;
import ru.maximuspokez.attacks.WienerAttack;
import ru.maximuspokez.rsa.RsaService;
import ru.maximuspokez.utils.math.ExtendedEuclid;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class AttackTests {

  @Test
  void testWienerAttackSuccess() {
    BigInteger p = new BigInteger("86969");
    BigInteger q = new BigInteger("86981");
    BigInteger n = p.multiply(q);
    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    BigInteger d = BigInteger.valueOf(3);

    BigInteger e = ExtendedEuclid.extendedEuclid(d, phi).x;
    if (e.signum() < 0) {
      e = e.add(phi);
    }

    WienerAttack attack = new WienerAttack();
    WienerAttack.WienerAttackResult result = attack.attack(e, n);

    assertNotNull(result);
    assertEquals(d, result.d());
  }

  @Test
  void testWienerAttackFail() {
    RsaService service = new RsaService(RsaService.PrimaryTestType.MILLER_RABIN, 0.99, 1024);
    BigInteger e = service.getKeyPair().publicKey().e();
    BigInteger n = service.getKeyPair().publicKey().n();

    WienerAttack attack = new WienerAttack();
    WienerAttack.WienerAttackResult result = attack.attack(e, n);

    assertNull(result);
  }

  @Test
  void testFermatAttackSuccess() {
    BigInteger p = new BigInteger("10007");
    BigInteger q = new BigInteger("10009");
    BigInteger n = p.multiply(q);
    BigInteger e = BigInteger.valueOf(65537);
    BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    BigInteger d = ExtendedEuclid.extendedEuclid(e, phi).x;

    if (d.signum() < 0) {
      d = d.add(phi);
    }

    FermatAttack attack = new FermatAttack();
    FermatAttack.FermatAttackResult result = attack.attack(n, e, 1000);

    assertNotNull(result, "Fermat attack should succeed for close primes");
    assertEquals(d, result.d());
    assertEquals(phi, result.phi());
  }

  @Test
  void testFermatAttackFail() {
    RsaService service = new RsaService(RsaService.PrimaryTestType.MILLER_RABIN, 0.99, 64);
    BigInteger e = service.getKeyPair().publicKey().e();
    BigInteger n = service.getKeyPair().publicKey().n();

    FermatAttack attack = new FermatAttack();
    FermatAttack.FermatAttackResult result = attack.attack(n, e, 100000);

    assertNull(result);
  }

}
