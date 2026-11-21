<?php
/**
 * Khoi tao ket noi PDO toi MySQL, JSON helpers va xac thuc Bearer Token.
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
header('X-Content-Type-Options: nosniff');
header('X-Frame-Options: DENY');
header('Referrer-Policy: no-referrer');
header('Cache-Control: no-store, no-cache, must-revalidate, private');
header('Pragma: no-cache');
if (defined('CORS_ALLOWED_ORIGIN') && CORS_ALLOWED_ORIGIN !== '') {
    header('Access-Control-Allow-Origin: ' . CORS_ALLOWED_ORIGIN);
    header('Vary: Origin');
}
header('Access-Control-Allow-Methods: GET, POST, PUT, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(204);
    exit;
}

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

function requireMethod(string $method): void
{
    if ($_SERVER['REQUEST_METHOD'] !== strtoupper($method)) {
        sendJson(false, "Method not allowed. Expected $method.", null, 405);
    }
}

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

/**
 * Doc Bearer token tu HTTP Header Authorization (ho tro nhieu cau hinh Apache/CGI).
 */
function getBearerToken(): ?string
{
    $header = $_SERVER['HTTP_AUTHORIZATION']
        ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION']
        ?? '';

    if ($header === '' && function_exists('getallheaders')) {
        foreach (getallheaders() as $key => $val) {
            if (strcasecmp((string)$key, 'Authorization') === 0) {
                $header = (string)$val;
                break;
            }
        }
    }

    if (preg_match('/^\s*Bearer\s+(\S+)\s*$/i', $header, $matches)) {
        return $matches[1];
    }
    return null;
}

/**
 * Xac thuc phien dang nhap qua Bearer Token. Tu dong gia han them 30 phut khi con hoat dong.
 */
function requireAuth(PDO $pdo): array
{
    $token = getBearerToken();
    if ($token === null || $token === '') {
        sendJson(false, 'Yeu cau dang nhap (Thieu Bearer Token).', null, 401);
    }

    $tokenHash = hash('sha256', $token);
    $stmt = $pdo->prepare(
        'SELECT s.id AS session_id, u.id, u.full_name, u.email, u.phone, u.role, u.ekyc_face_path, u.ekyc_verified, u.created_at
         FROM user_sessions s
         INNER JOIN users u ON s.user_id = u.id
         WHERE s.token_hash = :thash AND s.expires_at > NOW() AND s.created_at > DATE_SUB(NOW(), INTERVAL 12 HOUR)
         LIMIT 1'
    );
    $stmt->execute([':thash' => $tokenHash]);
    $row = $stmt->fetch();

    if (!$row) {
        sendJson(false, 'Phien dang nhap khong hop le hoac da het han.', null, 401);
    }

    // Gia han phien them 30 phut (sliding session timeout)
    $pdo->prepare('UPDATE user_sessions SET expires_at = DATE_ADD(NOW(), INTERVAL 30 MINUTE) WHERE id = :sid')
        ->execute([':sid' => $row['session_id']]);

    unset($row['session_id']);
    $row['id'] = (int)$row['id'];
    $row['ekyc_verified'] = (bool)$row['ekyc_verified'];
    return $row;
}

/**
 * Chi cho phep tai khoan nhan vien ngan hang (OFFICER) truy cap.
 */
function requireOfficer(PDO $pdo): array
{
    $user = requireAuth($pdo);
    if (strtoupper((string)$user['role']) !== 'OFFICER') {
        sendJson(false, 'Khong co quyen truy cap. Chi danh cho nhan vien ngan hang (OFFICER).', null, 403);
    }
    return $user;
}

/**
 * Gioi han tan suat request theo cua so truot bang file lock (khong can sua DB).
 */
function enforceRateLimit(string $bucket, string $key, int $maxRequests, int $windowSeconds): void
{
    $dir = rtrim(sys_get_temp_dir(), '/\\') . DIRECTORY_SEPARATOR . 'icbt_rl';
    if (!is_dir($dir) && !@mkdir($dir, 0700, true) && !is_dir($dir)) {
        return;
    }

    $file = $dir . DIRECTORY_SEPARATOR . hash('sha256', $bucket . '|' . $key) . '.json';
    $fp = @fopen($file, 'c+');
    if ($fp === false) {
        return;
    }

    if (flock($fp, LOCK_EX)) {
        rewind($fp);
        $raw = stream_get_contents($fp);
        $hits = ($raw !== false && $raw !== '') ? json_decode($raw, true) : [];
        if (!is_array($hits)) {
            $hits = [];
        }

        $now = time();
        $cutoff = $now - $windowSeconds;
        $filtered = [];
        foreach ($hits as $ts) {
            if (is_int($ts) && $ts > $cutoff) {
                $filtered[] = $ts;
            }
        }

        if (count($filtered) >= $maxRequests) {
            flock($fp, LOCK_UN);
            fclose($fp);
            sendJson(false, 'Qua nhieu yeu cau. Vui long thu lai sau it phut.', null, 429);
        }

        $filtered[] = $now;
        ftruncate($fp, 0);
        rewind($fp);
        fwrite($fp, json_encode($filtered));
        fflush($fp);
        flock($fp, LOCK_UN);
    }
    fclose($fp);
}
