package util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import Music.Mp3Info;

/**
 * 网络请求返回结果类
 * Created by SmileSB101 on 2016/11/1 0001.
 */

public class Result implements Parcelable{

	private int songCount;
	private List<Mp3Info> songs;

	public Result(Parcel parcel){
		songCount = parcel.readInt();
	}

	public static final Creator<Result> CREATOR = new Creator<Result>() {
		@Override
		public Result createFromParcel(Parcel in) {
			return new Result(in);
		}

		@Override
		public Result[] newArray(int size) {
			return new Result[size];
		}
	};
	@Override
	public int describeContents(){
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest,int flags){
		dest.writeInt(songCount);
	}

	public void setSongCount(int songCount){
		this.songCount = songCount;
	}

	public void setSongs(List<Mp3Info> songs){
		this.songs = songs;
	}

	public List<Mp3Info> getSongs(){
		return songs;
	}

	public int getSongCount(){
		return songCount;
	}
}
