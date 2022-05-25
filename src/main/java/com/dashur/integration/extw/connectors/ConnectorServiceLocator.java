package com.dashur.integration.extw.connectors;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.extw.Constant;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class ConnectorServiceLocator {
  @Inject
  @Named("everymatrix-connector")
  ConnectorService everymatrix;

  @Inject
  @Named("parimatch-connector")
  ConnectorService parimatch;
/*
  @Inject
  @Named("qt-connector")
  ConnectorService qt;

  @Inject
  @Named("vgs-connector")
  ConnectorService vgs;
*/

  @Inject
  @Named("relaxgaming-connector")
  ConnectorService relaxgaming;

  /**
   * @param operatorCode operator code
   * @return
   */
  public ConnectorService getConnector(String operatorCode) {
    if (Constant.OPERATOR_EVERYMATRIX.equals(operatorCode)) {
      return everymatrix;
    }

    if (Constant.OPERATOR_PARIMATCH.equals(operatorCode)) {
      return parimatch;
    }
/*
    if (Constant.OPERATOR_QTECH.equals(operatorCode)) {
      return qt;
    }

    if (Constant.OPERATOR_VGS.equals(operatorCode)) {
      return vgs;
    }
*/
    if (Constant.OPERATOR_RELAXGAMING.equals(operatorCode)) {
      return relaxgaming;
    }

    throw new ApplicationException(
        "Unable to find connector for operator with code [%s]", operatorCode);
  }
}
