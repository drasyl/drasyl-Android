package org.drasyl.android.chat.models;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import org.drasyl.util.Murmur3;

import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        return new String(Murmur3.murmur3_x86_32BytesLE(message.substring(0, Math.min(15, message.length())).getBytes()), UTF_8);
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
