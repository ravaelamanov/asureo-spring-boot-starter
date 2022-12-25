package ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.idents.SRepairIdents;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SAttributeType;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterParam;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterParamArray;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SUserState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Фильтры ремонтов по оборудованию.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings({
        "checkstyle:AbbreviationAsWordInName",
        "checkstyle:LocalVariableName"})
public class SRepairFilters {
  private List<String> categoriesIdents;
  private List<Integer> devices;
  private LocalDate createDateFrom;
  private LocalDate createDateTo;
  private String userState;

  /**
   * Преобразует фильтры в {@link SFilterParamArray}.
   *
   * @return {@link SFilterParamArray}
   */
  public SFilterParamArray toSFilterParamArray() {
    List<SFilterParam> filterParams = new ArrayList<>();

    if (!CollectionUtils.isEmpty(categoriesIdents)) {
      SFilterParam categoriesIdentsFilter = new SFilterParam();
      categoriesIdentsFilter.setIdent(SRepairIdents.CATEGORY_IDENT.asString());
      categoriesIdentsFilter.setValue(String.join(",", categoriesIdents));
      filterParams.add(categoriesIdentsFilter);
    }

    if (!CollectionUtils.isEmpty(devices)) {
      SFilterParam devicesFilter = new SFilterParam();
      devicesFilter.setIdent(SRepairIdents.DEVICES.asString());
      devicesFilter.setValue(String.join(",", devices.stream()
              .map(Object::toString).toList()));
      filterParams.add(devicesFilter);
    }

    if (createDateFrom != null && createDateTo != null) {
      SFilterParam createDatesFilter = new SFilterParam();
      createDatesFilter.setIdent(SRepairIdents.CREATE.asString());
      createDatesFilter.setValue(Stream.of(createDateFrom, createDateTo)
              .map(LocalDate::toString)
              .collect(Collectors.joining(",", "date,", "")));
      filterParams.add(createDatesFilter);
    }

    if (StringUtils.hasText(userState)) {
      SFilterParam devicesFilter = new SFilterParam();
      devicesFilter.setIdent(SRepairIdents.USER_STATE.asString());
      devicesFilter.setValue(SUserState.fromValue(userState).value());
      filterParams.add(devicesFilter);
    }

    filterParams.forEach(param -> param.setDataType(SAttributeType.AT_STRING));

    SFilterParamArray sFilterParamArray = new SFilterParamArray();
    sFilterParamArray.getSFilterParam().addAll(filterParams);
    return sFilterParamArray;
  }

  public void isViewed() {
    userState = SUserState.US_VIEWED.value();
  }

}
