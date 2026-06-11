<?php

declare(strict_types=1);

namespace EduGuard\Repositories;

use EduGuard\Core\Database;
use EduGuard\Support\Id;
use PDO;

final class UserRepository
{
    private PDO $db;

    public function __construct()
    {
        $this->db = Database::pdo();
    }

    public function findById(string $id): ?array
    {
        $stmt = $this->db->prepare('SELECT * FROM users WHERE id = ?');
        $stmt->execute([$id]);

        return $stmt->fetch() ?: null;
    }

    public function findByEmail(string $email): ?array
    {
        $stmt = $this->db->prepare('SELECT * FROM users WHERE email = ?');
        $stmt->execute([$email]);

        return $stmt->fetch() ?: null;
    }

    /** @return array<int, array> */
    public function allByRole(string $role): array
    {
        $stmt = $this->db->prepare('SELECT * FROM users WHERE role = ? ORDER BY name COLLATE NOCASE');
        $stmt->execute([$role]);

        return $stmt->fetchAll();
    }

    /** @return array<int, array> */
    public function studentsForMentor(string $mentorId): array
    {
        $stmt = $this->db->prepare(
            "SELECT * FROM users WHERE role = 'student' AND assigned_mentor = ? ORDER BY name COLLATE NOCASE"
        );
        $stmt->execute([$mentorId]);

        return $stmt->fetchAll();
    }

    public function create(array $data): array
    {
        $id = Id::generate();

        $stmt = $this->db->prepare(
            'INSERT INTO users (id, name, email, phone, password_hash, role, assigned_mentor, status, created_at)
             VALUES (:id, :name, :email, :phone, :password_hash, :role, :assigned_mentor, :status, :created_at)'
        );

        $stmt->execute([
            ':id'              => $id,
            ':name'            => $data['name'],
            ':email'           => $data['email'],
            ':phone'           => $data['phone'] ?? null,
            ':password_hash'   => password_hash($data['password'], PASSWORD_DEFAULT),
            ':role'            => $data['role'],
            ':assigned_mentor' => $data['assignedMentor'] ?? null,
            ':status'          => $data['status'] ?? 'active',
            ':created_at'      => gmdate('c'),
        ]);

        return $this->findById($id);
    }

    /** Update only the provided, whitelisted fields. */
    public function update(string $id, array $data): ?array
    {
        $map = [
            'name'           => 'name',
            'email'          => 'email',
            'phone'          => 'phone',
            'role'           => 'role',
            'assignedMentor' => 'assigned_mentor',
            'status'         => 'status',
        ];

        $sets = [];
        $params = [':id' => $id];

        foreach ($map as $field => $column) {
            if (array_key_exists($field, $data)) {
                $sets[] = "$column = :$column";
                $params[":$column"] = $data[$field];
            }
        }

        if (!empty($data['password'])) {
            $sets[] = 'password_hash = :password_hash';
            $params[':password_hash'] = password_hash($data['password'], PASSWORD_DEFAULT);
        }

        if ($sets !== []) {
            $stmt = $this->db->prepare('UPDATE users SET ' . implode(', ', $sets) . ' WHERE id = :id');
            $stmt->execute($params);
        }

        return $this->findById($id);
    }

    public function delete(string $id): bool
    {
        $stmt = $this->db->prepare('DELETE FROM users WHERE id = ?');
        $stmt->execute([$id]);

        return $stmt->rowCount() > 0;
    }

    /** Map a DB row to the JSON shape the Android client deserializes. */
    public function toApi(array $row): array
    {
        return [
            '_id'            => $row['id'],
            'name'           => $row['name'],
            'email'          => $row['email'],
            'phone'          => $row['phone'],
            'role'           => $row['role'],
            'assignedMentor' => $row['assigned_mentor'],
            'status'         => $row['status'],
        ];
    }
}
