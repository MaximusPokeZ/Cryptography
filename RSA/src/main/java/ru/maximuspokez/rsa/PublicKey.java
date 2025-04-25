package ru.maximuspokez.rsa;

import java.math.BigInteger;

public record PublicKey(BigInteger n, BigInteger e) {

}
