<?php

declare(strict_types=1);

namespace EduGuard\Middleware;

use EduGuard\Core\HttpException;
use EduGuard\Core\Jwt;
use EduGuard\Core\Request;
use EduGuard\Repositories\UserRepository;

/**
 * Authenticates the request from its bearer token and exposes role guards.
 */
final class AuthMiddleware
{
    /**
     * Resolve the currently authenticated user row.
     *
     * @throws HttpException 401 when the token is missing/invalid or the user is gone.
     */
    public static function user(): array
    {
        $token = Request::bearer();
        if ($token === null) {
            throw new HttpException(401, 'Missing or malformed Authorization header');
        }

        $config = require dirname(__DIR__, 2) . '/config/config.php';

        try {
            $claims = Jwt::decode($token, $config['jwt_secret']);
        } catch (\Throwable $e) {
            throw new HttpException(401, 'Invalid token: ' . $e->getMessage());
        }

        $user = (new UserRepository())->findById((string) ($claims['sub'] ?? ''));
        if ($user === null) {
            throw new HttpException(401, 'User no longer exists');
        }

        return $user;
    }

    /**
     * @throws HttpException 403 when the user lacks every allowed role.
     */
    public static function requireRole(array $user, string ...$roles): void
    {
        if (!in_array($user['role'], $roles, true)) {
            throw new HttpException(403, 'Forbidden: requires role ' . implode(' or ', $roles));
        }
    }
}
