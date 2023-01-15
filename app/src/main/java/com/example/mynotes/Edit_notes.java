package com.example.mynotes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Edit_notes extends AppCompatActivity {

    // initialising variables

    FrameLayout color_1, color_2, color_3, color_4, color_5, color_6;

    private EditText mDescription_ET;
    AppCompatEditText mTitle_ET;
    TextView mActualEditedDate;
    ImageView mBack_btn_Iv;
    ImageButton mColor_note_IV, mSave_btn_IV, mCopyNote_IV;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;

    ProgressBar mProgressBarOfSaveNote;

    String newDate;

    Boolean isDataInserted = false;

    private Date updatedDate;
    private SimpleDateFormat reFormatter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        // binding variables
        mTitle_ET = findViewById(R.id.Title_ET);
        mDescription_ET = findViewById(R.id.Description_ET);
        mBack_btn_Iv = findViewById(R.id.Back_btn_IV);
        mColor_note_IV = findViewById(R.id.color_note_IV);
        mSave_btn_IV = findViewById(R.id.save_btn_IV);
        mCopyNote_IV = findViewById(R.id.copy_btn_IV);
        mActualEditedDate = findViewById(R.id.ActualEditedDate);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        mProgressBarOfSaveNote = findViewById(R.id.ProgressBarOfLogin);

        Calendar calendar = Calendar.getInstance();
        updatedDate = calendar.getTime();  // getting date
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            reFormatter = new SimpleDateFormat("LLL dd, YYYY");
        }
        newDate = reFormatter.format(updatedDate);
        Log.e("today's date ",newDate);


        //  saving data
        mBack_btn_Iv.setOnClickListener(v -> {
            insertData();
        });

        mSave_btn_IV.setOnClickListener(v -> {
            insertData();
        });


        // for copying note text

        mCopyNote_IV.setOnClickListener(v -> {

            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(mTitle_ET.getText()+ ": EditText",mTitle_ET.getText().toString() + mDescription_ET.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();

        });

        // for picking or coloring a note

        mColor_note_IV.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.color_picker);

            color_1 = dialog.findViewById(R.id.color_1);
            color_2 = dialog.findViewById(R.id.color_2);
            color_3 = dialog.findViewById(R.id.color_3);
            color_4 = dialog.findViewById(R.id.color_4);
            color_5 = dialog.findViewById(R.id.color_5);
            color_6= dialog.findViewById(R.id.color_6);

            color_1.setOnClickListener(v1 -> {
                dialog.dismiss();
                Toast.makeText(this, "color_1", Toast.LENGTH_SHORT).show();
            });
            color_2.setOnClickListener(v2 -> {
                dialog.dismiss();
                Toast.makeText(this, "color_2", Toast.LENGTH_SHORT).show();
            });
            color_3.setOnClickListener(v3 -> {
                dialog.dismiss();
                Toast.makeText(this, "color_3", Toast.LENGTH_SHORT).show();
            });
            color_4.setOnClickListener(v4 -> {
                dialog.dismiss();
                Toast.makeText(this, "color_4", Toast.LENGTH_SHORT).show();
            });
            color_5.setOnClickListener(v5 -> {
                dialog.dismiss();
                Toast.makeText(this, "color_5", Toast.LENGTH_SHORT).show();
            });
            color_6.setOnClickListener(v6 -> {
                dialog.dismiss();
                Toast.makeText(this, "color_6", Toast.LENGTH_SHORT).show();
            });

            dialog.show();
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setGravity(Gravity.END);
//            dialog.getWindow().
        });



    }

    private void insertData() {

        String title = mTitle_ET.getText().toString();
        String description = mDescription_ET.getText().toString();
        Log.e("created notes date ", newDate);
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) {
            // discard the note
            Toast.makeText(this, "Empty Note Discarded", Toast.LENGTH_SHORT).show();
        } else {
            // code to set the non empty title and description
            // code if title and description exist

            if (TextUtils.isEmpty(title))
            {
                title += "Miscellaneous";
            }
            Toast.makeText(this, "Note Saved", Toast.LENGTH_SHORT).show();

            DocumentReference documentReference = firebaseFirestore
                    .collection("Notes")
                    .document(firebaseUser.getUid())
                    .collection("UserSpecificNotes")
                    .document();

            Map<String, Object> note = new HashMap<>();
            note.put("title", title);
            note.put("description", description);
            note.put("createdDate", newDate);
            Log.e("created date", newDate);

            documentReference.set(note)
                    .addOnCompleteListener(task -> {
                        isDataInserted = true;
                        if (task.isSuccessful()) {
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Note not saved " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        finish();
        startActivity(new Intent(Edit_notes.this,MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        insertData();
        }

    @Override
    protected void onStart() {
        super.onStart();
        mActualEditedDate.setText(newDate);
    }
}








//
//
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        calendar = Calendar.getInstance();
//        todayDate = dateFormat.format(calendar);
//        date = dateFormat.format(calendar.getTime());
//        mActualEditedDate.setText(date);
////        time = timeFormat.format("");
//        // if the edited date is today show time otherwise (not today ) show the date
//
//        if(date.equals(""))
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                dateFormat = new SimpleDateFormat("dd MM YYYY");
//                timeFormat = new SimpleDateFormat("HH:mm");
//            }
//
//
//
//        // String DateAndTime;
//        // mActualEditedDate.setText(DateAndTime);
//
//    }
//
//    private Note reset_save() {
//
//        String title = mTitle_ET.getText().toString();
//        String description = mDescription_ET.getText().toString();
//
//        // code for resetting or saving
//
//        // 1. code for resetting title if the title does not exit
//        if (title.isEmpty())   mTitle_ET.setText("No title");
//            // 2.code for resetting or overriding if the note is empty string
//        else if (description.isEmpty()) mDescription_ET.setHint("Note");
//
//        //  code for saving the Note
//
//        return
//    }


