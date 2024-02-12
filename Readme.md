# Z17 Android library
## Z17Android contains a group of modules util for android development in the Z17 Company's environment.

# Modules in RC:
- Singledi (singledi): Singleton module for custom dependency injection
- Views (views): View components for Jetpack Compose Toolkit. Depends of Singledi
- Preferences (preferences): Encrypted implementation and simplified for Jetpack Data Store

## NOTE: Docs missing

## Imports:
``` 
dependencyResolutionManagement {
      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
      repositories {
	  mavenCentral()
	  maven { url 'https://jitpack.io' }
     }
}
```

### Gradle KTS
```
implementation ("com.github.z17b:z17_android:<MODULE_NAME>:<VERSION_TAG>")
```
### Gradle Catalogue Declare
```
[versions]
z17 = "<VERSION_TAG>"

[libraries]
z17-MODULE_NAME = { group = "com.github.z17b.z17_android", name = "<MODULE_NAME>", version.ref = "z17" }
```
### Gradle Catalogue Import
```
implementation(libs.z17.MODULE_NAME)
```
### Changelog 0.0.8
- Z17PagerIndicator: pageCount parameter removed
- Z17BaseScaffold: added bottom bar as composable parameter
- Z17MutableListFlow: getSize method added