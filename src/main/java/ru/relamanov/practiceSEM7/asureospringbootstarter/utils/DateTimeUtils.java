package ru.relamanov.practiceSEM7.asureospringbootstarter.utils;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Утилиты работы с датой и временем.
 */
public class DateTimeUtils {
  private DateTimeUtils() {
  }

  public static LocalDateTime fromVariantDateTime(double date) {
    return new VariantDateTime(date).toLocalDateTime();
  }

  @EqualsAndHashCode
  @ToString
  static final class VariantDateTime {
    public static final LocalDate INIT_DATE = LocalDate.of(1899, 12, 30);
    private static final long SECONDS_IN_A_DAY = TimeUnit.of(ChronoUnit.DAYS).toSeconds(1);
    private final double dateTime;

    VariantDateTime(double dateTime) {
      this.dateTime = dateTime;
    }

    public LocalDateTime toLocalDateTime() {
      LocalDate date = toLocalDate();
      LocalTime time = toLocalTime();
      return date.atTime(time);
    }

    private LocalTime toLocalTime() {
      return LocalTime.MIN.plusSeconds((long) (timePart() * SECONDS_IN_A_DAY));
    }

    private LocalDate toLocalDate() {
      return INIT_DATE.plusDays((long) datePart());
    }

    public double datePart() {
      return Math.floor(dateTime);
    }

    public double timePart() {
      return dateTime % 1;
    }
  }
}
