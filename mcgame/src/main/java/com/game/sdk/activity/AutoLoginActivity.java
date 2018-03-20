package com.game.sdk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.Util;
import com.game.sdk_project.SelecteLoginActivity;
import com.game.sdkproxy.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 免密码开始登录
 */
public class AutoLoginActivity extends Activity implements View.OnClickListener {
	
	private int m_time  = 5 ;
	private Button  m_btn = null ;
	private ImageView back= null;
	private Activity m_activity = null ;
	private String  m_userNames = null ;
	private String  m_passwords = null ;
	private boolean m_dbHas     = false ;
	private boolean m_youke     = false ;
    private TextView other_login=null;
    private ArrayList<String> arrlist=new ArrayList<String>();//存放下拉listView中的账号；
    private PopupAdapter adapter;
    private ImageButton autoLogin_drop;
    private PopupWindow pop;
    private boolean isShow=false;
    private ListView etLv=null;
    private EditText account_et=null;
    private String name=null;



	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		KnLog.log("AutoLogin+++");
	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(GameSDK.getInstance().ismScreenSensor()){
	
		}else{
			setRequestedOrientation(GameSDK.getInstance().getmOrientation());
		}
		
		setContentView(R.layout.mc_auto_login_manager);

		m_activity = this ;

		initview();

		initUser();


		
		//下拉框，记录多个账号
		autoLogin_drop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (pop==null) {
					if (adapter==null) {
						adapter=new PopupAdapter(m_activity);
						etLv=new ListView(m_activity);
						etLv.setBackgroundColor(Color.BLACK);
						pop=new PopupWindow(etLv, account_et.getWidth(),
								LayoutParams.WRAP_CONTENT);
						pop.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFFFF")));
						etLv.setAdapter(adapter);
						pop.showAsDropDown(account_et,0,5);
						
						isShow=true;
					}
				}else if (isShow) {
					pop.dismiss();
					
					isShow=false;
				}else if (!isShow) {
					
					pop.showAsDropDown(account_et,0,5);
					isShow=true;
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(GameSDK.getInstance().getGameInfo().getOrientation() == Constants.LANDSCAPE){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}


	@Override
	public void onClick(View v) {

		int id = v.getId();
		Intent intent = null;

			if(id==R.id.login_game_bt){ //登录
			//	直接登录
			KnLog.log("直接登录++");
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.mc_tips_34).toString());
				return ;
			}
			
			LoadingDialog.show(m_activity, "登录中...", true);
			name=account_et.getText().toString();
			m_passwords = DBHelper.getInstance().findPwdByUsername(name);
			//账号登录
			HttpService.doLogin(getApplicationContext(), handler, name,m_passwords);

		   }else if (id==R.id.auto_login_back) { //返回

				Intent	inten=new Intent(m_activity, SelecteLoginActivity.class); //跳转到首页选择三种方式
				startActivity(inten);

				if (m_activity!=null){
					m_activity.finish();
					m_activity = null ;
				}


		}else if (id==R.id.other_login){ //其他登录

				Intent	inten=new Intent(m_activity, SelecteLoginActivity.class); //跳转到首页选择三种方式
				startActivity(inten);
				if (m_activity!=null) {
					m_activity.finish();
					m_activity=null;

				}
			}

	}




	//初始化view
	private void initview(){
		m_btn = (Button)findViewById(R.id.login_game_bt);
		other_login=(TextView) findViewById(R.id.other_login); //其他方式登录
		autoLogin_drop= (ImageButton) findViewById(R.id.drop_down);//
		account_et=(EditText) findViewById(R.id.auto_account_et); //输入的账号
		back = (ImageView)findViewById(R.id.auto_login_back); //返回

		m_btn.setOnClickListener(this);
		back.setOnClickListener(this);
		other_login.setOnClickListener(this);

	}


	//读取数据库账号名
	private void initUser() {
		String usernames[] = DBHelper.getInstance().findAllUserName();
		String password = "";
		String username = "";
		for (int i = 0; i < usernames.length; i++) {
			arrlist.add(usernames[i]);
		}
		if( usernames != null && usernames.length >0 ){
			username = usernames[0];
			account_et.setText(username);
		}


	}

	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			Intent intent = null;
			LoadingDialog.dismiss();
			switch (msg.what) {
			case ResultCode.SUCCESS:
				DBHelper.getInstance().insertOrUpdateUser( name , m_passwords );
				if(msg.obj!=null){
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );


						m_activity.finish();
						m_activity=null;


					}
				}
				break;

				case ResultCode.NONEXISTENT: //账号不存在

					Util.ShowTips(m_activity,"账号不存在！");

					break;

			case ResultCode.FAIL:
				if(msg.obj!=null)
				{
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );
						if(null==m_activity){
							
						}else{
							
							m_activity.finish();
							m_activity = null ;
							
						}
					}else{
//						KnLog.e("请先设置登录回调");
					}
				}
				break;
			case ResultCode.QUERY_ACCOUNT_BIND_SUCCESS: //绑定了手机号

				if(msg.obj!=null) {
					if (GameSDK.getInstance().getmLoginListener() != null) {
						GameSDK.getInstance().getmLoginListener().onSuccess(msg.obj.toString());


						String mobile= msg.obj.toString() ;

						//			intent.putExtra("userName",m_userNames);
						//			intent.putExtra("mobile",mobile);
                       //			if( null == intent ){
						// 			     return ;
                        //           }

						if(null==m_activity){

						}else{

							m_activity.finish();
							m_activity = null ;

						}


					}
				}

				break;

			case ResultCode.VISITOR_LOGIN_SUCCESS:
				KnLog.log("游客登录成功:"+msg.obj.toString());
//				DBHelper.getInstance().insertOrUpdateUser("youke","123456");
				if(m_dbHas){
					
				}
				
				if(msg.obj!=null){
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onSuccess( msg.obj.toString() );
						if(null==m_activity){
							
						}else{
							
							m_activity.finish();
							m_activity = null ;
							
						}
					}else{
						
					}
				}
				break;
			case ResultCode.VISITOR_LOGIN_FAIL:
				KnLog.log("游客登录失败:"+msg.obj.toString());
				if(msg.obj!=null)
				{
					if(GameSDK.getInstance().getmLoginListener()!=null){
						GameSDK.getInstance().getmLoginListener().onFail(  msg.obj.toString() );
						if(null==m_activity){
							
						}else{
							
							Util.ShowTips(m_activity,  Util.getJsonStringByName( msg.obj.toString() , "reason" ) );
							m_activity.finish();
							m_activity = null ;
							
						}
					}else{
						
					}
				}
				break;
			case ResultCode.UNKNOW:
				if(null==m_activity){
					
				}else{
					
					Util.ShowTips(m_activity,msg.obj.toString());
					
				}
				
				break;
			default:
				break;
			}
		}
	};
	


	//可编辑下拉框
	class PopupAdapter extends BaseAdapter{
        private LayoutInflater layoutInflater;
        private Context context;

        public PopupAdapter() {
          // TODO Auto-generated constructor stub
      }
          public PopupAdapter(Context context) {

          this.context = context;
      }

          @Override
          public int getCount() {
              // TODO Auto-generated method stub

              return arrlist.size();
          }

          @Override
          public Object getItem(int position) {
              // TODO Auto-generated method stub
              return null;
          }

          @Override
          public long getItemId(int position) {
              // TODO Auto-generated method stub
              return position;
          }

          @Override
          public View getView(int position, View convertView, ViewGroup parent) {
              // TODO Auto-generated method stub
              Holder holder=null;
              final String names=arrlist.get(position);        
              if (convertView==null) {
                  layoutInflater=LayoutInflater.from(context);
                  convertView=layoutInflater.inflate(R.layout.mc_list_item, null);
                  holder=new Holder();
                  holder.tv=(TextView) convertView.findViewById(R.id.textView);
                 convertView.setTag(holder);
              }else{
                  holder=(Holder) convertView.getTag();
              }
              if (holder!=null) {
                  convertView.setId(position);
                  holder.tv.setText(names);
                  holder.tv.setOnTouchListener(new OnTouchListener() {

                      @Override
                      public boolean onTouch(View v, MotionEvent event) {
                          // TODO Auto-generated method stub
                          boolean touch=false; //是否点击下拉的选项；
                          if (event.getAction()==MotionEvent.ACTION_DOWN) {
                              pop.dismiss();
                              isShow=false;
                              account_et.setText(names);
                              touch=true;
                          }else{
                              touch=false;
                          }
                          return touch;
                      }
                  });
              }

              return convertView;
          }

      }
      class Holder{
          TextView tv;
          
          void setId(int position){
              tv.setId(position);
              
          }
      }





}
