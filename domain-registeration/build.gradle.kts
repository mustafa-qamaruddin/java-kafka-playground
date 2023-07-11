plugins {
    id("java")
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
}

tasks.test {
    useJUnitPlatform()
}
