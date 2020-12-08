# just-pipe

This project provides a class for most of the common operation, that involves redirecting InputStream.

## Examples

### Simple piping from source to destination

```java
Pipe.from(new File("source.txt"))
    .to(new File("destination.txt"));
```

```java
System.out.println(Pipe.from(new File("source.txt")).toString());
```

```java
Pipe.from(inputStream)
    .to(outputStream);
```

### Processing input

```java
Pipe.from(new File("source.txt"))
    .through(bytes -> new String(bytes).replaceAll(" ", "_").getBytes())
    .to(new File("destination.txt"));
```

### Splitting input into multiple outputs

```java
Pipe.from(new File("source.txt"))
    .split(pipe -> Arrays.stream(pipe.toString().split(" ")).map(in -> Pipe.from(in).with("filename", in + ".txt")).collect(Collectors.toList()))
    .forEach(pipe -> RuntimeIOException.wrap(() -> pipe.to(new File((String) pipe.get("filename")))));
```

## Usage

Add a repository to pom.xml:

```xml
<repositories>
    <repository>
        <id>stirante-nexus-snapshots</id>
        <url>https://nexus.stirante.com/repository/maven-snapshots/</url>
    </repository>
</repositories>
```

Add dependency to pom.xml:

```xml
<dependencies>
    <dependency>
        <groupId>com.stirante</groupId>
        <artifactId>just-pipe</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```