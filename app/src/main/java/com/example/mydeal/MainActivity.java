package com.example.mydeal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements InputFragment.InputFragmentListener {
    public static final String DIALOG_TAG = "dialog_input_tag";
    TextView textTitle, textPrice, textWasPrice, textId, textPriceOff, textPercent, textSave;
    ImageView imageView;
    private final String TAG = "TAG";
    private Item newItem;
    private ArrayList<Item> itemList = new ArrayList<Item>();
    private RecyclerView recyclerView;

    public RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textTitle = (TextView)findViewById(R.id.textTitle);
        //textPrice= (TextView)findViewById(R.id.textPrice);

        //textWasPrice = (TextView)findViewById(R.id.textWasPrice);
        //textId = (TextView)findViewById(R.id.textId);
        //textPriceOff =(TextView)findViewById(R.id.textPriceOff);
        //textSave =(TextView)findViewById(R.id.textSave);

        //textPercent =(TextView)findViewById(R.id.textPercent);

        //imageView = (ImageView) findViewById(R.id.imageView);

        initUI();
    }
    private void initUI(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.re_itemList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        setUpAdapter();
    }
    public void setUpAdapter(){
        if (itemList !=null ){
            Log.d("setUpAdapter size", String.valueOf(itemList.size()));
            mAdapter = new RecyclerViewAdapter(itemList);
            recyclerView.setAdapter(mAdapter);
        }
        else {
            recyclerView.setAdapter(null);
            Log.d(TAG, "list is empty");

        }
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
            new LoadTextTask().execute(link);
            writeToFile(getApplicationContext());
        Log.d(TAG, "list size: "+String.valueOf(itemList.size()));


    }
    /* WRITE City_Location to textfile called inputFromUser.txt
    File is in csv format, parse file using comma
     */
    private void writeToFile(Context context) {
        String url = newItem.getUrl();
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

    public static String getDomainName(String url) throws URISyntaxException {
        //get retailer name from link
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    public void openWebURL( String inURL ) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

        startActivity( browse );
    }

        //TODO task to analyze link URL
    public class LoadTextTask extends AsyncTask<String, Void, Void>{
        String productTitle, productPrice, retailer, productId, productWasPrice, thumnailSrc;
        //String url = "https://firebase.google.com/";
        // no discount:
        // "https://www.chemistwarehouse.com.au/buy/78922/sarah-jessica-parker-lovely-eau-de-parfum-30ml-spray?rcid=542"
        //String url = "https://www.chemistwarehouse.com.au/buy/79803/neutrogena-hydro-boost-gel-cream-50g";
        //String url = "https://www.chemistwarehouse.com.au/buy/83652/a2-milk-powder-skim-1kg";
        Bitmap bitmap=null;
        Bitmap bitmapLogo = null;
        @Override
        protected Void doInBackground(String...strings) {
            try{
                Log.i(TAG, "doInBackground");
                Document doc = Jsoup.connect(strings[0]).get();
                //productTitle = doc.title();

                //get the product thumanil of the website
                Element img = doc.select("img[class=hero_image zoomer_harvey product-thumbnail]").first();  //product-thumbnail
                thumnailSrc = img.absUrl("src2");//get img link
                //InputStream input = new java.net.URL(thumnailSrc).openStream();//download image from URL??
                //bitmap = BitmapFactory.decodeStream(input);//decode bitmap??

                //retailer logo
                Element imgLogo = doc.select("img").first();
                String logoSrc = imgLogo.absUrl("src");
                //InputStream inputLogo = new java.net.URL(logoSrc).openStream();
                //bitmapLogo = BitmapFactory.decodeStream(inputLogo);
                retailer = getDomainName(strings[0]);
                Log.i(TAG, "retailer "+ retailer);

                //product name
                productTitle = doc.select("div[class=product-name]").first().text();

                //product id
                productId = doc.select("div[class=product-id]").first().text();
                productId=productId.split(": ")[1];

                //product price
                productPrice = doc.select("div[class=product__price]").first().text();  //.product__price
                //retail price
                productWasPrice = doc.select("div[class=retailPrice]").first().text();  //.retailPrice
                productWasPrice = productWasPrice.split(": ")[1];

                newItem.updateItem(productId, thumnailSrc,productPrice, productWasPrice, productTitle, strings[0]);

                itemList.add(newItem);
                Log.d(TAG, "list size post: "+String.valueOf(itemList.size()));


            }catch(Exception e){
                e.printStackTrace();
                Log.e(TAG, "do in background CATCH ERROR");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //textWasPrice.setText(productWasPrice);
            //textPrice.setText(productPrice);
            //textTitle.setText(productTitle);
            //textPercent.setText(newItem.getPercent());
            //textSave.setText("Was "+newItem.getSave());
            //textId.setText(productId);
            //textPriceOff.setText(String.valueOf(newItem.isPriceOff()));
            //imageView.setImageBitmap(bitmap);
            mAdapter.notifyDataSetChanged(); //update UI right after task is done


        }

    }
    //TODO RV Adapter for item list

    /*
     ------------------  start ADAPTER to Recycler View list of Item----------------------------
     */
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private final ArrayList<Item> itemList;
        private final String TAG = "TAG";
        private Item item;
        private Bitmap bitmap;

        public RecyclerViewAdapter(ArrayList<Item> items) {
            itemList = items;
            item= null;

            //mListener = listener; mListener = OnClickListener
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.list_item_row, viewGroup, false) ;
            //view.setOnClickListener(rvListener);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            item = itemList.get(position);

            if (item.getImageBitmap() ==null) {
                makeIcon(item.getImage());
                //viewHolder.imageIcon.setImageBitmap(item.getImageBitmap());
                Log.e(TAG, "1-CHECK image in onBindViewHolder: " + item.getImageBitmap());
            }
                viewHolder.imageIcon.setImageBitmap(item.getImageBitmap());
            Log.e(TAG, "2-CHECK image in onBindViewHolder: " + item.getImageBitmap());

            //position is index of item on the list
                viewHolder.titleTv.setText(item.getName());
                viewHolder.priceTv.setText(item.getPrice());
                if(item.isPriceOff()){
                    viewHolder.wasPriceTv.setText("Was "+item.getWasPrice());
                    viewHolder.textSave.setText("Save "+item.getSave());
                    viewHolder.percentTv.setText(item.getPercent());
                }
                viewHolder.buyButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        openWebURL(item.getUrl());
                    }
                });
                Log.e(TAG, "CHECK: "+viewHolder.titleTv.getText().toString());
                Log.e(TAG, "CHECK: "+viewHolder.wasPriceTv.getText().toString());


        }
        public void makeIcon(String imageURL){
            new LoadImage().execute(imageURL);
        }
        //TODO ASYNTASK make bitmap for product Icon
        /*
          RV ADAPTER------start LOAD IMAGE ASYN TASK to make Image Bitmap----
             */
        public class LoadImage extends AsyncTask<String, Void, Bitmap> {
            InputStream input;

            @Override
            protected Bitmap doInBackground(String... strings) {
                //get the product thumanil of the website
                input = null;//download image from URL??
                try {
                    input = new java.net.URL(strings[0]).openStream();
                    // = BitmapFactory.decodeStream(input);//decode bitmap??
                    bitmap =BitmapFactory.decodeStream(input);//decode bitmap??
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap mbitmap) {
                super.onPostExecute(mbitmap);
                item.setImageBitmap(mbitmap);
                Log.e(TAG, "bitmap: "+mbitmap);
                mAdapter.notifyDataSetChanged(); //update UI right after task is done

            }
        }
        /*
          RV ADAPTER------END LOAD IMAGE ASYN TASK to make Image Bitmap----
         */
        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView imageIcon;
            public final TextView titleTv;
            public final TextView priceTv;
            public final TextView wasPriceTv;
            public final TextView percentTv;
            public final Button buyButton;
            public final TextView textSave;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                imageIcon = (ImageView) mView.findViewById(R.id.imageView);
                titleTv = (TextView) mView.findViewById(R.id.textTitle);
                priceTv = (TextView) mView.findViewById(R.id.textPrice);
                wasPriceTv = (TextView) mView.findViewById(R.id.textWasPrice);
                percentTv = (TextView) mView.findViewById(R.id.textPercent);
                buyButton = (Button)mView.findViewById(R.id.buttonBuy);
                textSave  = (TextView) mView.findViewById(R.id.textSave);

            }

            @Override
            public String toString() {
                return super.toString();
            }
        }
    }
    /*
     ------------------  end ADAPTER to Recycler View list of Item----------------------------
     */



}
