package com.game.sdk.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.kngame_sdk_project.R;
import com.game.sdk.SDK;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdk.util.Md5Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity implements OnClickListener {

	public static String userName;
	private EditText userNameEt;
	private EditText passWordEt;
	private PopupWindow popupWindow ;
	
	private String md5_password;

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.login);

		if(GameSDK.getInstance().ismScreenSensor()){
			
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		initLoginView();
		
		initUser();
		
	}

	private void initLoginView()
	{
		userNameEt = (EditText) findViewById(R.id.lac_input_acc);
		passWordEt = (EditText) findViewById(R.id.lac_input_pwd);
		
		findViewById(R.id.lac_login_btn).setOnClickListener(this);
		findViewById(R.id.lac_register_btn).setOnClickListener(this);
		findViewById(R.id.lac_arrow_btn).setOnClickListener(this);
		findViewById(R.id.lac_clear_btn).setOnClickListener(this);
		findViewById(R.id.lac_changepwd_btn).setOnClickListener(this);
		
		passWordEt.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				String password = passWordEt.getText().toString();
				
				if( keyCode == KeyEvent.KEYCODE_DEL 
						&& !TextUtils.isEmpty(password)
						&& password.length() == 16
						&& !TextUtils.isEmpty(md5_password) 
						&& md5_password.length() == 32 
						&& md5_password.substring(0, 16).equals( password ) ){
					passWordEt.setText("");
				}
				return false;
			}
		});
	}
	
	private void initUser() {
		String usernames[] = DBHelper.getInstance().findAllUserName();
		String password = "";
		String username = "";
		
		if( usernames != null && usernames.length >0 ){
			username = usernames[0];
			password = DBHelper.getInstance().findPwdByUsername(username);
			
			userNameEt.setText(username);
			passWordEt.setText(password);
			
			md5_password = password;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.lac_login_btn) {
			Util.hideEditTextWindow(this, passWordEt);
			checkLoginParams(this, userNameEt, passWordEt);
		} 
		else if (id == R.id.lac_register_btn) { //注册账号

			Util.hideEditTextWindow(this, passWordEt);
			GameSDK.getInstance().register(this , true);
		} 
		else if (id == R.id.lac_arrow_btn) {
			if (popupWindow != null) {
				if (!popupWindow.isShowing()) {
					popupWindow.showAsDropDown(userNameEt);
				} else {
					popupWindow.dismiss();
				}
			} else {
				if (DBHelper.getInstance().findAllUserName().length > 0) {
					initPopupWindow();
					if (!popupWindow.isShowing()) {
						popupWindow.showAsDropDown(userNameEt);
					} else {
						popupWindow.dismiss();
					}
				} else {
					
				}
			}
		} 
		else if (id == R.id.lac_clear_btn) { //清除
			passWordEt.setText("");
		} 
		else if (id == R.id.lac_changepwd_btn) {
			Util.hideEditTextWindow(this, passWordEt);
			GameSDK.getInstance().changePwd(this , userNameEt.getText().toString(),true);
		} 
		else {
		}
	}
	

	private void initPopupWindow()
	{
		
		String usernames[] = DBHelper.getInstance().findAllUserName();
		
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < usernames.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", usernames[i]);
			map.put("drawable", R.drawable.list_item_close);
			list.add(map);
		}
		
		MyAdapter dropDownAdapter = new MyAdapter(this, list, R.layout.acc_list_item, new String[] { "name", "drawable" }, new int[] { R.id.textview, R.id.list_item_close });
		ListView listView = new ListView(this);
		listView.setAdapter(dropDownAdapter);
		listView.setDivider(getResources().getDrawable(R.color.PopViewDividerColor));
		listView.setDividerHeight(1);
		listView.setCacheColorHint(0);
		popupWindow = new PopupWindow(listView, userNameEt.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.acc_list_bg));
		
//		popupWindow.setAnimationStyle(R.style.anim_popupwindow);
	}

	private void checkLoginParams(Activity context, EditText userNameEt, EditText passWordEt) {
		String username = userNameEt.getText().toString();
		if (TextUtils.isEmpty(username)) {
			Util.ShowTips(context, getResources().getString(R.string.tips_2), Toast.LENGTH_SHORT);
			return;
		}
		String password = passWordEt.getText().toString();
		if (TextUtils.isEmpty(password)) {
			Util.ShowTips(context, getResources().getString(R.string.tips_8) , Toast.LENGTH_SHORT);
			return;
		}
		
		if(!Util.isNetWorkAvailable(getApplicationContext())){
			Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
			return ;
		}
		
		if( !TextUtils.isEmpty(md5_password) && md5_password.length() == 32 && md5_password.substring(0, 16).equals( password ) ){
			password = md5_password;
		}else{
			password = Md5Util.getMd5(password);
		}
		LoadingDialog.show(context, "登录中...", true);
		HttpService.doLogin(getApplicationContext(), handler, username, password);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ResultCode.SUCCESS:
				if(msg.obj!=null){
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );
						LoginActivity.this.finish();
					}else{
//						KnLog.e("请先设置登录回调");
					}
				}
				break;

				case ResultCode.NONEXISTENT: //账号不存在

					Util.ShowTips(LoginActivity.this,"账号不存在！");

					break;


				case ResultCode.FAIL:
				if(msg.obj!=null)
				{
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );
						Util.ShowTips(LoginActivity.this,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
					}else{
//						KnLog.e("请先设置登录回调");
					}
				}
//				LoginActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if( (requestCode == SDK.REQUESTCODE_REG || requestCode == SDK.REQUESTCODE_CHANGEPWD ) 
				&& resultCode == Activity.RESULT_OK ){
			initUser();
		}
		
	}
	
	class MyAdapter extends SimpleAdapter {

		private List<HashMap<String, Object>> data;

		public MyAdapter(Context context, List<HashMap<String, Object>> data, int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.acc_list_item, null);
				holder.btn = (ImageButton) convertView.findViewById(R.id.list_item_close);
				holder.tv = (TextView) convertView.findViewById(R.id.textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tv.setText(data.get(position).get("name").toString());
			holder.tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String[] usernames = DBHelper.getInstance().findAllUserName();
					
					String username = usernames[position];
					String password = DBHelper.getInstance().findPwdByUsername(usernames[position]);
					
					userNameEt.setText(username);
					passWordEt.setText(password);
					
					md5_password = password;
					
					popupWindow.dismiss();
				}
			});
			holder.btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					popupWindow.dismiss();
					
					String[] usernames = DBHelper.getInstance().findAllUserName();
					 if (usernames.length > 0) 
					 {
						long returnId = DBHelper.getInstance().deleteUser(usernames[position]);
						if (returnId == -1)
						{
							Util.ShowTips(LoginActivity.this, getResources().getString(R.string.tips_12) );
						}
						else
						{
							String[] newUserNames = DBHelper.getInstance().findAllUserName(); 
							initPopupWindow();
							Util.ShowTips(LoginActivity.this, getResources().getString(R.string.tips_13));
							if (newUserNames.length == 0)
							{
								userNameEt.setText("");
								passWordEt.setText("");
							}
							else if (usernames[position].equals(userNameEt.getText().toString()))
							{
								userNameEt.setText(newUserNames[0]);
								passWordEt.setText(DBHelper.getInstance().findPwdByUsername(newUserNames[0]));
							}
							else
							{
								
							}
						}
					 }
					 
				}
			});
			return convertView;
		}
	}
	
	class ViewHolder {
		private TextView tv;
		private ImageButton btn;
	}


}
