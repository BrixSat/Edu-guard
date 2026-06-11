<?php

declare(strict_types=1);

namespace EduGuard\Controllers;

use EduGuard\Core\HttpException;
use EduGuard\Core\Request;
use EduGuard\Core\Response;
use EduGuard\Middleware\AuthMiddleware;
use EduGuard\Repositories\UserRepository;

final class UserController
{
    private UserRepository $users;

    public function __construct()
    {
        $this->users = new UserRepository();
    }

    /** GET /users/me */
    public function me(): void
    {
        $user = AuthMiddleware::user();
        Response::json($this->users->toApi($user));
    }

    /** GET /users/students  (admin: all; mentor: only assigned) */
    public function students(): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'admin', 'mentor');

        $rows = $user['role'] === 'mentor'
            ? $this->users->studentsForMentor($user['id'])
            : $this->users->allByRole('student');

        Response::json(array_map([$this->users, 'toApi'], $rows));
    }

    /** GET /users/mentors  (admin only) */
    public function mentors(): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'admin');

        Response::json(array_map([$this->users, 'toApi'], $this->users->allByRole('mentor')));
    }

    /** POST /users  (admin only) */
    public function create(): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'admin');

        $body = Request::body();

        foreach (['name', 'email', 'password', 'role'] as $field) {
            if (empty($body[$field])) {
                throw new HttpException(400, "Field '$field' is required");
            }
        }

        if (!in_array($body['role'], ['admin', 'mentor', 'student'], true)) {
            throw new HttpException(422, 'role must be admin, mentor, or student');
        }

        if ($this->users->findByEmail($body['email']) !== null) {
            throw new HttpException(409, 'A user with that email already exists');
        }

        Response::json($this->users->toApi($this->users->create($body)), 201);
    }

    /** PATCH /users/{userId}  (admin only) */
    public function update(string $userId): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'admin');

        if ($this->users->findById($userId) === null) {
            throw new HttpException(404, 'User not found');
        }

        $updated = $this->users->update($userId, Request::body());
        Response::json($this->users->toApi($updated));
    }

    /** DELETE /users/{userId}  (admin only) */
    public function delete(string $userId): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'admin');

        if (!$this->users->delete($userId)) {
            throw new HttpException(404, 'User not found');
        }

        Response::noContent();
    }
}
