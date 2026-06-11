<?php

declare(strict_types=1);

namespace EduGuard\Core;

/**
 * Tiny regex router. Path params are written as {name} and passed to the
 * handler as positional arguments in declaration order.
 */
final class Router
{
    /** @var array<int, array{method:string, path:string, handler:callable}> */
    private array $routes = [];

    public function get(string $path, callable $handler): void    { $this->add('GET', $path, $handler); }
    public function post(string $path, callable $handler): void   { $this->add('POST', $path, $handler); }
    public function patch(string $path, callable $handler): void  { $this->add('PATCH', $path, $handler); }
    public function delete(string $path, callable $handler): void { $this->add('DELETE', $path, $handler); }

    private function add(string $method, string $path, callable $handler): void
    {
        $this->routes[] = compact('method', 'path', 'handler');
    }

    public function dispatch(string $method, string $uri): void
    {
        $path = trim(parse_url($uri, PHP_URL_PATH) ?: '', '/');

        // Strip the directory the front controller is served from, so the app
        // works whether it lives at the web root (php -S) or under a subpath
        // (e.g. Apache at /eduguard/...). Derived from SCRIPT_NAME, not hardcoded.
        $base = trim(str_replace('\\', '/', dirname($_SERVER['SCRIPT_NAME'] ?? '')), '/');
        if ($base !== '' && str_starts_with($path, $base)) {
            $path = trim(substr($path, strlen($base)), '/');
        }

        foreach ($this->routes as $route) {
            if ($route['method'] !== $method) {
                continue;
            }

            $pattern = '#^' . preg_replace('#\{[^/]+\}#', '([^/]+)', trim($route['path'], '/')) . '$#';

            if (preg_match($pattern, $path, $matches)) {
                array_shift($matches);
                ($route['handler'])(...array_map('urldecode', $matches));
                return;
            }
        }

        Response::json(['error' => 'Not found', 'path' => $path], 404);
    }
}
