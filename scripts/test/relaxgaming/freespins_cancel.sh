#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi


INPUT=""
user_input() {
	read -p "$1" INPUT
	if [ -z $INPUT ];
	then
		exit 1;
	fi
}

PLAYER_ID=83224
GAMEREF=rlx.em.em.7985
AMOUNT=10
FREESPINS_ID=1107
EXPIRES=$(date -u -Iseconds --date "today + 7 days")

user_input "enter freespinsid (empty to cancel. example $FREESPINS_ID):"
FREESPINS_ID=$INPUT

CRED='{ "partnerid": '$PARTNER_ID', "src": "partnerapi", "bouser": "" }'
REQ='{ "credentials": '$CRED', "jurisdiction": "EU", "playerid": '$PLAYER_ID',  
	"partnerid": '$PARTNER_ID', "freespinsid": "'$FREESPINS_ID'"}'

echo $REQ

curl -v "$BASE_URL/v1/extw/exp/relaxgaming/freespins/cancel" -d "$REQ" -H "Content-Type: application/json" -H "Authorization: Basic ZW06dGVzdA=="
