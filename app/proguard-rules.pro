# Room Database Proguard Rules
-keep class androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Data Models / Entities
-keep class com.japcounter.jap_counter_app.data.** { *; }

# Kotlinx Coroutines & Flow
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Compose optimization
-keep class androidx.compose.runtime.** { *; }