# jbit
Java Back In Time

## Demo

```
> jenv shell 14
> mvn install
[...]
> cd example/target
> jenv shell 1.8
> java -cp example-1.0-SNAPSHOT-jdk8.jar org.apache.camel.jbit.example.Example
timeunit: SECONDS
timeunit: SECONDS ms
timeunit: SECONDS ms 3
false
{test=val, key=val}
s
>
```