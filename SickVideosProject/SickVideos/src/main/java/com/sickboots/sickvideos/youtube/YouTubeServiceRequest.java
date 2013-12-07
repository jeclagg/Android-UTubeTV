package com.sickboots.sickvideos.youtube;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.util.HashMap;

public class YouTubeServiceRequest implements Parcelable {
  public enum RequestType {RELATED, SUBSCRIPTIONS, SEARCH, CATEGORIES, LIKED, PLAYLISTS, VIDEOS}
  private HashMap data;
  public RequestType type;

  // ===================================================================
  //  Parcelable - we send this to the service inside an intent

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeSerializable(type);
    dest.writeSerializable(data);
  }

  public static final Parcelable.Creator<YouTubeServiceRequest> CREATOR = new Parcelable.Creator<YouTubeServiceRequest>() {
    public YouTubeServiceRequest createFromParcel(Parcel in) {
      return new YouTubeServiceRequest(in);
    }

    public YouTubeServiceRequest[] newArray(int size) {
      return new YouTubeServiceRequest[size];
    }
  };

  private YouTubeServiceRequest(Parcel in) {
    type = (RequestType) in.readSerializable();
    data = (HashMap) in.readSerializable();
  }

  // ===================================================================

  public static YouTubeServiceRequest relatedSpec(YouTubeAPI.RelatedPlaylistType relatedPlayListType, String channelID) {
    YouTubeServiceRequest result = emptyRequest(RequestType.RELATED);

    result.data.put("type", relatedPlayListType);
    result.data.put("channel", channelID);

    return result;
  }

  public static YouTubeServiceRequest videosSpec(String playlistID) {
    YouTubeServiceRequest result = emptyRequest(RequestType.VIDEOS);

    result.data.put("playlist", playlistID);

    return result;
  }

  public static YouTubeServiceRequest searchSpec(String query) {
    YouTubeServiceRequest result = emptyRequest(RequestType.SEARCH);

    result.data.put("query", query);

    return result;
  }

  public static YouTubeServiceRequest subscriptionsSpec() {
    YouTubeServiceRequest result = emptyRequest(RequestType.SUBSCRIPTIONS);

    return result;
  }

  public static YouTubeServiceRequest categoriesSpec() {
    YouTubeServiceRequest result = emptyRequest(RequestType.CATEGORIES);

    return result;
  }

  public static YouTubeServiceRequest likedSpec() {
    YouTubeServiceRequest result = emptyRequest(RequestType.LIKED);

    return result;
  }

  public static YouTubeServiceRequest playlistsSpec(String channelID) {
    YouTubeServiceRequest result = emptyRequest(RequestType.PLAYLISTS);

    result.data.put("channel", channelID);

    return result;
  }

  public Object getData(String key) {
    return data.get(key);
  }

  public String name() {
    String result = "YouTube";
    switch (type) {
      case SUBSCRIPTIONS:
        result = "Subscriptions";
        break;
      case PLAYLISTS:
        result = "Playlists";
        break;
      case CATEGORIES:
        result = "Categories";
        break;
      case LIKED:
        result = "Liked";
        break;
      case RELATED:
        result = "Related Playlists";

        YouTubeAPI.RelatedPlaylistType type = (YouTubeAPI.RelatedPlaylistType) getData("type");
        switch (type) {
          case FAVORITES:
            result = "Favorites";
            break;
          case LIKES:
            result = "Likes";
            break;
          case UPLOADS:
            result = "Uploads";
            break;
          case WATCHED:
            result = "History";
            break;
          case WATCHLATER:
            result = "Watch later";
            break;
        }
        break;
      case VIDEOS:
        result = "Videos";
        break;
      case SEARCH:
        result = "Search";
        break;
    }

    return result;
  }

  // a new database is created for every list, so need a unique name that can match the spec
  public String databaseName() {
    String result = name();

    switch (type) {
      case SUBSCRIPTIONS:
        break;
      case PLAYLISTS:
        result += getData("channel");
        break;
      case CATEGORIES:
        break;
      case LIKED:
        break;
      case RELATED:
        result += getData("channel");
        break;
      case VIDEOS:
        result += getData("playlist");
        break;
      case SEARCH:
        result += getData("query");
        break;
    }

    return result;
  }

  // ===================================================================
  // private

  private YouTubeServiceRequest() {
    super();
  }

  private static YouTubeServiceRequest emptyRequest(RequestType type) {
    YouTubeServiceRequest result = new YouTubeServiceRequest();

    result.type = type;
    result.data = new HashMap();

    return result;
  }

}