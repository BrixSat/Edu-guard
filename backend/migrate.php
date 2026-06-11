<?php

declare(strict_types=1);

/**
 * Creates the SQLite database from schema.sql and seeds demo accounts.
 * Safe to run repeatedly — schema uses IF NOT EXISTS and seeding is skipped
 * when users already exist.
 *
 * Usage:  php migrate.php
 */

require __DIR__ . '/src/bootstrap.php';

use EduGuard\Core\Database;
use EduGuard\Repositories\PolicyRepository;
use EduGuard\Repositories\UserRepository;

$config = require __DIR__ . '/config/config.php';

// Ensure the storage directory exists.
$dir = dirname($config['db_path']);
if (!is_dir($dir)) {
    mkdir($dir, 0775, true);
}

$pdo = Database::pdo();
$pdo->exec(file_get_contents(__DIR__ . '/schema.sql'));
echo "Schema applied.\n";

$users = new UserRepository();

$alreadySeeded = (int) $pdo->query('SELECT COUNT(*) FROM users')->fetchColumn() > 0;
if ($alreadySeeded) {
    echo "Users already present — skipping seed.\n";
    return;
}

$admin = $users->create([
    'name'     => 'Admin User',
    'email'    => 'admin@eduguard.test',
    'phone'    => '0000000000',
    'password' => 'admin123',
    'role'     => 'admin',
]);

$mentor = $users->create([
    'name'     => 'Mentor User',
    'email'    => 'mentor@eduguard.test',
    'phone'    => '1111111111',
    'password' => 'mentor123',
    'role'     => 'mentor',
]);

$student = $users->create([
    'name'           => 'Student User',
    'email'          => 'student@eduguard.test',
    'phone'          => '2222222222',
    'password'       => 'student123',
    'role'           => 'student',
    'assignedMentor' => $mentor['id'],
]);

// A starter policy for the demo student.
(new PolicyRepository())->upsert($student['id'], [
    'allowedApps' => [
        ['packageName' => 'com.google.android.calculator', 'dailyLimitMinutes' => 0,  'blocked' => false],
        ['packageName' => 'com.android.chrome',            'dailyLimitMinutes' => 60, 'blocked' => false],
        ['packageName' => 'com.instagram.android',         'dailyLimitMinutes' => 0,  'blocked' => true],
    ],
    'sleepMode' => ['enabled' => true, 'startTime' => '22:00', 'endTime' => '06:00'],
    'theme'     => 'light',
]);

echo "Seeded demo accounts:\n";
echo "  admin   -> admin@eduguard.test   / admin123\n";
echo "  mentor  -> mentor@eduguard.test  / mentor123\n";
echo "  student -> student@eduguard.test / student123\n";
