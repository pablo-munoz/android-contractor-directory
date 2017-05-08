package munoz.pablo.directorio.models;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by pablo on 4/12/2017.
 */

public class Conversation {

    private String id;
    private Date lastMessageDate;
    public List<String> interlocutorIdList;
    public JSONObject interlocutorData;
    private List<Message> messageList;

    public Conversation(String id, Date lastMessageDate, List<String> interlocutorIdList, List<Message> messageList, JSONObject interlocutorData) {
        this.id = id;
        this.lastMessageDate = lastMessageDate;
        this.interlocutorIdList = interlocutorIdList;
        this.interlocutorData = interlocutorData;
        this.messageList = messageList;
    }

    public String getId() {
        return id;
    }

}
