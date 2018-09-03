# programming-test

```
mvn clean install
java -jar programming-test/target/dictionaryGenerator-jar-with-dependencies.jar -i /var/mnt/fserver/mirax/processor -c /var/mnt/fserver/mirax -s /var/mnt/fserver/mirax
```

If you want to display which files are processed, update src/main/resources/log4j.properties and set the log level to 'info'.