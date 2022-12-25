package ru.relamanov.practiceSEM7.asureospringbootstarter.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.support.MarshallingUtils;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.dto.SmsIntegrationServiceDeviceDto;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.dto.SmsIntegrationServiceRepairDto;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters.SDeviceFilters;
import ru.relamanov.practiceSEM7.asureospringbootstarter.client.filters.SRepairFilters;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.IntegrationServiceLogin;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.IntegrationServiceLoginResponse;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.IntegrationServiceRetDeviceList;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.IntegrationServiceRetDeviceListResponse;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.IntegrationServiceRetZVKAttributes;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.IntegrationServiceRetZVKAttributesResponse;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SDevice;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SZVKAttribute;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SZVKAttributeArray;
import ru.relamanov.practiceSEM7.asureospringbootstarter.jaxb2.SZvk;

import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPPart;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Клиент интеграционного сервиса.
 */
@Slf4j
public class SmsIntegrationServiceClient extends WebServiceGatewaySupport {
  private static final String SESSION_ID_HEADER = "ROClientIDHeader";
  private static final String TEMP_URI = "http://tempuri.org/";
  protected static final String TEMP_URI_PREFIX = "tem";

  private List<String> categoriesIdents;
  private SZVKAttributeArray attributeArray;
  private String username;
  private String password;

  /**
   * Запрашивает оборудование из АСУРЭО в соответствии с фильтрами.
   *
   * @param filters фильтры
   * @return ДТО оборудований
   */
  public List<SmsIntegrationServiceDeviceDto> getDeviceList(SDeviceFilters filters) {
    log.info("Запрос списка оборудований из АСУРЭО");

    IntegrationServiceRetDeviceList request = new IntegrationServiceRetDeviceList();
    request.setAFilter(filters.toSFilterConditionArray());
    return toDto((IntegrationServiceRetDeviceListResponse) getWebServiceTemplate()
            .marshalSendAndReceive(request, loginCallback()));
  }

  /**
   * Запрашивает ремонты по оборудованю из АСУРЭО в соответствии с фильтрами.
   *
   * @param filters фильтры
   * @return ДТО ремонтов по оборудованю
   */
  public List<SmsIntegrationServiceRepairDto> getRepairsList(SRepairFilters filters) {
    log.info("Запрос списка ДЗ из АСУРЭО");

    filters.setCategoriesIdents(categoriesIdents);
    IntegrationServiceRetZVKAttributes request = new IntegrationServiceRetZVKAttributes();
    request.setAFilter(filters.toSFilterParamArray());
    request.setAAttributes(attributeArray);
    return toDto((IntegrationServiceRetZVKAttributesResponse) getWebServiceTemplate()
            .marshalSendAndReceive(request, loginCallback()));
  }

  private String login() {
    log.info("Аутентификации в сервисе интеграции");

    IntegrationServiceLogin request = new IntegrationServiceLogin();
    request.setALogin(username);
    request.setAPassword(password);

    return getWebServiceTemplate().sendAndReceive(
            message -> MarshallingUtils.marshal(getMarshaller(), request, message),
            message -> {
              if (message instanceof SaajSoapMessage soapMessage) {
                SOAPPart soapPart = soapMessage.getSaajMessage().getSOAPPart();
                SOAPEnvelope envelope;
                SOAPHeader header;
                try {
                  envelope = soapPart.getEnvelope();
                  header = envelope.getHeader();
                } catch (SOAPException e) {
                  throw new RuntimeException(e);
                }

                Iterator<Node> childElements = Optional.ofNullable(header)
                        .map(SOAPElement::getChildElements)
                        .filter(Iterator::hasNext)
                        .orElseThrow(() -> new SmsIntegrationClientLoginException(
                                "Отсутствуют заголовки в ответе на запрос Login"
                        ));
                Optional<Node> roClientIdHeaderNodeOptional = Optional.empty();
                while (childElements.hasNext()) {
                  Node childEl = childElements.next();
                  if (childEl.getLocalName() != null
                          && childEl.getLocalName().equals(SESSION_ID_HEADER)) {
                    roClientIdHeaderNodeOptional = Optional.of(childEl);
                    break;
                  }
                }
                return roClientIdHeaderNodeOptional.map(roClientIdHeaderNode -> {
                          String sessionId = IntStream.range(0,
                                          roClientIdHeaderNode.getChildNodes().getLength())
                                  .filter(i ->
                                          roClientIdHeaderNode.getChildNodes().item(i).getLocalName() != null)
                                  .filter(i ->
                                          roClientIdHeaderNode.getChildNodes().item(i).getLocalName()
                                                  .equals("ID"))
                                  .mapToObj(i ->
                                          roClientIdHeaderNode.getChildNodes().item(i).getTextContent())
                                  .findFirst()
                                  .orElseThrow(() ->
                                          new SmsIntegrationClientLoginException("Отсутвует тэг <ID>"));
                          IntegrationServiceLoginResponse loginResponse;
                          try {
                            loginResponse = (IntegrationServiceLoginResponse)
                                    MarshallingUtils.unmarshal(getUnmarshaller(), message);
                          } catch (IOException e) {
                            throw new RuntimeException(e);
                          }
                          boolean isLogined = loginResponse.getReturn().getIsLogined() == 1;
                          if (isLogined) {
                            return sessionId;
                          } else {
                            throw new SmsIntegrationClientLoginException(
                                    "Не получилось аутентифицироваться в сервисе интеграции. "
                                            + "Причина: %s"
                                            .formatted(loginResponse.getReturn().getErrorMessage()));
                          }
                        }
                ).orElseThrow(() ->
                        new SmsIntegrationClientLoginException("Отсутствует заголовок ROClientIDHeader"));
              } else {
                throw new SmsIntegrationClientLoginException(
                        "Невозможно аутентифицироваться в сервисе интеграции. "
                                + "Отправляемое сообщение не является SOAP сообщением. "
                                + "Класс отправляемого сообщения: %s"
                                .formatted(message.getClass().getCanonicalName()));
              }
            }
    );
  }

  private WebServiceMessageCallback loginCallback() {
    return message -> {
      if (message instanceof SaajSoapMessage soapMessage) {
        String sessionId = login();
        SOAPPart soapPart = soapMessage.getSaajMessage().getSOAPPart();
        try {
          SOAPEnvelope envelope = soapPart.getEnvelope();
          envelope.addNamespaceDeclaration(TEMP_URI_PREFIX, TEMP_URI);
          SOAPHeader header = envelope.getHeader();
          Name roClientIDHeaderName = envelope.createName(SESSION_ID_HEADER, TEMP_URI_PREFIX, TEMP_URI);
          SOAPHeaderElement roClientHeader = header.addHeaderElement(roClientIDHeaderName);
          SOAPElement id = roClientHeader.addChildElement("ID", TEMP_URI_PREFIX, TEMP_URI);
          id.addTextNode(sessionId);

          soapMessage.getSaajMessage().saveChanges();
        } catch (SOAPException e) {
          throw new RuntimeException(e);
        }

      } else {
        log.warn("Невозможно аутентифицироваться с сервисом интеграции. "
                + "Отправляемое сообщение не является SOAP сообщением. "
                + "Класс отправляемого сообщения: {}", message.getClass().getCanonicalName());
      }
    };
  }

  private List<SmsIntegrationServiceRepairDto> toDto(IntegrationServiceRetZVKAttributesResponse repairsListResponse) {
    return repairsListResponse.getResult().getSZvk().stream()
            .map(this::toDto)
            .toList();

  }

  private SmsIntegrationServiceRepairDto toDto(SZvk sZvk) {
    SmsIntegrationServiceRepairDto dto = new SmsIntegrationServiceRepairDto();
    dto.setId(sZvk.getId());
    dto.setSelfNum(sZvk.getSelfNum());
    dto.setFactRepairDateBegin(sZvk.getFactRepDBeg());
    dto.setFactRepairDateEnd(sZvk.getFactRepDEnd());
    dto.setCategoryId(sZvk.getCatId());
    dto.setCategoryIdent(sZvk.getCatIdent());
    dto.setCategoryName(sZvk.getCatName());
    dto.setDeviceId(sZvk.getDevId());
    dto.setDeviceShifr(sZvk.getDevShifr());
    return dto;
  }

  private List<SmsIntegrationServiceDeviceDto> toDto(IntegrationServiceRetDeviceListResponse deviceListResponse) {
    return deviceListResponse.getReturn().getSDevice().stream()
            .map(this::toDto)
            .toList();
  }

  private SmsIntegrationServiceDeviceDto toDto(SDevice device) {
    SmsIntegrationServiceDeviceDto dto = new SmsIntegrationServiceDeviceDto();
    dto.setId(device.getId());
    dto.setName(device.getName());
    dto.setShifr(device.getShifr());
    dto.setIsActive(device.isIsActive());
    dto.setDeviceTypeId(device.getDeviceTypeId());
    dto.setGeneralizedDeviceId(device.getGeneralizedDeviceId());
    dto.setGeneralizedDeviceName(device.getGeneralizedDeviceName());
    dto.setIsGroup(device.isIsGroup());
    dto.setUseInChild(device.isUseInChild());
    dto.setLongName(device.getLongName());
    dto.setSelfDevice(device.isSelfDevice());
    dto.setIsAdditional(device.isIsAdditional());
    return dto;
  }

  public void setAttributeArray(List<String> attributes) {
    attributeArray = new SZVKAttributeArray();
    attributeArray.getSZVKAttribute().addAll(
            attributes.stream()
                    .map(SZVKAttribute::fromValue)
                    .toList()
    );
  }

  public void setCategoriesIdents(List<String> categoriesIdents) {
    this.categoriesIdents = categoriesIdents;
  }

  public void setCredentials(String username, String password) {
    Objects.requireNonNull(username);
    Objects.requireNonNull(password);
    this.username = username;
    this.password = password;
  }
}
