package munoz.pablo.directorio.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import munoz.pablo.directorio.R;
import munoz.pablo.directorio.models.Conversation;

/**
 * Created by pablo on 4/12/2017.
 */

public class ConversationAdapter extends BaseAdapter {
    private ArrayList<Conversation> conversationList;
    private Activity activity;

    public ConversationAdapter(ArrayList<Conversation> conversationList, Activity activity) {
        this.conversationList = conversationList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public Object getItem(int position) {
        return conversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.item_conversation, parent, false);
        }

        TextView conversationIdentifierTv = (TextView) convertView.findViewById(R.id.item_conversation_identifier);

        conversationIdentifierTv.setText(conversationList.get(position).getId());

        return convertView;
    }
}
