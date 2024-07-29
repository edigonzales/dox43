# dox43

Beispiele: Siehe ReportsController.


Für docx4j-Code siehe Branch.


Import für Grundbuchbeschrieb:

```
java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54322 --dbdatabase pub --strokeArcs --defaultSrsCode 2056 --nameByTopic --createBasketCol --createDatasetCol --dbusr postgres --dbpwd secret --dbschema agi_mopublic_pub --models SO_AGI_MOpublic_20240202 --setupPgExt --schemaimport

java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54322 --dbdatabase pub --disableValidation --strokeArcs --defaultSrsCode 2056 --nameByTopic --createBasketCol --createDatasetCol --dbusr postgres --dbpwd secret --dbschema agi_mopublic_pub --models SO_AGI_MOpublic_20240202 --dataset 2601 --replace ilidata:2601.ch.so.agi.av.mopublic
```

Für lokales Entwickeln mit Docker Image:


```
java -jar build/libs/dox43-0.0.LOCALBUILD.jar --spring.profiles.active=dev --spring.config.location=application-dev.properties,classpath:application.properties
```

```
docker run -p 8080:8080 -e SPRING_CONFIG_LOCATION=file:///config/application-dev.properties,classpath:application.properties -e SPRING_PROFILES_ACTIVE=dev -e WORK_DIRECTORY=/tmp/ -e CONFIG_DIRECTORY=/config  -v $PWD/infra-dev:/config sogis/dox43
```


## Conditional Formatting

Zwei Varianten: Entweder via "Conditional Formatting" in Excel. Habe damit gekämpft. Liegt aber auch vielleicht an mir. Mit einer schnöden Formal, z.B. $C$3 > 1000, habe ich es nicht zum Laufen gebracht. Dafür aber wenn ich die Formel quasi zusammenklicke. Siehe "Grundstück"-Mappe. Die andere Variante ist mittels JXLS-Expression. Siehe https://jxls.sourceforge.net/if.html und Mappe "Grundstücke_if". Dann kann ich aber (momentan ?) keine Summe berechnen (https://github.com/jxlsteam/jxls/discussions/330).