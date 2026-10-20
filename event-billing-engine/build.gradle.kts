plugins {
    java
    application
    // Análisis estático con SonarQube Cloud (etapa v1.0)
    id("org.sonarqube") version "7.5.0.8588"
}

group = "com.events.billing"
version = "1.0.0"

java {
    // Java moderno (17+). El toolchain hace que Gradle use exactamente esta version.
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}



application {
    mainClass = "com.events.billing.Main"
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

repositories {
    mavenCentral()
}

dependencies {
    // JUnit 5 (agregador: API + motor + parametros). Scope de test.
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")

    // Necesario en las versiones recientes de Gradle para lanzar la Plataforma JUnit.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    // Activa la Plataforma JUnit 5 (sin esto Gradle no encuentra los tests).
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        // Muestra en consola los println de los performance tests.
        showStandardStreams = true
    }
}

// ===================== SonarQube Cloud =====================
sonar {
    properties {
        property("sonar.projectKey", "MarianaMartinezGaviria_proyecto-calidad-sonar")
        property("sonar.organization", "marianamartinezgaviria")
        property("sonar.host.url", "https://sonarcloud.io")
        // Etiqueta de la etapa: aparece en la pestaña Activity de SonarCloud.
        property("sonar.projectVersion", "1.0")
    }
}
