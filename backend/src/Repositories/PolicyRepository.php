<?php

declare(strict_types=1);

namespace EduGuard\Repositories;

use EduGuard\Core\Database;
use EduGuard\Support\Id;
use PDO;

final class PolicyRepository
{
    private PDO $db;

    public function __construct()
    {
        $this->db = Database::pdo();
    }

    public function findByStudent(string $studentId): ?array
    {
        $stmt = $this->db->prepare('SELECT * FROM policies WHERE student_id = ?');
        $stmt->execute([$studentId]);

        return $stmt->fetch() ?: null;
    }

    /** Insert or update the policy for a student. */
    public function upsert(string $studentId, array $policy): array
    {
        $allowedApps  = json_encode($policy['allowedApps'] ?? []);
        $sleep        = $policy['sleepMode'] ?? [];
        $sleepEnabled = !empty($sleep['enabled']) ? 1 : 0;
        $sleepStart   = $sleep['startTime'] ?? null;
        $sleepEnd     = $sleep['endTime'] ?? null;
        $theme        = $policy['theme'] ?? null;
        $now          = gmdate('c');

        $existing = $this->findByStudent($studentId);

        if ($existing === null) {
            $stmt = $this->db->prepare(
                'INSERT INTO policies (id, student_id, allowed_apps, sleep_enabled, sleep_start, sleep_end, theme, updated_at)
                 VALUES (:id, :student_id, :allowed_apps, :sleep_enabled, :sleep_start, :sleep_end, :theme, :updated_at)'
            );
            $stmt->execute([
                ':id'            => Id::generate(),
                ':student_id'    => $studentId,
                ':allowed_apps'  => $allowedApps,
                ':sleep_enabled' => $sleepEnabled,
                ':sleep_start'   => $sleepStart,
                ':sleep_end'     => $sleepEnd,
                ':theme'         => $theme,
                ':updated_at'    => $now,
            ]);
        } else {
            $stmt = $this->db->prepare(
                'UPDATE policies
                    SET allowed_apps = :allowed_apps,
                        sleep_enabled = :sleep_enabled,
                        sleep_start = :sleep_start,
                        sleep_end = :sleep_end,
                        theme = :theme,
                        updated_at = :updated_at
                  WHERE student_id = :student_id'
            );
            $stmt->execute([
                ':student_id'    => $studentId,
                ':allowed_apps'  => $allowedApps,
                ':sleep_enabled' => $sleepEnabled,
                ':sleep_start'   => $sleepStart,
                ':sleep_end'     => $sleepEnd,
                ':theme'         => $theme,
                ':updated_at'    => $now,
            ]);
        }

        return $this->toApi($this->findByStudent($studentId), $studentId);
    }

    /**
     * Map a DB row (or null) to the Policy JSON shape. When no policy exists yet,
     * a sane empty default is returned so the client never receives null.
     */
    public function toApi(?array $row, string $studentId): array
    {
        if ($row === null) {
            return [
                '_id'         => null,
                'studentId'   => $studentId,
                'allowedApps' => [],
                'sleepMode'   => ['enabled' => false, 'startTime' => null, 'endTime' => null],
                'theme'       => null,
            ];
        }

        return [
            '_id'         => $row['id'],
            'studentId'   => $row['student_id'],
            'allowedApps' => json_decode($row['allowed_apps'], true) ?: [],
            'sleepMode'   => [
                'enabled'   => (bool) $row['sleep_enabled'],
                'startTime' => $row['sleep_start'],
                'endTime'   => $row['sleep_end'],
            ],
            'theme'       => $row['theme'],
        ];
    }
}
