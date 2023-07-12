plugins {
    id("java")
}

group = "org.qubits"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":producer"))
    implementation(project(":enricher"))
    implementation(project(":domain-registration"))
}

tasks.test {
    useJUnitPlatform()
}

subprojects {
    apply {
        plugin("java")
    }

    group = "org.qubits"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.9.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        compileOnly("org.projectlombok", "lombok", "1.18.28")
        annotationProcessor("org.projectlombok", "lombok", "1.18.28")
        testCompileOnly("org.projectlombok", "lombok", "1.18.28")
        testAnnotationProcessor("org.projectlombok", "lombok", "1.18.28")
        implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
        implementation("org.apache.kafka:kafka-clients:3.5.0")
        implementation("org.slf4j:slf4j-api:2.0.7")
        implementation("ch.qos.logback:logback-classic:1.4.8")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
