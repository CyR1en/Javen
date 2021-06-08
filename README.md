# Javen
A runtime maven dependency loader.

### Features
- Annotation based
- Artifacts locally saved to faster loading

### Requirements
- Java 1.8+

## Getting started
Add Javen as a project dependency
#### Maven
```xml
<dependency>
  <groupId>com.cyr1en</groupId>
  <artifactId>javen-core</artifactId>
  <version>{version}</version>
</dependency>
```
#### Gradle
```groovy
dependencies {
  implementation 'com.cyr1en:javen-core:{version}'
}
```

## Usage
Javen is annotation based, therefore, annotations are used to declare the dependencies that are going to be loaded by Javen.

#### - Declaring dependencies to load.
`@Lib` annotation could be placed on any class (not functions or fields).
```java
@Lib(group = "group", name = "artifact-id", version = "version")
private class SomeClass {
}
```

#### - Loading dependencies
Javen requires a lib directory where it's going to save all of the resolved artifacts.
```java
public static void main(String[] args) {
    // Initialize Javen with its lib directory being /libs
    Javen javen = new Javen(Paths.get("lib"));
    javen.loadDependencies(); // Load all declared deps.
}
```

#### - Adding repositories
Some dependencies requires a specific remote repository (other than Maven central), luckily Javen allows you to add a remote repositry. Just make sure to add your remote repositories using `.addRepositry()` before loading the jars.
```java
public static void main(String[] args) {
    // Initialize Javen with its lib directory being /libs
    Javen javen = new Javen(Paths.get("lib"));
    javen.addRepository(new Repository("JCenter", "https://jcenter.bintray.com/", "default"));
    javen.addRepository("JitPack", "https://Jitpack.io");
    javen.loadDependencies(); // Load all declared deps.
}
```
