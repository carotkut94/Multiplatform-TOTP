plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.library").version("8.2.0-alpha08").apply(false)
    kotlin("multiplatform").version("1.8.21").apply(false)
    kotlin("plugin.serialization").version("1.9.0").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
