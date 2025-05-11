package ru.maximuspokez.config.RC6;

public class Rc6ConfigurationFactory {

  public static Rc6Configuration rc6_128() { return new Rc6Configuration(32, 20, 16); }

  public static Rc6Configuration rc6_192() { return new Rc6Configuration(32, 20, 24); }

  public static Rc6Configuration rc6_256() { return new Rc6Configuration(32, 20, 32); }

}
