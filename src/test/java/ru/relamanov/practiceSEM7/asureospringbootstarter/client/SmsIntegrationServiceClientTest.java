package ru.relamanov.practiceSEM7.asureospringbootstarter.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.dto.SmsIntegrationServiceDeviceDto;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.dto.SmsIntegrationServiceRepairDto;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters.SDeviceFilters;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters.SRepairFilters;
import ru.relamanov.practiceSEM7.asureospringbootstarter.config.SmsIntegrationClientAutoconfigurationConfiguration;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matchingXPath;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest(classes = SmsIntegrationClientAutoconfigurationConfiguration.class)
class SmsIntegrationServiceClientTest {
  protected static final int PORT_NUMBER = 8080;
  protected static final String BASE_URL = "/soap";
  protected static final String XPATH_FOR_REQUEST = "/Envelope/Body/child::*[1][local-name() = 'IntegrationService___%s']";
  protected static final String SOAP_SERVICE_NAME = "IntegrationService";
  private static WireMockServer wireMockServer;

  @Autowired
  private SmsIntegrationServiceClient client;

  @BeforeAll
  public static void setup() {
    wireMockServer = new WireMockServer(WireMockConfiguration
            .wireMockConfig().port(PORT_NUMBER)
            .notifier(new ConsoleNotifier(true)));
    wireMockServer.start();
  }

  @Test
  void ifCantLoginGetDeviceListThrowsSmsIntegrationClientLoginException() {
    stubsBuilder()
            .withResponse(Requests.LOGIN, "login_failure.xml");

    Assertions.assertThrows(SmsIntegrationClientLoginException.class,
            () -> client.getDeviceList(SDeviceFilters.builder().build()));
  }

  @Test
  void ifCantLoginGetRepairsListThrowsSmsIntegrationClientLoginException() {
    stubsBuilder()
            .withResponse(Requests.LOGIN, "login_failure.xml");

    Assertions.assertThrows(SmsIntegrationClientLoginException.class,
            () -> client.getRepairsList(new SRepairFilters()));
  }

  @Test
  void ifNoHeadersInLoginResponseGetRepairsListThrowsSmsIntegrationClientLoginException() {
    stubsBuilder()
            .withResponse(Requests.LOGIN, "login_no_headers.xml");

    Assertions.assertThrows(SmsIntegrationClientLoginException.class,
            () -> client.getRepairsList(new SRepairFilters()));
  }

  @Test
  void ifNoSessionIdInLoginResponseGetRepairsListThrowsSmsIntegrationClientLoginException() {
    stubsBuilder()
            .withResponse(Requests.LOGIN, "login_no_session_id.xml");

    Assertions.assertThrows(SmsIntegrationClientLoginException.class,
            () -> client.getRepairsList(new SRepairFilters()));
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "login_no_roclientidheader.xml",
          "login_no_roclientidheader1.xml"
  })
  void ifNoROClientIdHeaderInLoginResponseGetRepairsListThrowsSmsIntegrationClientLoginException(String filename) {
    stubsBuilder()
            .withResponse(Requests.LOGIN, filename);

    Assertions.assertThrows(SmsIntegrationClientLoginException.class,
            () -> client.getRepairsList(new SRepairFilters()));
  }

  @Test
  void ifEmptyLoginResponseGetRepairsListThrowsSmsIntegrationClientLoginException() {
    stubsBuilder()
            .withResponse(Requests.LOGIN, "login_empty.xml");

    Assertions.assertThrows(Exception.class, () -> client.getRepairsList(new SRepairFilters()));
  }

  @Test
  void ifCanLoginRetDeviceListIsSuccessful() {
    stubsBuilder()
            .withResponse(Requests.LOGIN, "login_success.xml")
            .withResponse(Requests.RET_DEVICE_LIST, "ret_device_list.xml");

    List<SmsIntegrationServiceDeviceDto> devices = client.getDeviceList(SDeviceFilters.builder().build());

    Assertions.assertNotNull(devices);
    Assertions.assertEquals(2, devices.size());
  }

  @Test
  void ifCanLoginRetZvkAttributesIsSuccessful() {
    stubsBuilder()
            .withResponse(Requests.LOGIN, "login_success.xml")
            .withResponse(Requests.RET_ZVK_ATTRIBUTES, "ret_zvk_attributes.xml");

    List<SmsIntegrationServiceRepairDto> repairs = client.getRepairsList(new SRepairFilters());

    Assertions.assertNotNull(repairs);
    Assertions.assertEquals(2, repairs.size());
  }

  static StubsBuilder stubsBuilder() {
    return new StubsBuilder();
  }

  static class StubsBuilder {

    public StubsBuilder withResponse(Requests request, String responseFile) {
      return withResponse(request.getRequestName(), responseFile);
    }
    private StubsBuilder withResponse(String request, String responseFile) {
      wireMockServer.stubFor(post(urlPathEqualTo(BASE_URL))
              .withHeader("Content-Type", containing("text/xml"))
              .withQueryParam("service", equalTo(SOAP_SERVICE_NAME))
              .withRequestBody(matchingXPath(XPATH_FOR_REQUEST.formatted(request)))
              .willReturn(aResponse().withBodyFile(responseFile)));
      return this;
    }
  }

  @Getter
  enum Requests {
    LOGIN("Login"),
    RET_DEVICE_LIST("RetDeviceList"),
    RET_ZVK_ATTRIBUTES("RetZVKAttributes"),
    ;

    private final String requestName;

    Requests(String requestName) {
      this.requestName = requestName;
    }
  }

  @AfterAll
  public static void cleanUp() {
    wireMockServer.stop();
  }
}