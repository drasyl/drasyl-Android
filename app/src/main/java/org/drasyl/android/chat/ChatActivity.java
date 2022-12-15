package org.drasyl.android.chat;

import static org.drasyl.identity.Identity.POW_DIFFICULTY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.drasyl.android.chat.models.MessageWrapper;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylNode;
import org.drasyl.node.event.Event;
import org.drasyl.node.event.MessageEvent;
import org.drasyl.node.event.NodeOfflineEvent;
import org.drasyl.node.event.NodeOnlineEvent;
import org.drasyl.util.logging.Logger;
import org.drasyl.util.logging.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChatActivity extends AppCompatActivity {
    private static final Logger LOG = LoggerFactory.getLogger(ChatActivity.class);
    private static final AtomicBoolean firstStart = new AtomicBoolean(true);
    private static final AtomicBoolean isOnline = new AtomicBoolean(false);
    private String recipient;
    private MessagesListAdapter<MessageWrapper> adapter;
    private static DrasylNode node;

    public static void start(final Context context) {
        final Intent starter = new Intent(context, ChatActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        LOG.debug("POW_DIFFICULTY: {}", () -> POW_DIFFICULTY);
        initDrasyl();
    }

    private void askForRecipient() {
        final RelativeLayout loading = findViewById(R.id.loadingPanelChat);
        final RelativeLayout layout = findViewById(R.id.recipientIdLayout);
        final EditText userIdEditText = findViewById(R.id.userIdEditText);
        final Button submitButtonUserId = findViewById(R.id.submitButtonUserId);

        loading.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);

        submitButtonUserId.setOnClickListener(l -> {
            recipient = userIdEditText.getText().toString();

            layout.setVisibility(View.GONE);
            initViews();
        });
    }

    private void initViews() {
        final MessageInput inputView = findViewById(R.id.input);
        final MessagesList messagesList = findViewById(R.id.messageList);

        inputView.setInputListener(input -> {
            sendMessage(input.toString());

            return true;
        });

        final ImageLoader imageLoader = (imageView, url, payload) -> Picasso.get().load(url).into(imageView);
        adapter = new MessagesListAdapter<>(
                node.identity().getIdentityPublicKey().toString(),
                imageLoader);
        messagesList.setAdapter(adapter);

        messagesList.setVisibility(View.VISIBLE);
        inputView.setVisibility(View.VISIBLE);
    }

    private void sendMessage(final String message) {
        node.send(recipient, message).thenAccept(a -> addMessage(message, node.identity().getIdentityPublicKey().toString()));
    }

    private void addMessage(final String message, String sender) {
        runOnUiThread(() -> adapter.addToStart(new MessageWrapper(message, sender), true));
    }


    private void initDrasyl() {
        try {
            if (firstStart.compareAndSet(true, false)) {
                final DrasylConfig config = DrasylConfig.newBuilder()
                        .identityPath(getApplicationContext().getFilesDir().toPath().resolve("identity.json"))
                        .remoteLocalHostDiscoveryEnabled(false)
                        .remoteLocalNetworkDiscoveryEnabled(false)
                        .remoteExposeEnabled(false)
                        .build();

                node = new DrasylNode(config) {
                    @Override
                    public void onEvent(final Event event) {
                        LOG.debug("{}", () -> event);
                        if (event instanceof MessageEvent) {
                            final MessageEvent msgEvent = (MessageEvent) event;

                            if (msgEvent.getPayload() instanceof String) {
                                addMessage((String) msgEvent.getPayload(), ((MessageEvent) event).getSender().toString());
                            }
                        } else if (event instanceof NodeOnlineEvent) {
                            if (isOnline.compareAndSet(false, true)) {
                                runOnUiThread(() -> askForRecipient());
                            }
                        } else if (event instanceof NodeOfflineEvent) {
                            if (isOnline.compareAndSet(true, false)) {
                                runOnUiThread(() -> findViewById(R.id.loadingPanelChat).setVisibility(View.VISIBLE));
                            }
                        }
                    }
                };

                node.start();
            } else {
                runOnUiThread(this::askForRecipient);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}