# EmailDomainAllowList

Package: `com.isaiahnoelpulidosalazar.inpsandroid.core.validation`

`EmailDomainAllowList` (formerly `EmailValidator`) is an immutable, thread-safe email validator that checks local-part syntax and matches the domain against an allow-list (case-insensitive).

## Validation Rules

- Rejects `null`, missing/multiple `@` symbols, empty local parts, empty domains, and any whitespace in the local part or domain
- **Split mode** (`useFullDomain(false)`, default): requires the domain to be `name.extension` (exactly one `.`) where both `name` and `extension` are allow-listed
- **Full-domain mode** (`useFullDomain(true)`): checks the entire lower-cased domain against the configured domain set

## Usage

```java
import com.isaiahnoelpulidosalazar.inpsandroid.core.validation.EmailDomainAllowList;

// Split mode
EmailDomainAllowList splitAllowList = new EmailDomainAllowList.Builder()
        .addDomainName("gmail")
        .addDomainExtension("com")
        .build();

boolean ok = splitAllowList.isValid("user@gmail.com"); // true

// Full-domain mode
EmailDomainAllowList fullAllowList = new EmailDomainAllowList.Builder()
        .addDomain("gmail.com")
        .addDomain("outlook.com")
        .useFullDomain(true)
        .build();
```
