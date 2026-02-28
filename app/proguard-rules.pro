# ProGuard rules for Sleep Guardian

# Keep Room entities
-keep class com.sleepguardian.data.local.entities.** { *; }

# Keep Hilt generated code
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Compose
-dontwarn androidx.compose.**

# Keep Kotlin metadata
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
