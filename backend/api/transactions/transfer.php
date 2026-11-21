<?php
/**
 * POST /api/transactions/transfer.php
 * Yeu cau Bearer Token. Chuyen tien qua 2 buoc (OTP 2FA + ACID Transaction).
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);
$userId = (int)$authUser['id'];

$input = getRequestInput();
$action = trim((string)($input['action'] ?? 'request_otp'));

if ($action === 'request_otp') {
    enforceRateLimit('transfer_otp', 'u:' . $userId, 10, 300);

    $toAccountNumber = trim((string)($input['to_account_number'] ?? ''));
    $toBankName = trim((string)($input['to_bank_name'] ?? 'ICBT Bank'));
    $rawAmount = (float)($input['amount'] ?? 0);
    $amount = round($rawAmount, 2);
    $description = trim((string)($input['description'] ?? 'Chuyển tiền'));

    if ($toAccountNumber === '' || !is_finite($rawAmount) || $amount < 1000 || $amount > 500000000) {
        sendJson(false, 'So tai khoan nhan va so tien phai hop le (tu 1.000 den 500.000.000 VND).', null, 400);
    }

    $accStmt = $pdo->prepare(
        'SELECT id, account_number, balance FROM accounts WHERE user_id = :uid AND account_type = "CHECKING" LIMIT 1'
    );
    $accStmt->execute([':uid' => $userId]);
    $fromAcc = $accStmt->fetch();

    if (!$fromAcc) {
        sendJson(false, 'Khong tim thay tai khoan thanh toan.', null, 404);
    }
    if ($fromAcc['account_number'] === $toAccountNumber && strcasecmp($toBankName, 'ICBT Bank') === 0) {
        sendJson(false, 'Khong the chuyen tien cho chinh tai khoan nay.', null, 400);
    }
    if (strcasecmp($toBankName, 'ICBT Bank') === 0) {
        $chkTarget = $pdo->prepare('SELECT id FROM accounts WHERE account_number = :to_acc LIMIT 1');
        $chkTarget->execute([':to_acc' => $toAccountNumber]);
        if (!$chkTarget->fetch()) {
            sendJson(false, 'So tai khoan nhan ICBT khong ton tai.', null, 404);
        }
    }
    if ((float)$fromAcc['balance'] < $amount) {
        sendJson(false, 'So du tai khoan khong du de thuc hien giao dich.', null, 400);
    }

    $policy = $pdo->query('SELECT high_value_threshold FROM bank_policies ORDER BY id ASC LIMIT 1')->fetch();
    $threshold = $policy ? (float)$policy['high_value_threshold'] : 10000000.0;
    $requiredEkyc = ($amount >= $threshold) ? 1 : 0;

    if ($requiredEkyc === 1 && !(bool)$authUser['ekyc_verified']) {
        sendJson(false, 'Giao dich gia tri cao (>= ' . number_format($threshold) . ' VND) yeu cau xac thuc eKYC truoc.', null, 403);
    }

    $otpCode = str_pad((string)random_int(0, 999999), 6, '0', STR_PAD_LEFT);

    $ins = $pdo->prepare(
        'INSERT INTO transactions (from_account_id, to_account_number, to_bank_name, tx_type, amount, otp_code, status, required_ekyc, description)
         VALUES (:from_id, :to_acc, :to_bank, "TRANSFER", :amount, :otp, "PENDING_OTP", :ekyc, :desc)'
    );
    $ins->execute([
        ':from_id' => $fromAcc['id'],
        ':to_acc' => $toAccountNumber,
        ':to_bank' => $toBankName,
        ':amount' => $amount,
        ':otp' => $otpCode,
        ':ekyc' => $requiredEkyc,
        ':desc' => $description,
    ]);

    $respData = [
        'tx_id' => (int)$pdo->lastInsertId(),
        'required_ekyc' => (bool)$requiredEkyc,
    ];
    if (!defined('DEMO_RETURN_OTP') || DEMO_RETURN_OTP === true) {
        $respData['otp_code'] = $otpCode;
    }

    sendJson(true, 'Da tao ma OTP xac thuc 2FA.', $respData);
}

if ($action === 'confirm') {
    $txId = (int)($input['tx_id'] ?? 0);
    $otpInput = trim((string)($input['otp_code'] ?? ''));

    if ($txId <= 0 || $otpInput === '') {
        sendJson(false, 'Thieu tx_id hoac ma OTP.', null, 400);
    }

    enforceRateLimit('transfer_confirm', 'tx:' . $txId . ':u:' . $userId, 5, 300);

    try {
        $pdo->beginTransaction();

        $txStmt = $pdo->prepare(
            'SELECT t.*, a.user_id, a.balance, TIMESTAMPDIFF(SECOND, t.created_at, NOW()) AS age_sec
             FROM transactions t
             INNER JOIN accounts a ON t.from_account_id = a.id
             WHERE t.id = :tx_id AND a.user_id = :uid
             LIMIT 1 FOR UPDATE'
        );
        $txStmt->execute([':tx_id' => $txId, ':uid' => $userId]);
        $tx = $txStmt->fetch();

        if (!$tx) {
            $pdo->rollBack();
            sendJson(false, 'Khong tim thay giao dich cho xac thuc.', null, 404);
        }
        if ($tx['status'] !== 'PENDING_OTP') {
            $pdo->rollBack();
            sendJson(false, 'Giao dich nay da duoc xu ly truoc do.', null, 400);
        }
        if ((int)$tx['age_sec'] > 300) {
            $pdo->prepare('UPDATE transactions SET status = "FAILED", otp_code = NULL WHERE id = :id')->execute([':id' => $txId]);
            $pdo->commit();
            sendJson(false, 'Ma OTP da het han (qua 5 phut). Vui long tao lai giao dich.', null, 400);
        }
        if ((int)$tx['required_ekyc'] === 1 && !(bool)$authUser['ekyc_verified']) {
            $pdo->rollBack();
            sendJson(false, 'Giao dich gia tri cao yeu cau xac thuc eKYC truoc.', null, 403);
        }
        if ($tx['otp_code'] === null || !hash_equals((string)$tx['otp_code'], $otpInput)) {
            $pdo->rollBack();
            sendJson(false, 'Ma OTP khong chinh xac.', null, 401);
        }

        $amount = (float)$tx['amount'];
        if ((float)$tx['balance'] < $amount) {
            $pdo->prepare('UPDATE transactions SET status = "FAILED", otp_code = NULL WHERE id = :id')->execute([':id' => $txId]);
            $pdo->commit();
            sendJson(false, 'So du khong du tai thoi diem xac nhan.', null, 400);
        }

        $targetAccountId = null;
        if (strcasecmp((string)$tx['to_bank_name'], 'ICBT Bank') === 0) {
            $targetStmt = $pdo->prepare('SELECT id FROM accounts WHERE account_number = :to_acc LIMIT 1 FOR UPDATE');
            $targetStmt->execute([':to_acc' => $tx['to_account_number']]);
            $targetRow = $targetStmt->fetch();
            if (!$targetRow) {
                $pdo->prepare('UPDATE transactions SET status = "FAILED", otp_code = NULL WHERE id = :id')->execute([':id' => $txId]);
                $pdo->commit();
                sendJson(false, 'So tai khoan nhan ICBT khong ton tai.', null, 404);
            }
            $targetAccountId = (int)$targetRow['id'];
        }

        $pdo->prepare('UPDATE accounts SET balance = balance - :amt WHERE id = :acc_id')
            ->execute([':amt' => $amount, ':acc_id' => $tx['from_account_id']]);

        if ($targetAccountId !== null) {
            $pdo->prepare('UPDATE accounts SET balance = balance + :amt WHERE id = :to_id')
                ->execute([':amt' => $amount, ':to_id' => $targetAccountId]);
        }

        $pdo->prepare('UPDATE transactions SET status = "COMPLETED", otp_code = NULL WHERE id = :id')
            ->execute([':id' => $txId]);

        $pdo->commit();

        sendJson(true, 'Chuyen tien thanh cong.', [
            'tx_id' => $txId,
            'amount' => $amount,
            'status' => 'COMPLETED',
        ]);
    } catch (Throwable $e) {
        if ($pdo->inTransaction()) {
            $pdo->rollBack();
        }
        sendJson(false, 'Loi he thong khi xu ly giao dich.', null, 500);
    }
}

sendJson(false, 'Hanh dong khong hop le.', null, 400);
