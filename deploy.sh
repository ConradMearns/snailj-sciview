#!/bin/bash
rm -rf target
mvn
cp target/snailj-sciview-0.1.5-SNAPSHOT.jar ../sciview/Fiji.app/plugins/jars/snailj-sciview-0.1.5-SNAPSHOT.jar
