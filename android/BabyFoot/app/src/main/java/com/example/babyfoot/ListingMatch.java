package com.example.babyfoot;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ListingMatch extends Activity {

    private UserMatchDao MatchDao;
    private ArrayList<UserMatch> MatchLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_match);

        MatchDao = AppDataBase.getDatabase(this.getApplicationContext()).UserMatchDao();

        final ListView list = findViewById(R.id.match_list);
        ArrayList<String> arrayList = new ArrayList<>();



        for(UserMatch uM : MatchDao.getAll()){
            arrayList.add("id "+uM.idMatch+"  "+uM.sscore+" - "+uM.oscore);
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem=(String) list.getItemAtPosition(position);
                Toast.makeText(ListingMatch.this,clickedItem,Toast.LENGTH_LONG).show();
            }
        });

        final Button deleteHistory = (Button) findViewById(R.id.b_delete);
        deleteHistory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteAll();
                setContentView(R.layout.activity_listing_match);
            }
        });
    }

    public ArrayList<UserMatch> getMatchList() {
        return MatchLiveData;
    }
    public void insert(UserMatch... matchs) {
        MatchDao.insert(matchs);
    }
    public void update(UserMatch match) {
        MatchDao.update(match);
    }
    public void deleteAll() {
        MatchDao.deleteAll();
    }
}
