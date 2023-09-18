#!/bin/bash

REPO_URL="https://github.com/kimJH47/zerobase-commerce"

HOME_PATH="/home/ubuntu"
DIR_PATH="/home/ubuntu/application"
BRANCH_NAME="master"


if [ -d "$DIR_PATH" ]; then
  cd $DIR_PATH || return
  echo "setting jar file permission..."
  chmod +x *.jar
  echo " done!"
fi

echo " check using port..."
CURRENT_PID=$(sudo lsof -t -i :8080)
if [ -z $CURRENT_PID ];
then
  echo "not used port!"
else
  echo "killed process PID = $CURRENT_PID"
  sudo kill -15 $CURRENT_PID
  sleep 5
fi
echo "done!"

echo "start spring boot server!"
cd $HOME_PATH || return
sudo nohup java -jar -Dspring.profiles.active=prod -Duser.timezone=Asia/Seoul ${DIR_PATH}/*.jar > /dev/null 2>>/home/ubuntu/deploy/deploy_err.log &
echo "done!"