#!/bin/sh
### BEGIN INIT INFO
# Provides:          mouthpiece
# Required-Start:    $local_fs $remote_fs $network $syslog
# Required-Stop:     $local_fs $remote_fs $network $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# X-Interactive:     true
# Short-Description: Start/stop fin-kratzen server
### END INIT INFO

# Install via the following:
# sudo chmod +x fin-kratzen.sh
# sudo /usr/sbin/update-rc.d -f fin-kratzen.sh defaults

WORK_DIR="/home/pi/fin-kratzen"
JAR="fin-kratzen.jar"
USER="pi"
DAEMON="/usr/bin/java"
DAEMON_ARGS="-server \
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=8004 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false \
-Djava.rmi.server.hostname=stout-pi.local \
-jar $WORK_DIR/$JAR --server"

start () {
  echo "Starting fin-kratzen..."
  if [ ! -f $WORK_DIR/pid ]; then
    start-stop-daemon --start --verbose --background --chdir $WORK_DIR --exec $DAEMON \
      --pidfile $WORK_DIR/fin-kratzen.pid --chuid "$USER" --make-pidfile -- $DAEMON_ARGS 2>/dev/null
  else
    echo "fin-kratzen is already running..."
  fi
}

stop () {
    echo "Stopping fin-kratzen..."
    start-stop-daemon --stop --exec $DAEMON --pidfile $WORK_DIR/fin-kratzen.pid
    rm $WORK_DIR/fin-kratzen.pid
}

case $1 in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        stop
        start
    ;;
esac
