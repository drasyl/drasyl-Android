package org.drasyl.android.chat.models;

import com.stfalcon.chatkit.commons.models.IUser;

public class User implements IUser {
    private static final String ROBOHASH = "https://robohash.org/";
    private final String name;
    private final String avatar;

    public User(final String name) {
        this.name = name;
        this.avatar = ROBOHASH + name;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}
