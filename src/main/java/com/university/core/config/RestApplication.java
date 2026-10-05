package com.university.core.config;

import jakarta.ws.rs.ApplicationPath;

/**
 * Delegate/alias in core.config package for RestApplication.
 */
@ApplicationPath("/api")
public class RestApplication extends com.university.RestApplication {
}
