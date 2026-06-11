<?php

declare(strict_types=1);

require dirname(__DIR__) . '/src/bootstrap.php';

use EduGuard\Core\HttpException;
use EduGuard\Core\Response;
use EduGuard\Core\Router;

// --- CORS (harmless for the native app, handy for browser-based testing) ---
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Headers: Authorization, Content-Type');
header('Access-Control-Allow-Methods: GET, POST, PATCH, DELETE, OPTIONS');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

$router = new Router();
require dirname(__DIR__) . '/routes.php';

try {
    $router->dispatch($_SERVER['REQUEST_METHOD'], $_SERVER['REQUEST_URI']);
} catch (HttpException $e) {
    Response::json(['error' => $e->getMessage()], $e->status);
} catch (\Throwable $e) {
    Response::json(['error' => 'Internal server error', 'detail' => $e->getMessage()], 500);
}
