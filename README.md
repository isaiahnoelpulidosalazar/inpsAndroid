# inpsAndroid

`inpsAndroid` is a utility and UI component library for Android. It includes custom UI views, database management wrappers, sorting algorithms, cryptography helpers, validation tools, and thread execution utilities.

**Package:** `com.isaiahnoelpulidosalazar.inpsAndroid`

---

## Table of Contents
- [UI Components](#ui-components)
  - [Rounded Views (Button, Layouts, EditText)](#rounded-views)
  - [FlippableImageView](#flippableimageview)
  - [SimpleList](#simplelist)
  - [RoundedAlertDialog](#roundedalertdialog)
  - [Fullscreen](#fullscreen)
- [Data Storage & Persistence](#data-storage--persistence)
  - [EasySQL](#easysql)
  - [Memory](#memory)
- [Algorithms & Cryptography](#algorithms--cryptography)
  - [Sort](#sort)
  - [Cipher](#cipher)
- [Utilities & Helpers](#utilities--helpers)
  - [Check](#check)
  - [Convert](#convert)
  - [Actions](#actions)
  - [URLRequest](#urlrequest)

---

## UI Components

### Rounded Views

Custom UI components with built-in corner radius configuration and automatic adaptive ripple feedback.

#### XML Attributes
| Attribute | Format | Supported Views |
| :--- | :--- | :--- |
| `app:cornerRadius` | `dimension` | `RoundedButton`, `RoundedConstraintLayout`, `RoundedLinearLayout`, `RoundedEditText` |
| `app:isTranslucent` | `boolean` | `RoundedConstraintLayout`, `RoundedLinearLayout` |

#### XML Example
```xml
<LinearLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- Rounded Button with Dynamic Luminance-based Ripple -->
    <com.isaiahnoelpulidosalazar.inpsAndroid.RoundedButton
        android:id="@+id/roundedButton"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Click Me"
        app:cornerRadius="12dp" />

    <!-- Rounded Translucent ConstraintLayout -->
    <com.isaiahnoelpulidosalazar.inpsAndroid.RoundedConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="120dp"
        app:cornerRadius="16dp"
        app:isTranslucent="true" />

    <!-- Rounded EditText -->
    <com.isaiahnoelpulidosalazar.inpsAndroid.RoundedEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter text..."
        app:cornerRadius="8dp" />

    <!-- Rounded LinearLayout -->
    <com.isaiahnoelpulidosalazar.inpsAndroid.RoundedLinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:cornerRadius="20dp"
        app:isTranslucent="false" />
</LinearLayout>
```

---

### FlippableImageView

An `AppCompatImageView` subclass that flips horizontally between front and back images with Y-axis animation or instant state changes.

#### Speed Options
- `FlippableImageView.Speed.SLOW` (5° per frame)
- `FlippableImageView.Speed.NORMAL` (15° per frame)
- `FlippableImageView.Speed.FAST` (30° per frame)

#### Java Example
```java
FlippableImageView flipView = findViewById(R.id.flippableImageView);

// Set images
flipView.setFrontImage(R.drawable.card_front);
flipView.setReverseBackImage(true); // Horizontally mirrors back bitmap
flipView.setBackImage(R.drawable.card_back);
flipView.setSpeed(FlippableImageView.Speed.FAST);

// Animated flip
flipView.flip();

// Instant flip without animation
flipView.instantFlip();

// Check state
boolean isBackShowing = flipView.isFlipped();
```

---

### SimpleList

A lightweight `RecyclerView` wrapper for managing and displaying vertical lists of text items rendered as buttons.

#### Java Example
```java
SimpleList simpleList = findViewById(R.id.simpleList);

// Configure item padding (in dp)
simpleList.setItemPadding(8); // Uniform
// Or: simpleList.setItemPadding(left, top, right, bottom);

// Data manipulation
simpleList.addItem("First Item");
simpleList.addItem("Second Item");
simpleList.removeItem(0);

// Click & Long Click listeners
simpleList.setOnItemClickListener(position -> {
    String item = simpleList.getData().get(position);
    Toast.makeText(context, "Clicked: " + item, Toast.LENGTH_SHORT).show();
});

simpleList.setOnItemLongClickListener(position -> {
    Toast.makeText(context, "Long clicked position: " + position, Toast.LENGTH_SHORT).show();
});
```

---

### RoundedAlertDialog

A builder wrapper around `AlertDialog` with rounded borders, customizable dual action buttons, and scrollable custom content support.

#### Java Example
```java
RoundedAlertDialog dialog = new RoundedAlertDialog(context);
dialog.setTitle("Confirmation")
      .setupLeftButton("Cancel", Color.GRAY)
      .setupRightButton("Delete", Color.RED)
      .setupLeftButtonOnClick(v -> dialog.hide())
      .setupRightButtonOnClick(v -> {
          // Perform action
          dialog.hide();
      })
      .show();
```

---

### Fullscreen

Controls Android immersive sticky fullscreen mode.

```java
// Enable fullscreen (hides status bar, navigation bar, and action bar)
Fullscreen.enable(activity);

// Restore default UI visibility
Fullscreen.disable(activity);
```

---

## Data Storage & Persistence

### EasySQL

A boilerplate-free wrapper around `SQLiteDatabase`. Columns and insert/delete values are declared using `"column:value"` or `"column:TYPE"` colon notation.

#### Java Example
```java
EasySQL db = new EasySQL(context);
String dbName = "app_database";
String tableName = "users";

// Create Table
db.createTable(dbName, tableName, new String[]{
    "id:INTEGER PRIMARY KEY",
    "name:TEXT",
    "score:INTEGER"
});

// Check if table exists
boolean exists = db.doesTableExist(dbName, tableName);

// Insert records
db.insertToTable(dbName, tableName, new String[]{"id:1", "name:Alice", "score:100"});
db.insertToTable(dbName, tableName, new String[]{"id:2", "name:Bob", "score:85"});

// Delete by condition
db.deleteFromTable(dbName, tableName, "name:Bob");

// Fetch all rows
List<String> flatValues = db.getTableValues(dbName, tableName);
// Output format: ["id:1", "name:'Alice'", "score:100"]

List<String[]> rows = db.getTableValuesAsArray(dbName, tableName);
// Each row element is a String[] array containing "col:val" pairs

// Table maintenance
db.clearTable(dbName, tableName);  // DELETE FROM
db.deleteTable(dbName, tableName); // DROP TABLE
```

---

### Memory

A generic dynamic array storage container supporting mutation and element lookup without relying on `java.util.List`.

```java
Memory<String> cache = new Memory<>();

// Add elements
cache.add("alpha");
cache.add("beta");
cache.add("gamma");

// Query & Access
int total = cache.count();           // 3
boolean hasBeta = cache.contains("beta"); // true
String item = cache.get(1);          // "beta"

// Removal
cache.remove("beta");                // Removes first match
cache.removeAt(0);                   // Removes by index
cache.clear();                       // Resets container
```

---

## Algorithms & Cryptography

### Sort

A comprehensive collection of sorting algorithms implemented for `int[]` (and `double[]` for bucket sort).

| Algorithm | Method Signature | Behavior |
| :--- | :--- | :--- |
| **Bubble Sort** | `Sort.bubbleSort(int[] arr)` | In-place |
| **Cocktail Shaker Sort** | `Sort.cocktailShakerSort(int[] arr)` | In-place |
| **Odd-Even Sort** | `Sort.oddEvenSort(int[] arr)` | In-place |
| **Selection Sort** | `Sort.selectionSort(int[] arr)` | In-place |
| **Insertion Sort** | `Sort.insertionSort(int[] arr)` | In-place |
| **Shell Sort** | `Sort.shellsort(int[] arr)` | In-place (Knuth sequence) |
| **Quicksort** | `Sort.quicksort(int[] arr)` | Returns new sorted array |
| **Merge Sort** | `Sort.mergeSort(int[] arr)` | Returns new sorted array |
| **Heap Sort** | `Sort.heapsort(int[] arr)` | In-place ($O(1)$ aux space) |
| **Introsort** | `Sort.introsort(int[] arr)` | Returns new sorted array |
| **Timsort** | `Sort.timsort(int[] arr)` | Returns new sorted array |
| **Counting Sort** | `Sort.countingSort(int[] arr)` | Non-negative integers ($\le 10^7$) |
| **Bucket Sort** | `Sort.bucketSortUniform(double[] arr)` | In-place, values in range $[0.0, 1.0)$ |
| **Pigeonhole Sort** | `Sort.pigeonholeSort(int[] arr)` | Range $\le 10^7$, returns new array |
| **Tree Sort** | `Sort.treeSort(int[] arr)` | Iterative BST, returns new array |
| **Patience Sorting** | `Sort.patienceSorting(int[] arr)` | Returns new sorted array |
| **Bogo Sort** | `Sort.bogoSort(int[] arr)` | In-place |
| **Bead Sort** | `Sort.beadSort(int[] arr)` | Non-negative integers |

```java
int[] data = {5, 2, 9, 1, 5, 6};
int[] sortedData = Sort.quicksort(data);

double[] uniformData = {0.897, 0.565, 0.656, 0.1234, 0.665, 0.3434};
Sort.bucketSortUniform(uniformData);
```

---

### Cipher

Classical text encryption ciphers operating on uppercase Latin alphabetic characters. Non-alphabetic characters and spaces are preserved.

```java
// 1. Caesar Cipher (shifts letters by fixed offset)
String caesar = Cipher.caesarCipher("HELLO WORLD", 3);
// Output: "KHOOR ZRUOG"

// 2. Keyword Substitution Cipher
String keyword = Cipher.keywordCipher("HELLO WORLD", "KEYWORD");

// 3. Giovanni Cipher (keyword cipher rotated by a key letter)
String giovanni = Cipher.giovanniCipher("HELLO WORLD", "KEYWORD", "M");

// 4. Transposition Cipher (interleaves even and odd index characters, strips spaces)
String transposed = Cipher.transpositionCipher("HELLO");
// Output: "HLELO"
```

---

## Utilities & Helpers

### Check

Static validation and comparison helpers.

#### Email Validation
```java
// Split Mode (domain name + extension)
Check.Email.addValidDomainName("gmail");
Check.Email.addValidDomainExtension("com");
boolean isValid = Check.Email.isValid("test@gmail.com"); // true

// Full Domain Mode
Check.Email.addValidDomain("company.co.uk");
Check.Email.shouldUseFullDomain(true);
boolean isValidFull = Check.Email.isValid("user@company.co.uk"); // true
```

#### String Analysis & Resource Verification
```java
Check.hasSymbols("Password123!"); // true
Check.hasNumbers("Admin");        // false
Check.hasSpaces("NoSpacesHere");  // false

// Pixel-level equality check between a drawable resource and a Bitmap
boolean match = Check.equalDrawableAndBitmap(context, R.drawable.logo, bitmap);
```

#### Time Difference Calculations
```java
Date now = new Date();
Date futureDate = new Date(now.getTime() + 3600000); // +1 hour

double secLeft  = Check.howManySecondsLeft(now, futureDate); // ~3600.0
double minLeft  = Check.howManyMinutesLeft(now, futureDate); // ~60.0
double hrsLeft  = Check.howManyHoursLeft(now, futureDate);   // ~1.0
double daysLeft = Check.howManyDaysLeft(now, futureDate);    // ~0.0416
```

---

### Convert

Type parsing, encoding, and string formatting utilities.

```java
// Name Formatting
Convert.toRealName("john"); // "John"

// Base64 Encoding / Decoding
String encoded = Convert.toBase64("SecretMessage");
String decoded = Convert.fromBase64(encoded);

// Safe Number Parsing (Returns 0 on NumberFormatException)
int intVal       = Convert.toInt("123");     // 123
float floatVal   = Convert.toFloat("45.67"); // 45.67f
double doubleVal = Convert.toDouble("invalid"); // 0.0

// Safe String Conversion
String str = Convert.toString(someObject);

// Date Formatting (Deprecated in favor of java.time/Calendar)
Convert.dateToMMDDYY(new Date()); // "5/4/2026"
Convert.dateToDDMMYY(new Date()); // "4/5/2026"
Convert.dateToYYMMDD(new Date()); // "2026/5/4"
```

---

### Actions

A builder utility to dispatch parameter-carrying `Runnable` actions to the main UI thread immediately or with a delay.

```java
Actions action = new Actions()
    .setArg("Payload Data")
    .build(() -> {
        String data = (String) action.getArg();
        Log.d("Actions", "Executed with: " + data);
    });

// Run immediately on main thread
action.execute();

// Run with 2000ms delay
action.executeDelayed(2000);
```

---

### URLRequest

Performs asynchronous HTTP requests on a background thread and posts the response body back to the main UI thread via `Actions`.

```java
URLRequest request = new URLRequest();
request.build(() -> {
    String response = (String) request.getDefaultActionArg();
    if (response != null && !response.startsWith("Error:")) {
        Log.d("URLRequest", "Response: " + response);
    } else {
        Log.e("URLRequest", "Failed: " + response);
    }
});

// Execute request (URL, HTTP Method)
request.execute("https://jsonplaceholder.typicode.com/posts/1", "GET");
```