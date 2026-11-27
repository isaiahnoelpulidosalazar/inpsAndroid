package com.isaiahnoelpulidosalazar.inpsandroid.core.validation;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationTest {

    @Test
    public void testEmailDomainAllowListSplitMode() {
        EmailDomainAllowList allowList = new EmailDomainAllowList.Builder()
                .addDomainName("gmail")
                .addDomainName("Outlook")
                .addDomainExtension("com")
                .build();

        assertTrue(allowList.isValid("user@gmail.com"));
        assertTrue(allowList.isValid("USER@OUTLOOK.COM"));
        assertFalse(allowList.isValid("user@gmail.com.evil.example"));
        assertFalse(allowList.isValid("user@yahoo.com"));
        assertFalse(allowList.isValid("user@gmail.org"));
    }

    @Test
    public void testEmailDomainAllowListLocalPartValidation() {
        EmailDomainAllowList allowList = new EmailDomainAllowList.Builder()
                .addDomain("gmail.com")
                .useFullDomain(true)
                .build();

        assertTrue(allowList.isValid("alice.smith+tag@gmail.com"));
        assertFalse(allowList.isValid(null));
        assertFalse(allowList.isValid("@gmail.com"));
        assertFalse(allowList.isValid("user@"));
        assertFalse(allowList.isValid("usergmail.com"));
        assertFalse(allowList.isValid("us er@gmail.com"));
        assertFalse(allowList.isValid("user\t@gmail.com"));
        assertFalse(allowList.isValid("a@b@gmail.com"));
        assertFalse(allowList.isValid("user@gmail.com "));
    }

    @Test
    public void testEmailDomainAllowListFullDomainMode() {
        EmailDomainAllowList allowList = new EmailDomainAllowList.Builder()
                .addDomain("uni.edu.ph")
                .addDomain("Example.ORG")
                .useFullDomain(true)
                .build();

        assertTrue(allowList.isValid("student@uni.edu.ph"));
        assertTrue(allowList.isValid("admin@example.org"));
        assertFalse(allowList.isValid("student@evil.uni.edu.ph"));
    }

    @Test
    public void testStringChecks() {
        assertFalse(StringChecks.hasSymbols(null));
        assertFalse(StringChecks.hasSymbols("abc123 "));
        assertTrue(StringChecks.hasSymbols("hello!"));
        assertTrue(StringChecks.hasSymbols("a@b"));

        assertFalse(StringChecks.hasNumbers(null));
        assertFalse(StringChecks.hasNumbers("hello world!"));
        assertTrue(StringChecks.hasNumbers("abc9"));

        assertFalse(StringChecks.hasSpaces(null));
        assertFalse(StringChecks.hasSpaces("nospaces"));
        assertTrue(StringChecks.hasSpaces("has space"));
    }
}
