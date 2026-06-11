<?php

declare(strict_types=1);

/**
 * Shared bootstrap: a minimal PSR-4 autoloader mapping the EduGuard\ namespace
 * to the src/ directory. No Composer required.
 */
spl_autoload_register(static function (string $class): void {
    $prefix = 'EduGuard\\';
    if (strncmp($class, $prefix, strlen($prefix)) !== 0) {
        return;
    }

    $relative = substr($class, strlen($prefix));
    $file = __DIR__ . '/' . str_replace('\\', '/', $relative) . '.php';

    if (is_file($file)) {
        require $file;
    }
});
