package com.paysave;

import static com.paysave.utilities.PhoneUtilities.formatMobileNumber;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.paysave.databinding.FragmentHomeBinding;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class HomeFragment extends com.paysave.RequireLoginFragment {

    private FragmentHomeBinding binding;
    private NavController navController;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);

        binding.homeButton.setOnClickListener(v -> {
            navController.navigate(R.id.homeFragment);
        });

        binding.cashInButton.setOnClickListener(v -> {
            navController.navigate(R.id.cashInFragment);
        });

        binding.transferButton.setOnClickListener(v -> {
            navController.navigate(R.id.transferFragment);
        });

        binding.eLoadButton.setOnClickListener(v -> {
            navController.navigate(R.id.ELoadFragment);
        });

        binding.transactionsButton.setOnClickListener(v -> {
            navController.navigate(R.id.transactionsFragment);
        });

        binding.btnDeposit.setOnClickListener(v -> {
            navController.navigate(R.id.cashInFragment);
        });

        binding.btnTransfer.setOnClickListener(v -> {
            navController.navigate(R.id.transferFragment);
        });

        binding.btnBuyLoad.setOnClickListener(v -> {
            navController.navigate(R.id.ELoadFragment);
        });

        binding.btnTransactions.setOnClickListener(v -> {
            navController.navigate(R.id.transactionsFragment);
        });

    }

    private void updateSavingsUI() {
        String welcomeText = String.format("%s %s", getString(R.string.welcome), loggedInUser.getDisplayName());
        binding.welcomeText.setText(welcomeText);

        Locale locale = new Locale("en", "PH");
        Currency currency = Currency.getInstance(locale);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(currency);
        String formattedBalance = currencyFormatter.format(savingsAccount.getBalance());
        binding.balance.setText(formattedBalance);
        binding.phoneNumber.setText(formatMobileNumber(loggedInUser.getPhone()));
    }

    @Override
    public void onFetchedUser() {
        updateSavingsUI();
    }


}