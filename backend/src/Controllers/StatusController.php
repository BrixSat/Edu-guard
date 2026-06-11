<?php

declare(strict_types=1);

namespace EduGuard\Controllers;

use EduGuard\Core\Database;
use EduGuard\Core\Response;

/**
 * A small, no-auth, read-only status dashboard for local monitoring.
 * Server-rendered so it works in any browser with no token needed.
 */
final class StatusController
{
    /** GET /health -> machine-readable status. */
    public function health(): void
    {
        $config = require dirname(__DIR__, 2) . '/config/config.php';

        try {
            $writable = is_writable($config['db_path']);
            Database::pdo()->query('SELECT 1');
            Response::json([
                'status'      => 'ok',
                'database'    => 'connected',
                'db_writable' => $writable,
                'time'        => gmdate('c'),
            ]);
        } catch (\Throwable $e) {
            Response::json(['status' => 'error', 'detail' => $e->getMessage()], 500);
        }
    }

    /** GET / -> HTML dashboard. */
    public function dashboard(): void
    {
        $config = require dirname(__DIR__, 2) . '/config/config.php';
        $pdo = Database::pdo();

        $count = static fn (string $table): int =>
            (int) $pdo->query("SELECT COUNT(*) FROM $table")->fetchColumn();

        $counts = [
            'Users'    => $count('users'),
            'Policies' => $count('policies'),
            'Requests' => $count('requests'),
            'Logs'     => $count('logs'),
        ];

        $users = $pdo->query(
            'SELECT name, email, role, status FROM users ORDER BY role, name COLLATE NOCASE'
        )->fetchAll();

        $requests = $pdo->query(
            'SELECT r.type, r.status, r.created_at, u.name AS student
               FROM requests r LEFT JOIN users u ON u.id = r.student_id
              ORDER BY r.created_at DESC LIMIT 10'
        )->fetchAll();

        $dbWritable = is_writable($config['db_path']);

        header('Content-Type: text/html; charset=utf-8');
        echo $this->render($counts, $users, $requests, $dbWritable);
        exit;
    }

    private function render(array $counts, array $users, array $requests, bool $dbWritable): string
    {
        $e = static fn (?string $s): string => htmlspecialchars((string) $s, ENT_QUOTES);

        $cards = '';
        foreach ($counts as $label => $n) {
            $cards .= "<div class='card'><div class='num'>{$n}</div><div class='lbl'>" . $e($label) . "</div></div>";
        }

        $userRows = '';
        foreach ($users as $u) {
            $badge = $u['status'] === 'active' ? 'ok' : 'bad';
            $userRows .= '<tr>'
                . '<td>' . $e($u['name']) . '</td>'
                . '<td>' . $e($u['email']) . '</td>'
                . "<td><span class='pill role-" . $e($u['role']) . "'>" . $e($u['role']) . '</span></td>'
                . "<td><span class='pill {$badge}'>" . $e($u['status']) . '</span></td>'
                . '</tr>';
        }

        $reqRows = '';
        foreach ($requests as $r) {
            $reqRows .= '<tr>'
                . '<td>' . $e($r['student'] ?? '—') . '</td>'
                . '<td>' . $e($r['type']) . '</td>'
                . "<td><span class='pill st-" . $e($r['status']) . "'>" . $e($r['status']) . '</span></td>'
                . '<td>' . $e($r['created_at']) . '</td>'
                . '</tr>';
        }
        if ($reqRows === '') {
            $reqRows = "<tr><td colspan='4' class='muted'>No requests yet.</td></tr>";
        }

        $dbBadge = $dbWritable
            ? "<span class='pill ok'>writable</span>"
            : "<span class='pill bad'>READ-ONLY — fix storage/ permissions</span>";

        return <<<HTML
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>EduGuard Backend · Status</title>
<style>
  :root { color-scheme: light dark; }
  * { box-sizing: border-box; }
  body { font: 15px/1.5 system-ui, sans-serif; margin: 0; background: #0f1115; color: #e7e9ee; }
  header { padding: 24px 32px; background: #161922; border-bottom: 1px solid #262b38; }
  h1 { margin: 0; font-size: 20px; }
  h1 .dot { color: #34d399; }
  .sub { color: #8b93a7; font-size: 13px; margin-top: 4px; }
  main { padding: 24px 32px; max-width: 960px; }
  .cards { display: flex; gap: 16px; flex-wrap: wrap; margin-bottom: 28px; }
  .card { flex: 1; min-width: 120px; background: #161922; border: 1px solid #262b38; border-radius: 12px; padding: 18px; }
  .num { font-size: 30px; font-weight: 700; }
  .lbl { color: #8b93a7; font-size: 13px; text-transform: uppercase; letter-spacing: .04em; }
  h2 { font-size: 14px; text-transform: uppercase; letter-spacing: .05em; color: #8b93a7; margin: 28px 0 10px; }
  table { width: 100%; border-collapse: collapse; background: #161922; border: 1px solid #262b38; border-radius: 12px; overflow: hidden; }
  th, td { text-align: left; padding: 10px 14px; border-bottom: 1px solid #262b38; font-size: 14px; }
  th { color: #8b93a7; font-weight: 600; font-size: 12px; text-transform: uppercase; }
  tr:last-child td { border-bottom: 0; }
  .muted { color: #8b93a7; }
  .pill { display: inline-block; padding: 2px 9px; border-radius: 999px; font-size: 12px; font-weight: 600; }
  .ok  { background: #143526; color: #4ade80; }
  .bad { background: #3a1518; color: #f87171; }
  .role-admin   { background: #2a1d44; color: #c4b5fd; }
  .role-mentor  { background: #14304a; color: #7dd3fc; }
  .role-student { background: #2d2a14; color: #fde047; }
  .st-pending  { background: #2d2a14; color: #fde047; }
  .st-approved { background: #143526; color: #4ade80; }
  .st-rejected { background: #3a1518; color: #f87171; }
  code { background: #0b0d12; padding: 2px 6px; border-radius: 6px; }
</style>
</head>
<body>
<header>
  <h1><span class="dot">●</span> EduGuard Backend</h1>
  <div class="sub">Status dashboard · database {$dbBadge} · <a href="health" style="color:#7dd3fc">health</a> (JSON)</div>
</header>
<main>
  <div class="cards">{$cards}</div>

  <h2>Users</h2>
  <table>
    <tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th></tr>
    {$userRows}
  </table>

  <h2>Recent requests</h2>
  <table>
    <tr><th>Student</th><th>Type</th><th>Status</th><th>Created</th></tr>
    {$reqRows}
  </table>
</main>
</body>
</html>
HTML;
    }
}
