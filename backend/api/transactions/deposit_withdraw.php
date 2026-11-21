<?php
/**
 * POST /api/transactions/deposit_withdraw.php
 * Yeu cau Bearer Token. Nap tien (DEPOSIT) hoac Rut tien (WITHDRAW) tren tai khoan CHECKING.
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);
$userId = (int)$authUser['id'];

$input = getRequestInput();
$type = strtoupper(trim((string)($input['type'] ?? '')));
$rawAmount = (float)($input['amount'] ?? 0);
$amount = round($rawAmount, 2);
$description = trim((string)($input['description'] ?? ''));

if (!in_array($type, ['DEPOSIT', 'WITHDRAW'], true) || !is_finite($rawAmount) || $amount <= 0 || $amount > 500000000) {
    sendJson(false, 'Tham so khong hop le (so tien tu 1 den 500.000.000 VND).', null, 400);
}

if ($type === 'DEPOSIT' && strtoupper((string)$authUser['role']) !== 'OFFICER') {
    sendJson(false, 'Khach hang vui long nap tien qua cong thanh toan Stripe hoac tai quay giao dich.', null, 403);
}

$requiredEkyc = 0;
if ($type === 'WITHDRAW') {
    $policy = $pdo->query('SELECT high_value_threshold FROM bank_policies ORDER BY id ASC LIMIT 1')->fetch();
    $threshold = $policy ? (float)$policy['high_value_threshold'] : 10000000.0;
    $requiredEkyc = ($amount >= $threshold) ? 1 : 0;
    if ($requiredEkyc === 1 && !(bool)$authUser['ekyc_verified']) {
        sendJson(false, 'Giao dich rut tien gia tri cao (>= ' . number_format($threshold) . ' VND) yeu cau xac thuc eKYC truoc.', null, 403);
    }
}

try {
    $pdo->beginTransaction();

    $accStmt = $pdo->prepare(
        'SELECT id, account_number, balance FROM accounts WHERE user_id = :uid AND account_type = "CHECKING" LIMIT 1 FOR UPDATE'
    );
    $accStmt->execute([':uid' => $userId]);
    $acc = $accStmt->fetch();

    if (!$acc) {
        $pdo->rollBack();
        sendJson(false, 'Khong tim thay tai khoan thanh toan.', null, 404);
    }

    if ($type === 'WITHDRAW' && (float)$acc['balance'] < $amount) {
        $pdo->rollBack();
        sendJson(false, 'So du khong du de rut tien.', null, 400);
    }

    $sql = ($type === 'DEPOSIT')
        ? 'UPDATE accounts SET balance = balance + :amt WHERE id = :id'
        : 'UPDATE accounts SET balance = balance - :amt WHERE id = :id';
    $pdo->prepare($sql)->execute([':amt' => $amount, ':id' => $acc['id']]);

    if ($description === '') {
        $description = ($type === 'DEPOSIT') ? 'Nạp tiền vào tài khoản' : 'Rút tiền từ tài khoản';
    }

    $ins = $pdo->prepare(
        'INSERT INTO transactions (from_account_id, to_account_number, to_bank_name, tx_type, amount, status, required_ekyc, description)
         VALUES (:acc_id, :to_acc, "ICBT Bank", :tx_type, :amt, "COMPLETED", :ekyc, :desc)'
    );
    $ins->execute([
        ':acc_id' => $acc['id'],
        ':to_acc' => $acc['account_number'],
        ':tx_type' => $type,
        ':amt' => $amount,
        ':ekyc' => $requiredEkyc,
        ':desc' => $description,
    ]);

    $newBalance = ($type === 'DEPOSIT')
        ? ((float)$acc['balance'] + $amount)
        : ((float)$acc['balance'] - $amount);

    $pdo->commit();

    sendJson(true, ($type === 'DEPOSIT' ? 'Nap tien' : 'Rut tien') . ' thanh cong.', [
        'tx_id' => (int)$pdo->lastInsertId(),
        'new_balance' => round($newBalance, 2),
    ]);
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    sendJson(false, 'Loi he thong khi cap nhat so du.', null, 500);
}
