package util;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 网络请求返回的歌曲信息
 * Created by SmileSB101 on 2016/11/1 0001.
 */

public class MusicResponse implements Parcelable{
	private Result result;
	private int code;

	public MusicResponse(Parcel parcel){
		result = parcel.readParcelable(Result.class.getClassLoader());
		code = parcel.readInt();
	}

	public static final Creator<MusicResponse> CREATOR = new Creator<MusicResponse>() {
		@Override
		public MusicResponse createFromParcel(Parcel in) {
			return new MusicResponse(in);
		}

		@Override
		public MusicResponse[] newArray(int size) {
			return new MusicResponse[size];
		}
	};

	@Override
	public int describeContents(){
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest,int flags){
		dest.writeParcelable(result, flags);
		dest.writeInt(code);
	}

	public int getCode(){
		return code;
	}

	public Result getResult(){
		return result;
	}

	public void setResult(Result result){
		this.result = result;
	}

	public void setCode(int code){
		this.code = code;
}
}
