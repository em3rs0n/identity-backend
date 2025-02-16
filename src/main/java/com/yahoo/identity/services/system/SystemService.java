package com.yahoo.identity.services.system;

import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

/**
 * This service is used for accessing system resources.
 */
public class SystemService {

    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Nonnull
    public InputStream getResourceAsStream(@Nonnull String resource) throws IOException {
        return Resources.getResourceAsStream(resource);
    }
}
