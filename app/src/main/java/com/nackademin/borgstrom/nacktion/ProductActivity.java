package com.nackademin.borgstrom.nacktion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProductActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int minprice = 1;
    private String emailSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final int auktionId = getIntent().getIntExtra("productid", 0);
        final String name = getIntent().getStringExtra("produktnamn");
        String description = getIntent().getStringExtra("productdescription");
        String time = getIntent().getStringExtra("productsluttid");
        final String pris = getIntent().getStringExtra("produktpris");

        minBid(auktionId);
        emailSubject = name;

        TextView tName = (TextView) findViewById(R.id.productName);
        TextView tDescription = (TextView) findViewById(R.id.productDescription);
        TextView tTime = (TextView) findViewById(R.id.productTime);
        TextView tPrice = (TextView) findViewById(R.id.productPrice);
        tName.setText(name);
        tDescription.setText(description);
        tTime.setText("Sluttid: " + time);
        tPrice.setText("Acceptpris: " +pris + "kr");

        try {
            InputStream imageStream = getAssets().open("noimage.png");
            Drawable d = Drawable.createFromStream(imageStream, null);

            ImageView iv = (ImageView) findViewById(R.id.productImage);
            iv.setImageDrawable(d);

        } catch (IOException e) {
            e.printStackTrace();
        }


        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseBid(name, auktionId, Double.parseDouble(pris));

            }
        });
        assert fab2 != null;
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int fullprice = (int) Math.round( Double.parseDouble(pris));
                placeBid(auktionId,  fullprice);

            }
        });


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
        getMenuInflater().inflate(R.menu.product, menu);
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

        if (id == R.id.nav_camera) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(this, CategoryActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_email) {
            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.setType("text/mail");
            sendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{"borgstrom.simon@gmail.com"});
            sendMail.putExtra(Intent.EXTRA_SUBJECT, "Gällane Auktion " +emailSubject);
            sendMail.putExtra(Intent.EXTRA_TEXT, "Hej Nacktion");
            startActivity(Intent.createChooser(sendMail, "Välj epostprogram:"));
        } else if (id == R.id.nav_send) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nacktion.azurewebsites.net"));
            startActivity(browserIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void placeBid(int auktionid, int bid) {

        final int fauktionid = auktionid;
        final int fbid = bid;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://nackademiska.azurewebsites.net/4/addoffer";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        sentDialog("Bud lagt", "Ditt bud är registrerat");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                        sentDialog("Något gick fel", error.toString());

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("auctionid", String.valueOf(fauktionid));
                params.put("customerid", String.valueOf(MainActivity.id));
                params.put("offer", String.valueOf(fbid));
                return params;
            }
        };
        queue.add(postRequest);

    }

    private void chooseBid(String namn, int auktion, double pris) {
        final int auktionid = auktion;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Lägg bud på " + namn);

        final NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(minprice);
        picker.setMaxValue((int) Math.round(pris));
        final FrameLayout parent = new FrameLayout(this);
        parent.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        builder.setView(parent);

        builder.setPositiveButton("Lägg bud", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                placeBid(auktionid, picker.getValue());
                Log.d("pos", "ok");
            }
        })
                .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void sentDialog(String header, String message) {
        new AlertDialog.Builder(this)
                .setTitle(header)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_menu_bid)
                .show();
    }



    private void minBid(int auktion) {

        final int auktionId = auktion;
        final ArrayList<Integer> bids = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(ProductActivity.this);
                String url = "http://nackademiska.azurewebsites.net/4/getoffers?auctionid=" + auktionId;


                RequestFuture<JSONArray> future = RequestFuture.newFuture();
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, future, future);
                queue.add(request);

                try {
                    JSONArray response = future.get(3, TimeUnit.SECONDS);
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject js = response.getJSONObject(i);
                        bids.add(js.getInt("Offer"));
                        if (bids.get(i) > minprice)
                            minprice = bids.get(i)+1;
                        Log.d("temp", String.valueOf(js.getInt("Offer")));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }

        }); t.start();

        /*return minprice;*/
    }

}
