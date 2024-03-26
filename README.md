## Install moderne cli

Download latest version from https://repo1.maven.org/maven2/io/moderne/moderne-cli/

This is an optional step, but allows us to refer to the latest CLI JAR with just mod. In git bash add a function to .bashrc with:

```bash
mod() {
  java -jar "/path/to/mod.jar" $@
}
```

## Install recipes

This will align your CLI’s locally available recipe marketplace to the recipes currently installed on app.moderne.io.

```bash
mod config recipes moderne sync
```

Or install a single recipt:

```bash
mod config recipes moderne install CommonStaticAnalysis
```

After this is complete, you can see how many recipes are now available:

```
mod config recipes list
```

## Run a refactoring recipe

```bash
mod run . --recipe CommonStaticAnalysis
mod run . --recipe UpgradeToJava17
```

These recipes will prepare patch files in .moderne folders in each repository, which can then be studied further or applied to the repository contents and committed.

```bash
mod git apply . --last-recipe-run
mod git checkout . java-17-upgrade -b
mod git commit . -m "Upgrade to Java 17"
```

## Studying the results of a recipe run

Recipes can produce data tables as a recipe run proceeds. Data tables are columnar data in a schema defined by the recipe.

```bash
mod study . --last-recipe-run --data-table SourcesFileResults
```

## Local Publishing for Testing

Build and install locally for testing:

```bash
mvn clean install && mod config recipes jar install com.sap.cds.openrewrite:recipes:1.0.0-SNAPSHOT
```

Execute it on commmand line:

```bash
mod run . --recipe com.sap.cds.openrewrite.recipe.SelectWithStringSearch
mod run . --recipe com.sap.cds.openrewrite.recipe.CdsDataNameSearch
mod run . --recipe com.sap.cds.openrewrite.recipe.CdsDataRewriteRecipes
```

With maven:

```bash
mvn org.openrewrite.maven:rewrite-maven-plugin:run -Drewrite.recipeArtifactCoordinates=com.sap.cds.openrewrite:recipes:1.0.0-SNAPSHOT -Drewrite.activeRecipes=com.sap.cds.openrewrite.recipe.CdsDataRewriteRecipes
```

Before you publish your recipe module to an artifact repository, you may want to try it out locally.
To do this on the command line, run:
```bash
./gradlew publishToMavenLocal
# or ./gradlew pTML
```
This will publish to your local maven repository, typically under `~/.m2/repository`.

Replace the groupId, artifactId, recipe name, and version in the below snippets with the ones that correspond to your recipe.

In the pom.xml of a different project you wish to test your recipe out in, make your recipe module a plugin dependency of rewrite-maven-plugin:
```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>org.openrewrite.maven</groupId>
                <artifactId>rewrite-maven-plugin</artifactId>
                <version>RELEASE</version>
                <configuration>
                    <activeRecipes>
                        <recipe>com.yourorg.NoGuavaListsNewArrayList</recipe>
                    </activeRecipes>
                </configuration>
                <dependencies>
                    <dependency>
                        <groupId>com.yourorg</groupId>
                        <artifactId>rewrite-recipe-starter</artifactId>
                        <version>0.1.0-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
</project>
```

Unlike Maven, Gradle must be explicitly configured to resolve dependencies from Maven local.
The root project of your Gradle build, make your recipe module a dependency of the `rewrite` configuration:

```groovy
plugins {
    id("java")
    id("org.openrewrite.rewrite") version("latest.release")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    rewrite("com.yourorg:rewrite-recipe-starter:latest.integration")
}

rewrite {
    activeRecipe("com.yourorg.NoGuavaListsNewArrayList")
}
```

Now you can run `mvn rewrite:run` or `gradlew rewriteRun` to run your recipe.

## Publishing to Artifact Repositories

This project is configured to publish to Moderne's open artifact repository (via the `publishing` task at the bottom of
the `build.gradle.kts` file). If you want to publish elsewhere, you'll want to update that task.
[app.moderne.io](https://app.moderne.io) can draw recipes from the provided repository, as well as from [Maven Central](https://search.maven.org).

Note:
Running the publish task _will not_ update [app.moderne.io](https://app.moderne.io), as only Moderne employees can
add new recipes. If you want to add your recipe to [app.moderne.io](https://app.moderne.io), please ask the
team in [Slack](https://join.slack.com/t/rewriteoss/shared_invite/zt-nj42n3ea-b~62rIHzb3Vo0E1APKCXEA) or in [Discord](https://discord.gg/xk3ZKrhWAb).

These other docs might also be useful for you depending on where you want to publish the recipe:

* Sonatype's instructions for [publishing to Maven Central](https://maven.apache.org/repository/guide-central-repository-upload.html)
* Gradle's instructions on the [Gradle Publishing Plugin](https://docs.gradle.org/current/userguide/publishing\_maven.html).

### From Github Actions

The `.github` directory contains a Github action that will push a snapshot on every successful build.

Run the release action to publish a release version of a recipe.

### From the command line

To build a snapshot, run `./gradlew snapshot publish` to build a snapshot and publish it to Moderne's open artifact repository for inclusion at [app.moderne.io](https://app.moderne.io).

To build a release, run `./gradlew final publish` to tag a release and publish it to Moderne's open artifact repository for inclusion at [app.moderne.io](https://app.moderne.io).


## Applying OpenRewrite recipe development best practices

We maintain a collection of [best practices for writing OpenRewrite recipes](https://github.com/openrewrite/rewrite-recommendations/).
You can apply these recommendations to your recipes by running the following command:
```bash
./gradlew rewriteRun -Drewrite.activeRecipe=org.openrewrite.recipes.OpenRewriteBestPractices
```
