# Habit Stakes ProGuard Rules

# Keep Room entities
-keep class com.habitstakes.app.data.model.** { *; }

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }

# Keep ViewModels
-keep class com.habitstakes.app.ui.**ViewModel { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Kotlinx serialization
-keep class kotlinx.serialization.** { *; }

# Keep Coil
-keep class coil.** { *; }

# Keep Kotlinx datetime
-keep class kotlinx.datetime.** { *; }

# Keep Room
-keep class androidx.room.** { *; }

# Keep Hilt Navigation Compose
-keep class androidx.hilt.navigation.compose.** { *; }
