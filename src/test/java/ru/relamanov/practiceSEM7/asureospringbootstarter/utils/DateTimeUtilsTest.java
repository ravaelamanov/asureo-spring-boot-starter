package ru.relamanov.practiceSEM7.asureospringbootstarter.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

class DateTimeUtilsTest {
  @ParameterizedTest
  @MethodSource("variantDateTimes")
  @DisplayName("Проверка конвертации VariantDateTime в LocalDateTime")
  void fromVariantDateTime(double variantDateTime, LocalDateTime expected) {
    Assertions.assertEquals(expected, DateTimeUtils.fromVariantDateTime(variantDateTime));
  }

  private static Stream<Arguments> variantDateTimes() {
    return Stream.of(
        Arguments.of(42842.370277778, LocalDateTime.of(2017, 4, 17, 8, 53, 12)),
        Arguments.of(25569, LocalDateTime.of(1970, 1, 1, 0, 0))
    );
  }
}