# Файл: android/app/proguard-rules.pro

# Правила для flutter_local_notifications
-keep class com.dexterous.** { *; }
-keep class y3.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# Сохраняем информацию о дженериках
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Правила для GSON
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer