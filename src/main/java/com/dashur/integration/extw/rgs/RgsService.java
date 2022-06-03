package com.dashur.integration.extw.rgs;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.extw.rgs.data.GameHash;
import com.dashur.integration.extw.rgs.elysium.ElysiumRgsService;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class RgsService {
   @Inject @RestClient ElysiumRgsService elysiumRgs;

   public enum Provider {
    ELYSIUM;
   }

   public RgsServiceProvider getProvider(String rgsProvider) {
    if (rgsProvider.equals(Provider.ELYSIUM.toString())) {
      return (RgsServiceProvider) elysiumRgs;
    }
    throw new ApplicationException("Unsupported rgs provider [%s] != [%s]", rgsProvider, Provider.ELYSIUM.toString());
   }
}
