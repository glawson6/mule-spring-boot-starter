# mule-spring-boot-starter

Mule CE is an open source integration tool. Mule CE applications are normally run inside a Mule runtime. 
With mule-spring-boot-starter, you can run Mule CE embedded in a Spring Boot application. This allows Mule 
developers to quickly prototype and/or deploy Mule applications with having to download Mule runtime, create 
A Maven artifact and push the artifact to the Mule runtime. This project will allow developers to build the 
Mule application in much the same manner as other Spring Boot applications. 

## Maven Dependency:
To get started simply include the dependency in your pom file:
```
<dependency>
    <groupId>net.taptech</groupId>
	<artifactId>mule-spring-boot-starter</artifactId>
	<version>1.5.9-SNAPSHOT</version>
</dependency>
```

## Add repositories:
```

		<repositories>
        		<repository>
        			<id>Central</id>
        			<name>Central</name>
        			<url>http://repo1.maven.org/maven2/</url>
        			<layout>default</layout>
        		</repository>
        		<repository>
        			<id>mulesoft-releases</id>
        			<name>MuleSoft Repository</name>
        			<url>http://repository.mulesoft.org/releases/</url>
        			<layout>default</layout>
        		</repository>
        		<repository>
        			<id>mulesoft-snapshots</id>
        			<name>MuleSoft Snapshot Repository</name>
        			<url>http://repository.mulesoft.org/snapshots/</url>
        			<layout>default</layout>
        		</repository>
        		<repository>
        			<id>spring-milestones</id>
        			<name>Spring Milestones Repository</name>
        			<url>http://repo.spring.io/milestone</url>
        			<layout>default</layout>
        		</repository>
        		<repository>
        			<id>spock-snapshots</id>
        			<url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        			<snapshots>
        				<enabled>true</enabled>
        			</snapshots>
        		</repository>
        
        </repositories>

```
## Add mule modules and dependencies as needed.

Ex.

```
<dependency>
	<groupId>org.mule.transports</groupId>
	<artifactId>mule-transport-http</artifactId>
	<version>${mule.version}</version>
</dependency>
```

## Create a mule config file:
Make sure this file is in the artifact classpath. Create an application property called
mule.config.files. Add a comma separated list of mule config files.
```
mule.config.files=mule-config.xml
```

## Add annotation to your Spring Boot application entry point.
@EnableMuleConfiguration

```

@EnableMuleConfiguration
@SpringBootApplication
public class DemoMuleSpringBootApplication {

	private static final Logger logger = LoggerFactory.getLogger(DemoMuleSpringBootApplication.class);

	@Autowired
	private ApplicationContext context;

	public static void main(String... args) {
		logger.info("Starting SpringApplication...");
		SpringApplication app = new SpringApplication(DemoMuleSpringBootApplication.class);
		app.setBannerMode(Banner.Mode.CONSOLE);
		app.setWebEnvironment(false);
		app.run();
		logger.info("SpringApplication has started...");
	}
}
```

The demo project can be viewed [here.](https://github.com/glawson6/demo-mule-spring-boot)