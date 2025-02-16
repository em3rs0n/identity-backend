package com.yahoo.identity.services.storage.sql;

import static com.kosprov.jargon2.api.Jargon2.Hasher;
import static com.kosprov.jargon2.api.Jargon2.Type;
import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;

import com.kosprov.jargon2.api.Jargon2;
import com.yahoo.identity.services.account.Account;
import com.yahoo.identity.services.account.AccountUpdate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.apache.ibatis.session.SqlSessionFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Instant;

import javax.ws.rs.NotAuthorizedException;

public class SqlAccountTest {
    @Tested
    SqlAccount account;
    @Injectable
    SqlSessionFactory sqlSessionFactory;
    @Injectable
    String username;
    @Injectable
    AccountModel accountModel;

    String password;

    @BeforeMethod
    public void setAccountUpdate() {
        password = "00000";
    }

    @Test
    public void testVerifyUnblockedAndRight() throws Exception {
        new Expectations() {{
            account.getBlockUntilTime();
            result = Instant.now().toEpochMilli();
            account.getConsecutiveFails();
            result = 0;
            accountModel.verify(anyString);
            result = true;
        }};
        Assert.assertTrue(account.verify(password));

        new Expectations() {{
           account.getConsecutiveFails();
           result = 1;
        }};
        Assert.assertTrue(account.verify(password));
    }

    @DataProvider(name = "Fails")
    public static Object[][] fails() {
        return new Object[][] {{1}, {6}};
    }

    @Test(expectedExceptions = NotAuthorizedException.class, dataProvider = "Fails")
    public void testVerifyUnblockedAndWrong(int fails) throws Exception {
        new Expectations() {{
            account.getBlockUntilTime();
            result = Instant.now().toEpochMilli();
            account.getConsecutiveFails();
            result = fails;
            accountModel.verify(anyString);
            result = false;
        }};
        account.verify(password);
    }

    @Test
    public void testVerifyBlocked() throws Exception {
        new Expectations() {{
           account.getConsecutiveFails();
           result = 0;
           account.getBlockUntilTime();
           result = Instant.now().plusSeconds(10000).toEpochMilli();
        }};
        Assert.assertFalse(account.verify(password));
        Assert.assertFalse(account.verify(password));
    }
}