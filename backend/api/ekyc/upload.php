<?php
/**
 * POST /api/ekyc/upload.php
 * Yeu cau Bearer Token. Nhan bo ho so eKYC gom:
 *   - image_base64 (bat buoc: anh khuon mat)
 *   - id_front_base64 (tuy chon: anh mat truoc CCCD)
 *   - id_back_base64 (tuy chon: anh mat sau CCCD)
 *   - customer_id (tuy chon, chi danh cho OFFICER ho tro khach hang)
 */

require_once __DIR__ . '/../config/db.php';

requireMethod('POST');

$pdo = getDbConnection();
$authUser = requireAuth($pdo);

$input = getRequestInput();
$faceBase64 = trim((string)($input['image_base64'] ?? ''));
$frontBase64 = trim((string)($input['id_front_base64'] ?? ''));
$backBase64 = trim((string)($input['id_back_base64'] ?? ''));

$targetUserId = (int)$authUser['id'];
if (isset($input['customer_id']) && (int)$input['customer_id'] > 0) {
    if (strtoupper((string)$authUser['role']) !== 'OFFICER') {
        sendJson(false, 'Chi nhan vien ngan hang moi co quyen cap nhat eKYC cho khach hang khac.', null, 403);
    }
    $targetUserId = (int)$input['customer_id'];
}

if ($faceBase64 === '') {
    sendJson(false, 'Thieu du lieu anh khuon mat (image_base64).', null, 400);
}

$uploadDir = __DIR__ . '/../uploads/ekyc';
if (!is_dir($uploadDir) && !mkdir($uploadDir, 0755, true)) {
    sendJson(false, 'Khong tao duoc thu muc luu anh eKYC.', null, 500);
}

$uploadsHtaccess = dirname($uploadDir) . '/.htaccess';
if (!file_exists($uploadsHtaccess)) {
    @file_put_contents(
        $uploadsHtaccess,
        "Options -Indexes\n<IfModule mod_authz_core.c>\n    Require all denied\n</IfModule>\n<IfModule !mod_authz_core.c>\n    Deny from all\n</IfModule>\n"
    );
}

/**
 * Giai ma, kiem tra dinh dang anh that, tai ma hoa JPEG va luu file ngau nhien trong uploads/ekyc/.
 */
function saveEkycJpeg(string $rawBase64, string $uploadDir, string $prefix, int $userId): string
{
    $clean = $rawBase64;
    if (str_contains($clean, ',')) {
        $parts = explode(',', $clean, 2);
        $clean = $parts[1];
    }

    $binary = base64_decode($clean, true);
    if ($binary === false || strlen($binary) < 100) {
        sendJson(false, "Du lieu anh ($prefix) khong hop le.", null, 400);
    }
    if (strlen($binary) > 2 * 1024 * 1024) {
        sendJson(false, "Anh ($prefix) vuot qua dung luong toi da 2MB.", null, 400);
    }

    $imgInfo = @getimagesizefromstring($binary);
    $allowedMimes = ['image/jpeg', 'image/png', 'image/webp'];
    if ($imgInfo === false || empty($imgInfo['mime']) || !in_array($imgInfo['mime'], $allowedMimes, true)) {
        sendJson(false, "File ($prefix) khong phai anh JPEG/PNG/WEBP hop le.", null, 400);
    }

    if (function_exists('imagecreatefromstring') && function_exists('imagejpeg')) {
        $gdImg = @imagecreatefromstring($binary);
        if ($gdImg === false) {
            sendJson(false, "Khong the doc noi dung anh ($prefix).", null, 400);
        }
        ob_start();
        imagejpeg($gdImg, null, 85);
        $reencoded = ob_get_clean();
        unset($gdImg);
        if ($reencoded === false || strlen($reencoded) < 100) {
            sendJson(false, "Loi xu ly anh ($prefix).", null, 500);
        }
        $binary = $reencoded;
    }

    $fileName = $prefix . '_' . bin2hex(random_bytes(16)) . '.jpg';
    if (file_put_contents($uploadDir . '/' . $fileName, $binary) === false) {
        sendJson(false, "Khong the luu file anh ($prefix) len may chu.", null, 500);
    }

    return 'uploads/ekyc/' . $fileName;
}

$facePath = saveEkycJpeg($faceBase64, $uploadDir, 'ekyc_face', $targetUserId);
$frontPath = ($frontBase64 !== '') ? saveEkycJpeg($frontBase64, $uploadDir, 'ekyc_front', $targetUserId) : null;
$backPath = ($backBase64 !== '') ? saveEkycJpeg($backBase64, $uploadDir, 'ekyc_back', $targetUserId) : null;

$stmt = $pdo->prepare('UPDATE users SET ekyc_face_path = :path, ekyc_verified = 1 WHERE id = :uid');
$stmt->execute([
    ':path' => $facePath,
    ':uid' => $targetUserId,
]);

sendJson(true, 'Xac thuc ho so eKYC thanh cong.', [
    'user_id' => $targetUserId,
    'ekyc_face_path' => $facePath,
    'id_front_path' => $frontPath,
    'id_back_path' => $backPath,
    'ekyc_verified' => true,
]);
