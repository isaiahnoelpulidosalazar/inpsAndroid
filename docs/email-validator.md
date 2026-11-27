# EmailValidator

`EmailValidator` is an immutable, thread-safe email domain allow-list validator built with a `Builder`. It replaces the global static state of `Check.Email`.

## Purpose & Scope

The purpose of this utility is to provide:
- Domain allow-listing in either full-domain mode or split (`name` + `extension`) mode
- Case-insensitive matching
- Protection against spoofed subdomains: split mode requires the domain to be exactly `name.extension`, so `user@gmail.com.evil.example` is rejected

Multi-label domains such as `co.uk` should use full-domain mode. The validator only checks the domain against your allow-list; it does not validate the local part beyond requiring it to be non-empty.

## API Reference

### EmailValidator.Builder

#### Builder addDomainName(String name)
Adds an accepted domain name (the label before the dot) for split mode.

```java
new EmailValidator.Builder().addDomainName("gmail");
```

#### Builder addDomainExtension(String extension)
Adds an accepted extension (the label after the dot) for split mode.

```java
builder.addDomainExtension("com");
```

#### Builder addDomain(String domain)
Adds an accepted fully-qualified domain for full-domain mode.

```java
builder.addDomain("company.org");
```

#### Builder useFullDomain(boolean useFullDomain)
Selects full-domain mode (`true`) or split mode (`false`, the default).

```java
builder.useFullDomain(true);
```

#### EmailValidator build()
Creates the immutable validator.

```java
EmailValidator v = new EmailValidator.Builder()
        .addDomainName("gmail").addDomainExtension("com").build();
```

---

### boolean isValid(String email)

Returns `true` when the address's domain is on the configured allow-list. `null` and malformed addresses return `false`.

```java
boolean ok = v.isValid("user@gmail.com"); // true
boolean bad = v.isValid("user@gmail.com.evil.example"); // false
```
