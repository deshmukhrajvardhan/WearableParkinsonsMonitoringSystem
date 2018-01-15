/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * imitations under the License.
 */

package com.example.android.wearable.wcldemo.pages;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.wearable.wcldemo.MobileApplication;
import com.example.android.wearable.wcldemo.R;
import com.example.android.wearable.wcldemo.common.Constants;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.WearableStatusCodes;
import com.google.devrel.wcl.WearManager;
import com.google.devrel.wcl.callbacks.AbstractWearConsumer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static android.os.Environment.getExternalStorageState;

//0import com.example.android.wearable.wcldemo.DatabaseAccess;

/**
 * A fragment that receives the files transferred from the wear app. There are two files that are
 * transferred; one is a text file and an image. When the text file is transferred, this fragment
 * reads the content of the file and presents that to the user in a text box. When the image starts
 * to its transfer, this fragment shows a spinner and when the transfer is complete, it shows the
 * image.
 */
public class FileTransferFragment extends Fragment {

    private static final String TAG = "FileTransferFragment";
    private WearManager mWearManager;
    private AbstractWearConsumer mWearConsumer;
    private ImageView mImageView;
    private TextView mTextView;
    //private AsyncTask<Void, Void, Bitmap> mAsyncTask;
    private AsyncTask<Void, Void, String> mAsyncTask;
    private ProgressBar mProgressBar;
    private static final int BUFFER_SIZE = 1024;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpWearListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.file_transfer, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image);
        mTextView = (TextView) view.findViewById(R.id.text);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
        view.findViewById(R.id.clear_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageResource(R.drawable.ic_photo_200dp);
            }
        });
        view.findViewById(R.id.clear_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText(R.string.text_file_here);
            }
        });

        return view;
    }

    /**
     * Creates two listeners to be called when the transfer of the text file is completed and when
     * a channel and an input stream is available to receive the image file. In some cases, it is
     * desired to be transfer files even if the application at the receiving node is not in front.
     * In those cases, one can define the same {@link com.google.devrel.wcl.callbacks.WearConsumer}
     * in the application instance; then the WearableListener that the WCL library provides will be
     * able to handle the transfer.
     */
    private void setUpWearListeners() {
        mWearManager = WearManager.getInstance();
        mWearConsumer = new AbstractWearConsumer() {

            @Override
            public void onWearableInputStreamForChannelOpened(int statusCode, String requestId,
                                                              final Channel channel, final InputStream inputStream) {
                if (statusCode != WearableStatusCodes.SUCCESS) {
                    Log.e(TAG, "onWearableInputStreamForChannelOpened(): "
                            + "Failed to get input stream");
                    return;
                }
                Log.d(TAG, "Channel opened for path: " + channel.getPath());
                //mAsyncTask = new AsyncTask<Void, Void, Bitmap>() {
                mAsyncTask = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        mImageView.setImageResource(R.drawable.ic_photo_200dp);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    // protected Bitmap doInBackground(Void... params) {
                    protected String doInBackground(Void... params) {
                        //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        final byte[] buffer = new byte[BUFFER_SIZE];
                        BufferedInputStream in = null;
                        BufferedOutputStream out = null;
                        //int nRead;
                       File file = new File(getContext().getExternalFilesDir(null), "Wear.db");
                        //File file = new File("Wear.db");
                        try
                        {
                            in =  new BufferedInputStream(inputStream);
                            out = new BufferedOutputStream(new FileOutputStream(file));
                            while((in.read(buffer))!= -1)
                            {
                                out.write(buffer);

                               // Log.d("tag", IOUtils.toString(buffer, StandardCharsets.UTF_8));
                            }



                            // mTextView.setText(message);

                           /* Cursor cursor = db.rawQuery("SELECT * FROM " + table, new String[0]);
                            for(int i = 0; i < cursor.getCount(); i++) {
                                cursor.moveToPosition(i);
                                Log.v(TAG, "Row " + i);
                                for(int c = 0; c < cursor.getColumnCount(); c++) {
                                    Log.v(TAG, cursor.getColumnName(c)  + ":" + cursor.getString(c));
                                }
                            }*/
                        }
                        catch(IOException e)
                        {
                            Log.e(TAG, "Error!",e);
                        }
                        closeStreams();
                        if (isCancelled()) {
                            return null;
                        }
                        // return bitmap;
                        //outputStream.write(string.getBytes());
                        return "DB Transfer completed!";
                    }

                    @Override
                    protected void onCancelled() {
                        mProgressBar.setVisibility(View.GONE);
                        mAsyncTask = null;
                        closeStreams();
                    }

                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    protected void onPostExecute(String message) {
                        //mProgressBar.setVisibility(View.GONE);
                        // mImageView.setImageBitmap(bitmap);
                        //super.onPostExecute();

                        File file = new File(getContext().getExternalFilesDir(null), "Wear.db");
                        //File file = new File("Wear.db");
                       // String file = "Wear.db";
                        // Find the object of ListView in the onCreate method of MainActivity and feed the quotes which are read form the database.
                        //private ListView listView;
                        //listView = (ListView) getView().findViewById(R.id.listView);
                        //List<String> text = new ArrayList<String>();
                         String line;
                        StringBuilder text = new StringBuilder();
                        //DatabaseAccess databaseAccess = new DatabaseAccess(getActivity());
                        //databaseAccess.open();
                        //List<String> quotes = databaseAccess.getQuotes();
                        //databaseAccess.close();
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));



                            while ((line = br.readLine()) != null) {
                                text.append(line);
                             //  text.append('\n');
                            }
                            br.close();
                        }
                        catch (IOException e) {
                            //You'll need to add proper error handling here
                        }
                        //listView.setText(line);
                       //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, (String[]) text.toArray() );
                        //listView.setAdapter(adapter);
                       // Log.d("abcdefgh", android.text.TextUtils.join(",", quotes));

                        if (file != null) {
                            mTextView.setText(String.valueOf(file.length()) + " " + getExternalStorageState(file) +  "DB Transfer completed" + String.valueOf(file.exists()) + text);

                        }
                       //mTextView.setText(text + "DB Transfer completed");
                        //databaseAccess.close();


                        mAsyncTask = null;



                    }

                    public void closeStreams() {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                            // no-op
                        }
                    }
                };
                mAsyncTask.execute();
            }

            @Override
            public void onWearableFileReceivedResult(int statusCode, String requestId,
                                                     File savedFile, String originalName) {
                Log.d(TAG, String.format(
                        "File Received: status=%d, requestId=%s, savedLocation=%s, originalName=%s",
                        statusCode, requestId, savedFile.getAbsolutePath(), originalName));
                String fileContent = getSimpleTextFileContent(savedFile);
                mTextView.setText(fileContent);
            }

        };
    }

    /**
     * A rudimentary method to read the content of the {@code file}.
     */
    private String getSimpleTextFileContent(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("file is null or doesn't exists!");
        }
        try {
            return new Scanner(file).useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            // already captured
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        // register our listeners
        mWearManager.addWearConsumer(mWearConsumer);

        // add the local capability to handle file transfer
        mWearManager.addCapabilities(Constants.CAPABILITY_FILE_PROCESSOR);

        MobileApplication.setPage(Constants.TARGET_FILE_TRANSFER);
    }

    @Override
    public void onPause() {
        // unregister our listeners
        mWearManager.removeWearConsumer(mWearConsumer);

        // remove the capability to handle file transfer
        mWearManager.removeCapabilities(Constants.CAPABILITY_FILE_PROCESSOR);

        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        super.onPause();
    }
}
