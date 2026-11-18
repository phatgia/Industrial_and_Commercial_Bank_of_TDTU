<?php
/**
 * POST /api/officer/update_policy.php
 * Nhan vien ngan hang (OFFICER) cap nhat lai suat tiet kiem trong bank_policies.
 * Body: { "officer_id": 3, "saving_interest_rate": 6.2, "high_value_threshold": 10000000 }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$input = getRequestInput();
$officerId = (int)($input['officer_id'] ?? 0);
$savingRate = isset($input['saving_interest_rate']) ? (float)$input['saving_interest_rate'] : -1.0;
$threshold = isset($input['high_value_threshold']) ? (float)$input['high_value_threshold'] : 10000000.0;

if ($officerId <= 0 || $savingRate < 0 || $savingRate > 50) {
    sendJson(false, 'Thong tin cap nhat lai suat khong hop le.', null, 400);
}

$pdo = getDbConnection();

$roleCheck = $pdo->prepare('SELECT role FROM users WHERE id = :id LIMIT 1');
$roleCheck->execute([':id' => $officerId]);
$officer = $roleCheck->fetch();
if (!$officer || $officer['role'] !== 'OFFICER') {
    sendJson(false, 'Chi nhan vien ngan hang (OFFICER) moi co quyen thay doi chinh sach.', null, 403);
}

try {
    $pdo->beginTransaction();

    $pdo->prepare(
        'UPDATE bank_policies
         SET saving_interest_rate = :rate,
             high_value_threshold = :threshold,
             updated_by_officer_id = :oid
         WHERE id = 1'
    )->execute([
        ':rate' => $savingRate,
        ':threshold' => $threshold,
        ':oid' => $officerId,
    ]);

    $pdo->prepare('UPDATE accounts SET interest_rate = :rate WHERE account_type = "SAVING"')
        ->execute([':rate' => $savingRate]);

    $pdo->commit();

    sendJson(true, 'Cap nhat lai suat tiet kiem thanh cong.', [
        'saving_interest_rate' => $savingRate,
        'high_value_threshold' => $threshold,
    ]);
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendJson(false, 'Loi he thong khi cap nhat chinh sach.', null, 500);
}
