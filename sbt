#!/bin/sh
sbtansicolor=${SBTANSICOLOUR:-false}
java -Dsbt.boot.directory=project/boot/ -Dsbt.log.noformat=$sbtansicolor -XX:MaxPermSize=1024M -Xmx2048m -jar `find tools -name sbt-launch*.jar` "$@"
