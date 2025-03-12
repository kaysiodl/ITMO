plugins {
    id("java")
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.google.code.gson:gson:2.12.1")
}

application {
    mainClass.set("lab5.itmo.Main")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf(
        "--add-opens", "java.base/java.time=ALL-UNNAMED"
    )
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "--add-opens", "java.base/java.time=ALL-UNNAMED"
    )
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE // Исключает дубликаты файлов
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    from(sourceSets.main.get().output)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}