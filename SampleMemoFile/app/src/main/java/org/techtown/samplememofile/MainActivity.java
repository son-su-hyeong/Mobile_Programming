package org.techtown.samplememofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText contentView;
    Button btn;

    boolean fileReadPermission;
    boolean fileWritePermission;

    boolean externalStorageReadable;
    boolean externalStorageWritable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentView=findViewById(R.id.content);
        btn=findViewById(R.id.btn);
        btn.setOnClickListener(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
            fileReadPermission=true;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED){
            fileWritePermission=true;
        }

        if(!fileReadPermission || !fileWritePermission){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }

        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            if(state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
                externalStorageReadable = true;
                externalStorageWritable = false;
            }
            else{
                externalStorageReadable = externalStorageWritable = true;
            }
        }
        else{
            externalStorageReadable = externalStorageWritable = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200 && grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                fileReadPermission=true;
            if(grantResults[1]== PackageManager.PERMISSION_GRANTED)
                fileWritePermission=true;
        }
    }

    @Override
    public void onClick(View v) {
        String content=contentView.getText().toString();
        if(!(externalStorageReadable && externalStorageWritable)){
            showToast("외부 스토리지 장치 읽기 또는 쓰기를 할수 없습니다.");
        }
        else if(fileReadPermission && fileWritePermission){
            FileWriter writer;
            try{
                String dirPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/myApp";
                File dir=new File(dirPath);
                if(!dir.exists()){
                    dir.mkdir();
                }
                File file=new File(dir+"/myfile.txt");
                if(!file.exists()){
                    file.createNewFile();
                }
                writer=new FileWriter(file, true);
                writer.write(content);
                writer.flush();
                writer.close();

                Intent intent=new Intent(this, ReadFileActivity.class);
                startActivity(intent);

            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            showToast("permission 이 부여 안되어 기능 실행이 안됩니다.");
        }
    }

    private void showToast(String message){
        Toast toast=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
