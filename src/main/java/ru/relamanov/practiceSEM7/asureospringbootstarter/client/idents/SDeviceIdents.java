package ru.relamanov.practiceSEM7.asureospringbootstarter.client.idents;

/**
 * Ident'ы структуры {@link ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SDevice}.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public enum SDeviceIdents implements Ident {
  IS_ACTIVE("IsActive");

  private final String asString;

  SDeviceIdents(String asString) {
    this.asString = asString;
  }

  public String asString() {
    return asString;
  }
}
