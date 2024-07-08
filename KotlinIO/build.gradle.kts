plugins {
    `common-kotlin`
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springBoot)
}

dependencies {
    implementation(libs.bundles.kotlin.jvm)
    implementation(libs.bundles.spring.coroutines)

    implementation(libs.springBoot.r2dbc)
    runtimeOnly(libs.r2dbc.h2)

    // https://stackoverflow.com/questions/65954571/spring-boot-2-4-2-dns-resolution-problem-at-start-on-apple-m1
    runtimeOnly(libs.netty.macOsDns) {
        artifact {
            classifier = "osx-aarch_64"
        }
    }

    testImplementation(libs.kotlinX.test)
}