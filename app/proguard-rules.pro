# keep Retrofit interfaces — stripping them breaks API calls at runtime
-keep interface com.misspathan.weatherapp.data.remote.api.** { *; }

# Gson needs field names for serialization
-keepclassmembers class com.misspathan.weatherapp.data.remote.dto.** { *; }
-keepclassmembers class com.misspathan.weatherapp.domain.model.** { *; }

# OkHttp + Retrofit internals
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# Room — keep generated _Impl classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
