package com.example.mydeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity implements InputFragment.InputFragmentListener {
    public static final String DIALOG_TAG = "dialog_input_tag";

    TextView textTitle, textPrice;
    ImageView imageView;
    Button button;
    private final String TAG = "TAG";
    Item newItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        textTitle = (TextView)findViewById(R.id.textTitle);
        textPrice= (TextView)findViewById(R.id.textPrice);
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "loading..");
                textPrice.setText("New");
                textTitle.setText("New 2");
                //new LoadTextTask().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tool_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.addIcon):
                openInputFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void openInputFragment() {

        InputFragment fragmentInput = InputFragment.newInstance("Enter link URL");
        fragmentInput.show(getSupportFragmentManager(), DIALOG_TAG);

    }
    /*  CALL BACK fragment to get CLASS info input by tutor
     *   Perform add link to load item */
    @Override
    public void onInputFragmentListenerOK(String link, String tag) {
            newItem = new Item(link);
            Log.i(TAG, link + " ,tag: "+tag);
            writeToFile(getApplicationContext());
            //updateUI(newClass);
    }
    /* WRITE City_Location to textfile called inputFromUser.txt
    File is in csv format, parse file using comma
     */
    private void writeToFile(Context context) {
        String url = newItem.getUrl();
        new LoadTextTask().execute(url);
        String data = newItem.getName()+","+newItem.getPrice() +","+newItem.getUrl()+","+newItem.getImage();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("inputFromUser.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            Log.i("WRITE", data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    public class LoadTextTask extends AsyncTask<String, Void, Void>{
        String productTitle, productPrice, retailer;
        //String url = "https://firebase.google.com/";
        //String url = "https://www.chemistwarehouse.com.au/buy/79803/neutrogena-hydro-boost-gel-cream-50g";
        //String url = "https://www.chemistwarehouse.com.au/buy/83652/a2-milk-powder-skim-1kg";
        Bitmap bitmap=null;
        String thumnailSrc;
        @Override
        protected Void doInBackground(String...strings) {
            try{
                Log.i(TAG, "doInBackground");
                Document doc = Jsoup.connect(strings[0]).get();
                productTitle = doc.title();//

                //get the product thumanil of the website
                Element img = doc.select("img[class=hero_image zoomer_harvey product-thumbnail]").first();  //product-thumbnail
                Log.i(TAG, "selector "+ img.attr("src2"));

                thumnailSrc = img.absUrl("src2");
                //Log.i(TAG, "jgp link "+ thumnailSrc);

                //download image from URL??
                InputStream input = new java.net.URL(thumnailSrc).openStream();

                //decode bitmap??
                bitmap = BitmapFactory.decodeStream(input);

                //product name
                //productTitle = doc.select("div[class=product-name]").first().text();

                //product price
                productPrice = doc.select("div[class=product__price]").first().text();  //.product__price
                newItem.updateItem(productPrice, productTitle, strings[0]);
            }catch(Exception e){
                e.printStackTrace();
                Log.e(TAG, "CATCH ERROR");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            textPrice.setText(productPrice);
            textTitle.setText(productTitle);
            imageView.setImageBitmap(bitmap);

        }
    }
}
