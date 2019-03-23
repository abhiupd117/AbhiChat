package com.amandeep.abhichat;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager mviewpager;
    private SectionPagerAdapter sectionPagerAdapter;
    private TabLayout tabLayout;


    ChatListFragment chatListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();

        //Adding toolbar to the activity
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("CHATS"));
        tabLayout.addTab(tabLayout.newTab().setText("FREINDS"));

        //Initializing viewPager
        mviewpager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        SectionPagerAdapter adapter = new SectionPagerAdapter( getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        mviewpager.setAdapter(adapter);

        //Adding onTabSelectedListener to swipe views


        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mviewpager));
        mviewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    public void onTabSelected(TabLayout.Tab tab) {
        mviewpager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onCreateNavigateUpTaskStack(builder);
    }

    @Override
    public   void onStart() {
        super.onStart();
        FirebaseUser currentuser=mAuth.getCurrentUser();
        if (currentuser==null)
        {
            sendtoStart();
        }

    }

    private void sendtoStart() {
        Intent start_intent= new Intent(MainActivity.this, StartActivity.class);
        startActivity(start_intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId()==R.id.main_logout_button)
         {
             FirebaseAuth.getInstance().signOut();
             sendtoStart();
         }
         return true;
    }
}
