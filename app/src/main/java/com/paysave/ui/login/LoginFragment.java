package com.paysave.ui.login;

import static com.paysave.utilities.Constants.USERS_COLLECTION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paysave.databinding.FragmentLoginBinding;

import com.paysave.R;
import com.paysave.ui.main.MainActivity;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private NavController navController;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();


        final EditText emailFieldText = binding.emailField;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmailValid = isValidEmail(emailFieldText.getText().toString().trim());
                boolean isPasswordValid = com.paysave.ui.login.LoginViewModel.isPasswordValid(passwordEditText.getText().toString().trim());

                loginButton.setEnabled(isEmailValid & isPasswordValid);
            }
        };

        emailFieldText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                login(emailFieldText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);

            login(emailFieldText.getText().toString().trim(),
                    passwordEditText.getText().toString().trim());
        });

        navController = Navigation.findNavController(requireActivity(), R.id.navHostFragment);

        binding.registerLink.setOnClickListener(v -> navController.navigate(R.id.registerFragment));
    }

    // Function to validate email format
    private boolean isValidEmail(String email) {
        // You can use a regular expression or any other validation logic to check the email format
        // Here's a simple regular expression for basic email format validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }


    public void login(String username, String password) {
        // can be launched in a separate asynchronous job

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        FirebaseFirestore.getInstance().collection(USERS_COLLECTION).document(task.getResult().getUser().getUid()).get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        startActivity(new Intent(requireContext(), MainActivity.class));
                                        requireActivity().finish();
                                    }
                                    else{
                                        showLoginFailed(task2.getException().getMessage());
                                    }
                                    binding.loading.setVisibility(View.GONE);
                                });

                    } else {
                        showLoginFailed(task.getException().getMessage());
                        binding.loading.setVisibility(View.GONE);
                    }
                });
    }
                private void showLoginFailed (String errorString){
                    Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDestroyView () {
                    super.onDestroyView();
                    binding = null;
                }
            }