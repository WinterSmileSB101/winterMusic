package Music;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Mp3信息类
 * Created by SmileSB101 on 2016/9/29.
 */

public class Mp3Info{
	private long id; // 歌曲ID
	private String title; // 歌曲名称
	private String album; // 专辑
	private long albumId;// 专辑ID
	private String displayName; // 显示名称
	private String artist; // 歌手名称
	private long duration; // 歌曲时长
	private long size; // 歌曲大小
	private String url; // 歌曲路径
	private String lrcTitle; // 歌词名称
	private String lrcSize; // 歌词大小
	private String lrcUrl;//歌词位置
	private Bitmap Image;//音乐专辑图片

	public void setLrcUrl(String lrcUrl){
		this.lrcUrl = lrcUrl;
	}

	public String getLrcUrl(){
		return lrcUrl;
	}

	public Bitmap getImage(){
		return Image;
	}

	public void setImage(Bitmap image){
		Image = image;
	}

	public String getDisplayName(){
		return displayName;
	}

	public void setDisplayName(String displayName){
		this.displayName = displayName;
	}

	public long getId(){
		return id;
	}

	public void setId(long id){
		this.id = id;
	}

	public String getTitle(){
		return title;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getAlbum(){
		return album;
	}

	public void setAlbum(String album){
		this.album = album;
	}

	public long getAlbumId(){
		return albumId;
	}

	public void setAlbumId(long albumId){
		this.albumId = albumId;
	}

	public long getDuration(){
		return duration;
	}

	public void setDuration(long duration){
		this.duration = duration;
	}

	public String getArtist(){
		return artist;
	}

	public void setArtist(String artist){
		this.artist = artist;
	}

	public long getSize(){
		return size;
	}

	public void setSize(long size){
		this.size = size;
	}

	public String getUrl(){
		return url;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getLrcTitle(){
		return lrcTitle;
	}

	public void setLrcTitle(String lrcTitle){
		this.lrcTitle = lrcTitle;
	}

	public String getLrcSize(){
		return lrcSize;
	}

	public void setLrcSize(String lrcSize){
		this.lrcSize = lrcSize;
	}


	/**
	 *
	 * @param id 歌曲ID
	 * @param title 歌曲名称
	 * @param album 专辑
	 * @param albumId 专辑ID
	 * @param displayName 显示名称
	 * @param artist 歌手名称
	 * @param duration 歌曲时长
	 * @param size 歌曲大小
	 * @param url 歌曲路径
	 * @param lrcTitle 歌词名称
	 * @param lrcSize 歌词大小
	 */
	public Mp3Info(Long id,String title,String album,long albumId,String displayName,String artist,long duration,long size,String url,String lrcTitle,String lrcSize)
	{
		super();
		this.id = id;
		this.title = title;
		this.album = album;
		this.albumId = albumId;
		this.displayName = displayName;
		this.artist = artist;
		this.duration = duration;
		this.size = size;
		this.url = url;
		this.lrcTitle = lrcTitle;
		this.lrcSize = lrcSize;
	}

	public Mp3Info()
	{
		super();
	}

	/**
	 * 返回为Mp3Info[..|..|..]json格式化的字符串
	 * @return
	 */
	@Override
	public String toString(){
		return "Mp3Info[id:"+id+"|title:"+title+"|album:"+album+"|albumId:"+albumId+"|displayName:"+displayName+"|artist:"+artist+"|duration:"+duration+"|size:"+size+"|url:"+url+"|lrcTitle:"+lrcTitle+"|lrcSize:"+lrcSize+"]";
	}

	@Override
	public boolean equals(Object obj){
		Mp3Info anthor = (Mp3Info)obj;
		return anthor.getUrl().equals(this.getUrl());
	}
}
