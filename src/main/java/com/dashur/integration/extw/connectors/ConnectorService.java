package com.dashur.integration.extw.connectors;

import com.dashur.integration.extw.data.*;

/** Connector services that will need to be implemented by each connecting operator. */
public interface ConnectorService {
  /**
   * @param request
   * @return
   */
  DasAuthResponse auth(Long companyId, DasAuthRequest request);

  /**
   * @param request
   * @return
   */
  DasBalanceResponse balance(Long companyId, DasBalanceRequest request);

  /**
   * @param request
   * @return
   */
  DasTransactionResponse transaction(Long companyId, DasTransactionRequest request);

  /**
   * @param request
   * @return
   */
  DasEndRoundResponse endRound(Long companyId, DasEndRoundRequest request);

  /**
   * @param companyId
   * @param hmacHash
   * @param rawData
   */
  void validate(Long companyId, String hmacHash, String rawData);

  /**
   * validate incoming operator ip.
   *
   * @param companyId
   * @param callerIp
   */
  void validateIp(Long companyId, String callerIp);
}
