<?php

declare(strict_types=1);

namespace EduGuard\Controllers;

use EduGuard\Core\HttpException;
use EduGuard\Core\Jwt;
use EduGuard\Core\Request;
use EduGuard\Core\Response;
use EduGuard\Repositories\UserRepository;

final class AuthController
{
    /** POST /auth/login  { email, password } -> { token, role, userId, name } */
    public function login(): void
    {
        $body     = Request::body();
        $email    = trim((string) ($body['email'] ?? ''));
        $password = (string) ($body['password'] ?? '');

        if ($email === '' || $password === '') {
            throw new HttpException(400, 'email and password are required');
        }

        $user = (new UserRepository())->findByEmail($email);

        if ($user === null || !password_verify($password, $user['password_hash'])) {
            throw new HttpException(401, 'Invalid credentials');
        }

        if ($user['status'] !== 'active') {
            throw new HttpException(403, 'Account is disabled');
        }

        $config = require dirname(__DIR__, 2) . '/config/config.php';

        $token = Jwt::encode([
            'sub'  => $user['id'],
            'role' => $user['role'],
            'name' => $user['name'],
            'iat'  => time(),
            'exp'  => time() + $config['jwt_ttl'],
        ], $config['jwt_secret']);

        Response::json([
            'token'  => $token,
            'role'   => $user['role'],
            'userId' => $user['id'],
            'name'   => $user['name'],
        ]);
    }
}
