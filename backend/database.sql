-- ============================================================================
-- ICBT (Industrial and Commercial Bank of TDTU) - MySQL Database Schema & Seed
-- Tuong thich: MySQL 5.7+ / 8.0+ / MariaDB (DirectAdmin + phpMyAdmin)
--
-- HUONG DAN IMPORT TREN PHPMYADMIN (DIRECTADMIN):
-- 1. Vao DirectAdmin -> Account Manager -> Databases -> Tao 1 Database moi.
-- 2. Mo phpMyAdmin -> Chon Database vua tao o cot ben trai.
-- 3. Chon tab "Import" -> Chon file database.sql nay -> Bam "Import" / "Go".
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user_sessions`;
DROP TABLE IF EXISTS `transactions`;
DROP TABLE IF EXISTS `utility_bills`;
DROP TABLE IF EXISTS `accounts`;
DROP TABLE IF EXISTS `bank_policies`;
DROP TABLE IF EXISTS `bank_branches`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `full_name` VARCHAR(100) NOT NULL,
    `email` VARCHAR(120) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('CUSTOMER', 'OFFICER') NOT NULL DEFAULT 'CUSTOMER',
    `ekyc_face_path` VARCHAR(255) DEFAULT NULL COMMENT 'Duong dan anh khuon mat eKYC tren hosting',
    `ekyc_verified` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_email` (`email`),
    UNIQUE KEY `uk_users_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `bank_policies` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `saving_interest_rate` DECIMAL(5,2) NOT NULL DEFAULT 5.50 COMMENT 'Lai suat tiet kiem (%/nam)',
    `high_value_threshold` DECIMAL(15,2) NOT NULL DEFAULT 10000000.00 COMMENT 'Han muc giao dich bat buoc quet mat eKYC (VND)',
    `updated_by_officer_id` INT UNSIGNED DEFAULT NULL,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_policies_officer` (`updated_by_officer_id`),
    CONSTRAINT `fk_policies_officer` FOREIGN KEY (`updated_by_officer_id`)
        REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `accounts` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `account_number` VARCHAR(20) NOT NULL,
    `user_id` INT UNSIGNED NOT NULL,
    `account_type` ENUM('CHECKING', 'SAVING', 'MORTGAGE') NOT NULL,
    `balance` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'So du (Checking/Saving) hoac Du no goc (Mortgage)',
    `interest_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT 'Lai suat %/nam (Saving/Mortgage)',
    `periodic_payment` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT 'So tien phai tra moi ky (Mortgage)',
    `payment_cycle` ENUM('MONTHLY', 'BIWEEKLY') DEFAULT NULL COMMENT 'Chu ky tra no Mortgage',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_accounts_number` (`account_number`),
    KEY `idx_accounts_user` (`user_id`),
    CONSTRAINT `fk_accounts_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `transactions` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `from_account_id` INT UNSIGNED NOT NULL,
    `to_account_number` VARCHAR(30) DEFAULT NULL,
    `to_bank_name` VARCHAR(80) DEFAULT 'ICBT Bank',
    `tx_type` ENUM('TRANSFER', 'DEPOSIT', 'WITHDRAW', 'BILL_PAYMENT', 'ECOMMERCE') NOT NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `otp_code` VARCHAR(10) DEFAULT NULL COMMENT 'Ma OTP 6 so de xac thuc 2FA',
    `status` ENUM('PENDING_OTP', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING_OTP',
    `required_ekyc` TINYINT(1) NOT NULL DEFAULT 0,
    `description` VARCHAR(255) DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_tx_from_account` (`from_account_id`),
    KEY `idx_tx_created_at` (`created_at`),
    CONSTRAINT `fk_tx_from_account` FOREIGN KEY (`from_account_id`)
        REFERENCES `accounts` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `utility_bills` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` INT UNSIGNED NOT NULL,
    `service_type` ENUM('ELECTRICITY', 'WATER', 'PHONE_TOPUP', 'FLIGHT', 'MOVIE', 'HOTEL') NOT NULL,
    `provider_name` VARCHAR(100) NOT NULL,
    `bill_code` VARCHAR(50) NOT NULL,
    `amount` DECIMAL(15,2) NOT NULL,
    `is_paid` TINYINT(1) NOT NULL DEFAULT 0,
    `due_date` DATE DEFAULT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_bills_user` (`user_id`),
    CONSTRAINT `fk_bills_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `bank_branches` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `branch_name` VARCHAR(120) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `latitude` DOUBLE NOT NULL,
    `longitude` DOUBLE NOT NULL,
    `opening_hours` VARCHAR(60) NOT NULL DEFAULT '08:00 - 17:00',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_sessions` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` INT UNSIGNED NOT NULL,
    `token_hash` CHAR(64) NOT NULL,
    `expires_at` DATETIME NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sessions_token_hash` (`token_hash`),
    KEY `idx_sessions_user` (`user_id`),
    CONSTRAINT `fk_sessions_user` FOREIGN KEY (`user_id`)
        REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- Mat khau mac dinh cho cac tai khoan mau la: 123456
INSERT INTO `users` (`id`, `full_name`, `email`, `phone`, `password_hash`, `role`, `ekyc_face_path`, `ekyc_verified`) VALUES
(1, 'Trần Gia Phát', 'phat.tran@tdtu.edu.vn', '0901234567', SHA2('123456', 256), 'CUSTOMER', NULL, 1),
(2, 'Nguyễn Minh Anh', 'anh.nguyen@tdtu.edu.vn', '0909876543', SHA2('123456', 256), 'CUSTOMER', NULL, 0),
(3, 'Lê Hoàng Nam', 'officer@icbtdtu.vn', '0911222333', SHA2('123456', 256), 'OFFICER', NULL, 1);

INSERT INTO `bank_policies` (`id`, `saving_interest_rate`, `high_value_threshold`, `updated_by_officer_id`) VALUES
(1, 5.50, 10000000.00, 3);

INSERT INTO `accounts` (`id`, `account_number`, `user_id`, `account_type`, `balance`, `interest_rate`, `periodic_payment`, `payment_cycle`) VALUES
(1, '1900100001', 1, 'CHECKING', 25500000.00, 0.20, 0.00, NULL),
(2, '1900200001', 1, 'SAVING', 120000000.00, 5.50, 0.00, NULL),
(3, '1900300001', 1, 'MORTGAGE', 500000000.00, 7.80, 8500000.00, 'MONTHLY'),
(4, '1900100002', 2, 'CHECKING', 15000000.00, 0.20, 0.00, NULL),
(5, '1900200002', 2, 'SAVING', 50000000.00, 5.50, 0.00, NULL),
(6, '1900300002', 2, 'MORTGAGE', 240000000.00, 7.80, 4200000.00, 'BIWEEKLY');

INSERT INTO `transactions` (`from_account_id`, `to_account_number`, `to_bank_name`, `tx_type`, `amount`, `otp_code`, `status`, `required_ekyc`, `description`, `created_at`) VALUES
(1, '1900100002', 'ICBT Bank', 'TRANSFER', 1500000.00, '482910', 'COMPLETED', 0, 'Chuyển tiền ăn trưa cho Minh Anh', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(1, 'EVN-HCM-001', 'EVN HCMC', 'BILL_PAYMENT', 650000.00, '193842', 'COMPLETED', 0, 'Thanh toán tiền điện tháng 10', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(1, 'STRIPE_TEST', 'Stripe Gateway', 'DEPOSIT', 5000000.00, '774120', 'COMPLETED', 0, 'Nạp tiền vào tài khoản thanh toán', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, '9704366801234567', 'Vietcombank', 'TRANSFER', 2200000.00, '550192', 'COMPLETED', 0, 'Thanh toán tiền học phí TDTU', NOW());

INSERT INTO `utility_bills` (`user_id`, `service_type`, `provider_name`, `bill_code`, `amount`, `is_paid`, `due_date`) VALUES
(1, 'ELECTRICITY', 'EVN TP.HCM', 'EVN-TDTU-1126', 780000.00, 0, DATE_ADD(CURDATE(), INTERVAL 7 DAY)),
(1, 'WATER', 'SAWACO Quận 7', 'WTR-Q7-8821', 145000.00, 0, DATE_ADD(CURDATE(), INTERVAL 10 DAY)),
(1, 'PHONE_TOPUP', 'Viettel Telecom', 'VT-0901234567', 100000.00, 0, DATE_ADD(CURDATE(), INTERVAL 15 DAY)),
(1, 'FLIGHT', 'Vietnam Airlines (SGN - HAN)', 'VNA-202611', 2450000.00, 0, DATE_ADD(CURDATE(), INTERVAL 20 DAY)),
(1, 'MOVIE', 'CGV Crescent Mall Q7', 'CGV-99102', 220000.00, 0, DATE_ADD(CURDATE(), INTERVAL 3 DAY)),
(1, 'HOTEL', 'Mường Thanh Luxury', 'HTL-44510', 1800000.00, 0, DATE_ADD(CURDATE(), INTERVAL 12 DAY));

INSERT INTO `bank_branches` (`branch_name`, `address`, `latitude`, `longitude`, `opening_hours`) VALUES
('ICBT Chi nhánh Tôn Đức Thắng', '19 Nguyễn Hữu Thọ, P. Tân Phong, Quận 7, TP.HCM', 10.732668, 106.699768, '07:30 - 17:00'),
('ICBT PGD Lotte Mart Quận 7', '469 Nguyễn Hữu Thọ, P. Tân Hưng, Quận 7, TP.HCM', 10.740986, 106.701736, '08:00 - 20:00'),
('ICBT PGD Phú Mỹ Hưng (Crescent Mall)', '101 Tôn Dật Tiên, P. Tân Phú, Quận 7, TP.HCM', 10.728922, 106.718944, '08:00 - 19:00'),
('ICBT Chi nhánh Him Lam Quận 7', '23-25 Nguyễn Thị Thập, P. Tân Hưng, Quận 7, TP.HCM', 10.741850, 106.696120, '08:00 - 17:00'),
('ICBT PGD Nguyễn Văn Linh', '801 Nguyễn Văn Linh, P. Tân Phú, Quận 7, TP.HCM', 10.729510, 106.721890, '08:00 - 17:00');
