package ru.relamanov.practiceSEM7.asureospringbootstarter.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ДТО оборудования из АСУРЭО.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsIntegrationServiceDeviceDto {
  private Integer id;
  private String name;
  private String shifr;
  protected Boolean isActive;
  private Integer deviceTypeId;
  private Integer generalizedDeviceId;
  private String generalizedDeviceName;
  private Boolean isGroup;
  private Boolean useInChild;
  private String longName;
  private Boolean selfDevice;
  private Boolean isAdditional;
}
