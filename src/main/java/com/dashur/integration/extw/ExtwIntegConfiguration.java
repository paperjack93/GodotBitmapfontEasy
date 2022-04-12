package com.dashur.integration.extw;

import com.dashur.integration.commons.exception.ApplicationException;
import com.dashur.integration.commons.exception.EntityNotExistException;
import com.dashur.integration.extw.connectors.everymatrix.EveryMatrixConfiguration;
import com.dashur.integration.extw.connectors.parimatch.PariMatchConfiguration;
import com.dashur.integration.extw.connectors.qt.QtConfiguration;
import com.dashur.integration.extw.connectors.vgs.VgsConfiguration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.quarkus.logging.Log; 

@ApplicationScoped
@Getter
@Slf4j
public class ExtwIntegConfiguration {
  @ConfigProperty(name = "extw.version", defaultValue = "v1.0")
  String version;

  Map<String, Configuration> operators;

  /**
   * @param operatorCode
   * @return
   */
  public Configuration configuration(String operatorCode) {
    System.out.format("configuration was called %s\n", operatorCode);
//    Log.info("configuration was called (Log.info)");
    if (Objects.isNull(operators) || operators.isEmpty() || !operators.containsKey(operatorCode)) {
      throw new EntityNotExistException(
          "Unable to find operator [%s - %s - %s]",
          operatorCode, (Objects.isNull(operators)), (operators.isEmpty()));
    }

    return operators.get(operatorCode);
  } 

  /**
   * get configuration and cast.
   *
   * @param operatorCode
   * @param type
   * @param <T>
   * @return
   */
  public <T extends Configuration> T configuration(String operatorCode, Class<T> type) {
    return (T) configuration(operatorCode);
  }

  @PostConstruct
  public void init() {
    System.out.println("init was called (System.out.println)");
    Log.info("init was called (log.info)");
    Config config = ConfigProvider.getConfig();
    {
      // parse and handle operator
      operators = new ConcurrentHashMap<>();
      String[] operatorList = config.getValue("extw.operators", String.class).split(",");

      for (String code : operatorList) {
        System.out.format("creating config for operator %s\n", code);
        switch (code) {
          case Constant.OPERATOR_EVERYMATRIX:
            this.operators.put(code, new EveryMatrixConfiguration().configure(config));
            break;
          case Constant.OPERATOR_PARIMATCH:
            this.operators.put(code, new PariMatchConfiguration().configure(config));
            break;
          case Constant.OPERATOR_QTECH:
            this.operators.put(code, new QtConfiguration().configure(config));
            break;
          case Constant.OPERATOR_VGS:
            this.operators.put(code, new VgsConfiguration().configure(config));
            break;
          default:
            throw new ApplicationException("Not supported operator code - [%s]", code);
        }
      }
    }
  }
}
