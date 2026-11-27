package com.isaiahnoelpulidosalazar.inpsandroid.core.validation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Immutable, thread-safe e-mail domain allow-list validator.
 *
 * <pre>{@code
 * EmailDomainAllowList v = new EmailDomainAllowList.Builder()
 *         .addDomainName("gmail").addDomainExtension("com").build();
 * v.isValid("user@gmail.com"); // true
 * }</pre>
 *
 * <p>Validates that the address has a non-empty local part without whitespace, exactly one
 * {@code @}, and a domain matching the allow-list (case-insensitive). Split mode requires the
 * domain to be exactly {@code name.extension}.</p>
 */
public final class EmailDomainAllowList {

    private final Set<String> domains;
    private final Set<String> names;
    private final Set<String> extensions;
    private final boolean useFullDomain;

    private EmailDomainAllowList(Builder b) {
        domains = Collections.unmodifiableSet(new HashSet<>(b.domains));
        names = Collections.unmodifiableSet(new HashSet<>(b.names));
        extensions = Collections.unmodifiableSet(new HashSet<>(b.extensions));
        useFullDomain = b.useFullDomain;
    }

    /** @return {@code true} when the address has a valid local part and an allow-listed domain. */
    public boolean isValid(String email) {
        if (email == null) return false;
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            return false;
        }
        for (int i = 0; i < at; i++) {
            if (Character.isWhitespace(email.charAt(i))) return false;
        }

        String domain = email.substring(at + 1).toLowerCase(Locale.ROOT);
        for (int i = 0; i < domain.length(); i++) {
            if (Character.isWhitespace(domain.charAt(i))) return false;
        }

        if (useFullDomain) {
            return domains.contains(domain);
        }
        int dot = domain.indexOf('.');
        if (dot <= 0 || dot != domain.lastIndexOf('.') || dot == domain.length() - 1) {
            return false;
        }
        return names.contains(domain.substring(0, dot)) && extensions.contains(domain.substring(dot + 1));
    }

    /** Builder for {@link EmailDomainAllowList}. */
    public static final class Builder {
        private final Set<String> domains = new HashSet<>();
        private final Set<String> names = new HashSet<>();
        private final Set<String> extensions = new HashSet<>();
        private boolean useFullDomain;

        /** Adds an accepted domain name (the label before the dot), e.g. {@code "gmail"}. */
        public Builder addDomainName(String name) { add(names, name); return this; }

        /** Adds an accepted extension (the label after the dot), e.g. {@code "com"}. */
        public Builder addDomainExtension(String extension) { add(extensions, extension); return this; }

        /** Adds an accepted full domain, e.g. {@code "gmail.com"}. */
        public Builder addDomain(String domain) { add(domains, domain); return this; }

        /** Selects full-domain mode ({@code true}) or split mode ({@code false}, default). */
        public Builder useFullDomain(boolean useFullDomain) { this.useFullDomain = useFullDomain; return this; }

        public EmailDomainAllowList build() { return new EmailDomainAllowList(this); }

        private static void add(Set<String> set, String value) {
            if (value != null && !value.isEmpty()) {
                set.add(value.toLowerCase(Locale.ROOT));
            }
        }
    }
}
