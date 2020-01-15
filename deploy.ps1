Remove-Item -Recurse -Force .\target\
mvn
Copy-Item target/snailj-sciview-0.1.5-SNAPSHOT.jar ../Fiji.app/plugins/snailj-sciview.jar
