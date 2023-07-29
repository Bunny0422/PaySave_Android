package com.paysave.ui.transactions;

import static com.paysave.utilities.Constants.TRANSACTIONS_COLLECTION;
import static com.paysave.utilities.Constants.USERS_COLLECTION;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.paysave.R;
import com.paysave.RequireLoginFragment;
import com.paysave.adapters.TransactionsAdapter;
import com.paysave.data.model.Transaction;
import com.paysave.databinding.FragmentTransactionsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TransactionsFragment extends RequireLoginFragment {
    private TransactionsAdapter adapter;

    private final List<Transaction> transactions = new ArrayList<>();
    private String type = "money";
    private FragmentTransactionsBinding binding;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TransactionsAdapter();

        binding.recyclerView.setAdapter(adapter);

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

    }

    private void refreshList(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference transactionsRef = db.collection(TRANSACTIONS_COLLECTION);

        Query query = transactionsRef.where(Filter.or(
                        Filter.equalTo("sender", db.collection(USERS_COLLECTION).document(loggedInUser.getUserId())),
                        Filter.equalTo("receiver", db.collection(USERS_COLLECTION).document(loggedInUser.getUserId()))
                ))
                .whereEqualTo("type", type)
                .orderBy("date", Query.Direction.DESCENDING);

        adapter.notifyItemRangeRemoved(0, transactions.size());
        transactions.clear();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        assert transaction != null;
                        transaction.setId(document.getId());
                        if(transactions.contains(transaction))
                            continue;
                        transactions.add(transaction);
                        adapter.notifyItemInserted(transactions.size()-1);
                    }
                }
                else{
                    String message = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    public void onFetchedUser() {
        adapter.setTransactions(transactions, loggedInUser, requireContext());
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                type = Objects.requireNonNull(tab.getText()).toString().toLowerCase(Locale.getDefault());
                refreshList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Called when a tab is unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Called when a tab is reselected (tab was already selected)
            }
        });
        refreshList();
    }
}
