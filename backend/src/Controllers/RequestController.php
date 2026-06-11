<?php

declare(strict_types=1);

namespace EduGuard\Controllers;

use EduGuard\Core\HttpException;
use EduGuard\Core\Request;
use EduGuard\Core\Response;
use EduGuard\Middleware\AuthMiddleware;
use EduGuard\Repositories\RequestRepository;

final class RequestController
{
    private RequestRepository $requests;

    public function __construct()
    {
        $this->requests = new RequestRepository();
    }

    /** POST /requests  (student submits an extra-time / emergency request) */
    public function create(): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'student');

        $body = Request::body();

        // The student is always the authenticated user; the mentor defaults to
        // the student's assigned mentor unless the client supplies one.
        $body['studentId'] = $user['id'];
        $body['mentorId']  = $body['mentorId'] ?? $user['assigned_mentor'];

        Response::json($this->requests->toApi($this->requests->create($body)), 201);
    }

    /** GET /requests  (role-filtered: student=own, mentor=assigned, admin=all) */
    public function index(): void
    {
        $user = AuthMiddleware::user();

        $rows = match ($user['role']) {
            'student' => $this->requests->allForStudent($user['id']),
            'mentor'  => $this->requests->allForMentor($user['id']),
            default   => $this->requests->all(),
        };

        Response::json(array_map([$this->requests, 'toApi'], $rows));
    }

    /** PATCH /requests/{id}  { status }  (mentor or admin approves/rejects) */
    public function updateStatus(string $id): void
    {
        $user = AuthMiddleware::user();
        AuthMiddleware::requireRole($user, 'admin', 'mentor');

        $status = Request::body()['status'] ?? '';
        if (!in_array($status, ['pending', 'approved', 'rejected'], true)) {
            throw new HttpException(422, 'status must be pending, approved, or rejected');
        }

        if ($this->requests->findById($id) === null) {
            throw new HttpException(404, 'Request not found');
        }

        Response::json($this->requests->toApi($this->requests->updateStatus($id, $status)));
    }
}
