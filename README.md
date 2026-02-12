## How to use ?

C:\Users\\{user}\\.m2\settings.xml
```xml
<settings>
    <servers>
        <server>
            <id>a1a-nexus</id> <username>admin</username>
            <password>Password</password>
        </server>
    </servers>
	<mirrors>
	  <mirror>
		<id>allow-http-nexus</id>
		<mirrorOf>a1a-nexus</mirrorOf> <name>Allow HTTP for local Nexus</name>
		<url>http://{host}:8081/repository/maven-snapshots/</url>
	  </mirror>
	</mirrors>
</settings>
```

pom.xml
```xml
<dependencies>
    <dependency>
        <groupId>com.a1a.shared</groupId>
        <artifactId>auth</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>a1a-nexus</id>
        <url>http://{host}:8081/repository/maven-snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>
```