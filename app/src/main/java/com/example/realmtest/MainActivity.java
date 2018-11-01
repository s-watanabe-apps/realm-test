package com.example.realmtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import io.realm.RealmResults;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    EditText editId;
    EditText editName;
    Button buttonSync;
    Button buttonRm;
    TextView textLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getInstance(this);
        editId = findViewById(R.id.editId);
        editName = findViewById(R.id.editName);
        buttonSync = findViewById(R.id.buttonSync);
        buttonRm = findViewById(R.id.buttonRm);
        textLog = findViewById(R.id.textLog);

        // 検索
        RealmResults<Item> items = realm.where(Item.class).findAll();
        for (Item i : items) {
            addText(i.getId() + ":" + i.getName());
        }

        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try{
                            clearText();

                            int id = Integer.parseInt(editId.getText().toString());
                            String name = editName.getText().toString();

                            RealmResults<Item> finds = realm.where(Item.class).equalTo("id", id).findAll();

                            if(finds.size() > 0) {
                                // 更新
                                finds.get(0).setName(name);
                            } else{
                                // 追加
                                Item item = realm.createObject(Item.class);
                                item.setId(id);
                                item.setName(name);
                                realm.copyFromRealm(item);
                            }

                            RealmResults<Item> items = realm.where(Item.class).findAll();
                            for (Item i : items) {
                                addText(i.getId() + ":" + i.getName());
                            }

                        } catch(Exception e) {
                            addText(e.getMessage());
                        }
                    }
                });
            }
        });

        buttonRm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try{
                            clearText();

                            int id = Integer.parseInt(editId.getText().toString());

                            RealmResults<Item> finds = realm.where(Item.class).equalTo("id", id).findAll();

                            if(finds.size() > 0) {
                                // 削除
                                finds.get(0).removeFromRealm();
                            }

                            RealmResults<Item> items = realm.where(Item.class).findAll();
                            for (Item i : items) {
                                addText(i.getId() + ":" + i.getName());
                            }

                        } catch(Exception e) {
                            addText(e.getMessage());
                        }
                    }
                });
            }
        });

    }

    public void addText(String text) {
        textLog.setText(textLog.getText().toString() + text + "\n");
    }

    public void clearText() {
        textLog.setText("");
    }
}
