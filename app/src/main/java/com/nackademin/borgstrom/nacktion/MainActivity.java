package com.nackademin.borgstrom.nacktion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        id = getIntent().getIntExtra("customerid",0);

        final ArrayList<Product> productlist = new ArrayList<Product>();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://nackademiska.azurewebsites.net/4/getongoingauctions";
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<Bitmap> bitmaps = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject json = response.getJSONObject(i);
                                productlist.add(new Product(json.getInt("Id"), json.getString("Name"), json.getString("Description"),
                                        json.getString("AcceptPrice"), json.getString("EndTime"), json.getString("Image")));


                                byte[] base64String;
                                base64String = Base64.decode(productlist.get(i).getImage(), Base64.DEFAULT);
                                Bitmap bitMap = BitmapFactory.decodeByteArray(base64String, 0, base64String.length);
                                bitmaps.add(bitMap);
                            }
                            ListView lv=(ListView) findViewById(R.id.listView);
                            lv.setAdapter(new CustomAdapter(MainActivity.this, productlist, bitmaps));
/*
                            ArrayAdapter<Product> arrayAdapter = new ArrayAdapter<Product>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, android.R.id.text1, productlist);
                            ListView lv = (ListView) findViewById(R.id.listView);
*/
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent i = new Intent(MainActivity.this, ProductActivity.class);
                                    i.putExtra("productid", productlist.get(position).getId());
                                    i.putExtra("produktnamn", productlist.get(position).getName());
                                    i.putExtra("productdescription", productlist.get(position).getDescription());
                                    i.putExtra("produktpris", productlist.get(position).getAcceptpris());
                                    i.putExtra("productsluttid", productlist.get(position).getSlutTid());
                                    Log.i("image", productlist.get(position).getImage());
                                    Log.i("image", String.valueOf(productlist.get(position).getId()) );
                                    Log.i("image",productlist.get(position).getName());

                                    byte[] base64String;
                                    base64String = Base64.decode(productlist.get(position).getImage(), Base64.DEFAULT);

                                    i.putExtra("productimage", base64String);
                                    startActivity(i);
                                }
                            });


                       /*     lv.setAdapter(arrayAdapter);*/

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        queue.add(jsObjRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {
            // Handle the camera action
        } else if (id == R.id.nav_category) {
            Intent i = new Intent(this, CategoryActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_supplier) {
            Intent i = new Intent(this, LeverantorerActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_email) {
            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.setType("text/mail");
            sendMail.putExtra(Intent.EXTRA_EMAIL, new String[] {"borgstrom.simon@gmail.com"});
            sendMail.putExtra(Intent.EXTRA_SUBJECT, "Till Nacktion");
            sendMail.putExtra(Intent.EXTRA_TEXT, "Hej Nacktion");
            startActivity(Intent.createChooser(sendMail,"Välj epostprogram:"));

        } else if (id == R.id.nav_send) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nacktion.azurewebsites.net"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
