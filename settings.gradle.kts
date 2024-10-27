import java.net.URI
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
val gprUser: String? by settings
val gprToken: String? by settings

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