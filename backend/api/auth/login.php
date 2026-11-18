<?php
/**
 * POST /api/auth/login.php
 * Nhan: { "identifier": "0901234567 hoac email", "password": "..." }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$input = getRequestInput();
$identifier = trim((string)($input['identifier'] ?? ''));
$password = (string)($input['password'] ?? '');

if ($identifier === '' || $password === '') {
    sendJson(false, 'Vui long nhap day du so dien thoai/email va mat khau.', null, 400);
}

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
    sendJson(false, 'Tai khoan khong ton tai.', null, 401);
}

// Ho tro ca bcrypt (password_hash) va SHA-256 (du lieu seed tu database.sql)
$storedHash = $user['password_hash'];
$isValid = password_verify($password, $storedHash)
    || hash_equals($storedHash, hash('sha256', $password));

if (!$isValid) {
    sendJson(false, 'Mat khau khong chinh xac.', null, 401);
}

// Tu dong nang cap hash SHA-256 cu len bcrypt + random salt khi dang nhap thanh cong
if (!password_get_info($storedHash)['algo']) {
    $newHash = password_hash($password, PASSWORD_BCRYPT);
    $upd = $pdo->prepare('UPDATE users SET password_hash = :hash WHERE id = :id');
    $upd->execute([':hash' => $newHash, ':id' => $user['id']]);
}

unset($user['password_hash']);
$user['id'] = (int)$user['id'];
$user['ekyc_verified'] = (bool)$user['ekyc_verified'];

sendJson(true, 'Dang nhap thanh cong.', ['user' => $user]);
