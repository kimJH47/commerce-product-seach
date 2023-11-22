GREEN_PORT=0
BLUE_PORT=0
if nc -z localhost 8080; then
  GREEN_PORT=8081
  BLUE_PORT=8080
else
  GREEN_PORT=8080
  BLUE_PORT=8081
fi

HOME_PATH="/home/ubuntu"
JAR_PATH="/home/ubuntu"
HEALTH_CHECK_URL="http://localhost:$GREEN_PORT/api/categories/TOP"

TRY_COUNT=10
WAIT_TIME=5
echo "BLUE_PORT(old) : $BLUE_PORT, GRREN_PORT(new) : $GREEN_PORT"

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

cd "$HOME_PATH" || exit 1
echo "새로운 어플리케이션 서버를 실행했습니다."
nohup java -jar -Duser.timezone=Asia/Seoul "$JAR_FILE" --spring.profiles.active=prod --server.port="$GREEN_PORT" &
while true; do
  STATUS=$(curl -s -o :/dev/null -w '%{http_code}' "$HEALTH_CHECK_URL")
  if [ "$STATUS" -eq 200 ]; then
    echo "$GREEN_PORT 포트로 열린 서버가 정상적으로 동작중."
    echo "NGINX 포트 포워딩 변경"
    sudo sed -i "s/${BLUE_PORT}/$GREEN_PORT/" /etc/nginx/conf.d/default.conf
    sudo nginx -s reload
    sleep 8
    PID=$(sudo lsof -t -i:$BLUE_PORT)
    if [ -n "$PID" ]; then
      sudo kill -15 "$PID"
      echo "$BLUE_PORT 포트로 열린 서버 종료"
    fi
    echo "$GREEN_PORT 포트로 Green Blue 실행 완료."
    exit 0
  fi
  TRY_COUNT=$((TRY_COUNT - 1))
  if [ $TRY_COUNT -le 0 ]; then
    break
  fi
  echo "서버가 켜졌는지 대기중... 남은 시도 횟수=$TRY_COUNT, $WAIT_TIME초 대기합니다."
  sleep $WAIT_TIME
done

echo "$GREEN_PORT 포트로 서버를 실행하는데 실패했습니다."
exit 1
