package com.example.suketurastogi.booklistingapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class BookListActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookListActivity.class.getSimpleName();
    //Array list in which all book names are fetched from server and saved.
    final ArrayList<BookList> bookArrayList = new ArrayList<>();
    //Url to hit for Getting Book List.
    String bookListUrl = "https://www.googleapis.com/books/v1/volumes?q=";
    //Initializing server results to null.
    String resultBookListServer = null;

    //List View in which all the Books names will be visible.
    ListView bookList;
    private TextView noData;

    //Where the user will type to search for books.
    EditText searchBar;
    Button searchButton;
    //Custom Adapter to show staff list.
    BookListAdapter bookListAdapter;
    private String bookListServerUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        searchBar = (EditText) findViewById(R.id.search_bar_edit_text);

        searchButton = (Button) findViewById(R.id.search_button);

        bookList = (ListView) findViewById(R.id.book_list);

        bookListAdapter = new BookListAdapter(BookListActivity.this, bookArrayList);

        noData = (TextView)findViewById(R.id.no_data_text_view);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String bookInfo = searchBar.getText().toString().replaceAll(" ", "+");
                bookListServerUrl = bookListUrl + bookInfo;

                // Perform the network request
                BooksAsyncTask task = new BooksAsyncTask();
                task.execute();
            }
        });
    }

    public class BooksAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            // Create URL object
            URL url = createUrl(bookListServerUrl);

            Log.v("url",""+url);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            resultBookListServer = jsonResponse;

            Log.v("resultBookListServer ",""+resultBookListServer);

            return resultBookListServer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (resultBookListServer == null) {
                Log.v("No Data ","No Data");
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();

                noData.setVisibility(View.VISIBLE);


            } else {
                try {
                    noData.setVisibility(View.GONE);

                    bookArrayList.clear();

                    String BOOKS_LIST_JSON_RESPONSE = resultBookListServer;

                    // Parse the response given by the STAFF_LIST_JSON_RESPONSE string
                    // and build up a list of Staff objects with the corresponding data.

                    JSONObject bookJsonResponse = new JSONObject(BOOKS_LIST_JSON_RESPONSE);
                    JSONArray bookItemsJSONArray = bookJsonResponse.getJSONArray("items");

                    for (int i = 0; i < bookItemsJSONArray.length(); i++) {

                        JSONObject currentItem = bookItemsJSONArray.getJSONObject(i);
                        JSONObject currentVolumeInfo = currentItem.getJSONObject("volumeInfo");

                        Log.v("currentVolumeInfo",""+ currentVolumeInfo);

                        String title = currentVolumeInfo.getString("title");
                        JSONArray authorJSONArray = currentVolumeInfo.getJSONArray("authors");
                        String author = authorJSONArray.getString(0);

                        bookArrayList.add(new BookList(title, author));

                        bookList.setAdapter(bookListAdapter);
                    }
                } catch (JSONException e) {
                    // If an error is thrown when executing any of the above statements in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("Exception : ", "Problem parsing the bookList JSON results", e);
                }
            }
        }

        /**
         * Returns new URL object from the given string URL.
         */

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                Log.v("ResponseCode : ",""+urlConnection.getResponseCode());

                if (urlConnection.getResponseCode() == 200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }

            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }
}
