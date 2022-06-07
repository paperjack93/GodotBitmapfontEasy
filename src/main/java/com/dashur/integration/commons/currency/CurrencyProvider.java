package com.dashur.integration.commons.currency;

import java.util.*;
import java.util.stream.Collectors;
import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.spi.CurrencyProviderSpi;
import lombok.extern.slf4j.Slf4j;
import org.javamoney.moneta.CurrencyUnitBuilder;

@Slf4j
public class CurrencyProvider implements CurrencyProviderSpi {

  // List of currencies not available in JDK
  public static final String CURRENCY_CODE_BTC = "BTC";
  public static final String CURRENCY_CODE_CNH = "CNH";
  public static final String CURRENCY_CODE_GGP = "GGP";
  public static final String CURRENCY_CODE_IMP = "IMP";
  public static final String CURRENCY_CODE_JEP = "JEP";
  public static final String CURRENCY_CODE_MBC = "MBC"; // mini  BTC or mBTC which is 1/1000 BTC
  public static final String CURRENCY_CODE_UBC = "UBC"; // micro BTC or uBTC which is 1/1000000 BTC
  public static final String CURRENCY_CODE_ARX = "ARX"; // DAS-75, ARS informal
  public static final String CURRENCY_CODE_VEX = "VEX"; // DAS-75, VEX Blackmarket
  public static final String CURRENCY_CODE_TTH = "TTH"; // TTH USD-Tether

  public static final CurrencyUnit CURRENCY_BTC = Monetary.getCurrency(CURRENCY_CODE_BTC);
  public static final CurrencyUnit CURRENCY_MBC = Monetary.getCurrency(CURRENCY_CODE_MBC);
  public static final CurrencyUnit CURRENCY_UBC = Monetary.getCurrency(CURRENCY_CODE_UBC);
  public static final CurrencyUnit CURRENCY_CNH = Monetary.getCurrency(CURRENCY_CODE_CNH);
  public static final CurrencyUnit CURRENCY_GGP = Monetary.getCurrency(CURRENCY_CODE_GGP);
  public static final CurrencyUnit CURRENCY_IMP = Monetary.getCurrency(CURRENCY_CODE_IMP);
  public static final CurrencyUnit CURRENCY_JEP = Monetary.getCurrency(CURRENCY_CODE_JEP);
  public static final CurrencyUnit CURRENCY_ARX = Monetary.getCurrency(CURRENCY_CODE_ARX);
  public static final CurrencyUnit CURRENCY_VEX = Monetary.getCurrency(CURRENCY_CODE_VEX);
  public static final CurrencyUnit CURRENCY_TTH = Monetary.getCurrency(CURRENCY_CODE_TTH);

  /** Internal shared cache of {@link javax.money.CurrencyUnit} instances. */
  private static Map<String, CurrencyUnit> CACHED = new HashMap<>();

  public CurrencyProvider() {
    Map<String, CurrencyUnit> loadMap = new HashMap<>();
    for (Currency jdkCurrency : Currency.getAvailableCurrencies()) {
      CurrencyUnit cu = new JDKCurrencyAdapter(jdkCurrency);
      loadMap.put(cu.getCurrencyCode(), cu);
    }
    CACHED = loadMap;
    if (CACHED.size() != Currency.getAvailableCurrencies().size()) {
      log.error(
          "Loaded currencies differs from available currencies loaded: {} jvm: {}",
          CACHED.keySet().size(),
          Currency.getAvailableCurrencies().size());
      throw new IllegalStateException("Loaded currencies differs from available currencies");
    }

    // DAS-1471 Add BTC as supported currency
    CACHED.put(
        CURRENCY_CODE_BTC,
        CurrencyUnitBuilder.of(CURRENCY_CODE_BTC, getProviderName())
            .setDefaultFractionDigits(8)
            .build(true));

    // DASSUP-52 Add new currencies
    CACHED.put(
        CURRENCY_CODE_CNH,
        CurrencyUnitBuilder.of(CURRENCY_CODE_CNH, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));
    CACHED.put(
        CURRENCY_CODE_GGP,
        CurrencyUnitBuilder.of(CURRENCY_CODE_GGP, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));
    CACHED.put(
        CURRENCY_CODE_IMP,
        CurrencyUnitBuilder.of(CURRENCY_CODE_IMP, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));
    CACHED.put(
        CURRENCY_CODE_JEP,
        CurrencyUnitBuilder.of(CURRENCY_CODE_JEP, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));

    // DASSUP-52 MBC and UBC mBTC and uBTC
    CACHED.put(
        CURRENCY_CODE_MBC,
        CurrencyUnitBuilder.of(CURRENCY_CODE_MBC, getProviderName())
            .setDefaultFractionDigits(5)
            .build(true));
    CACHED.put(
        CURRENCY_CODE_UBC,
        CurrencyUnitBuilder.of(CURRENCY_CODE_UBC, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));

    // DAS-75 add in VEF Blackmarket rate & ARS informal rate.
    CACHED.put(
        CURRENCY_CODE_ARX,
        CurrencyUnitBuilder.of(CURRENCY_CODE_ARX, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));
    CACHED.put(
        CURRENCY_CODE_VEX,
        CurrencyUnitBuilder.of(CURRENCY_CODE_VEX, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));

    // TTH USD-Tether
    CACHED.put(
        CURRENCY_CODE_TTH,
        CurrencyUnitBuilder.of(CURRENCY_CODE_TTH, getProviderName())
            .setDefaultFractionDigits(2)
            .build(true));

    log.info(
        "Loaded {} currencies into cache\n{}",
        CACHED.size(),
        CACHED.keySet().stream().sorted().collect(Collectors.toList()));
  }

  @Override
  public String getProviderName() {
    return "default";
  }

  /**
   * Return a {@link CurrencyUnit} instances matching the given {@link javax.money.CurrencyContext}.
   *
   * @param currencyQuery the {@link javax.money.CurrencyContext} containing the parameters
   *     determining the query. not null.
   * @return the corresponding {@link CurrencyUnit}, or null, if no such unit is provided by this
   *     provider.
   */
  public Set<CurrencyUnit> getCurrencies(CurrencyQuery currencyQuery) {
    Set<CurrencyUnit> result = new HashSet<>();
    if (!currencyQuery.getCurrencyCodes().isEmpty()) {
      for (String code : currencyQuery.getCurrencyCodes()) {
        CurrencyUnit cu = CACHED.get(code);
        if (cu != null) {
          result.add(cu);
        }
      }
      return result;
    }
    if (!currencyQuery.getCountries().isEmpty()) {
      for (Locale country : currencyQuery.getCountries()) {
        CurrencyUnit cu = getCurrencyUnit(country);
        if (cu != null) {
          result.add(cu);
        }
      }
      return result;
    }
    result.addAll(CACHED.values());
    return result;
  }

  private CurrencyUnit getCurrencyUnit(Locale locale) {
    Currency cur;
    try {
      cur = Currency.getInstance(locale);
      if (Objects.nonNull(cur)) {
        return CACHED.get(cur.getCurrencyCode());
      }
    } catch (Exception e) {
      log.error("No currency for locale found: " + locale);
    }
    return null;
  }
}
