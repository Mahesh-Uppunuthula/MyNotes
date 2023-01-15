package com.example.mynotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class updateNote extends AppCompatActivity {

    private TextView mActualEditedDate;
    private AppCompatEditText mUN_Title_ET;
    private EditText mUN_Description_ET;

    private ImageButton mBack_btn_IV, mDelete_btn_IV, mCopy_IV;

    Intent getDataIntent;

    private Date updatedDate;
    private SimpleDateFormat reFormatter;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;

    String passedNoteId;
    private static boolean isDataInserted = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);

        getDataIntent = getIntent();

        mUN_Title_ET = findViewById(R.id.UN_Title_ET);
        mUN_Description_ET = findViewById(R.id.UN_Description_ET);

        mActualEditedDate = findViewById(R.id.ActualEditedDate);

        mDelete_btn_IV = findViewById(R.id.Delete_btn_IV);

        mCopy_IV = findViewById(R.id.copy_btn_IV);

        mBack_btn_IV = findViewById(R.id.Back_btn_IV);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Calendar calendar = Calendar.getInstance();
        updatedDate = calendar.getTime();  // getting date
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            reFormatter = new SimpleDateFormat("LLL dd, YYYY");
        }

        mUN_Title_ET.setText(getDataIntent.getStringExtra("title"));
        mUN_Description_ET.setText(getDataIntent.getStringExtra("description"));
        mActualEditedDate.setText(getDataIntent.getStringExtra("createdDate"));
        passedNoteId = getDataIntent.getStringExtra("NoteId");

        // saving the updated note when clicked back
        mBack_btn_IV.setOnClickListener(v -> {
            insertData();
        });

        // for copying note text

        mCopy_IV.setOnClickListener(v -> {

            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(mUN_Title_ET.getText()+ ": EditText", "~Title: "+ mUN_Title_ET.getText().toString() + " \n\n~Note: \n"+mUN_Description_ET.getText().toString() + "\n\n~Created on: "+mActualEditedDate.getText().toString()+".");
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();

        });

        mDelete_btn_IV.setOnClickListener(v -> {
            DocumentReference documentReference = firebaseFirestore.collection("Notes")
                    .document(firebaseUser.getUid())
                    .collection("UserSpecificNotes")
                    .document(passedNoteId);
            documentReference.delete()
                    .addOnCompleteListener(task -> {
                        Log.e("passed id",passedNoteId);
                        if (task.isSuccessful())
                            Toast.makeText(this, "Note Discarded", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to deleted: "+e, Toast.LENGTH_SHORT).show();
                    });
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        });
    }

//    to insert the data into the database
    private void insertData() {

        String newTitle = mUN_Title_ET.getText().toString();
        String newDescription = mUN_Description_ET .getText().toString();
        String newDate = reFormatter.format(updatedDate);

        if (TextUtils.isEmpty(newTitle) && TextUtils.isEmpty(newDescription)){

            // discard the note

        }
        else if (TextUtils.isEmpty(newTitle)){

            // if title is empty set the title as other
        }
        else if (TextUtils.isEmpty(newDescription)) {
            //  set the description as empty as string

        }
        else{
            DocumentReference documentReference = firebaseFirestore
                    .collection("Notes")
                    .document(firebaseUser.getUid())
                    .collection("UserSpecificNotes")
                    .document(getDataIntent.getStringExtra("NoteId"));
            Log.e("doc Id",passedNoteId);

            Map<String,Object> note = new HashMap<>();
            note.put("title",newTitle);
            note.put("description",newDescription);
            note.put("createdDate",newDate);
            documentReference.set(note)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            isDataInserted = true;
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        finish();
        startActivity(new Intent(updateNote.this,MainActivity.class));
        Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressed() {
        insertData();
        super.onBackPressed();

    }
}