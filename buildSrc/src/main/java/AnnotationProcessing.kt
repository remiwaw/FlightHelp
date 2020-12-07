package dependencies

object AnnotationProcessing {
	val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt_version}"
	val hiltCompiler = "androidx.hilt:hilt-compiler:${Versions.hilt_viewmodels}"
	val room_compiler = "androidx.room:room-compiler:${Versions.room}"
    val lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle_version}"
}
