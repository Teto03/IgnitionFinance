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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Previene repository a livello di modulo
    repositories {
        google() // Repository per dipendenze Google e Android
        mavenCentral() // Repository Maven centrale
    }
}

rootProject.name = "IgnitionFinance" // Nome del progetto
include(":app") // Moduli inclusi
