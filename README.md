# jbit
Java Back In Time

## Demo

```
> jenv shell 14
> mvn install
[...]
> jenv shell 1.8
> java -cp example/target/example-1.0-SNAPSHOT-jdk8.jar:example/target/lib/asm-8.0.1.jar  org.apache.camel.jbit.example.Example
timeunit: SECONDS
timeunit: SECONDS ms
timeunit: SECONDS ms 3
false
{test=val, key=val}
s
> java -javaagent:agent/target/agent-1.0-SNAPSHOT-shaded.jar -cp example/target/example-1.0-SNAPSHOT.jar  org.apache.camel.jbit.example.Example
timeunit: SECONDS
timeunit: SECONDS ms
timeunit: SECONDS ms 3
false
{test=val, key=val}
s
```