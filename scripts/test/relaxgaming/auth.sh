#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

HMAC_KEY="test"
GAME_ID=8243

UUID=$(uuid)
TIME=$(date -u +'%Y-%m-%d %H:%M:%S.%3N')
TOKEN="testticket-em1" # $(sh fetchtoken.sh)
OPR_META='{ "opr_meta": { "gameRef": "rlx.em.em.'$GAMEID'", "clientId": "dev_test", "req_ip_addr": "127.0.0.1" }}'
REQ='{"req_id":"'$UUID'","timestamp":"'$TIME'","token":"'$TOKEN'","ctx":'$OPR_META'}'
HMAC=$(echo -n "$REQ" | openssl dgst -hmac "$HMAC_KEY" -binary | base64)

curl -v "$BASE_URL/v1/extw/connect/relaxgaming/$COMPANY_ID/v1/auth" -d "$REQ" -H "X-DAS-HMAC: $HMAC"
#curl -v "$BASE_URL/v1/auth" -d "$REQ" -H "X-DAS-HMAC: $HMAC"