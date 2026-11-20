<?php
/**
 * GET /api/transactions/stripe_success.php?session_id=cs_test_...
 * Stripe chuyen huong ve day sau khi thanh toan thanh cong.
 * Xac minh lai voi Stripe API, cong tien vao tai khoan CHECKING (chong cong trung lap).
 */

require_once __DIR__ . '/../config/config.php';

header('Content-Type: text/html; charset=utf-8');

$sessionId = trim((string)($_GET['session_id'] ?? ''));
if ($sessionId === '' || !preg_match('/^cs_[a-zA-Z0-9_]+$/', $sessionId)) {
    http_response_code(400);
    echo '<h3>Mã phiên thanh toán không hợp lệ.</h3>';
    exit;
}

$ch = curl_init('https://api.stripe.com/v1/checkout/sessions/' . urlencode($sessionId));
curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_USERPWD => STRIPE_SECRET_KEY . ':',
    CURLOPT_TIMEOUT => 20,
]);
$raw = curl_exec($ch);
$httpCode = (int)curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

$session = json_decode((string)$raw, true);
if ($httpCode < 200 || $httpCode >= 300 || ($session['payment_status'] ?? '') !== 'paid') {
    http_response_code(400);
    echo '<h3>Thanh toán chưa được xác nhận từ Stripe.</h3>';
    exit;
}

$userId = (int)($session['metadata']['user_id'] ?? 0);
$amount = (float)($session['metadata']['amount'] ?? 0);

if ($userId <= 0 || $amount <= 0) {
    http_response_code(400);
    echo '<h3>Thiếu thông tin giao dịch.</h3>';
    exit;
}

$dsn = 'mysql:host=' . DB_HOST . ';dbname=' . DB_NAME . ';charset=utf8mb4';
$pdo = new PDO($dsn, DB_USER, DB_PASS, [
    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES => false,
]);

$txDesc = 'Nạp tiền qua Stripe (' . $sessionId . ')';

try {
    $pdo->beginTransaction();

    // Kiem tra chong cong tien 2 lan neu trang bi tai lai
    $dupStmt = $pdo->prepare('SELECT id FROM transactions WHERE description = :desc AND status = "COMPLETED" LIMIT 1 FOR UPDATE');
    $dupStmt->execute([':desc' => $txDesc]);
    if (!$dupStmt->fetch()) {
        $accStmt = $pdo->prepare(
            'SELECT id, account_number FROM accounts WHERE user_id = :uid AND account_type = "CHECKING" LIMIT 1 FOR UPDATE'
        );
        $accStmt->execute([':uid' => $userId]);
        $acc = $accStmt->fetch();

        if ($acc) {
            $pdo->prepare('UPDATE accounts SET balance = balance + :amt WHERE id = :id')
                ->execute([':amt' => $amount, ':id' => $acc['id']]);

            $pdo->prepare(
                'INSERT INTO transactions (from_account_id, to_account_number, to_bank_name, tx_type, amount, status, required_ekyc, description)
                 VALUES (:acc_id, "STRIPE_TEST", "Stripe Gateway", "DEPOSIT", :amt, "COMPLETED", 0, :desc)'
            )->execute([
                ':acc_id' => $acc['id'],
                ':amt' => $amount,
                ':desc' => $txDesc,
            ]);
        }
    }

    $pdo->commit();
} catch (Throwable $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    http_response_code(500);
    echo '<h3>Lỗi cập nhật số dư hệ thống.</h3>';
    exit;
}

echo '<!DOCTYPE html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>'
    . '<body style="font-family:sans-serif;text-align:center;padding:40px;background:#F4F7FC;">'
    . '<h2 style="color:#16A34A;">Nạp tiền thành công!</h2>'
    . '<p>Số tiền: <b>' . number_format($amount, 0, ',', '.') . ' VND</b> đã được cộng vào tài khoản ICBT Bank.</p>'
    . '</body></html>';
