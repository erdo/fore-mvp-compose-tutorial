buildscript {
    val kotlinVersion by extra("1.6.10")
    val composeVersion by extra("1.1.0-rc03")
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

allprojects {
    repositories {
       // mavenLocal()
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class){
    delete(rootProject.buildDir)
}
