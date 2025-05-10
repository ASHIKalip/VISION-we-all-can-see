package org.tensorflow.lite.examples.detection.Message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;

public class MessageListAdapter extends ArrayAdapter<Message> {
    private final Context ctx;
    public ArrayList<Message> messageListArray;
    @SuppressLint("StaticFieldLeak")
    static Holder holder;

    public MessageListAdapter(Context context, int textViewResourceId,
                              ArrayList<Message> messageListArray) {
        super(context, textViewResourceId);
        this.messageListArray = messageListArray;
        this.ctx = context;

    }


    @SuppressLint({"InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View convertView1 = convertView;
        if (convertView1 == null) {
            holder = new Holder();
            LayoutInflater vi = (LayoutInflater) ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView1 = vi.inflate(R.layout.message_list_item, null);
            holder.messageTo = (TextView) convertView1.findViewById(R.id.txt_msgTO);
            holder.messageContent = (TextView) convertView1.findViewById(R.id.txt_messageContent);

            convertView1.setTag(holder);
        } else {
            holder = (Holder) convertView1.getTag();
        }
        Message message = getItem(position);

        if (message != null && message.messageNumber != null && message.messageContent != null) {
            holder.messageTo.setText(message.messageNumber + " : ");
            holder.messageContent.setText(message.messageContent);
            notifyDataSetChanged();
            setArrayList(messageListArray);

        }


        return convertView1;
    }

    @Override
    public int getCount() {
        return messageListArray.size();
    }

    @Override
    public Message getItem(int position) {
        return messageListArray.get(position);
    }

    public void setArrayList(ArrayList<Message> messageList) {
        this.messageListArray = messageList;
        notifyDataSetChanged();
    }

    private static class Holder {
        public TextView messageTo, messageContent;
    }

}
