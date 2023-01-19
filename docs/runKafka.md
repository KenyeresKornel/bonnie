##### Get kafka

[[https://downloads.apache.org/kafka/3.2.1/kafka_2.13-3.2.1.tgz][link]]

##### unpack

##### adjust kafka_2.13-3.2.1/config/zookeeper.properties

change dataDir to existing directory (create it if it does not exist)
for example:
dataDir=c:/Users/vanisp/tmp/zookeeper

##### adjust kafka_2.13-3.2.1/config/server.properties

change log.dirs to existing directory (create it if it does not exist)
for example:
log.dirs=c:/Users/vanisp/tmp/kafka-logs
uncomment and change listeners
listeners=PLAINTEXT://127.0.0.1:9092

##### following commands to be run from kafka home

example:  C:\Users\vanisp\bin\kafka_2.13-3.2.1\

##### run zookeeper

.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

##### run kafka

.\bin\windows\kafka-server-start.bat .\config\server.properties

##### create topic (optional since the application will create it, if it does not exists)

.\bin\windows\kafka-topics.bat --bootstrap-server localhost:9092 -create --topic test-topic

##### create consumer

.\bin\windows\kafka-console-consumer.bat --topic test-topic --from-beginning --bootstrap-server localhost:9092

##### create a producer/send a message

.\bin\windows\kafka-console-producer.bat --bootstrap-server localhost:9092 --topic test-topic
the above command eventually shows a prompt ">"
you can type a message and send it by hitting `enter`
the prompt re-appears in a while and your consumer should print the message you just typed
