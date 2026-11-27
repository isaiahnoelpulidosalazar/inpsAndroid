package com.isaiahnoelpulidosalazar.inpsAndroid.validation;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationTest {

    @Test
    public void testEmailValidatorSplitMode() {
        EmailValidator validator = new EmailValidator.Builder()
                .addDomainName("gmail")
                .addDomainName("Outlook")
                .addDomainExtension("com")
                .build();

        assertTrue(validator.isValid("user@gmail.com"));
        assertTrue(validator.isValid("USER@OUTLOOK.COM"));
        assertFalse(validator.isValid("user@gmail.com.evil.example"));
        assertFalse(validator.isValid("user@yahoo.com"));
        assertFalse(validator.isValid("user@gmail.org"));
        assertFalse(validator.isValid("@gmail.com"));
        assertFalse(validator.isValid("user@"));
        assertFalse(validator.isValid("usergmail.com"));
        assertFalse(validator.isValid(null));
    }

    @Test
    public void testEmailValidatorFullDomainMode() {
        EmailValidator validator = new EmailValidator.Builder()
                .addDomain("uni.edu.ph")
                .addDomain("Example.ORG")
                .useFullDomain(true)
                .build();

        assertTrue(validator.isValid("student@uni.edu.ph"));
        assertTrue(validator.isValid("admin@example.org"));
        assertFalse(validator.isValid("student@evil.uni.edu.ph"));
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
