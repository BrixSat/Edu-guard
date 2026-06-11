<?php

declare(strict_types=1);

namespace EduGuard\Controllers;

use EduGuard\Core\Request;
use EduGuard\Core\Response;
use EduGuard\Middleware\AuthMiddleware;
use EduGuard\Repositories\PolicyRepository;

final class PolicyController
{
    private PolicyRepository $policies;

    public function __construct()
    {
        $this->policies = new PolicyRepository();
    }

    /** GET /policies/{studentId}  (any authenticated user) */
    public function get(string $studentId): void
    {
        AuthMiddleware::user();

        $row = $this->policies->findByStudent($studentId);
        Response::json($this->policies->toApi($row, $studentId));
    }

    /** POST /policies/{studentId}  (admin or mentor) */
    public function update(string $studentId): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'admin', 'mentor');

        Response::json($this->policies->upsert($studentId, Request::body()));
    }
}
