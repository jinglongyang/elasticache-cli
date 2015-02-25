Maven 3:
1.Build the project
    $>mvn package or mvn clean install
  
2.run spring shell
    $>java -jar target/elasticache-cli-0.0.1.jar

Sample commands:
1. Config simple name for ElastiCache servers
    $>config --n dev --h dev.usw2.cache.amazonaws.com:11211
    $>config --name dev --host dev.usw2.cache.amazonaws.com:11211

2. List all configed ElastiCache servers
    $>env

3.Connect to ElastiCache server
    $>connect --h dev.usw2.cache.amazonaws.com:11211
    $>connect --e dev

4.Disconnect the ElastiCache server
    $>disconnect

5.Get value by key
    $>get key

6.Get json value by key and print pretty json format
    $>get key --type json

7.Delete key from ElastiCache server
    $>delete key

8.Exist the shell
    $>exit
    $>quit