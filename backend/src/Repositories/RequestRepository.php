<?php

declare(strict_types=1);

namespace EduGuard\Repositories;

use EduGuard\Core\Database;
use EduGuard\Support\Id;
use PDO;

final class RequestRepository
{
    private PDO $db;

    public function __construct()
    {
        $this->db = Database::pdo();
    }

    public function findById(string $id): ?array
    {
        $stmt = $this->db->prepare('SELECT * FROM requests WHERE id = ?');
        $stmt->execute([$id]);

        return $stmt->fetch() ?: null;
    }

    public function create(array $data): array
    {
        $id = Id::generate();

        $stmt = $this->db->prepare(
            'INSERT INTO requests
                (id, student_id, mentor_id, type, message, requested_for_app, requested_extra_minutes, status, created_at)
             VALUES
                (:id, :student_id, :mentor_id, :type, :message, :requested_for_app, :requested_extra_minutes, :status, :created_at)'
        );

        $stmt->execute([
            ':id'                      => $id,
            ':student_id'              => $data['studentId'],
            ':mentor_id'               => $data['mentorId'] ?? null,
            ':type'                    => $data['type'] ?? 'extraTime',
            ':message'                 => $data['message'] ?? null,
            ':requested_for_app'       => $data['requestedForApp'] ?? null,
            ':requested_extra_minutes' => $data['requestedExtraMinutes'] ?? null,
            ':status'                  => 'pending',
            ':created_at'              => gmdate('c'),
        ]);

        return $this->findById($id);
    }

    public function updateStatus(string $id, string $status): ?array
    {
        $stmt = $this->db->prepare(
            'UPDATE requests SET status = :status, resolved_at = :resolved_at WHERE id = :id'
        );
        $stmt->execute([
            ':status'      => $status,
            ':resolved_at' => gmdate('c'),
            ':id'          => $id,
        ]);

        return $this->findById($id);
    }

    /** @return array<int, array> */
    public function allForStudent(string $studentId): array
    {
        $stmt = $this->db->prepare('SELECT * FROM requests WHERE student_id = ? ORDER BY created_at DESC');
        $stmt->execute([$studentId]);

        return $stmt->fetchAll();
    }

    /** Requests for a mentor: assigned directly OR belonging to one of the mentor's students. */
    public function allForMentor(string $mentorId): array
    {
        $stmt = $this->db->prepare(
            'SELECT r.* FROM requests r
             LEFT JOIN users s ON s.id = r.student_id
             WHERE r.mentor_id = :mentor OR s.assigned_mentor = :mentor
             ORDER BY r.created_at DESC'
        );
        $stmt->execute([':mentor' => $mentorId]);

        return $stmt->fetchAll();
    }

    /** @return array<int, array> */
    public function all(): array
    {
        return $this->db->query('SELECT * FROM requests ORDER BY created_at DESC')->fetchAll();
    }

    public function toApi(array $row): array
    {
        return [
            '_id'                   => $row['id'],
            'studentId'             => $row['student_id'],
            'mentorId'              => $row['mentor_id'],
            'type'                  => $row['type'],
            'message'               => $row['message'],
            'requestedForApp'       => $row['requested_for_app'],
            'requestedExtraMinutes' => $row['requested_extra_minutes'] !== null
                ? (int) $row['requested_extra_minutes']
                : null,
            'status'                => $row['status'],
            'createdAt'             => $row['created_at'],
            'resolvedAt'            => $row['resolved_at'],
        ];
    }
}
