<?php

declare(strict_types=1);

namespace EduGuard\Repositories;

use EduGuard\Core\Database;
use EduGuard\Support\Id;
use PDO;

final class LogRepository
{
    private PDO $db;

    public function __construct()
    {
        $this->db = Database::pdo();
    }

    /** Insert or replace the usage log for a (student, date) pair. */
    public function upsert(string $studentId, string $date, array $appUsage): void
    {
        $stmt = $this->db->prepare(
            'INSERT INTO logs (id, student_id, date, app_usage, created_at)
             VALUES (:id, :student_id, :date, :app_usage, :created_at)
             ON CONFLICT(student_id, date)
             DO UPDATE SET app_usage = excluded.app_usage, created_at = excluded.created_at'
        );

        $stmt->execute([
            ':id'         => Id::generate(),
            ':student_id' => $studentId,
            ':date'       => $date,
            ':app_usage'  => json_encode(array_values($appUsage)),
            ':created_at' => gmdate('c'),
        ]);
    }

    /** @return array<int, array> list of {studentId, date, appUsage} */
    public function allForStudent(string $studentId): array
    {
        $stmt = $this->db->prepare('SELECT * FROM logs WHERE student_id = ? ORDER BY date DESC');
        $stmt->execute([$studentId]);

        return array_map(
            fn (array $row): array => [
                'studentId' => $row['student_id'],
                'date'      => $row['date'],
                'appUsage'  => json_decode($row['app_usage'], true) ?: [],
            ],
            $stmt->fetchAll()
        );
    }
}
