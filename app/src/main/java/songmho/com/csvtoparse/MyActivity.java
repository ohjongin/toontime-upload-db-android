package songmho.com.csvtoparse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MyActivity extends Activity {
    protected ArrayList<ParseObject> WebToonDbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Button read = (Button) findViewById(R.id.read);
        Button send = (Button) findViewById(R.id.send);
        final TextView text = (TextView) findViewById(R.id.text);

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ReadDbFileAsyncTask().execute("webtoondb.csv");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WebToonDbList != null) {
                    new UploadDbAsyncTask().execute(WebToonDbList);
                } else {
                    Toast.makeText(MyActivity.this, getString(R.string.error_no_loaded_webtoon_list), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected ArrayList<ParseObject> getWebToonDbList(String filename) {
        ArrayList<ParseObject> readDbList = new ArrayList<ParseObject>();

        File fileCsv = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filename);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileCsv), "UTF-8"));
            String line = "";

            while ((line = br.readLine()) != null) {
                String[] token = line.split(",", -1);

                ParseObject po = new ParseObject("parseexam");
                po.put("company", token[0]);
                po.put("genre1", token[1]);
                po.put("genre2", token[2]);
                po.put("genre3", token[3]);
                po.put("toonname", token[4]);
                po.put("artist1", token[5]);
                po.put("artist2", token[6]);
                po.put("isend", "연재".equals(token[7]) ? false : true);
                po.put("day1", token[8]);
                po.put("day2", token[9]);
                po.put("day3", token[10]);
                po.put("day4", token[11]);
                po.add("day5", token[12]);
                po.put("isfree", "무료".equals(token[13]) ? true : false);
                po.put("year", token[14]);
                po.put("detail", token[15]);
                po.put("url", token[16]);
                po.put("thumbnail", token[17]);

                readDbList.add(po);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readDbList;
    }

    protected class ReadDbFileAsyncTask extends AsyncTask<String, Void, ArrayList<ParseObject>> {
        protected ProgressDialog progressDialog;

        public ReadDbFileAsyncTask() {
            progressDialog = new ProgressDialog(MyActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.load_webtoon_list_from_file));
        }

        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected ArrayList<ParseObject> doInBackground(String... filenames) {
            return getWebToonDbList(filenames[0]);
        }

        protected void onPostExecute(ArrayList<ParseObject> result) {
            progressDialog.dismiss();
            WebToonDbList = result;

            ((TextView) findViewById(R.id.text)).setText(String.format(getString(R.string.read_file_result_format), result.size()));
        }
    }

    protected class UploadDbAsyncTask extends AsyncTask<ArrayList<ParseObject>, Void, Void> {
        protected ProgressDialog progressDialog;
        protected int failCount = 0;

        public UploadDbAsyncTask() {
            progressDialog = new ProgressDialog(MyActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.uploading_webtoon_to_server));
        }

        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(ArrayList<ParseObject>... lists) {
            if (lists == null || lists.length < 1) return null;

            ArrayList<ParseObject> webToonList = lists[0];
            for (ParseObject po : webToonList) {
                try {
                    po.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                    failCount++;
                }
            }

            return null;
        }

        protected void onPostExecute() {
            progressDialog.dismiss();

            ((TextView) findViewById(R.id.text)).setText(String.format(getString(R.string.read_file_result_format), failCount));
        }
    }
}
