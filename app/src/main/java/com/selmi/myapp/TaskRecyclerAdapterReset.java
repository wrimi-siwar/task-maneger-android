package com.selmi.myapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class TaskRecyclerAdapterReset extends RecyclerView.Adapter<TaskRecyclerAdapterReset.ViewHolder> {
    private Context context;
    private DatabaseReference databaseReference;
    private ArrayList<TaskItem> mTasks;
    private OnItemClickListener mListener;

    public TaskRecyclerAdapterReset(Context context, ArrayList<TaskItem> tasksItemArrayList) {
        this.context = context;
        this.mTasks = tasksItemArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;
        public TextView mDescriptionTextView;
        public TextView mDateTextView;
        public Button buttonDelete;
        public Button buttonUpdate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.task_title);
            mDescriptionTextView = itemView.findViewById(R.id.task_description);
            mDateTextView = itemView.findViewById(R.id.task_due_date);
            buttonUpdate = itemView.findViewById(R.id.edit_button);
            buttonDelete = itemView.findViewById(R.id.delete_button);
            buttonUpdate.setText("Reset");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context) ;
        View view =  layoutInflater.inflate(R.layout.task_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TaskItem task = mTasks.get(position);
        holder.mTitleTextView.setText(task.getTitle());
        holder.mDescriptionTextView.setText(task.getDescription());
        holder.mDateTextView.setText(task.getDueDate());

        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth auth = FirebaseAuth.getInstance() ;
                FirebaseUser currentUser =auth.getCurrentUser();
                String uid = currentUser.getUid();

                databaseReference.child("Tasks")
                        .child(task.getId())
                        .setValue(new TaskItem(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(),
                                uid, false));
                Toast.makeText(context, "Task reset successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, task.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }
    public class ViewDialogUpdate {
        public void showDialog(Context context, String id, String title, String description, String date, String userID, boolean completed) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_task);

            EditText textTitle = dialog.findViewById(R.id.task_title);
            EditText textDes = dialog.findViewById(R.id.task_description);
            CheckBox checkBox = dialog.findViewById(R.id.checkbox_completed);

            textTitle.setText(title);
            textDes.setText(description);


            Button buttonUpdate = dialog.findViewById(R.id.edit_button);
            Button buttonCancel = dialog.findViewById(R.id.cancel_button);


            buttonUpdate.setText("Reset");

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String newTitle = textTitle.getText().toString();
                    String newDes = textDes.getText().toString();
                    boolean newCompleted = checkBox.isChecked();
                    if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {

                        if (newTitle.equals(title) && newDes.equals(description)  && newCompleted == true ) {
                            Toast.makeText(context, "you don't change anything", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseAuth auth = FirebaseAuth.getInstance() ;
                            FirebaseUser currentUser =auth.getCurrentUser();
                            String uid = currentUser.getUid();
                            databaseReference.child("Tasks")
                                    .child(id)
                                    .setValue(new TaskItem(id, newTitle, newDes, date, uid, newCompleted));
                            Toast.makeText(context, "Task Updated successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }

    public class ViewDialogConfirmDelete {
        public void showDialog(Context context, String id) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.view_dialog_confirm_delete);

            Button buttonDelete = dialog.findViewById(R.id.buttonDelete);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancel);

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    databaseReference.child("Tasks").child(id).removeValue();
                    Toast.makeText(context, "Task Deleted successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }

}

