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

TX_ID=114551608043791948 # $(uuid -F siv | cut -c 3-20)
PLAYER_ID=83224
GAMEREF=rlx.em.em.7985
AMOUNT=10
FREESPINS_VALUE=100
EXPIRES=$(date -u -Iseconds --date "today + 7 days")

#user_input 'enter playerid (empty to cancel):'
#PLAYER_ID=$INPUT
#echo "PLAYER_ID $PLAYER_ID"

CRED='{ "partnerid": '$PARTNER_ID', "src": "partnerapi", "bouser": "" }'
REQ='{ "credentials": '$CRED', "jurisdiction": "EU", "txid": '$TX_ID', "playerid": '$PLAYER_ID',  
	"partnerid": '$PARTNER_ID', "gameref": "'$GAMEREF'", "amount": '$AMOUNT', 
	"freespinvalue": '$FREESPINS_VALUE', "expires": "'$EXPIRES'" }'

echo $REQ

curl -v "$BASE_URL/v1/extw/exp/relaxgaming/freespins/add" -d "$REQ" -H "Content-Type: application/json" -H "Authorization: Basic ZW06dGVzdA=="
