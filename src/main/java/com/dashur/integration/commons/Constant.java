package com.dashur.integration.commons;

import com.google.common.collect.Sets;
import java.time.ZoneId;
import java.util.Set;

public final class Constant {
  public static final String DEFAULT_TIMEZONE_CODE = "UTC";
  public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of(DEFAULT_TIMEZONE_CODE);

  public static final String REST_HEADER_AUTHORIZATION = "Authorization";
  public static final String REST_HEADER_X_DAS_TZ = "X-DAS-TZ";
  public static final String REST_HEADER_X_DAS_CURRENCY = "X-DAS-CURRENCY";
  public static final String REST_HEADER_X_DAS_TX_ID = "X-DAS-TX-ID";
  public static final String REST_HEADER_X_DAS_TX_LANG = "X-DAS-LANG";
  public static final String REST_HEADER_X_DAS_TENANT_ID = "X-DAS-TENANT-ID";

  public static final String REST_HEADER_VALUE_DEFAULT_TZ = DEFAULT_TIMEZONE_CODE;
  public static final String REST_HEADER_VALUE_DEFAULT_CURRENCY = "USD";
  public static final String REST_HEADER_VALUE_DEFAULT_LANG = "en";

  public static final String REST_AUTH_PARAM_GRANT_TYPE = "grant_type";
  public static final String REST_AUTH_PARAM_CLIENT_ID = "client_id";
  public static final String REST_AUTH_PARAM_CLIENT_SECRET = "client_secret";
  public static final String REST_AUTH_PARAM_REFRESH_TOKEN = "refresh_token";
  public static final String REST_AUTH_PARAM_USERNAME = "username";
  public static final String REST_AUTH_PARAM_PASSWORD = "password";
  public static final String REST_AUTH_PARAM_USER_ID = "user_id";

  public static final String REST_AUTH_AUTHORIZATION_TYPE_BASIC = "Basic";
  public static final String REST_AUTH_AUTHORIZATION_TYPE_BEARER = "Bearer";
  public static final String REST_AUTH_AUTHORIZATION_TYPE_DIGEST = "Digest";
  public static final String REST_AUTH_AUTHORIZATION_TYPE_HOBA = "HOBA";
  public static final String REST_AUTH_AUTHORIZATION_TYPE_MUTUAL = "Mutual";
  public static final String REST_AUTH_AUTHORIZATION_TYPE_AWS4_HMAC_SHA256 = "AWS4-HMAC-SHA256";
  public static final String REST_AUTH_GRANT_TYPE_PASSWORD = "password";
  public static final String REST_AUTH_GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
  public static final String REST_AUTH_GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
  public static final String REST_AUTH_GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";

  public static final String CACHE_NAME_ACCESS_TOKEN = "das-integ.cache.accesstoken";
  public static final String CACHE_NAME_REFRESH_TOKEN = "das-integ.cache.refreshtoken";
  public static final String CACHE_NAME_SESSION_TOKEN = "das-integ.cache.sessiontoken";
  public static final String CACHE_NAME_CURRENCY = "das-integ.cache.currency";
  public static final String CACHE_NAME_AFFILIATE_ID = "das-integ.cache.affiliate-id";
  public static final String CACHE_NAME_CLIENT_APP_ACCESS_TOKEN =
      "das-integ.cache.client-app.accesstoken";
  public static final String CACHE_NAME_ACCOUNT_INFO = "das-integ.cache.account-info";
  public static final String CACHE_NAME_ITEM_INFO = "das-integ.cache.item-info";
  public static final String CACHE_NAME_BET_LEVEL = "das-integ.cache.bet-level";
  public static final String CACHE_NAME_GAME_PARAMS = "das-integ.cache.game-params";
  public static final String CACHE_NAME_GAME_CONFIG = "das-integ.cache.game-config";
  public static final String CACHE_NAME_CAMPAIGN_ASSIGNMENT = "das-integ.cache.campaign-assignment";
  public static final String CACHE_NAME_CAMPAIGN_VOUCHER = "das-integ.cache.campaign-voucher";
  public static final String CACHE_NAME_LINKED_ACCOUNT_ID = "das-integ.cache.linked-account-id";
  public static final String CACHE_NAME_IS_LINKED_COMPANY = "das-integ.cache.is-linked-company";

  public static final String DAS_TX_CATEGORY_WAGER = "WAGER";
  public static final String DAS_TX_CATEGORY_PAYOUT = "PAYOUT";
  public static final String DAS_TX_CATEGORY_REFUND = "REFUND";
  public static final String DAS_TX_CATEGORY_ENDROUND = "ENDROUND";
  public static final String DAS_TX_CATEGORY_RESERVE = "RESERVE";
  public static final String DAS_TX_CATEGORY_RELEASE = "RELEASE";
  public static final String DAS_TX_SUB_CATEGORY_FREEGAME = "FREE";
  public static final String DAS_TX_SUB_CATEGORY_POOL = "POOL";
  public static final String DAS_TX_META_DATA_DUPLICATE = "duplicate";

  public static final String VENDOR_GNRC_MAVERICK = "maverick";
  public static final String VENDOR_GNRC_EVERYMATRIX = "everymatrix";
  public static final String VENDOR_GNRC_KIRON = "kiron";
  public static final String VENDOR_GNRC_TOPPLAYER = "topplayer";
  public static final String VENDOR_GNRC_YAHU = "yahu";
  public static final String VENDOR_ELK = "elk";
  public static final String VENDOR_PNG = "png";
  public static final String VENDOR_REDR = "redr";
  public static final String VENDOR_RELAX_RG = "rg";
  public static final String VENDOR_RELAX_QS = "qs";
  public static final String VENDOR_RELAX_4TP = "4tp";
  public static final String VENDOR_YGGDRASIL = "yg";
  public static final String VENDOR_NETENT = "ne";
  public static final String VENDOR_BOOONGO = "bngo";
  public static final String VENDOR_BLUEPRINT = "blueprint";
  public static final String VENDOR_PLAYTECH = "ptech";
  public static final String VENDOR_DRAGOON = "dragoon";
  public static final String VENDOR_EVOLUTION = "evo";
  public static final String VENDOR_SLOTEGRATOR = "slote";
  public static final String VENDOR_EGT = "egt";

  public static final String ENV_KEY_VENDOR_ID_MAPPING = "VENDOR_ID_MAPPING";

  public static final String LAUNCHER_META_DATA_KEY_GAME_ID = "req_game_id";
  public static final String LAUNCHER_META_DATA_KEY_IP_ADDRESS = "req_ip_addr";
  public static final String LAUNCHER_META_DATA_KEY_META_DATA = "meta_data";
  public static final String LAUNCHER_META_DATA_KEY_OPR_META = "opr_meta";
  public static final String LAUNCHER_META_DATA_KEY_LANG = "lang";

  public static final Set<String> SUPPORTED_CURRENCIES =
      Sets.newHashSet(
          "AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AWG", "AZN", "ARX", "BAM", "BBD", "BDT",
          "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTC", "BTN", "BWP", "BYN", "BZD",
          "CAD", "CDF", "CHF", "CLF", "CLP", "CNH", "CNY", "COP", "CRC", "CUC", "CUP", "CVE", "CZK",
          "DJF", "DKK", "DOP", "DZD", "EGP", "ERN", "ETB", "EUR", "FJD", "FKP", "GBP", "GEL", "GGP",
          "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD", "HKD", "HNL", "HRK", "HTG", "HUF", "IDR", "ILS",
          "IMP", "INR", "IQD", "IRR", "ISK", "JEP", "JMD", "JOD", "JPY", "KES", "KGS", "KHR", "KMF",
          "KPW", "KRW", "KWD", "KYD", "KZT", "LAK", "LBP", "LKR", "LRD", "LSL", "LYD", "MAD", "MDL",
          "MGA", "MKD", "MMK", "MNT", "MOP", "MRO", "MRU", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN",
          "MBC", "NAD", "NGN", "NIO", "NOK", "NPR", "NZD", "OMR", "PAB", "PEN", "PGK", "PKR", "PLN",
          "PYG", "QAR", "RON", "RSD", "RUB", "RWF", "SAR", "SBD", "SCR", "SDG", "SEK", "SHP", "SLL",
          "SOS", "SRD", "SSP", "STD", "STN", "SVC", "SYP", "SZL", "THB", "TJS", "TMT", "TND", "TOP",
          "TRY", "TTD", "TZS", "UAH", "UGX", "USD", "UYU", "UZS", "UBC", "VEF", "VES", "VND", "VUV",
          "VEX", "WST", "XAF", "XAG", "XAU", "XCD", "XDR", "XOF", "XPD", "XPF", "XPT", "YER", "ZAR",
          "ZMW", "ZWL");

  public static final Set<String> DAS_FAILED_REFUND_SUB_CATEGORIES =
      Sets.newHashSet("ORIGINAL_NOT_FOUND", "MORE_THAN_ONE_ORIGINAL", "AMOUNT_EXCEEDS_ORIGINAL");

  public static final String PROPERTY_EMPTY_VALUE = "not-used-left-empty";
}
