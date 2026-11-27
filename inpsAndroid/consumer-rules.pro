# Consumer ProGuard / R8 rules for com.isaiahnoelpulidosalazar:inpsAndroid
# Merged automatically into consuming applications that enable minification.
# Rules are scoped with -keepclassmembers / enum rules so unused library classes can still be shrunk.

# Preserve standard Android View inflation constructors for kept UI widgets
# (e.g. RoundedButton, RoundedEditText, RoundedLinearLayout, RoundedConstraintLayout,
# SimpleList, FlippableImageView).
-keepclassmembers class com.isaiahnoelpulidosalazar.inpsandroid.ui.** extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

# Preserve Parcelable CREATOR for View state restoration (FlippableImageView$SavedState)
-keepnames class com.isaiahnoelpulidosalazar.inpsandroid.ui.FlippableImageView$SavedState
-keepclassmembers class com.isaiahnoelpulidosalazar.inpsandroid.ui.FlippableImageView$SavedState {
    public static final android.os.Parcelable$Creator CREATOR;
}
-keepclassmembers class com.isaiahnoelpulidosalazar.inpsandroid.** implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Preserve standard enum methods if library enums (e.g. FlippableImageView$Speed) are kept
-keepclassmembers enum com.isaiahnoelpulidosalazar.inpsandroid.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
