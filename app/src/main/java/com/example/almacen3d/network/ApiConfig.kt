package com.example.almacen3d.network

import com.example.almacen3d.BuildConfig

object ApiConfig {
    /**
     * Default API base URL. Resolved at build time from the `apiBaseUrl` Gradle property
     * (see app/build.gradle.kts). When empty, the user must set the server URL in the
     * app's Settings screen before logging in. Never hardcode a LAN IP here.
     */
    val DEFAULT_BASE_URL: String = BuildConfig.DEFAULT_API_BASE_URL
}
