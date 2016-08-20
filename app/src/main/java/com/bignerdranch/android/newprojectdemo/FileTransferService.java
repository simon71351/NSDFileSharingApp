package com.bignerdranch.android.newprojectdemo;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by simon on 7/24/16.
 */
public class FileTransferService {
    private FileServer mFileServer;
    private FileClient mFileClient;
    private MainActivity mNsdChatActivity;
    private int mPort;
    private Socket mSocket;
    private ViewPagerAdapter mViewPagerAdapter;

    public void setViewPagerAdapter(ViewPagerAdapter viewPagerAdapter) {
        mViewPagerAdapter = viewPagerAdapter;
    }

    public int getLocalPort() {
        return mPort;
    }

    public void setLocalPort(int port) {
        mPort = port;
    }

    public void tearDown() {
        try {
            getSocket().close();
        } catch (IOException ioe) {
            Log.e("ServerSocket", "Error when closing server socket.");
        }
    }

    private synchronized void setSocket(Socket socket) {
        Log.d(TAG, "setSocket being called.");
        if (socket == null) {
            Log.d(TAG, "Setting a null socket.");
        }
        if (mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    // TODO(alexlucas): Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        mSocket = socket;
    }

    private Socket getSocket() {
        return mSocket;
    }

    private String TAG = "FileTransferService";

    public FileTransferService(Context context){
        mFileServer = new FileServer();
        mNsdChatActivity = (MainActivity) context;
        Log.e("FileTransferService", "FileTransferService gets created");
    }

    class FileServer{
        ServerSocket mServerSocket = null;
        Thread mThread = null;

        public FileServer(){
            new Thread(new ServerThread()).start();
        }

        class ServerThread implements Runnable{
            @Override
            public void run() {
                try{
                    mServerSocket = new ServerSocket(8080);
                    setLocalPort(mServerSocket.getLocalPort());

                    Log.e(TAG, "Creating Server Socket");

                    while(!Thread.currentThread().isInterrupted()){
                        Log.e(TAG, "Server socket created. Awaiting connection");

                        Socket connectedClientSocket = mServerSocket.accept();
                        Log.e(TAG, "Connected to FileServer!");

                        readData(connectedClientSocket);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            public void readData(Socket connectedSocket){
                try{
                    //BufferedReader input = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));

                    BufferedInputStream bis = new BufferedInputStream(connectedSocket.getInputStream());
                    DataInputStream input = new DataInputStream(bis);

                    int occurance = 0;

                    String fileName = null;
                    BufferedOutputStream bos = null;

                    int numOfFiles = input.readInt();
                    String[] createdFilePaths = new String[numOfFiles];

                    for(int j = 0; j < numOfFiles; j++) {


                        fileName = input.readUTF();  //First line read = filename
                        Log.e("Filename", "Filename: " + fileName);
                        //Log.e("FileName", fileName);
                        File dir = new File(Environment.getExternalStorageDirectory() + "/Pictures/FileShare");

                        File[] filesList = dir.listFiles();
                        for (File file : filesList) {
                            if (file.getName().contains(fileName)) {
                                occurance++;
                            }
                        }

                        if (occurance != 0) fileName += "(" + occurance + ")";

                        File createdFile = new File(Environment.getExternalStorageDirectory() + "/Pictures/FileShare/" + fileName);
                        createdFilePaths[j] = createdFile.getAbsolutePath();
                        //if(!createdFile.exists()) createdFile.createNewFile();

                        //BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(createdFile)));

                        FileOutputStream fos = new FileOutputStream(createdFile);
                        bos = new BufferedOutputStream(fos);

                        int byteRead = 0;
                        long size = input.readLong(); //Second line read = size
                        Log.e("Size", "Size: " + Long.toString(size));

                        byte[] buffer = new byte[1024];
//                        while((byteRead = input.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
//                            //byte[] bufferByte = new String(buffer).getBytes();   //Thrid and other times read data of the actual file with specified buffer size
//
//                            output.write(buffer, 0, byteRead);
//                            size -= byteRead;
//                        }


                        for (int i = 0; i < size; i++) bos.write(bis.read());

                    }

                    mViewPagerAdapter.getLogFragment().writeToLogFile(createdFilePaths, false);

                    bos.close();
                    input.close();
                    connectedSocket.close();


                    mNsdChatActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mNsdChatActivity, "New File written", Toast.LENGTH_SHORT).show();
                        }
                    });



                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }



}
