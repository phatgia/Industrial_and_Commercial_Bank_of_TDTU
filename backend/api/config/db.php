<?php
/**
 * Khoi tao ket noi PDO toi MySQL va cac ham helper tra ve JSON chuan.
 */

$configFile = __DIR__ . '/config.php';
if (!file_exists($configFile)) {
    http_response_code(500);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode([
        'success' => false,
        'message' => 'Thieu file config/config.php. Hay copy tu config.example.php va dien thong tin DB.',
    ], JSON_UNESCAPED_UNICODE);
    exit;
}

require_once $configFile;

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

/**
 * Tra ve JSON response va dung script.
 */
function sendJson(bool $success, string $message, mixed $data = null, int $statusCode = 200): never
{
    http_response_code($statusCode);
    $payload = [
        'success' => $success,
        'message' => $message,
    ];
    if ($data !== null) {
        $payload['data'] = $data;
    }
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

/**
 * Kiem tra HTTP method bat buoc.
 */
function requireMethod(string $method): void
{
    if ($_SERVER['REQUEST_METHOD'] !== strtoupper($method)) {
        sendJson(false, "Method not allowed. Expected $method.", null, 405);
    }
}

/**
 * Doc body JSON hoac fallback ve $_POST.
 */
function getRequestInput(): array
{
    $raw = file_get_contents('php://input');
    if ($raw !== false && trim($raw) !== '') {
        $decoded = json_decode($raw, true);
        if (is_array($decoded)) {
            return $decoded;
        }
    }
    return $_POST;
}

/**
 * Ket noi PDO singleton toi MySQL.
 */
function getDbConnection(): PDO
{
    static $pdo = null;
    if ($pdo instanceof PDO) {
        return $pdo;
    }

    $dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4';
    try {
        $pdo = new PDO($dsn, DB_USER, DB_PASS, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::ATTR_EMULATE_PREPARES => false,
        ]);
        return $pdo;
    } catch (PDOException $e) {
        sendJson(false, 'Khong the ket noi co so du lieu. Kiem tra lai config/config.php.', null, 500);
    }
}
