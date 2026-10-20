plugins {
    java
    application
    // Cobertura de pruebas (etapa v1.1)
    jacoco
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

    // Al terminar las pruebas, se genera el reporte de cobertura.
    finalizedBy(tasks.jacocoTestReport)
}

// ===================== JaCoCo (cobertura) =====================
jacoco {
    // 0.8.14+ es necesaria para leer clases compiladas con Java 25.
    toolVersion = "0.8.15"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true   // lo lee SonarCloud
        html.required = true  // lo abres tu en: build/reports/jacoco/test/html/index.html
    }
}

// ===================== SonarQube Cloud =====================
sonar {
    properties {
        property("sonar.projectKey", "MarianaMartinezGaviria_proyecto-calidad-sonar")
        property("sonar.organization", "marianamartinezgaviria")
        property("sonar.host.url", "https://sonarcloud.io")
        // Ruta del reporte XML de JaCoCo que SonarCloud importa como cobertura.
        property("sonar.coverage.jacoco.xmlReportPaths",
                 "build/reports/jacoco/test/jacocoTestReport.xml")
        // Main.java es la interfaz de consola (entrada/salida), no logica de negocio:
        // se excluye SOLO de la cobertura. Sonar lo sigue analizando (issues y complejidad).
        property("sonar.coverage.exclusions", "**/Main.java")
        // En el plan gratuito no se puede editar el Quality Profile, asi que la regla
        // java:S106 ("no usar System.out") se ignora SOLO en Main.java: es la consola
        // interactiva y su funcion es justamente imprimir. Las demas reglas siguen activas.
        property("sonar.issue.ignore.multicriteria", "e1")
        property("sonar.issue.ignore.multicriteria.e1.ruleKey", "java:S106")
        property("sonar.issue.ignore.multicriteria.e1.resourceKey", "**/Main.java")
        // Etiqueta de la etapa: aparece en la pestaña Activity de SonarCloud.
        property("sonar.projectVersion", "1.4")
    }
}
