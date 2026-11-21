<?php
/**
 * POST /api/auth/login.php
 * Nhan: { "identifier": "0901234567 hoac email", "password": "..." }
 * Tra ve: { user, token, expires_in }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$input = getRequestInput();
$identifier = trim((string)($input['identifier'] ?? ''));
$password = (string)($input['password'] ?? '');

if ($identifier === '' || $password === '') {
    sendJson(false, 'Vui long nhap day du so dien thoai/email va mat khau.', null, 400);
}

$clientIp = $_SERVER['REMOTE_ADDR'] ?? 'unknown';
enforceRateLimit('login', $clientIp . ':' . strtolower($identifier), 10, 300);

$pdo = getDbConnection();
$stmt = $pdo->prepare(
    'SELECT id, full_name, email, phone, password_hash, role, ekyc_face_path, ekyc_verified, created_at
     FROM users
     WHERE phone = :phone OR email = :email
     LIMIT 1'
);
$stmt->execute([
    ':phone' => $identifier,
    ':email' => $identifier,
]);
$user = $stmt->fetch();

if (!$user) {
    // Thuc hien hash gia de giam chenh lech thoi gian phan hoi (chong do tai khoan)
    password_verify($password, '$2y$10$abcdefghijklmnopqrstuv12345678901234567890123456789012');
    sendJson(false, 'So dien thoai/email hoac mat khau khong chinh xac.', null, 401);
}

$storedHash = $user['password_hash'];
$isValid = password_verify($password, $storedHash)
    || hash_equals($storedHash, hash('sha256', $password));

if (!$isValid) {
    sendJson(false, 'So dien thoai/email hoac mat khau khong chinh xac.', null, 401);
}

// Tu dong nang cap hash SHA-256 cu len bcrypt + random salt
if (!password_get_info($storedHash)['algo']) {
    $newHash = password_hash($password, PASSWORD_BCRYPT);
    $upd = $pdo->prepare('UPDATE users SET password_hash = :hash WHERE id = :id');
    $upd->execute([':hash' => $newHash, ':id' => $user['id']]);
}

// Don dep cac session het han va tao Bearer Token moi (het han sau 30 phut khong hoat dong)
$pdo->prepare('DELETE FROM user_sessions WHERE user_id = :uid AND expires_at <= NOW()')
    ->execute([':uid' => $user['id']]);

$rawToken = bin2hex(random_bytes(32));
$tokenHash = hash('sha256', $rawToken);

$pdo->prepare(
    'INSERT INTO user_sessions (user_id, token_hash, expires_at)
     VALUES (:uid, :thash, DATE_ADD(NOW(), INTERVAL 30 MINUTE))'
)->execute([
    ':uid' => $user['id'],
    ':thash' => $tokenHash,
]);

unset($user['password_hash']);
$user['id'] = (int)$user['id'];
$user['ekyc_verified'] = (bool)$user['ekyc_verified'];

sendJson(true, 'Dang nhap thanh cong.', [
    'token' => $rawToken,
    'expires_in' => 1800,
    'user' => $user,
]);
