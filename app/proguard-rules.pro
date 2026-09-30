# ──────────────────────────────────────────────────────────────────────────
# ML Kit barcode scanning
#
# R8 full mode (the default since AGP 8) strips the component registry that
# BarcodeScanning.getClient() resolves at runtime, which surfaces as:
#
#   java.lang.NullPointerException: Attempt to read from field '... .a'
#   on a null object reference
#       at com.google.mlkit.vision.barcode.internal.zzg.zzb(BarcodeScannerOptions)
#
# The registry is looked up reflectively, so R8 cannot see the references and
# prunes them. These keeps are what make the QR scanner work in release.
# ──────────────────────────────────────────────────────────────────────────
-keep class com.google.mlkit.** { *; }
-keep interface com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_** { *; }
-dontwarn com.google.mlkit.**

# ──────────────────────────────────────────────────────────────────────────
# Backup file format
#
# The .sinope backup is kotlinx.serialization JSON. The plugin emits consumer
# rules that cover the generated $$serializer classes, but the models are only
# ever reached through serializer() lookups, so keep them explicitly: a backup
# that cannot be read is data loss, not a crash.
# ──────────────────────────────────────────────────────────────────────────
-keep,includedescriptorclasses class com.example.sinope.data.backup.model.** { *; }
-keepclassmembers class com.example.sinope.data.backup.model.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}

# ──────────────────────────────────────────────────────────────────────────
# BouncyCastle: only Argon2id is used, but the provider resolves algorithms
# by name, so the lightweight crypto API must survive.
# ──────────────────────────────────────────────────────────────────────────
-keep class org.bouncycastle.crypto.** { *; }
-dontwarn org.bouncycastle.**
