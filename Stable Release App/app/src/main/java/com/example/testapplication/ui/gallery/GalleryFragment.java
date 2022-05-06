package com.example.testapplication.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.testapplication.R;
import com.example.testapplication.databinding.FragmentHomeBinding;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.testapplication.databinding.FragmentGalleryBinding;
import com.example.testapplication.ui.home.HomeFragment;

import org.pytorch.Module;

public class GalleryFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, OnClickListener {

    private static final int RESULT_OK = -1;
    private GalleryViewModel galleryViewModel;
    private FragmentGalleryBinding binding;


    public static float[] TORCHVISION_NORM_MEAN_RGB_RANZ = new float[] {0.5f, 0.5f, 0.5f};
    public static float[] TORCHVISION_NORM_STD_RGB_RANZ = new float[] {0.5f, 0.5f, 0.5f};
    public static float[] result;
    // public static float[] TORCHVISION_RANZ_TEST = new float[] {0.5f, 0.5f, 0.5f,0.5f, 0.5f, 0.5f,0.5f, 0.5f, 0.5f,-0.5f, -0.5f, -0.5f,-0.5f, -0.5f, -0.5f,-0.5f, -0.5f, -0.5f};
    public float[] TORCHVISION_RANZ_TEST = new float[] {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};
    Bitmap bitmap = null;
    Module module = null;
    Module enc = null;
    Module enc4 = null;
    Module enc8 = null;
    Boolean start = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       /* final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/


        Button btnPic, btnUpload;

        btnPic = binding.buttonPic;
        btnUpload = binding.buttonUpload;


        btnUpload.setOnClickListener(this);

        //Button pic handler
        btnPic.setOnClickListener(new OnClickListener() {

             @Override
             public void onClick(View view) {
                     Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                     startActivityForResult(takePicture, 0);//zero can be replaced with any action code


              }
            });

        //Button upload from gallery handler
        btnUpload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });







        return root;
    }



    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:

                System.out.println("dentro: " + resultCode);
                if(resultCode == RESULT_OK){
                    System.out.println("fuori");
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    binding.image.setImageBitmap(imageBitmap);


                    try {
                        // creating bitmap from packaged into app android asset 'image_wolf.jpg',
                        // app/src/main/assets/image_wolf.jpg
                        //bitmap = BitmapFactory.decodeStream(getContext().getAssets().open("150619.jpg"));
                        // loading serialized torchscript module from packaged into app android asset model.pt,
                        // app/src/model/assets/model.pt
                        //Log.e("test", "test");
                        //module = LiteModuleLoader.load(assetFilePath(this, "model.pt"));

                        //Loading module for Im2Im



                        module = LiteModuleLoader.load(assetFilePath(this, "model_Im2Im_lite.pt"));
                        enc = LiteModuleLoader.load(assetFilePath(this, "model_Im2Im_enc_lite.pt"));
                        enc4 = LiteModuleLoader.load(assetFilePath(this, "model_Im2Im_enc4_lite.pt"));
                        enc8 = LiteModuleLoader.load(assetFilePath(this, "model_Im2Im_enc8_lite.pt"));
                    } catch (IOException e) {
                        Log.e("PytorchHelloWorld", "Error reading assets", e);

                    }


                    // showing image on UI
                    ImageView imageView = binding.image;//findViewById(R.id.image);
                    //imageView.setImageBitmap(bitmap);

                    //devo fare resize bitmap per dimensione 128x128
                    bitmap = Bitmap.createScaledBitmap(imageBitmap, 128, 128, true);

                    imageView = binding.image;
                    imageView.setImageBitmap(bitmap);


                    // preparing input tensor for Uploaded Image
                    Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                            TORCHVISION_NORM_MEAN_RGB_RANZ, TORCHVISION_NORM_STD_RGB_RANZ);

                    // running the model
                    final Tensor encTensor = enc.forward(IValue.from(inputTensor)).toTensor();
                    final Tensor enc4Tensor = enc4.forward(IValue.from(inputTensor)).toTensor();
                    final Tensor enc8Tensor = enc8.forward(IValue.from(inputTensor)).toTensor();

                    //final Tensor encConcTensor =


                    float[] encFloat = encTensor.getDataAsFloatArray() ;
                    float[] enc4Float = enc4Tensor.getDataAsFloatArray() ;
                    float[] enc8Float = enc8Tensor.getDataAsFloatArray() ;

                    int encLength = encFloat.length;
                    int enc4Length = enc4Float.length;
                    int enc8Length = enc8Float.length;

                    result = new float[encLength + enc4Length + enc8Length];  //resultant array of size first array and second array
                    System.arraycopy(encFloat, 0, result, 0, encLength);
                    System.arraycopy(enc4Float, 0 , result , encLength, enc4Length);
                    System.arraycopy(enc8Float, 0 , result , (encLength + enc4Length), enc8Length);
                    System.out.println(" arrayFloat : " + Arrays.toString(result));

                    final long[] shape = new long[]{1, result.length};

                    Tensor vetTensor = Tensor.fromBlob(result, shape);

                    SeekBar seekBar1,seekBar2,seekBar3,seekBar4,seekBar5,seekBar6,seekBar7,seekBar8,seekBar9,seekBar10,seekBar11,seekBar12,seekBar13,seekBar14,seekBar15,seekBar16,seekBar17,seekBar18;


                    seekBar1 = binding.seekBar1;
                    seekBar2 = binding.seekBar2;
                    seekBar3 = binding.seekBar3;
                    seekBar4 = binding.seekBar4;
                    seekBar5 = binding.seekBar5;
                    seekBar6 = binding.seekBar6;
                    seekBar7 = binding.seekBar7;
                    seekBar8 = binding.seekBar8;
                    seekBar9 = binding.seekBar9;
                    seekBar10 = binding.seekBar10;
                    seekBar11 = binding.seekBar11;
                    seekBar12 = binding.seekBar12;
                    seekBar13 = binding.seekBar13;
                    seekBar14 = binding.seekBar14;
                    seekBar15 = binding.seekBar15;
                    seekBar16 = binding.seekBar16;
                    seekBar17 = binding.seekBar17;
                    seekBar18 = binding.seekBar18;


                    //Gestione SeekBar Completo

                    SeekBar[] seekBarVet = {seekBar1,seekBar2,seekBar3,seekBar4,seekBar5,seekBar6,seekBar7,seekBar8,seekBar9,seekBar10,seekBar11,seekBar12,seekBar13,seekBar14,seekBar15,seekBar16,seekBar17,seekBar18};



                    for(int i = 0; i < seekBarVet.length; i++){
                        seekBarVet[i].setId(2000 + i);

                        seekBarVet[i].setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) this);
                        seekBarVet[i].setMax(10);

                        if(result[i] >= 0.5)
                            seekBarVet[i].setProgress(10);
                        else if(result[i] <= -0.5)
                            seekBarVet[i].setProgress(0);
                        else if(result[i] < 0.0 && result[i] > -0.5) {
                            int app = (int)(result[i] * 10) * -1;
                            seekBarVet[i].setProgress(app);
                        }
                        else if(result[i] > 0.0 && result[i] < 0.5) {
                            int app = (int)(result[i] * 10) + 5;
                            seekBarVet[i].setProgress(app);
                        }
                        System.out.println("SET ID " + i + " : " + seekBarVet[i].getId() + "  Value  " + seekBarVet[i].getProgress());
                    }



                    start = true;
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    binding.image.setImageURI(selectedImage);
                }
                break;
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

        if(start == true) {



            // showing image on UI
            ImageView imageView = binding.image;
            //imageView.setImageBitmap(bitmap);

            //devo fare resize bitmap per dimensione 128x128
            bitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true);

            imageView.setImageBitmap(bitmap);


            int id = seekBar.getId();
            System.out.println("Seekbar click: " + id);

            int sliderId = id - 2000;
            int realValue = seekBar.getProgress() - 5;

            float positiveValue = Math.abs(realValue);


            float signedValue = (float) (positiveValue / 10);

            System.out.println("Real Value: " + realValue + " positiveValue: " + positiveValue + " Valore: " + signedValue);

            if (realValue < 0)
                signedValue = -signedValue;


            Toast.makeText(getContext().getApplicationContext(), "seekbar progress: " + realValue, Toast.LENGTH_SHORT).show();

            Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                    TORCHVISION_NORM_MEAN_RGB_RANZ, TORCHVISION_NORM_STD_RGB_RANZ);

            //corretto float to tensor
            final long[] shape = new long[]{1, TORCHVISION_RANZ_TEST.length};


            TORCHVISION_RANZ_TEST[sliderId] = signedValue;

            System.out.println("Valore: " + signedValue);


            Tensor vetTensor = Tensor.fromBlob(TORCHVISION_RANZ_TEST, shape);


            for (int i = 0; i < TORCHVISION_RANZ_TEST.length; i++) {
                System.out.println(TORCHVISION_RANZ_TEST[i]);
            }
            final Tensor outputTensor = module.forward(IValue.from(inputTensor), IValue.from(vetTensor)).toTensor();


            // Converting output Tensor into Float and after into Bitmap image

            float[] array1 = outputTensor.getDataAsFloatArray();

            List<Float> floatArray = new ArrayList<Float>();

            for (int i = 0; i < array1.length; i++) {
                floatArray.add(array1[i]);
            }

            int width = 128;
            int height = 128;
            Bitmap outBitmap = arrayFlotToBitmap(floatArray, width, height);


            // showing Im2Im image on UI

            imageView.setImageBitmap(outBitmap);

            Toast.makeText(getContext().getApplicationContext(), "image Changed", Toast.LENGTH_SHORT).show();

        }
    }


    public void onStartTrackingTouch(SeekBar seekBar) {
        //Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        //Toast.makeText(getApplicationContext(),"seekbar touch stopped!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Copies specified asset to the file in /files app directory and returns this file absolute path.
     *
     * @return absolute file path
     */

    public String assetFilePath(GalleryFragment context, String assetName) throws IOException {
        File file = new File(getContext().getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = getContext().getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }


    private Bitmap arrayFlotToBitmap(List<Float> floatArray, int width, int height){
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        int pixels[] = new int[width * height * 4];

        float Maximum = Collections.max(floatArray);
        float minmum = Collections.min(floatArray);
        float delta = Maximum - minmum ;

        for (int i=0; i<width*height; i++){
            int r = ((int) ((((floatArray.get(i) - minmum) / delta) * 255)));
            int g = ((int) ((((floatArray.get(i + width*height) - minmum) / delta) * 255)));
            int b = ((int) ((((floatArray.get(i + 2 * width*height) - minmum) / delta) * 255)));
            pixels[i] = Color.argb(255, r, g, b);

        }
        bmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmp ;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View view) {

    }
}