package org.drasyl.android.chat.models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import org.drasyl.crypto.Hashing;

import java.util.Date;

public class MessageWrapper implements IMessage {
    private final String message;
    private final Date date;
    private final User user;

    public MessageWrapper(final String message, final String user) {
        this.message = message;
        this.date = new Date();
        this.user = new User(user);
    }

    @Override
    public String getId() {
        return Hashing.murmur3x32Hex(message.substring(0, Math.min(15, message.length())));
    }

    @Override
    public String getText() {
        return message;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return date;
    }
}
