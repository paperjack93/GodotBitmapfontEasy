package com.dashur.integration.extw.playchecks;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.extw.playchecks.elysium.ElysiumCheckService;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class CheckService {
   @Inject @RestClient ElysiumCheckService elysiumCheckService;

   public enum Provider {
    ELYSIUM;
   }

   public CheckService getProvider(String provider) {
    if (provider.equals(Provider.ELYSIUM.toString())) {
      return (CheckService) elysiumCheckService;
    }
    throw new ApplicationException("Unsupported playcheck service provider [%s]", provider);
   }
}
