package com.clikader.newsgateway;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.drm.DrmStore;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ArrayAdapter listAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<Source> sourceList = new ArrayList<>();
    private ArrayList<String> rnList;
    private ArrayList<Article> articleList = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private String sourceSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (networkCheck()) {
            try {
                sourceList = new AsyncLoad(this).execute("all").get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerList = (ListView) findViewById(R.id.leftDrawer);
            rnList = getSourceNameList(sourceList);
            listAdapter = new ArrayAdapter<>(this, R.layout.drawer_list_item, rnList);
            drawerList.setAdapter(listAdapter);
            drawerList.setOnItemClickListener(
                    new ListView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selectItem(position);
                        }
                    }
            );
            drawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.string.drawer_open,
                    R.string.drawer_close
            );

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
            mViewPager.setBackground(getResources().getDrawable(R.drawable.news_background, this.getTheme()));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final TextView tv = new TextView(this);
            builder.setView(tv);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without internet connection. " +
                    "Please check your internet service and open the app again.");
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (networkCheck()) {
            drawerToggle.syncState();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final TextView tv = new TextView(this);
            builder.setView(tv);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without internet connection. " +
                    "Please check your internet service and open the app again.");
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        if (networkCheck()) {
            // the internet is ok.
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final TextView tv = new TextView(this);
            builder.setView(tv);
            builder.setTitle("No Network Connection");
            builder.setMessage("Data cannot be accessed/loaded without internet connection. " +
                    "Please check your internet service and open the app again.");
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menuAll:
                loadSource(getString(R.string.all));
                return true;
            case R.id.menuBusiness:
                loadSource(getString(R.string.business));
                return true;
            case R.id.menuEntertainment:
                loadSource(getString(R.string.entertainment));
                return true;
            case R.id.menuGeneral:
                loadSource(getString(R.string.general));
                return true;
            case R.id.menuScience:
                loadSource(getString(R.string.science));
                return true;
            case R.id.menuSports:
                loadSource(getString(R.string.sports));
                return true;
            case R.id.menuTechnology:
                loadSource(getString(R.string.technology));
                return true;

                default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadSource(String cat) {
        if (AsyncLoad.running) {
            return;
        }

        AsyncLoad.running = true;

        try {
            sourceList.clear();
            sourceList = new AsyncLoad(this).execute(cat).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sourceList == null) {
            displayMessage("No Data Found", "Sorry, No data was found for this category.");
        } else {
           rnList.clear();
           rnList.addAll(getSourceNameList(sourceList));
           listAdapter.notifyDataSetChanged();
        }
    }

    public void displayMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean networkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void selectItem(int pos) {
        Toast.makeText(this, sourceList.get(pos).getSourceId() + " selected.",
                Toast.LENGTH_SHORT).show();
        sourceSelected = sourceList.get(pos).getSourceName();
        try {
            articleList.clear();
            articleList = new AsyncLoadArticle(this).execute(sourceList.get(pos).getSourceId()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (articleList == null) {
            displayMessage("No Data Found", "No Data Found for the source you selected.");
        } else {
            for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
                mSectionsPagerAdapter.notifyChangeInPosition(i);

            fragments.clear();
            for (int i = 0; i < articleList.size(); i++) {
                String pi = (i + 1) + " of " + articleList.size();
                fragments.add(myFragment.newInstance(pi, articleList.get(i)));
            }

            mSectionsPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(0);
            mViewPager.setBackground(null);

            getSupportActionBar().setTitle(sourceSelected);
        }
        drawerLayout.closeDrawer(drawerList);
    }

    public ArrayList<String> getSourceNameList(ArrayList<Source> sList) {
        ArrayList<String> sourceNames = new ArrayList<>();
        for (int i = 0; i < sList.size(); i++) {
            sourceNames.add(sList.get(i).getSourceName());
        }
        return sourceNames;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        public SectionsPagerAdapter(FragmentManager fm) {super(fm);}

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }

    public static class myFragment extends Fragment {
        private Context mContext;

        public myFragment(){
        }

        public static myFragment newInstance(String pi, Article art) {
            myFragment f = new myFragment();
            Bundle bdl = new Bundle();
            bdl.putSerializable("PageInfo", pi);
            bdl.putSerializable("Article", art);
            f.setArguments(bdl);
            return f;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.pager_fragment, container, false);

            TextView authorView = (TextView) rootView.findViewById(R.id.articleAuthor);
            TextView titleView = (TextView) rootView.findViewById(R.id.articleTitle);
            TextView dateView = (TextView) rootView.findViewById(R.id.articleDate);
            TextView contentView = (TextView) rootView.findViewById(R.id.articleContent);
            TextView pageView = (TextView) rootView.findViewById(R.id.articlePage);
            final ImageView imgView = (ImageView) rootView.findViewById(R.id.articleImg);

            Article detailedArticle = (Article) getArguments().get("Article");
            String pageInfo = (String) getArguments().get("PageInfo");
            String urlToArticle = detailedArticle.getUrl();
            final Uri url = Uri.parse(urlToArticle);

            pageView.setText(pageInfo);

            if (detailedArticle.getAuthor().equals("null")) {
                authorView.setText("Unknown author");
            } else {
                authorView.setText(detailedArticle.getAuthor());
            }

            titleView.setText(detailedArticle.getTitle());
            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent aIntent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(aIntent);
                }
            });

            if (detailedArticle.getTime().equals("null")) {
                dateView.setText("Unknown publish time");
            } else {
                String originalTime = detailedArticle.getTime();
                String usePart = originalTime.split("Z")[0];
                String[] parts = usePart.split("T");

                String finalTime;
                if (parts[1].contains("+")) {
                    finalTime = parts[0] + " at " + parts[1].split("\\+")[0];
                } else {
                    finalTime = parts[0] + " at " + parts[1];
                }
                dateView.setText(finalTime);
            }

            if (detailedArticle.getDescription().equals("null")) {
                contentView.setText("No description");
            } else {
                contentView.setText(detailedArticle.getDescription());
            }
            contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent aIntent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(aIntent);
                }
            });

            final String imgURL = detailedArticle.getImgUrl();
            Picasso picasso = new Picasso.Builder(mContext).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = imgURL.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.noimg)
                            .placeholder(R.drawable.loading)
                            .into(imgView);
                }
            }).build();
            picasso.load(imgURL)
                    .error(R.drawable.noimg)
                    .placeholder(R.drawable.loading)
                    .into(imgView);

            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent aIntent = new Intent(Intent.ACTION_VIEW, url);
                    startActivity(aIntent);
                }
            });
            return rootView;
        }
    }
}
