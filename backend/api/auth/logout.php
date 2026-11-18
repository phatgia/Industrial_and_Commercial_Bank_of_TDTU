<?php
/**
 * POST /api/auth/logout.php
 * Thu hoi Bearer Token hien tai trong bang user_sessions.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$token = getBearerToken();
if ($token !== null && $token !== '') {
    $pdo = getDbConnection();
    $pdo->prepare('DELETE FROM user_sessions WHERE token_hash = :thash')
        ->execute([':thash' => hash('sha256', $token)]);
}

sendJson(true, 'Da dang xuat va thu hoi phien.');
