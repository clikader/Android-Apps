package com.clikader.multinotes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class NoteViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView content;
    public TextView lastModify;

    public NoteViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.noteTitle);
        content = (TextView) view.findViewById(R.id.noteContent);
        lastModify = (TextView) view.findViewById(R.id.noteLastmodify);
    }
}
