#!/bin/bash

REPO_URL="https://github.com/kimJH47/zerobase-commerce"

HOME_PATH="/home/ubuntu"
DIR_PATH="/home/ubuntu/application"
BRANCH_NAME="master"

echo "서버 시작 스크립트 실행"

if [ -d "$DIR_PATH" ]; then
  cd $DIR_PATH || return

  git fetch

  git checkout $BRANCH_NAME

  echo "원격 저장소에서 프로젝트를 업데이트 합니다."
  git pull --rebase
else
  echo "프로젝트가 존재하지 않습니다. 프로젝트를 받아옵니다."
  git clone -b $BRANCH_NAME $REPO_URL $DIR_PATH
  cd $DIR_PATH || return
fi

PID=$(lsof -t -i:8080)
if [ -n "$PID" ]; then
  echo "8080 포트로 실행중인 프로세스가 있습니다. 해당 프로세스를 종료합니다."
  kill -15 $PID
  sleep 5
fi

echo "프로젝트를 빌드 시작"
cd ecommerce || return
./gradlew bootJar
echo "프로젝트 빌드 완료"

echo "서버를 시작합니다."
cd $HOME_PATH || return
nohup java -jar -Duser.timezone=Asia/Seoul ${DIR_PATH}/ecommerce/build/libs/*.jar