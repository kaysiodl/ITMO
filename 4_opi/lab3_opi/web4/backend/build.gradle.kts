import java.security.MessageDigest
import java.io.File

plugins {
    java
    war
}

group = "ru.kaysiodl"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")

    testImplementation("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    testImplementation("org.glassfish.jersey.core:jersey-common:3.1.3") // Для работы Response в тестах

    implementation("org.hibernate:hibernate-core:5.6.15.Final")
    implementation("org.postgresql:postgresql:42.7.7")

    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.slf4j:slf4j-api:2.0.16")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    testCompileOnly("org.projectlombok:lombok:1.18.20")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.20")

    testImplementation("org.mockito:mockito-core:5.12.0")
}

fun hashFile(file: File, algorithm: String): String {
    if (!file.isFile) return ""
    val digest = MessageDigest.getInstance(algorithm)
    file.inputStream().use { fis ->
        val buffer = ByteArray(1024)
        var read = fis.read(buffer)
        while (read != -1) {
            digest.update(buffer, 0, read)
            read = fis.read(buffer)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}

tasks.register("compile") {
    group = "build"
    dependsOn("classes")
}

tasks.register("buildAll") {
    dependsOn("compile")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    dependsOn("buildAll")
}

tasks.register("doc") {
    group = "documentation"
    dependsOn("javadoc", "war")
    doLast {
        println("Documentation generated and hashes added to manifest.")
    }
}

tasks.war {
    archiveFileName.set("web4.war")

    dependsOn("javadoc")

    from(tasks.javadoc.get().destinationDir!!) {
        into("javadoc")
    }

    doFirst {
        val srcFiles = fileTree("src/main/java").files
        val md5Log = srcFiles.joinToString(", ") { "${it.name}:${hashFile(it, "MD5")}" }
        val sha1Log = srcFiles.joinToString(", ") { "${it.name}:${hashFile(it, "SHA-1")}" }

        manifest {
            attributes(
                "MD5-Files" to md5Log.take(2000), // Ограничение длины строки для манифеста
                "SHA1-Files" to sha1Log.take(2000)
            )
        }
    }
}

tasks.register("alt") {
    group = "build"
    val altDir = layout.buildDirectory.dir("alt-src").get().asFile

    doFirst {
        delete(altDir)

        copy {
            from("src/main/java")
            into(altDir)

            filter { line ->
                line.replace("Result", "AltResult")
                    .replace("User", "AltUser")
                    .replace("Auth", "AltAuth")
                    .replace("PageResponse", "AltPageResponse")
                    .replace("RestApplication", "AltRestApplication")
                    .replace("PasswordUtil", "AltPasswordUtil")
                    .replace("ValidationUtil", "AltValidationUtil")
            }
        }

        altDir.walkTopDown().forEach { file ->
            if (file.isFile && file.extension == "java") {
                val newName = "Alt${file.name}"
                val newFile = File(file.parent, newName)
                file.renameTo(newFile)
            }
        }
    }
    tasks.withType<Javadoc> {
        options {
            (this as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
            encoding = "UTF-8"
            docEncoding = "UTF-8"
            charSet = "UTF-8"
        }
    }
}