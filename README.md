# dox43

Branch mit docx4j

```
java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54322 --dbdatabase pub --strokeArcs --defaultSrsCode 2056 --nameByTopic --createBasketCol --createDatasetCol --dbusr postgres --dbpwd secret --dbschema agi_mopublic_pub --models SO_AGI_MOpublic_20240202 --setupPgExt --schemaimport

java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54322 --dbdatabase pub --disableValidation --strokeArcs --defaultSrsCode 2056 --nameByTopic --createBasketCol --createDatasetCol --dbusr postgres --dbpwd secret --dbschema agi_mopublic_pub --models SO_AGI_MOpublic_20240202 --dataset 2601 --replace ilidata:2601.ch.so.agi.av.mopublic
```

Für lokales Entwickeln mit Docker Image:

```
https://www.baeldung.com/spring-properties-file-outside-jar#load-config-environment-var
```
