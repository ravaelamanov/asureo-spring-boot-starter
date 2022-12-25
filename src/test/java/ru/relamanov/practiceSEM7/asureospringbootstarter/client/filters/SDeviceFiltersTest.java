package ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.idents.SDeviceIdents;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterCondition;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SFilterConditionArray;

class SDeviceFiltersTest {
  @Test
  void toSFilterConditionArray() {
    SDeviceFilters filter = SDeviceFilters.builder()
            .isActive(true)
            .build();
    SFilterConditionArray actual = filter.toSFilterConditionArray();
    Assertions.assertEquals(1, actual.getSFilterCondition().size());
    Assertions.assertEquals(1, actual.getSFilterCondition().stream()
            .map(SFilterCondition::getIdent)
            .filter(ident -> ident.equals(SDeviceIdents.IS_ACTIVE.asString()))
            .distinct()
            .count());
  }

  @Test
  void ifNoPropertySetOnBuilderThenConditionArrayIsEmpty() {
    SDeviceFilters filter = SDeviceFilters.builder()
            .build();

    Assertions.assertEquals(0, filter.toSFilterConditionArray().getSFilterCondition().size());
  }
}