package dependencies.dependencies

import dependencies.Versions

object TestDependencies {
	val hiltAndroidTesting = "com.google.dagger:hilt-android-testing:${Versions.hilt_version}"
    val mockk = "io.mockk:mockk:${Versions.mockk_version}"
    val junit4 = "junit:junit:${Versions.junit_4_version}"
}
