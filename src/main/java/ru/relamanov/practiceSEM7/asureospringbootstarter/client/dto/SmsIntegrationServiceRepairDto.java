package ru.relamanov.practiceSEM7.asureospringbootstarter.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.relamanov.practiceSEM7.asureospringbootstarter.utils.DateTimeUtils;

import java.time.LocalDateTime;

/**
 * ДТО ремонта по оборудованию из АСУРЭО.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsIntegrationServiceRepairDto {
  private Integer id;
  private Integer selfNum;
  private Double factRepairDateBegin;
  private Double factRepairDateEnd;
  private Integer categoryId;
  private String categoryIdent;
  private String categoryName;
  private Integer deviceId;
  private String deviceShifr;

  public LocalDateTime factRepairDateBeginAsLocalDateTime() {
    return DateTimeUtils.fromVariantDateTime(factRepairDateBegin);
  }

  public LocalDateTime factRepairDateEndAsLocalDate() {
    return DateTimeUtils.fromVariantDateTime(factRepairDateEnd);
  }

  public boolean hasFactRepairDates() {
    return factRepairDateBegin != null && factRepairDateEnd != null
            && factRepairDateBegin != 0.0 && factRepairDateEnd != 0.0;
  }
}
