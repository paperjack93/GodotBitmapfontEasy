#!/bin/sh

if [ -z "$BASE_URL" ];
then
	echo "run either: source server_local.sh or source server_remote.sh"
	exit 1;
fi

#GAME_ID=7797
GAME_ID=8243

curl -v -X GET "$BASE_URL/v1/extw/exp/relaxgaming/launch?gameid=$GAME_ID&ticket=testticket_em1&lang=en_GB&channel=web&partnerid=10&moneymode=fun&currency=EUR&clientid=dev" 
#&homeurl=localhost"