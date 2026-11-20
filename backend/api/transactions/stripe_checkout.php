<?php
/**
 * POST /api/transactions/stripe_checkout.php
 * Yeu cau Bearer Token. Tao Stripe Checkout Session (Test Mode) de nap tien vao tai khoan CHECKING.
 * Body: { "amount": 500000 }
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);
$userId = (int)$authUser['id'];

if (!defined('STRIPE_SECRET_KEY') || STRIPE_SECRET_KEY === '' || STRIPE_SECRET_KEY === 'sk_test_your_key_here') {
    sendJson(false, 'Chua cau hinh STRIPE_SECRET_KEY trong backend/api/config/config.php.', null, 500);
}

if (!function_exists('curl_init')) {
    sendJson(false, 'PHP cURL chua duoc bat tren server.', null, 500);
}

$input = getRequestInput();
$amount = (int)round((float)($input['amount'] ?? 0));

if ($amount < 20000) {
    sendJson(false, 'So tien nap qua Stripe toi thieu la 20.000 VND.', null, 400);
}

$scheme = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
$host = $_SERVER['HTTP_HOST'] ?? 'phatg.id.vn';
$baseDir = rtrim(dirname($_SERVER['SCRIPT_NAME'] ?? '/api/transactions/stripe_checkout.php'), '/\\');

$successUrl = $scheme . '://' . $host . $baseDir . '/stripe_success.php?session_id={CHECKOUT_SESSION_ID}';
$cancelUrl = $scheme . '://' . $host . $baseDir . '/stripe_cancel.php';

$postFields = http_build_query([
    'mode' => 'payment',
    'payment_method_types[0]' => 'card',
    'line_items[0][price_data][currency]' => 'vnd',
    'line_items[0][price_data][unit_amount]' => $amount,
    'line_items[0][price_data][product_data][name]' => 'Nạp tiền vào tài khoản ICBT Bank',
    'line_items[0][quantity]' => 1,
    'success_url' => $successUrl,
    'cancel_url' => $cancelUrl,
    'metadata[user_id]' => $userId,
    'metadata[amount]' => $amount,
]);

$ch = curl_init('https://api.stripe.com/v1/checkout/sessions');
curl_setopt_array($ch, [
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST => true,
    CURLOPT_POSTFIELDS => $postFields,
    CURLOPT_USERPWD => STRIPE_SECRET_KEY . ':',
    CURLOPT_TIMEOUT => 20,
]);

$rawResponse = curl_exec($ch);
$httpCode = (int)curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curlErr = curl_error($ch);
curl_close($ch);

if ($rawResponse === false) {
    sendJson(false, 'Loi ket noi Stripe API: ' . $curlErr, null, 502);
}

$stripeData = json_decode($rawResponse, true);
if ($httpCode < 200 || $httpCode >= 300 || empty($stripeData['url'])) {
    $errMsg = $stripeData['error']['message'] ?? 'Khong tao duoc phien thanh toan Stripe.';
    sendJson(false, 'Stripe loi: ' . $errMsg, null, 400);
}

sendJson(true, 'Tao phien thanh toan Stripe thanh cong.', [
    'session_id' => $stripeData['id'],
    'checkout_url' => $stripeData['url'],
    'success_url_prefix' => $scheme . '://' . $host . $baseDir . '/stripe_success.php',
    'cancel_url_prefix' => $cancelUrl,
]);
