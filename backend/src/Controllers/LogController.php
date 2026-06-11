<?php

declare(strict_types=1);

namespace EduGuard\Controllers;

use EduGuard\Core\HttpException;
use EduGuard\Core\Request;
use EduGuard\Core\Response;
use EduGuard\Middleware\AuthMiddleware;
use EduGuard\Repositories\LogRepository;

final class LogController
{
    private LogRepository $logs;

    public function __construct()
    {
        $this->logs = new LogRepository();
    }

    /**
     * POST /logs  { studentId?, date, appUsage:[{packageName, duration}] }
     * studentId defaults to the authenticated user (a student logging itself).
     */
    public function upload(): void
    {
        $user = AuthMiddleware::user();
        $body = Request::body();

        $studentId = $body['studentId'] ?? $user['id'];
        $date      = $body['date'] ?? gmdate('Y-m-d');
        $appUsage  = is_array($body['appUsage'] ?? null) ? $body['appUsage'] : [];

        if ($date === '') {
            throw new HttpException(400, 'date is required');
        }

        $this->logs->upsert($studentId, $date, $appUsage);
        Response::noContent();
    }

    /** GET /logs/{studentId} */
    public function get(string $studentId): void
    {
        AuthMiddleware::user();
        Response::json($this->logs->allForStudent($studentId));
    }
}
