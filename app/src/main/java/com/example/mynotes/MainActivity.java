package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.RenderProcessGoneDetail;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.FirestoreGrpc;
import com.google.firestore.v1.StructuredQuery;
import com.google.firestore.v1.UpdateDocumentRequest;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    // initializing variables

    int isEmpty = 0;  // 0 - empty, 1 - non_empty

    FrameLayout m_main_frame;

    TextView m_app_nam;
    RecyclerView mRecycler_view;
    ConstraintLayout m_row_item;
    FloatingActionButton mFAB;
    AppCompatButton mLogout_btn;

    ArrayList<Note> noteArrayList;

    FirestoreRecyclerAdapter<Note, NoteViewHolder> adapter;

    StaggeredGridLayoutManager staggeredGridLayoutManager;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        replaceFragment(new NotesList());
        getNotesCount(isEmpty);


        // binding all variables
        mLogout_btn = findViewById(R.id.Logout_btn_home);
        m_app_nam = findViewById(R.id.app_name);
        mRecycler_view = findViewById(R.id.Recycler_view);
//        m_row_item = findViewById(R.id.row_item);
        mFAB = findViewById(R.id.FAB_add);
        m_main_frame = findViewById(R.id.main_frame);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        noteArrayList = new ArrayList<>();

        mFAB.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(MainActivity.this, Edit_notes.class));

        });

        mLogout_btn.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), Login_page.class));
        });

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    public void noteEventListener() {

        //Query
        // setting up a query to retrieve all the notes from the FireStore
        Query query = firebaseFirestore.collection("Notes")
                .document(firebaseUser.getUid())
                .collection("UserSpecificNotes")
                .orderBy("createdDate", Query.Direction.DESCENDING);


        // options
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false);
                return new NoteViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note model) {

                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.createdDate.setText(model.getCreatedDate());

                getNotesCount(getItemCount());

                //  Log.e("DateSET",""+model.getCreatedDate());
                String docId = adapter.getSnapshots().getSnapshot(position).getId();
                holder.itemView.setOnClickListener(v -> {
                    // view details of the selected note item
                    Intent startUpdateNote = new Intent(getApplicationContext(), updateNote.class);
                    startUpdateNote.putExtra("title", model.getTitle());
                    startUpdateNote.putExtra("description", model.getDescription());
                    startUpdateNote.putExtra("createdDate", model.getCreatedDate());
                    startUpdateNote.putExtra("NoteId", docId);
//                    Log.d("note_id",docId);
                    Toast.makeText(getApplicationContext(), "Opening Note", Toast.LENGTH_SHORT).show();
                    finish();
                    v.getContext().startActivity(startUpdateNote);
                });
            }
        };

        mRecycler_view.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecycler_view.setLayoutManager(staggeredGridLayoutManager);
        mRecycler_view.setAdapter(adapter);


    }

    private void getNotesCount(int notesCount) {

        //todo if not empty

        if (notesCount > 0)
        {
            //todo if the list is not empty
            // should replace the main frame with the recycler view
            isEmpty = notesCount;
            Log.i("isEmptyTag", "not empty" + isEmpty );
            replaceFragment(new NotesList());
        }
        else
        {
            // todo if the list is empty
            //  should replace the main frame with the empty list page
            Log.i("isEmptyTag", "empty" + isEmpty );
            replaceFragment(new EmptyNotesList());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login_page.class));
        } else if (mAuth.getCurrentUser() != null) {
            noteEventListener();
            adapter.startListening();


            // load the appropriate fragment for notes list
//            loadNotesList();

        }
    }

//    private void loadNotesList() {
//
//        if ()
//
//    }

    @Override
    protected void onStop() {
//        adapter.stopListening();
        if (adapter != null) adapter.stopListening();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView createdDate;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_tv);
            description = itemView.findViewById(R.id.description_tv);
            createdDate = itemView.findViewById(R.id.created_date_tv);
        }
    }

}


//    private void noteEventListener() {
//
//        firebaseFirestore.collection("My Notes")
//                .document(firebaseUser.getUid())
//                .collection("UserSpecificNotes")
//                .orderBy("title")
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) {
//                        Log.e( "ErrorFirebase"," "+error);
//                        if (progressDialog.isShowing()) progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(),"Error in Loading Notes",Toast.LENGTH_SHORT);
//                         return;
//                    }
//                    else
//                    {
//                        assert value != null;
//                        for (DocumentChange dc : value.getDocumentChanges()){
//
//                            if (dc.getType() ==  DocumentChange.Type.ADDED ){
//                                noteArrayList.add(dc.getDocument().toObject(Note.class));
//                            }
//                            myAdapter.notifyDataSetChanged();
//                        }
//                        if (progressDialog.isShowing()) progressDialog.dismiss();
//                    }
//                });
//    }

