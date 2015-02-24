Maven 3:

1.Build the project
  $>mvn package or mvn clean install
  
2.run spring shell
  $>java -jar target/elasticache-cli-0.0.1.jar


Sample commands:

1.Connect to ElastiCache server
connect --h rpcloud-cache-dev.zdkdqj.cfg.usw2.cache.amazonaws.com:11211

2.Get value by key
get key

3.Get json value by key and print pretty json format
get key --type json

4.Delete key from ElastiCache server
delete key