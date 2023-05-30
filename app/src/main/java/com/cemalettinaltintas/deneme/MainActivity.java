package com.cemalettinaltintas.deneme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cemalettinaltintas.deneme.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding binding;
    ArrayList<Urun> urunArrayList;
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);

        urunArrayList=new ArrayList<>();
        getData();

         arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                urunArrayList.stream().map(urun->urun.getUrunadi()).collect(Collectors.toList())
        );
        binding.listView.setAdapter(arrayAdapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this,urunArrayList.get(i).getUrunadi(),Toast.LENGTH_LONG).show();
                Intent intent =new Intent(MainActivity.this,UrunActivity.class);
                //intent.putExtra("test",urunArrayList.get(i));
                Singleton singleton=Singleton.getInstance();
                singleton.setSentUrun(urunArrayList.get(i));
                startActivity(intent);
            }
        });
    }
    private  void getData(){
        try {
            SQLiteDatabase sqLiteDatabase=this.openOrCreateDatabase("urundb",MODE_PRIVATE,null);
            Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM urunler",null);
            int idIx=cursor.getColumnIndex("id");
            int urunAdiIx=cursor.getColumnIndex("urunadi");
            int urunFiyatIx=cursor.getColumnIndex("urunfiyat");
            int urunAdetIx=cursor.getColumnIndex("urunadet");
            int resimIx=cursor.getColumnIndex("urunresim");
            while(cursor.moveToNext()){
                int id=cursor.getInt(idIx);
                String urunadi=cursor.getString(urunAdiIx);
                double urunFiyat=cursor.getDouble(urunFiyatIx);
                int urunAdet=cursor.getInt(urunAdetIx);
                byte[] bytes= cursor.getBlob(resimIx);
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                Urun urun=new Urun(id,urunadi,urunFiyat,urunAdet,bitmap);
                urunArrayList.add(urun);
            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.urun_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.urunEkle){
            Intent intent=new Intent(MainActivity.this,UrunActivity.class);
            intent.putExtra("durum",1);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}