pg_ctl start -D  "./postgreSQL_minimal/data"

start cmd.exe /k "java -jar ./back-end/target/uml-conference-0.0.1-SNAPSHOT.jar"
start cmd.exe /k "http-server ./front-end/dist/front-end --port=8100" 
 start http://localhost:8100