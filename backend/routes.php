<?php

declare(strict_types=1);

/**
 * Route table. Mirrors the contract declared in the Android client's
 * data/remote/ApiService.java. $router is provided by public/index.php.
 *
 * @var \EduGuard\Core\Router $router
 */

use EduGuard\Controllers\AuthController;
use EduGuard\Controllers\LogController;
use EduGuard\Controllers\PolicyController;
use EduGuard\Controllers\RequestController;
use EduGuard\Controllers\StatusController;
use EduGuard\Controllers\UserController;

// --- Status dashboard (no auth, read-only) ---
$router->get('', [new StatusController(), 'dashboard']);
$router->get('health', [new StatusController(), 'health']);

// --- Auth ---
$router->post('auth/login', [new AuthController(), 'login']);

// --- Users ---
$router->get('users/me',        [new UserController(), 'me']);
$router->get('users/students',  [new UserController(), 'students']);
$router->get('users/mentors',   [new UserController(), 'mentors']);
$router->post('users',          [new UserController(), 'create']);
$router->patch('users/{userId}',  [new UserController(), 'update']);
$router->delete('users/{userId}', [new UserController(), 'delete']);

// --- Policies ---
$router->get('policies/{studentId}',  [new PolicyController(), 'get']);
$router->post('policies/{studentId}', [new PolicyController(), 'update']);

// --- Requests ---
$router->post('requests',        [new RequestController(), 'create']);
$router->get('requests',         [new RequestController(), 'index']);
$router->patch('requests/{id}',  [new RequestController(), 'updateStatus']);

// --- Logs ---
$router->post('logs',             [new LogController(), 'upload']);
$router->get('logs/{studentId}',  [new LogController(), 'get']);
