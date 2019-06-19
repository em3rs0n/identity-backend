package com.yahoo.identity.services.session;

import com.yahoo.identity.Identity;
import com.yahoo.identity.services.account.Account;
import com.yahoo.identity.services.account.AccountService;
import com.yahoo.identity.services.account.AccountUpdate;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import mockit.*;

import java.time.Instant;

public class AbuseDetectionImplTest {
    @Tested
    AbuseDetectionImpl abuseDetection;
    @Injectable
    Identity identity;
    @Injectable
    AccountService accountService;
    @Injectable
    Account account;
    @Injectable
    AccountUpdate accountUpdate;

    String username;
    String password;

    @BeforeMethod
    public void setUpIdentity() {
        username = "alice";
        password = "00000";
        new Expectations() {{
            identity.getAccountService();
            result = accountService;
            accountService.getAccount(username);
            result = account;
            account.getPassword();
            result = password;
        }};
    }

    @Test
    public void testAbuseDetectionUnblockedAndRight() throws Exception {
        new Expectations() {{
            account.getNthTrial();
            result = 0;
            account.getBlockUntil();
            result = Instant.now();
            accountService.newAccountUpdate(username);
            result = accountUpdate;
        }};
        Assert.assertFalse(abuseDetection.abuseDetection(username, password));
        new Expectations() {{
            account.getNthTrial();
            result = 1;
        }};
        Assert.assertFalse(abuseDetection.abuseDetection(username, password));
    }

    @Test
    public void testAbuseDetectionUnblockedAndWrong() throws Exception {
        new Expectations() {{
            account.getNthTrial();
            result = 1;
            account.getBlockUntil();
            result = Instant.now();
            accountService.newAccountUpdate(username);
            result = accountUpdate;
        }};
        Assert.assertTrue(abuseDetection.abuseDetection(username, "12345"));
        new Expectations() {{
           account.getNthTrial();
           result = 6;
        }};
        Assert.assertTrue(abuseDetection.abuseDetection(username, "12345"));
    }

    @Test
    public void testAbuseDetectionBlocked() throws Exception {
        new Expectations() {{
            account.getNthTrial();
            result = 0;
            account.getBlockUntil();
            result = Instant.now().plusSeconds(10000);
        }};
        Assert.assertTrue(abuseDetection.abuseDetection(username, password));
        Assert.assertTrue(abuseDetection.abuseDetection(username, "12345"));
    }
}