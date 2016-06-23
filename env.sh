#!/bin/bash
ENV_FILE="$WORKSPACE/pay-scripts/services/cardid.env"
if [ -f $ENV_FILE ]
then
  set -a
  source $ENV_FILE
  set +a  
fi

export CERTS_PATH=$WORKSPACE/pay-scripts/services/ssl/certs

export TEST_CARD_DATA_LOCATION=./data/sources/test-cards
export WORLDPAY_DATA_LOCATION=./data/sources/worldpay
export DISCOVER_DATA_LOCATION=./data/sources/discover

eval "$@"
