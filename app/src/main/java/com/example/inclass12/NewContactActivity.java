package com.example.inclass12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class NewContactActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView iv_camera;

    private EditText et_name;
    private EditText et_email;
    private EditText et_phone;

    private Button btn_submit;

    Bitmap bitmap;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef;

    DocumentReference contact_id;
    String contact_img_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        setTitle("Create New Contact");

        iv_camera = findViewById(R.id.iv_camera);

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);

        btn_submit = findViewById(R.id.btn_submit);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_submit.setClickable(false);
                btn_submit.setAlpha(0.5f);

                String name = et_name.getText().toString();
                String email = et_email.getText().toString();
                String phone = et_phone.getText().toString();

                if (!name.equals("") && !phone.equals("") && Pattern.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", name);
                    data.put("email", email);
                    data.put("phone", phone);
                    data.put("id", "");
                    data.put("img_url", "");

                    db.collection("users").document(mAuth.getUid()).collection("contacts")
                            .add(data)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(final DocumentReference documentReference) {
                                    Log.d("demo", "DocumentSnapshot written with ID: " + documentReference.getId());

                                    contact_id = documentReference;
                                    Map<String,Object> updates_id = new HashMap<>();
                                    updates_id.put("id", contact_id.getId());

                                    contact_id.update(updates_id)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("demo", "DocumentSnapshot ka 'id' successfully updated!");

                                                    Intent intent = new Intent(NewContactActivity.this, ContactsActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("demo", "Error updating document ka IIIDDDD", e);
                                                }
                                            });

                                    if (iv_camera.getDrawable() instanceof BitmapDrawable) {
                                        Log.d("demo", "if camera se kheecha to andar");

                                        final StorageReference imageStorageRef = storageRef.child("images/" + mAuth.getUid() + "/" + contact_id.getId() + ".jpg");
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] data = baos.toByteArray();
                                        UploadTask uploadTask = imageStorageRef.putBytes(data);

                                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return imageStorageRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();
                                                    Log.d("demo", "Image URI: " + downloadUri);

                                                    contact_img_url = String.valueOf(downloadUri);

                                                    Map<String,Object> updates_img_url = new HashMap<>();
                                                    updates_img_url.put("img_url", contact_img_url);

                                                    documentReference.update(updates_img_url)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d("demo", "DocumentSnapshot ka image url successfully updated!");

                                                                    Intent intent = new Intent(NewContactActivity.this, ContactsActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w("demo", "Error updating document ka image url", e);
                                                                }
                                                            });

                                                } else {
                                                    Log.d("demo", "in else, something went wrong!");
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(NewContactActivity.this, "Contact will be saved with default image", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("demo", "Error adding document", e);
                                }
                            });




                } else {

                    btn_submit.setClickable(true);
                    btn_submit.setAlpha(1);

                    if (name.equals("")) {

                        et_name.setError("Enter a name");
                    }

                    if (!Pattern.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", email)) {
                        et_email.setError("Enter an valid email");
                    }

                    if (phone.equals("")) {
                        et_phone.setError("Enter a phone number");
                    }
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            iv_camera.setImageBitmap(bitmap);
            final float scale = getResources().getDisplayMetrics().density;
            iv_camera.getLayoutParams().height = (int) (128 * scale);
            iv_camera.getLayoutParams().width = (int) (128 * scale);
//            isTakenPhoto = true;
        }
    }
}
