package com.example.inclass12;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactsActivity extends AppCompatActivity implements OnItemListener {

    private Button btn_create_contact;
    private ImageView iv_logout;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<Contact> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        setTitle("Contacts");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btn_create_contact = findViewById(R.id.btn_create_contact);
        iv_logout = findViewById(R.id.iv_logout);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ContactAdapter(data, this);
        recyclerView.setAdapter(mAdapter);

        CollectionReference colRef = db.collection("users").document(mAuth.getUid()).collection("contacts");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("demo", "listen:error", e);
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            Log.d("demo", "New contact: " + dc.getDocument().getData());

                            HashMap hashMap = (HashMap) dc.getDocument().getData();

                            Contact contact = new Contact();
                            contact.name = (String) hashMap.get("name");
                            contact.email = (String) hashMap.get("email");
                            contact.phone = (String) hashMap.get("phone");
                            contact.id = (String) hashMap.get("id");
                            contact.img_url = (String) hashMap.get("img_url");

                            data.add(contact);
                            mAdapter.notifyItemInserted(data.size()-1);

                            break;
                        case MODIFIED:
                            Log.d("demo", "Modified contact: " + dc.getDocument().getData());
                            break;
                        case REMOVED:
                            Log.d("demo", "Removed contact: " + dc.getDocument().getData());
                            break;
                    }
                }

            }
        });

//        CollectionReference colRef = db.collection("users").document(mAuth.getUid()).collection("contacts");
//        colRef.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            data.clear();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("demo", document.getId() + " => " + document.getData());
//                                try {
////                                    Contact contact = gson.fromJson(document.getData().toString(), Contact.class);
//                                    HashMap hashMap = (HashMap) document.getData();
//
//                                    Contact contact = new Contact();
//                                    contact.name = (String) hashMap.get("name");
//                                    contact.email = (String) hashMap.get("email");
//                                    contact.phone = (String) hashMap.get("phone");
//                                    contact.id = (String) hashMap.get("id");
//                                    contact.img_url = (String) hashMap.get("img_url");
//                                    data.add(contact);
//
//                                } catch (JsonSyntaxException e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d("demo", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });

        btn_create_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsActivity.this, NewContactActivity.class);
                startActivity(intent);
            }
        });

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    @Override
    public void onItemClick(int pos) {
        Log.d("demo", "onItemClick: clicked " + pos);
    }

    @Override
    public void onItemLongPress(final int pos) {
        Contact contact = data.get(pos);

        db.collection("users").document(mAuth.getUid()).collection("contacts")
                .document(contact.id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //mData.remove(p);
                        data.remove(pos);
                        mAdapter.notifyItemRemoved(pos);
                        Toast.makeText(ContactsActivity.this, "Contact Deleted", Toast.LENGTH_SHORT).show();
                        Log.d("demo", "Delete Successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ContactsActivity.this, "Could not delete Contact", Toast.LENGTH_SHORT).show();
                        Log.d("demo", "Delete Unsuccessful " + e.getMessage());
                    }
                });
//        mAdapter.notifyDataSetChanged();
        Log.d("demo", "onItemLongPress: long pressed " + pos);
    }


}
