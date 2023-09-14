package com.selmi.myapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selmi.myapp.databinding.ActivityMenuBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference myRef ;
    private FirebaseAuth auth  ;
    private FirebaseUser currentUser  ;
    private SharedPreferences sharedPref  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Tasks");
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        setSupportActionBar(binding.appBarMenu.toolbar);
        binding.appBarMenu.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuActivity.ViewDialogAdd viewDialogAdd = new MenuActivity.ViewDialogAdd();
                viewDialogAdd.showDialog(MenuActivity.this);
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu) ;
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        MenuItem bluetoothItem = menu.findItem(R.id.action_bluetooth);
        MenuItem profileItem = menu.findItem(R.id.action_updateProfile);
        MenuItem signoutItem = menu.findItem(R.id.action_signout);
        MenuItem cameraItem = menu.findItem(R.id.action_camera);
        settingsItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the settings item click here
                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        });
        bluetoothItem.setOnMenuItemClickListener(item -> {
            // Handle the settings item click here
            Intent intent = new Intent(MenuActivity.this, bluetoothActivity.class);
            startActivity(intent);
            return true;
        });
        profileItem.setOnMenuItemClickListener(item -> {
            // Handle the settings item click here
            Intent intent = new Intent(MenuActivity.this, updateprofilActivity.class);
            startActivity(intent);
            return true;
        });
        cameraItem.setOnMenuItemClickListener(item -> {
            // Handle the settings item click here
            Intent intent = new Intent(MenuActivity.this, CameraActivity.class);
            startActivity(intent);
            return true;
        });
        signoutItem.setOnMenuItemClickListener(item -> {
            if (currentUser != null) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("email", "");
                editor.putString("password", "");
                editor.apply();
                auth.signOut();
                Intent intent = new Intent(MenuActivity.this, loginActivity.class);
                startActivity(intent);
            }
            return true;
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public class ViewDialogAdd {
        public void showDialog(Context context) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog_add_new_task);
            CalendarView calendarView = (CalendarView)  dialog.findViewById(R.id.task_due_date);

            EditText textTitle = dialog.findViewById(R.id.task_title);
            EditText textDes = dialog.findViewById(R.id.task_description);
            dialog.findViewById(R.id.checkbox_completed).setVisibility(View.GONE);
            final String[] date = new String[1];
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) ;
            date[0]=sdf.format(new Date()) ;
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(CalendarView view, int year, int month,
                                                int dayOfMonth) {
                    String  curDate = String.valueOf(dayOfMonth);
                    String  Year = String.valueOf(year);
                    int newMonth= month+1 ;
                    String  Month = String.valueOf(newMonth);

                    date[0] = curDate+"/"+Month+"/"+Year;

                }
            });


            Button buttonAdd = dialog.findViewById(R.id.edit_button);
            Button buttonCancel = dialog.findViewById(R.id.cancel_button);

            buttonAdd.setText("ADD");
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            buttonAdd.setOnClickListener(view -> {
                String id = "Task" + new Date().getTime();
                String title = textTitle.getText().toString();
                String des = textDes.getText().toString();

                if (title.isEmpty() || des.isEmpty() || date[0].isEmpty()) {
                    Toast.makeText(context, "Please Enter All data...", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance() ;
                    FirebaseUser currentUser =auth.getCurrentUser();
                    String uid = currentUser.getUid();

                    myRef.child(id).setValue(new TaskItem(id, title, des, date[0], uid, false));

                    Toast.makeText(context, "DONE!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });


            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

        }
    }




}