package com.cemalettinaltintas.deneme;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.Manifest;
import android.widget.Toast;

import com.cemalettinaltintas.deneme.databinding.ActivityUrunBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UrunActivity extends AppCompatActivity {
    private ActivityUrunBinding binding;
    Bitmap selectedImage;
    SQLiteDatabase database;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUrunBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        registerLauncher();

        database=this.openOrCreateDatabase("urundb",MODE_PRIVATE,null);

        Singleton singleton=Singleton.getInstance();
        Urun selectedProduct=singleton.getSentUrun();
            binding.urunAdiText.setText(selectedProduct.getUrunadi());
            binding.urunAdetText.setText(String.valueOf(selectedProduct.getAdet()));
            binding.urunFiyatText.setText(String.valueOf(selectedProduct.getFiyat()));
            binding.imageView.setImageBitmap(selectedProduct.getResim());
            binding.button.setVisibility(View.INVISIBLE);
        Intent test=getIntent();
        int durum=test.getIntExtra("durum",0);

        if (durum==1){
            binding.urunAdiText.setText("");
            binding.urunAdetText.setText("");
            binding.urunFiyatText.setText("");
            binding.imageView.setImageResource(R.drawable.select_image);
            binding.button.setVisibility(View.VISIBLE);
        }



        //byte[] bytes=selectedProduct.getResim();
        /*
        Toast.makeText(this,singleton.getSentUrun().getUrunadi(),Toast.LENGTH_SHORT).show();
            int urunId=selectedProduct.getId();
            binding.button.setVisibility(View.INVISIBLE);
            try {
                Cursor cursor=database.rawQuery("SELECT * FROM urunler WHERE id=?",new String[]{String.valueOf(urunId)});
                int urunAdiIx=cursor.getColumnIndex("urunadi");
                int urunFiyatIx=cursor.getColumnIndex("urunfiyat");
                int urunAdetIx=cursor.getColumnIndex("urunadet");
                int resimIx=cursor.getColumnIndex("urunresim");
                while (cursor.moveToNext()){
                    binding.urunAdiText.setText(cursor.getString(urunAdiIx));
                    binding.urunFiyatText.setText(cursor.getString(urunFiyatIx));
                    binding.urunAdetText.setText(cursor.getString(urunAdetIx));
                    byte[] bytes= cursor.getBlob(resimIx);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();

            }catch (Exception e){
                e.printStackTrace();
            }

         */
    }

    public void selectImage(View view) {
        //Android 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                Uri imageData = intentFromResult.getData();
                                try {

                                    if (Build.VERSION.SDK_INT >= 28) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(UrunActivity.this.getContentResolver(),imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);
                                        binding.imageView.setImageBitmap(selectedImage);

                                    } else {
                                        selectedImage = MediaStore.Images.Media.getBitmap(UrunActivity.this.getContentResolver(),imageData);
                                        binding.imageView.setImageBitmap(selectedImage);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });


        permissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            //permission granted
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(intentToGallery);

                        } else {
                            //permission denied
                            Toast.makeText(UrunActivity.this,"Permisson needed!",Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    public Bitmap makeSmallerImage(Bitmap image,int maximumSize){
        int width=image.getWidth();
        int height=image.getHeight();
        float bitmapRatio=(float) width/(float)height;
        if (bitmapRatio>1){
            width=maximumSize;
            height=(int) (width/bitmapRatio);
        }else{
            height=maximumSize;
            width=(int) (height*bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    public void save(View view) {
        String urunAdi=binding.urunAdiText.getText().toString();
        double fiyat= Double.parseDouble(binding.urunFiyatText.getText().toString());
        int adet=Integer.parseInt(binding.urunAdetText.getText().toString());

        Bitmap smallImage=makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray=outputStream.toByteArray();
        try{
            database.execSQL("CREATE TABLE IF NOT EXISTS urunler " +
                    "(id INTEGER PRIMARY KEY,urunadi VARCHAR,urunfiyat DOUBLE,urunadet INT,urunresim BLOB)");
            String sqlString="INSERT INTO urunler(urunadi,urunfiyat,urunadet,urunresim) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,urunAdi);
            sqLiteStatement.bindDouble(2,fiyat);
            sqLiteStatement.bindLong(3,adet);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intent=new Intent(UrunActivity.this,MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}