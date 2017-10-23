package com.game.sdk.bean;

public class GameInfo {
	
	private String appKey;
	
	private String gameId;
	private String channel = "u7game";
	private String platform = "android";
	private String adChannel;
	private String gid;

	private String regKey = "kuniu@!#2014";
	
	private int orientation;
	
	private String adChannelTxt;
	
	public GameInfo(){
		
	}

	
	public GameInfo(String appKey, String gameId, String channel,
			String platform, String adChannel, int orientation , String adChannelTxt) {
		super();
		this.appKey = appKey;
		this.gameId = gameId;
		this.channel = channel;
		this.platform = platform;
		this.adChannel = adChannel;
		this.orientation = orientation;
		this.adChannelTxt = adChannelTxt;
	}

	public int getOrientation() {
		return orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	public String getAdChannel() {
		return adChannel;
	}
	public void setAdChannel(String adChannel) {
		this.adChannel = adChannel;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getRegKey() {
		return regKey;
	}
	public void setRegKey(String regKey) {
		this.regKey = regKey;
	}
	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getAdChannelTxt() {
		return adChannelTxt;
	}
	
}
