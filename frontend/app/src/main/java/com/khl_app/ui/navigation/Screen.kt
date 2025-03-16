package com.khl_app.ui.navigation

sealed class Screen(val route: String) {
    companion object {
        private const val ROUTER_SPLASH = "splash_screen"
        private const val ROUTER_REGISTRATION = "registration_screen"
        private const val ROUTER_LOGIN = "login_screen"

        private const val ROUTER_ABOUT = "about_screen"

        private const val ROUTER_PROFILE = "profile_screen"
        private const val ROUTER_MAIN = "main_screen"
        private const val ROUTER_TRACKABLE = "trackable_screen"
        private const val ROUTER_SETTINGS = "settings_screen"
    }

    object SplashScreen: Screen(ROUTER_SPLASH)
    object LoginScreen: Screen(ROUTER_LOGIN)
    object RegistrationScreen: Screen(ROUTER_REGISTRATION)
    object AboutScreen: Screen(ROUTER_ABOUT)

    // Для экранов с аргументами добавляем базовые маршруты и функции для создания полного маршрута
    object ProfileScreen: Screen("$ROUTER_PROFILE?userId={userId}&isFromMenu={isFromMenu}&isYou={isYou}") {
        fun createRoute(userId: String? = null, isFromMenu: Boolean = true, isYou: Boolean = false): String {
            return "$ROUTER_PROFILE?userId=$userId&isFromMenu=$isFromMenu&isYou=$isYou"
        }
    }

    object MainScreen: Screen("$ROUTER_MAIN?isFromMenu={isFromMenu}&name={name}&canRedact={canRedact}") {
        fun createRoute(isFromMenu: Boolean = true, name: String? = null, canRedact: Boolean = false): String {
            return "$ROUTER_MAIN?isFromMenu=$isFromMenu&name=$name&canRedact=$canRedact"
        }
    }

    object TrackableScreen: Screen(ROUTER_TRACKABLE)
    object SettingsScreen: Screen(ROUTER_SETTINGS)
}