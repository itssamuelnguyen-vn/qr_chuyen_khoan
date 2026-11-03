# R8 rules for the release build. Nothing app-specific is needed yet: ZXing core is plain Java without reflection.
# Add rules here only when a release build fails or crashes.

# Keep line numbers so crash stack traces stay readable.
-keepattributes SourceFile,LineNumberTable
