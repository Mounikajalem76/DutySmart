package com.example.dutysmart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class Sidenavigatiobbar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    androidx.appcompat.widget.Toolbar toolbar;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sidenavigatiobbar);
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        builder=new AlertDialog.Builder(this);

        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Assignduty()).commit();
            navigationView.setCheckedItem(R.id.nav_assign);

        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.nav_assign){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Assignduty()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        } else if (id==R.id.nav_postatten) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Postattendance()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        } else if (id==R.id.nav_viewatten) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Showattendance()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;

        } else if (id==R.id.nav_logout) {
            builder.setTitle("Alert")
                    .setMessage("Do you want to SignOut")
                    .setCancelable(true)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(Sidenavigatiobbar.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment=getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof Showattendance){
            ((Showattendance) currentFragment).handlebackpressed();

        } else if (currentFragment instanceof Postattendance) {
            ((Postattendance)currentFragment).handlebackpressed();

        }else {
            builder.setTitle("Alert")
                    .setMessage("Do you want to SignOut")
                    .setCancelable(true)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Sidenavigatiobbar.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
    }
}