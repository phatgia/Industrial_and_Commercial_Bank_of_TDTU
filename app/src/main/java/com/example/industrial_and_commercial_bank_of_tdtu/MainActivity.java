package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

public class MainActivity extends AppCompatActivity {

    private LinearLayout bottomNav;
    private ImageButton btnCenter;

    private LinearLayout navHome, navAccount, navUtilities, navAI;
    private ImageView imgNavHome, imgNavAccount, imgNavUtilities, imgNavAI;
    private TextView txtNavHome, txtNavAccount, txtNavUtilities, txtNavAI;

    private int activeColor = Color.parseColor("#0B63E5");
    private int inactiveColor = Color.parseColor("#64748B");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);
        btnCenter = findViewById(R.id.btnCenter);

        navHome = findViewById(R.id.navHome);
        navAccount = findViewById(R.id.navAccount);
        navUtilities = findViewById(R.id.navUtilities);
        navAI = findViewById(R.id.navAI);

        imgNavHome = findViewById(R.id.imgNavHome);
        imgNavAccount = findViewById(R.id.imgNavAccount);
        imgNavUtilities = findViewById(R.id.imgNavUtilities);
        imgNavAI = findViewById(R.id.imgNavAI);

        txtNavHome = findViewById(R.id.txtNavHome);
        txtNavAccount = findViewById(R.id.txtNavAccount);
        txtNavUtilities = findViewById(R.id.txtNavUtilities);
        txtNavAI = findViewById(R.id.txtNavAI);

        setupBottomNavListeners();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                syncNavWithCurrentFragment();
            }
        });

        if (savedInstanceState == null) {
            loadFragment(new LoginFragment(), false);
        }
    }

    public void showMainContent() {
        bottomNav.setVisibility(View.VISIBLE);
        btnCenter.setVisibility(View.VISIBLE);
        loadFragment(new HomeFragment(), false);
        updateNavUI(0);
    }

    public void showLoginScreen() {
        bottomNav.setVisibility(View.GONE);
        btnCenter.setVisibility(View.GONE);
        loadFragment(new LoginFragment(), false);
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        if (!addToBackStack) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        androidx.fragment.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private void syncNavWithCurrentFragment() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (current instanceof HomeFragment) {
            updateNavUI(0);
        } else if (current instanceof ProfileFragment) {
            updateNavUI(1);
        } else if (current instanceof UtilitiesFragment) {
            updateNavUI(2);
        } else if (current instanceof ChatBotFragment) {
            updateNavUI(3);
        }
    }

    private void setupBottomNavListeners() {
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new HomeFragment(), false);
                updateNavUI(0);
            }
        });

        navAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ProfileFragment(), false);
                updateNavUI(1);
            }
        });

        navUtilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new UtilitiesFragment(), false);
                updateNavUI(2);
            }
        });

        navAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ChatBotFragment(), false);
                updateNavUI(3);
            }
        });

        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new TransactionHistoryFragment(), true);
            }
        });
    }

    public void updateNavUI(int selectedTab) {
        imgNavHome.setImageTintList(ColorStateList.valueOf(selectedTab == 0 ? activeColor : inactiveColor));
        txtNavHome.setTextColor(selectedTab == 0 ? activeColor : inactiveColor);

        imgNavAccount.setImageTintList(ColorStateList.valueOf(selectedTab == 1 ? activeColor : inactiveColor));
        txtNavAccount.setTextColor(selectedTab == 1 ? activeColor : inactiveColor);

        imgNavUtilities.setImageTintList(ColorStateList.valueOf(selectedTab == 2 ? activeColor : inactiveColor));
        txtNavUtilities.setTextColor(selectedTab == 2 ? activeColor : inactiveColor);

        imgNavAI.setImageTintList(ColorStateList.valueOf(selectedTab == 3 ? activeColor : inactiveColor));
        txtNavAI.setTextColor(selectedTab == 3 ? activeColor : inactiveColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Xoa phien dang nhap khi thoat app (khong xoa neu chi xoay man hinh)
        if (!isChangingConfigurations()) {
            new SessionManager().logoutAndClear();
        }
    }
}