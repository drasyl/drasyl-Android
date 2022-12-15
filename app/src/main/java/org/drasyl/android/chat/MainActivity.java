package org.drasyl.android.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.drasyl.identity.Identity;
import org.drasyl.node.DrasylConfig;
import org.drasyl.node.DrasylException;
import org.drasyl.node.DrasylNode;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(() -> {
            try {
                Identity identity = DrasylNode.generateIdentity(
                        DrasylConfig.newBuilder().identityPath(
                                getApplicationContext().getFilesDir().toPath().resolve("identity.json")).build()
                );

                initViews(identity);
            } catch (DrasylException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initViews(Identity identity) {
        final RelativeLayout loading = findViewById(R.id.loadingPanel);
        final RelativeLayout layout = findViewById(R.id.ownIdLayout);
        final TextView id = findViewById(R.id.ownId);
        final Button next = findViewById(R.id.next);

        runOnUiThread(() -> {
            loading.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            id.setText(identity.getIdentityPublicKey().toString());
        });

        next.setOnClickListener(view -> ChatActivity.start(view.getContext()));
    }
}