<?php

declare(strict_types=1);

namespace EduGuard\Support;

/**
 * Generates 24-character hex identifiers, matching the Mongo ObjectId shape
 * the Android client expects in the "_id" field.
 */
final class Id
{
    public static function generate(): string
    {
        return bin2hex(random_bytes(12));
    }
}
