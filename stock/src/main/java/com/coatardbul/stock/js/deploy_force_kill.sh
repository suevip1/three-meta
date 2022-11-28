while 2>1; do

  pid1=$(netstat -antlp|grep  CLOSE_WAIT |wc -l)

  if [ $pid1 -gt  2000 ];then

    echo "总数  $pid1"

     pid4=$(ps -ef | grep stock  | awk '{print $2}')
     kill -9 $pid4
     echo "kill -9  $pid4"

     cd /usr/local/sail
     sh deploy_all.sh

     sleep 5

     cd /usr/local/stock
     sh deploy.sh
  fi

  cd  /usr/local/sail/logs
  sailCount=$(ls |wc -l)

    if [ $sailCount -gt  30 ];then

      echo "总数  $sailCount"

       rm -rf spring.log.*
    fi
cd  /usr/local/sail1/logs
  sail1Count=$(ls |wc -l)

    if [ $sail1Count -gt  30 ];then

      echo "总数  $sail1Count"

       rm -rf spring.log.*
    fi

    cd  /usr/local/sail2/logs
      sail2Count=$(ls |wc -l)

        if [ $sail2Count -gt  30 ];then

          echo "总数  $sail2Count"

           rm -rf spring.log.*
        fi



  sleep 30
done
