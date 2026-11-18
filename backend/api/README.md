# Backend PHP 8.3 API — ICBT Bank (TDTU)

Thu muc nay chua ma nguon Database MySQL (`database.sql`) va he thong REST API bang PHP 8.3 (PDO) phuc vu ung dung Android ICBT Bank.

## 1. Cau truc thu muc

```text
backend/
├── database.sql                      # Schema 6 bang InnoDB + du lieu mau
└── api/
    ├── config/
    │   ├── .htaccess                 # Chan truy cap truc tiep qua trinh duyet
    │   ├── config.example.php        # File mau cau hinh (an toan khi push GitHub)
    │   ├── config.php                # (Ignored boi .gitignore) Chua mat khau DB that
    │   └── db.php                    # Ket noi PDO singleton & JSON helpers
    ├── health.php                    # GET  /api/health.php
    ├── auth/
    │   └── login.php                 # POST /api/auth/login.php
    ├── accounts/
    │   └── list.php                  # GET  /api/accounts/list.php?user_id=...
    ├── transactions/
    │   ├── history.php               # GET  /api/transactions/history.php?user_id=...
    │   ├── transfer.php              # POST /api/transactions/transfer.php (OTP 2FA)
    │   └── deposit_withdraw.php      # POST /api/transactions/deposit_withdraw.php
    ├── utilities/
    │   ├── bills.php                 # GET  /api/utilities/bills.php
    │   └── pay.php                   # POST /api/utilities/pay.php
    ├── officer/
    │   ├── customers.php             # GET/POST /api/officer/customers.php
    │   └── update_policy.php         # POST /api/officer/update_policy.php
    └── branches/
        └── list.php                  # GET  /api/branches/list.php
```

## 2. Huong dan cai dat tren Hosting (DirectAdmin)

1. Tao Database trong **DirectAdmin -> Databases**, sau do mo **phpMyAdmin** va Import file `backend/database.sql`.
2. Copy `backend/api/config/config.example.php` thanh `backend/api/config/config.php` va dien `DB_NAME`, `DB_USER`, `DB_PASS`.
3. Upload toan bo noi dung thu muc `backend/api/` len `public_html/api/` tren hosting.
4. Truy cap `https://phatg.id.vn/api/health.php` de xac nhan `"success": true`.

> **Bao mat GitHub:** File `backend/api/config/config.php` da duoc them vao `.gitignore` o thu muc goc de khong bao gio bi lo thong tin ket noi database hay API key khi `git push`.
