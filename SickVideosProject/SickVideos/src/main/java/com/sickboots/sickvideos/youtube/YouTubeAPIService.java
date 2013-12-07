package com.sickboots.sickvideos.youtube;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.sickboots.sickvideos.MainActivity;
import com.sickboots.sickvideos.YouTubeGridFragment;
import com.sickboots.sickvideos.lists.UIAccess;
import com.sickboots.sickvideos.lists.YouTubeListDB;
import com.sickboots.sickvideos.misc.Util;

import java.util.List;

/**
 * Created by sgehrman on 12/6/13.
 */
public class YouTubeAPIService extends IntentService {

  public static void startRequest(Context context, YouTubeServiceRequest request) {
    Intent i = new Intent(context, YouTubeAPIService.class);
    i.putExtra("request", request);
    context.startService(i);
  }

  public YouTubeAPIService() {
    super("YouTubeAPIService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      UIAccess access = createUIAccess();

      YouTubeListDB result = new YouTubeListDB(YouTubeServiceRequest.relatedSpec(YouTubeAPI.RelatedPlaylistType.FAVORITES, null), access);
      List items = result.getItems();

    } catch (Exception e) {
    }
  }

  private UIAccess createUIAccess() {
    final Context appContext = this;
    UIAccess access = new UIAccess() {
      @Override
      public void onResults() {

        Intent intent = new Intent(YouTubeGridFragment.DATA_READY_INTENT);
        intent.putExtra(YouTubeGridFragment.DATA_READY_INTENT_PARAM, "FUCK");

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(appContext);
        manager.sendBroadcast(intent);

        Util.log(String.format("Sent broadcast %s", MainActivity.REQUEST_AUTHORIZATION_INTENT));

      }

      @Override
      public Context getContext() {
        return appContext;
      }
    };

    return access;
  }


}