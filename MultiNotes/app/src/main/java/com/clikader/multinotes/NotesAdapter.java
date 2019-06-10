package com.clikader.multinotes;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private static final String TAG = "NotesAdapter";
    private List<Note> adaNList;
    private MainActivity mainAct;

    public NotesAdapter(List<Note> nList, MainActivity ma) {
        mainAct = ma;
        adaNList = nList;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW HOLDER HERE HERE HERE HERE HERE!!!!!!!");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = adaNList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        holder.lastModify.setText(note.getLastmodify());
    }

    @Override
    public int getItemCount() {
        return adaNList.size();
    }
}
