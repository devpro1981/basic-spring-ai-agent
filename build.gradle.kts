plugins {
	java
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
}

group = "com.example.poc"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot AI Agent"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.spring.boot.starter.web)
	// LangChain4j core + Ollama integration
	implementation(libs.langchain4j.core)
	implementation(libs.langchain4j.ollama)
	// Jackson for DTOs
	implementation(libs.jackson.databind)

	testImplementation(libs.spring.boot.starter.test)
	// Mockito for unit tests
	testImplementation(libs.mockito.core)
	testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
