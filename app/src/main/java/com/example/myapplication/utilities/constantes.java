package com.example.myapplication.utilities;

import java.util.HashMap;
import java.util.HashSet;

public class constantes {
    public static final String KEY_COLLECTION_USERS ="users";
    public static final String KEY_NAME ="name";
    public static final String KEY_EMAIL="email";
    public static final String KEY_PASSWORD="password";
    public static final String KEY_PRE_NAME="chatapppref";
    public static final String KEY_IS_SIGNED_IN="issignedin";
    public static final String KEY_USER_ID="userid";
    public static final String KEY_IMAGE="image";
    public static final String KEY_FCM_TOKEN="fcmtoken";
    public static final String KEY_USER="user";
    public static final String KEY_COLLECTION_CHAT="chat";
    public static final String KEY_SENDER_ID="senderid";
    public static final String KEY_RECEIVER_ID="receiverid";
    public static final String KEY_MESSAGE="message";
    public static final String KEY_TIMESTAMP="timestamp";
    public static final String KEY_CONVERSATIONS="conversation";
    public static final String KEY_SENDER_NAME="sendername";
    public static final String KEY_RECEIVER_NAME="receivername";
    public static final String KEY_SENDER_IMAGE="senderimage";
    public static final String KEY_RECEIVER_IMAGE="receiverimage";
    public static final String KEY_LAST_MESSAGE="lastmessage";
    public static final String KEY_AVABILITY="avability";
    public static final String REMOTE_MSG_AUTHORISATION="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE="Content-Type";
    public static final String REMOTE_MSG_DATA="data";
    public static final String REMOTE_MSG_REGISTRATION_IDS="registration_ids";
    public static HashMap<String,String> remoteheaders=null;
    public static HashMap<String,String> getRemoteheaders(){
        if (remoteheaders==null){
            remoteheaders=new HashMap<>();
            remoteheaders.put(
                    REMOTE_MSG_AUTHORISATION,
                    "key=AAAAw_LxwGk:APA91bFw-5IG_K6XvYHspDP2BwhD_nn9NYtwqI_13AzIZWdWt7P3HS0drPX2uUWdIY2cyMFvDP7BO30DoYQ0wkqPqvBXM9gN0e0a_RkNd3VUI-GSirNUVwuryOBfn8ZZKNVvPbQ4wEhe"
            );
            remoteheaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteheaders;
    }
}
