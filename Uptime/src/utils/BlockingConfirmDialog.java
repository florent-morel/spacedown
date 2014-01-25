package utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class BlockingConfirmDialog {

    private Activity context;

    BlockingQueue<Boolean> blockingQueue;

    public BlockingConfirmDialog(Activity activity) {
        super();
        this.context = activity;
        blockingQueue = new ArrayBlockingQueue<Boolean>(1);
    }

    public boolean confirm(final String title, final String message){

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { 
                        blockingQueue.add(true);
                    }
                 })
                 .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        blockingQueue.add(false);
                    }
                })
                 .show();
            }
        });

        try {
            return blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

    }
}