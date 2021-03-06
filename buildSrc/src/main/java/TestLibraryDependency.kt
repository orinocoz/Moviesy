private object TestLibraryVersion {
    const val DEBUG_DB = "1.0.4"
    const val JUNIT = "4.12"
    const val JUNIT_TEST = "1.1.1"
    const val ESPRESSO = "3.2.0"
    const val LEAK_CANARY = "2.4"
    const val STETHO = "1.5.1"
}

object TestLibraryDependency {
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${TestLibraryVersion.ESPRESSO}"
    const val JUNIT_TEST_EXT = "androidx.test.ext:junit:${TestLibraryVersion.JUNIT_TEST}"
    const val JUNIT = "junit:junit:${TestLibraryVersion.JUNIT}"
    const val ANDROID_DEBUG_DB = "com.amitshekhar.android:debug-db:${TestLibraryVersion.DEBUG_DB}"
    const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${TestLibraryVersion.LEAK_CANARY}"
    const val STETHO = "com.facebook.stetho:stetho:${TestLibraryVersion.STETHO}"
}