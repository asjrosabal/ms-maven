#!/bin/bash -eux
ZAP_TARGET_URL=$1
ZAP_REPORTS_PATH=$2
ZAP_ALERT_LVL=$3

echo "ZAP_TARGET_URL='$ZAP_REPORTS_PATH'"
echo "ZAP_REPORTS_PATH='$ZAP_REPORTS_PATH'"
echo "ZAP_REPORTS_PATH='$ZAP_ALERT_LVL'"

reportsDirectory="$ZAP_REPORTS_PATH/reports"
mkdir $reportsDirectory

ZAP_CONT_ID=$(docker run --name zap -p 2375:2375 -d owasp/zap2docker-stable zap.sh -daemon \
-port 2375 \
-host 127.0.0.1 \
-config api.disablekey=true \
-config scanner.attackOnStart=true \
-config view.mode=attack \
-config connection.dnsTtlSuccessfulQueries=-1 \
-config api.addrs.addr.name=.* \
-config api.addrs.addr.regex=true)

docker exec $ZAP_CONT_ID zap-cli -v -p 2375 status -t 120
docker exec $ZAP_CONT_ID zap-cli -v -p 2375 open-url $ZAP_TARGET_URL
docker exec $ZAP_CONT_ID zap-cli -v -p 2375 active-scan $ZAP_TARGET_URL
docker exec $ZAP_CONT_ID zap-cli -v -p 2375 spider $ZAP_TARGET_URL
docker exec $ZAP_CONT_ID zap-cli -v -p 2375 active-scan --scanners xss,sqli --recursive $ZAP_TARGET_URL

docker exec $ZAP_CONT_ID zap-cli -p 2375 report -o /home/zap/report.html -f html
docker exec $ZAP_CONT_ID zap-cli -p 2375 report -o /home/zap/report.xml -f xml

docker cp $ZAP_CONT_ID:/home/zap/report.xml $reportsDirectory
docker cp $ZAP_CONT_ID:/home/zap/report.html $reportsDirectory

# Check alerts
ALERT_CNT=$(docker exec $ZAP_CONT_ID zap-cli -p 2375 --verbose alerts --alert-level $ZAP_ALERT_LVL -f json | jq length)

if [[ "${ALERT_CNT}" -gt 0 ]]; then
  echo "Vulnerabilities dectected, Lvl='$ZAP_ALERT_LVL' Alert count='${ALERT_CNT}'"
  exit 1
fi
