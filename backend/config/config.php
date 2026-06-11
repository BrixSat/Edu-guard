<?php

declare(strict_types=1);

/**
 * Central configuration. Values can be overridden with environment variables
 * so you never have to commit secrets.
 */
return [
    // Secret used to sign JWTs. CHANGE THIS in production.
    'jwt_secret' => getenv('EDUGUARD_JWT_SECRET') ?: 'dev-secret-change-me',

    // Token lifetime in seconds (default: 7 days).
    'jwt_ttl' => (int) (getenv('EDUGUARD_JWT_TTL') ?: 60 * 60 * 24 * 7),

    // Absolute path to the SQLite database file.
    'db_path' => getenv('EDUGUARD_DB_PATH') ?: dirname(__DIR__) . '/storage/eduguard.sqlite',
];
