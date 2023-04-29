plugins {
	java
	id("org.springframework.boot") version "3.1.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.report"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://repository.aspose.com/repo/") }
}

dependencies {
	//implementation("com.aspose:aspose-email:20.3")
	implementation("javax.mail:mail:1.4.7")
	implementation("javax.activation:activation:1.1.1")
	implementation("com.microsoft.sqlserver:mssql-jdbc:12.2.0.jre11")
	implementation("io.springfox:springfox-boot-starter:3.0.0")

	implementation("com.opencsv:opencsv:5.7.1")

	implementation("com.opencsv:opencsv:5.7.1")

	//implementation("io.springfox:springfox-swagger-ui:3.0.0-SNAPSHOT")
	implementation("org.apache.poi:poi:5.2.3")
	implementation("org.apache.poi:poi-ooxml:5.2.3")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.quartz-scheduler:quartz")

	/*    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
*/
	implementation("org.springframework.boot:spring-boot-starter-batch")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
