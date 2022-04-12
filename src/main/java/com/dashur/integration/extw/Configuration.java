package com.dashur.integration.extw;

import org.eclipse.microprofile.config.Config;

/** Configuration classes implementation */
public interface Configuration {
  /** @return the operator name. */
  String operator();

  /**
   * @param config global config
   * @return the configured configuration.
   */
  Configuration configure(Config config);
}
