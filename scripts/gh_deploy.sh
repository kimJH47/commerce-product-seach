PORT=-1;
if nc -z localhost 8080; then
	PORT=8081
else
	PORT=8080
fi

HOME_PATH="/home/ubuntu"
JAR_PATH="/home/ubuntu"
URL="http://localhost:$PORT/api/cart"

SHUT_PORT=0
TRY_COUNT=10
WAIT_TIME=5
GREEN_PORT=8081
BLUE_PORT=8080

if ! [ -d "$HOME_PATH" ]; then
  echo "HOME_PATH 경로가 존재하지 않습니다! HOME_PATH= $HOME_PATH"
  exit 1
fi

if ! [ -d "$JAR_PATH" ]; then
  echo "JAR_PATH 경로가 존재하지 않습니다! JAR_PATH= $JAR_PATH"
  exit 1
fi

JAR_FILE=$(find "$JAR_PATH" -maxdepth 1 -name '*.jar' -print -quit)
if [ -z "$JAR_FILE" ]; then
  echo "$JAR_PATH 경로에 JAR 파일이 없거나 2개 이상입니다."
  exit 1
fi

if [ "$PORT" -eq "$GREEN_PORT" ]; then
  SHUT_PORT=$BLUE_PORT
else
  SHUT_PORT=$GREEN_PORT
fi

cd "$HOME_PATH" || exit 1
echo "서버를 실행했습니다. PORT = $PORT, SHUT_PORT = $SHUT_PORT"
nohup java -jar -Duser.timezone=Asia/Seoul "$JAR_FILE" --spring.profiles.active=prod --server.port="$PORT" &
sleep 3
while true; do
  STATUS=$(curl -s -o /dev/null -w '%{http_code}' "$URL")
  if [ "$STATUS" -eq 400 ]; then
    echo "$PORT 포트로 열린 서버가 정상적으로 동작중."
    echo "NGINX 포트 포워딩 변경"
    sudo sed -i "s/${SHUT_PORT}/$PORT/" /etc/nginx/conf.d/default.conf
    sudo nginx -s reload

    PID=$(sudo lsof -t -i:$SHUT_PORT)
    if [ -n "$PID" ]; then
      kill -15 "$PID"
      echo "$SHUT_PORT 포트로 열린 서버 종료"
    fi
    echo "$PORT 포트로 Green Blue 실행 완료."
    exit 0
  fi
  TRY_COUNT=$((TRY_COUNT - 1))
  if [ $TRY_COUNT -le 0 ]; then
    break
  fi
  echo "서버가 켜졌는지 대기중... 남은 시도 횟수=$TRY_COUNT, $WAIT_TIME초 대기합니다."
  sleep $WAIT_TIME
done

echo "$PORT 포트로 서버를 실행하는데 실패했습니다."
exit 1