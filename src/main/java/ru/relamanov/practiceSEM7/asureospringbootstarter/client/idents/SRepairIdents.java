package ru.relamanov.practiceSEM7.asureospringbootstarter.client.idents;

/**
 * Ident'ы структуры {@link ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SZvk}.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public enum SRepairIdents implements Ident {
  CATEGORY_IDENT("CategoryIdent"),
  DEVICES("Devices"),
  CREATE("Create"),
  USER_STATE("UserState");

  private final String asString;

  SRepairIdents(String asString) {
    this.asString = asString;
  }

  public String asString() {
    return asString;
  }
}
