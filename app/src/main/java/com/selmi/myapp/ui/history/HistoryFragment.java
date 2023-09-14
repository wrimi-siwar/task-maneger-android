package com.selmi.myapp.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.selmi.myapp.R;
import com.selmi.myapp.TaskItem;
import com.selmi.myapp.TaskRecyclerAdapter;
import com.selmi.myapp.TaskRecyclerAdapterReset;
import com.selmi.myapp.databinding.FragmentHistoryBinding;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference myRef ;

    private RecyclerView recyclerView;
    private ArrayList<TaskItem> tasksItemArrayList;
    private TaskRecyclerAdapterReset adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true); // work offline
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Tasks");
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        tasksItemArrayList = new ArrayList<>();

        readData();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void readData() {
        FirebaseAuth auth = FirebaseAuth.getInstance() ;
        FirebaseUser currentUser =auth.getCurrentUser();
        String uid = currentUser.getUid();
        Query query = myRef
                .orderByChild("userID").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasksItemArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TaskItem tasks = dataSnapshot.getValue(TaskItem.class);
                    if(tasks.isCompleted())
                        tasksItemArrayList.add(tasks);
                }
                adapter = new TaskRecyclerAdapterReset(getContext(), tasksItemArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}