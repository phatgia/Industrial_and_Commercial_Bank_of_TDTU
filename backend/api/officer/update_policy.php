<?php
/**
 * POST /api/officer/update_policy.php
 * Chi nhan vien ngan hang (OFFICER) duoc cap nhat lai suat tiet kiem trong bank_policies.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
$officer = requireOfficer($pdo);
$officerId = (int)$officer['id'];

$input = getRequestInput();
$savingRate = isset($input['saving_interest_rate']) ? (float)$input['saving_interest_rate'] : -1.0;
$threshold = isset($input['high_value_threshold']) ? (float)$input['high_value_threshold'] : 10000000.0;

if (!is_finite($savingRate) || $savingRate < 0 || $savingRate > 50) {
    sendJson(false, 'Thong tin cap nhat lai suat khong hop le.', null, 400);
}
if (!is_finite($threshold) || $threshold < 100000 || $threshold > 100000000000) {
    sendJson(false, 'Han muc eKYC khong hop le (tu 100.000 den 100.000.000.000 VND).', null, 400);
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
