<?php

declare(strict_types=1);

namespace EduGuard\Core;

/**
 * Helpers for reading the incoming HTTP request.
 */
final class Request
{
    /** Decoded JSON body as an associative array (empty array if none). */
    public static function body(): array
    {
        $raw = file_get_contents('php://input');
        $data = json_decode($raw ?: '', true);

        return is_array($data) ? $data : [];
    }

    /** The raw bearer token, or null if absent. */
    public static function bearer(): ?string
    {
        // [^\s,]+ stops at the first whitespace or comma, so a duplicated header
        // ("Bearer x, Bearer x" — sent by some buggy clients) still yields one token.
        $header = self::authHeader();
        if ($header !== null && preg_match('/Bearer\s+([^\s,]+)/i', $header, $m)) {
            return $m[1];
        }

        return null;
    }

    private static function authHeader(): ?string
    {
        if (function_exists('getallheaders')) {
            foreach (getallheaders() as $key => $value) {
                if (strcasecmp($key, 'Authorization') === 0) {
                    return $value;
                }
            }
        }

        return $_SERVER['HTTP_AUTHORIZATION']
            ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION']
            ?? null;
    }
}
