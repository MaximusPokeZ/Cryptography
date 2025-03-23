package ru.maximuspokez.interfaces;

public interface EncryptionTransformation {

  byte[] transform(byte[] inputBlock, byte[] roundKey);

  int getInputBlockSize();

  int getRoundKeySize();

  int getOutputBlockSize();

  String getTransformationName();
}