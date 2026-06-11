<?php

declare(strict_types=1);

namespace EduGuard\Core;

/**
 * Exception carrying an HTTP status code. Thrown anywhere in a controller and
 * translated into a JSON error response by the front controller.
 */
final class HttpException extends \Exception
{
    public function __construct(public readonly int $status, string $message)
    {
        parent::__construct($message);
    }
}
