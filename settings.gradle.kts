import java.net.URI
import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Read local.properties
val localProperties = Properties()
val localPropertiesFile = rootDir.resolve("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

val gprUser: String? by localProperties
val gprToken: String? by localProperties
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = URI.create("https://maven.pkg.github.com/bxute/dlms-schemas") // For release
            credentials {
                username = gprUser
                password = gprToken
            }
        }
    }
}

rootProject.name = "DLMS-Demo-Android"
include(":app")
include(":trackingGrpcClient")