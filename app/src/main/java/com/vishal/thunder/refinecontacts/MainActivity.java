package com.vishal.thunder.refinecontacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    HashMap<String, ArrayList<String>> contacts=new HashMap<>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=(ListView)findViewById(R.id.listview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            marshmallowPermission();                        //***** marshmaloow runtime permission*****//
        }
    }
    boolean marshmallowPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {

                getContacts();
                        } else {
                requestPermission();
            }
        }
        return true;

    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission(){
        int storage = checkSelfPermission(Manifest.permission.READ_CONTACTS);

        if (storage == PackageManager.PERMISSION_GRANTED ){
            return true;
        } else {
            return false;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission(){

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){

            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)){
                Toast.makeText(this,"Stroge Permission must be needed which allows to access sms . Please allow sms in App Settings for additional functionality.",Toast.LENGTH_LONG).show();
            }



        } else {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recreate();
                } else {
                    Toast.makeText(this,"Permission Denied, App maybe get crashed.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);

                    int i=0;

                    ArrayList<String> arr=new ArrayList<>(pCur.getCount());
                    assert pCur != null;
                    while (pCur.moveToNext()){
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        arr.add(phoneNo);

                    }
                    contacts.put(name,arr);

                    if(!contacts.isEmpty())
                        Log.d(String.valueOf(contacts.get(name)),"hello");
                    arr.clear();
                    pCur.close();
                }
            }

        }

        cursor.close();

        setView();


    }

    private void setView() {

        ArrayList<String>  names=new ArrayList<>();

        for (Map.Entry<String, ArrayList<String>> entry : contacts.entrySet()) {
           names.add(entry.getKey());
        }
        Collections.sort(names);

        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, names);

        listView.setAdapter(adapter);
    }

}
