package ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.idents.SRepairIdents;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SAttributeType;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterParam;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterParamArray;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class SRepairFiltersTest {
  @Test
  void testParamCountIsCorrect() {
    SRepairFilters filters = withAllFiltersSet();
    SFilterParamArray sFilterParamArray = filters.toSFilterParamArray();

    Assertions.assertEquals(4, sFilterParamArray.getSFilterParam().size());
  }

  @Test
  void testAllIdentsPresent() {
    SRepairFilters filters = withAllFiltersSet();
    SFilterParamArray sFilterParamArray = filters.toSFilterParamArray();

    Assertions.assertEquals(Arrays.stream(SRepairIdents.values())
                    .map(SRepairIdents::asString)
                    .collect(Collectors.toSet()),
            sFilterParamArray.getSFilterParam().stream()
                    .map(SFilterParam::getIdent)
                    .collect(Collectors.toSet()));
  }

  @Test
  void testAllDataTypesAreStrings() {
    SRepairFilters filters = withAllFiltersSet();
    SFilterParamArray sFilterParamArray = filters.toSFilterParamArray();

    List<SAttributeType> attributeTypes = sFilterParamArray.getSFilterParam().stream()
            .map(SFilterParam::getDataType)
            .distinct()
            .toList();

    Assertions.assertEquals(1, attributeTypes.size());
    Assertions.assertEquals(SAttributeType.AT_STRING, attributeTypes.get(0));
  }

  @Test
  void ifNoPropertySetOnFilterThenFilterParamArrayIsEmpty() {
    SRepairFilters filter = withNoFiltersSet();

    Assertions.assertEquals(0, filter.toSFilterParamArray().getSFilterParam().size());
  }

  @Test
  void ifOnlyCreateDateFromSetOnFilterThenFilterParamArrayIsEmpty() {
    SRepairFilters filter = withNoFiltersSet();
    filter.setCreateDateFrom(LocalDate.now());

    Assertions.assertEquals(0, filter.toSFilterParamArray().getSFilterParam().size());
  }

  @Test
  void ifOnlyCreateDateToSetOnFilterThenFilterParamArrayIsEmpty() {
    SRepairFilters filter = withNoFiltersSet();
    filter.setCreateDateTo(LocalDate.now());

    Assertions.assertEquals(0, filter.toSFilterParamArray().getSFilterParam().size());
  }


  private static SRepairFilters withAllFiltersSet() {
    SRepairFilters filters = new SRepairFilters();
    filters.setDevices(Collections.singletonList(1));
    filters.setCategoriesIdents(Collections.singletonList("ПЛ"));
    filters.isViewed();
    filters.setCreateDateFrom(LocalDate.now());
    filters.setCreateDateTo(LocalDate.now());
    return filters;
  }

  private static SRepairFilters withNoFiltersSet() {
    return new SRepairFilters();
  }
}