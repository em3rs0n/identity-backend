package com.yahoo.identity.services.storage.sql;

import com.yahoo.identity.IdentityException;
import com.yahoo.identity.services.account.AccountUpdate;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.Instant;

import javax.annotation.Nonnull;

public class SqlAccountUpdate implements AccountUpdate {

    private final SqlSessionFactory sqlSessionFactory;
    private AccountModel account = new AccountModel();

    public SqlAccountUpdate(@Nonnull SqlSessionFactory sqlSessionFactory, @Nonnull String username) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.account.setUsername(username);
    }

    @Override
    @Nonnull
    public AccountUpdate setEmail(@Nonnull String email) {
        account.setEmail(email);
        return this;
    }

    @Override
    @Nonnull
    public AccountUpdate setEmailStatus(@Nonnull int emailStatus) {
        account.setEmailStatus(emailStatus);
        return this;
    }

    @Override
    @Nonnull
    public AccountUpdate setPassword(@Nonnull String password) {
        account.setPassword(password);
        return this;
    }

    @Override
    @Nonnull
    public AccountUpdate setUpdateTime(@Nonnull Instant updateTime) {
        account.setUpdateTs(updateTime.toEpochMilli());
        return this;
    }

    @Nonnull
    @Override
    public AccountUpdate setDescription(@Nonnull String title) {
        account.setDescription(title);
        return this;
    }

    @Nonnull
    @Override
    public AccountUpdate setBlockUntilTime(@Nonnull Instant blockUntil) {
        account.setBlockUntilTs(blockUntil.toEpochMilli());
        return this;
    }

    @Nonnull
    @Override
    public AccountUpdate setConsecutiveFails(@Nonnull int consecutiveFails) {
        account.setConsecutiveFails(consecutiveFails);
        return this;
    }

    @Nonnull
    @Override
    public String update() throws IdentityException {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            AccountMapper mapper = session.getMapper(AccountMapper.class);
            mapper.updateAccount(account);
            session.commit();
        }
        return account.getUsername();
    }
}