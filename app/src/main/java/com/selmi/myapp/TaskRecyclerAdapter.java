package com.selmi.myapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder> {
    private Context context;
    private DatabaseReference databaseReference;
    private ArrayList<TaskItem> mTasks;
    private OnItemClickListener mListener;

    public TaskRecyclerAdapter(Context context, ArrayList<TaskItem> tasksItemArrayList) {
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
        public TextView mCalendarView;
        public Button buttonDelete;
        public Button buttonShare ;

        public Button buttonUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.task_title);
            mDescriptionTextView = itemView.findViewById(R.id.task_description);
            mCalendarView = itemView.findViewById(R.id.task_due_date);
            buttonUpdate = itemView.findViewById(R.id.edit_button);
            buttonDelete = itemView.findViewById(R.id.delete_button);
            buttonShare = itemView.findViewById(R.id.share_button);
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
        holder.mCalendarView.setText(task.getDueDate());

        holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialogUpdate viewDialogUpdate = new ViewDialogUpdate();
                viewDialogUpdate.showDialog(context, task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getUserID(), task.isCompleted());
            }
        });
        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_text));
                shareIntent.putExtra(Intent.EXTRA_TEXT, task.getTitle());
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_with)));
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
            CalendarView calendarView = (CalendarView)  dialog.findViewById(R.id.task_due_date);;
            CheckBox checkBox = dialog.findViewById(R.id.checkbox_completed);


            textTitle.setText(title);
            textDes.setText(description);


            Button buttonUpdate = dialog.findViewById(R.id.edit_button);
            Button buttonCancel = dialog.findViewById(R.id.cancel_button);
            final String[] newDate = new String[1];
            newDate[0]=date ;
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month,
                                                int dayOfMonth) {
                    String  curDate = String.valueOf(dayOfMonth);
                    String  Year = String.valueOf(year);
                    int newMonth= month+1 ;
                    String  Month = String.valueOf(newMonth);
                    newDate[0] = curDate+"/"+Month+"/"+Year;

                }
            });


            buttonUpdate.setText("UPDATE");

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
                    if (title.isEmpty() || description.isEmpty() || newDate[0] == null ) {
                        Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                    } else {

                        if (newTitle.equals(title) && newDes.equals(description) && newDate[0].equals(date) && newCompleted == false ) {
                            Toast.makeText(context, "you don't change anything", Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseAuth auth = FirebaseAuth.getInstance() ;
                            FirebaseUser currentUser =auth.getCurrentUser();
                            String uid = currentUser.getUid();

                            databaseReference.child("Tasks")
                                    .child(id)
                                    .setValue(new TaskItem(id, newTitle, newDes, newDate[0], uid, newCompleted));
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

