#!/bin/bash
#REQ='
#{
#  "game": "fox-tale", // the id of the game, optional
#  "id": "1234 4567 8900", // the id of the round
#  "start": "2022-06-09 10:39:21.149", // the start timestamp, i.e. when the round was started
#  "end": "2022-06-09 10:39:28.287", // the end timestamp, i.e. when the round was closed
#  "betamount": 0.600, // the stake /bet/ amount
#  "winamount": 0.150, // the won amount
#  "currency": "EUR", // the currency code
#
#  // the round symbols - it is a triple nested array because if there is free spins
#  // they would be multiple rounds in the array
#  "rounds": [
#    [[6, 1, 0], [6, 0, 1], [1, 11, 3], [1, 7, 0], [1, 5, 8]],
#    [[0, 0, 2], [6, 7, 10], [1, 9, 8], [2, 2, 0], [4, 7, 4]],
#  ],
#}'
REQ='{"id": "1234 5678","start": "2022-06-09 10:39:21.149","end": "2022-06-09 10:39:28.287","betamount": 0.600,"winamount": 0.150,"currency": "EUR","rounds": [[[6, 1, 0], [6, 0, 1], [1, 11, 3], [1, 7, 0], [1, 5, 8]]]}'
DATA=$(echo "$REQ" | base64 -w 0)
URL="https://dev.elysiumstudios.se/game-history?game=fox-tale&d=$DATA"
echo "$URL"
curl -X GET -L "$URL"