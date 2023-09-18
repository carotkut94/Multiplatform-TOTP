plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "otp"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(platform("org.kotlincrypto.macs:bom:0.3.0"))

                implementation("org.kotlincrypto.macs:hmac-sha1")
                implementation("org.kotlincrypto.macs:hmac-sha2")
                implementation(libs.secure.random)
                implementation(libs.kotlinx.datetime)
                implementation(libs.buffer)
                api(libs.base32)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.death.otpman"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
}