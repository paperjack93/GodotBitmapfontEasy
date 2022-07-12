#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

read -p 'enter playerid (empty to cancel):' PLAYER_ID

if [ -z $PLAYER_ID ];
then
	exit 1
fi



CRED='{ "partnerid": '$PARTNER_ID', "src": "partnerapi", "bouser": "" }'
REQ='{ "credentials": '$CRED', "jurisdiction": "EU", "playerid": "'$PLAYER_ID'", "partnerid": "'$PARTNER_ID'" }'

echo $REQ

curl -v "$BASE_URL/v1/extw/exp/relaxgaming/freespins/get" -d "$REQ" -H "Content-Type: application/json" -H "Authorization: Basic ZW06dGVzdA=="
