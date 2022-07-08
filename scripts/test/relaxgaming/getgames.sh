#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

AUTH="Basic ZW06dGVzdA=="
CREDENTIALS='{ "partnerid": 10, "src": "dev" }'
REQ='{"credentials":'$CREDENTIALS', "jurisdiction": "SE"}'

echo "$REQ"
echo "<<<"
curl -d "$REQ" \
	-H "Authorization: $AUTH" \
	-H "Content-Type: application/json" \
	"$BASE_URL/v1/extw/exp/relaxgaming/games/getgames"