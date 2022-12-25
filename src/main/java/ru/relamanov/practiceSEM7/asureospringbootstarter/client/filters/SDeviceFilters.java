package ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters;

import lombok.Builder;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.idents.SDeviceIdents;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterCondition;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterConditionArray;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterConditionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Фильтры оборудований в АСУРЭО.
 */
@SuppressWarnings({
        "checkstyle:AbbreviationAsWordInName",
        "checkstyle:LocalVariableName"})
@Builder
public class SDeviceFilters {
  private Boolean isActive;

  /**
   * Преобразует фильтры в {@link SFilterConditionArray}.
   *
   * @return {@link SFilterConditionArray}
   */
  public SFilterConditionArray toSFilterConditionArray() {
    List<SFilterCondition> conditions = new ArrayList<>();

    if (isActive != null) {
      SFilterCondition isActiveCondition = new SFilterCondition();
      isActiveCondition.setIdent(SDeviceIdents.IS_ACTIVE.asString());
      isActiveCondition.setConditionType(SFilterConditionType.FCT_BOOL_TRUE);
      conditions.add(isActiveCondition);
    }

    SFilterConditionArray sFilterConditionArray = new SFilterConditionArray();
    sFilterConditionArray.getSFilterCondition().addAll(conditions);
    return sFilterConditionArray;
  }
}
