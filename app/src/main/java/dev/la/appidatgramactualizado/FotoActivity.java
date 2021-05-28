package dev.la.appidatgramactualizado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dev.la.appidatgramactualizado.databinding.ActivityFotoBinding;

public class FotoActivity extends AppCompatActivity {


    private static final int CAMERA_REQUEST= 1880;
    String mRutaFotoActual;
    ActivityFotoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnTomarFoto.setOnClickListener(view -> {
            if(PermisoEscrituraAlmacenamiento()){
                IntencionTomarFoto();
            }else{
                requestStoragePermission();
            }
        });
    }




    private boolean PermisoEscrituraAlmacenamiento(){
        //Se obtiene si la aplicación tiene el permiso solicitado.
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean exito = false;
        if(result == PackageManager.PERMISSION_GRANTED){
            exito = true;
        }
        return exito;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission
                        .WRITE_EXTERNAL_STORAGE},
                0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0){
            if(grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(FotoActivity.this,
                        "Permiso ACEPTADO",
                        Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(FotoActivity.this,
                        "Permiso DENEGADO",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat(
                "yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mRutaFotoActual = image.getAbsolutePath();
        return image;
    }
    private void grabarFotoGaleria(){
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mRutaFotoActual);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Uri contentUri = FileProvider.getUriForFile(
                    getApplicationContext(),
                    "dev.la.appidatgramactualizado.provider",
                    f
            );
            mediaScanIntent.setData(contentUri);
        }else{
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
        }
        this.sendBroadcast(mediaScanIntent);
    }

    private void mostrarFoto(){
        //Declaramos y obtenemos las dimensiones del ImageView
        int targetW = binding.imgFoto.getWidth();
        int targetH = binding.imgFoto.getHeight();

        BitmapFactory.Options bmOption = new BitmapFactory.Options();
        bmOption.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mRutaFotoActual, bmOption);
        int photoW = bmOption.outWidth;
        int photoH = bmOption.outHeight;
        //Determinar la escala de la imagen
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decodificar el archivo en un mapa de bits al tamaño del
        // ImageView
        bmOption.inJustDecodeBounds = false;
        bmOption.inSampleSize = scaleFactor;
        bmOption.inPurgeable = true;
        // Asignar el archivo de la imagen en el imageview
        Bitmap bitmap = BitmapFactory.decodeFile(mRutaFotoActual,
                bmOption);
        binding.imgFoto.setImageBitmap(bitmap);
    }

    private void IntencionTomarFoto(){
        Intent takePictureIntent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Preguntamos si existe una actividad de cámara para manejar
        //el intent.
        if(takePictureIntent.resolveActivity(getPackageManager())
                != null){
            File photoFile = null;
            try {
                photoFile = crearArchivoImagen();
            }catch (IOException ex){

            }
            //Validamos si el archivo fue creado correctamente
            if(photoFile != null){
                Uri phoURI = FileProvider.getUriForFile(
                        this,
                        "dev.la.appidatgramactualizado.provider",
                        photoFile
                );
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT
                        , phoURI);
                startActivityForResult(takePictureIntent,
                        CAMERA_REQUEST);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAMERA_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                grabarFotoGaleria();
                mostrarFoto();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}