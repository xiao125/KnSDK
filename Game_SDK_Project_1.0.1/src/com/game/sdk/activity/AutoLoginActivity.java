package com.game.sdk.activity;

import java.util.ArrayList;
import java.util.Timer;
import com.example.kngame_sdk_project.R;
import com.game.sdk.Constants;
import com.game.sdk.GameSDK;
import com.game.sdk.ResultCode;
import com.game.sdk.service.HttpService;
import com.game.sdk.util.DBHelper;
import com.game.sdk.util.LoadingDialog;
import com.game.sdk.util.KnLog;
import com.game.sdk.util.Util;
import com.game.sdk_project.SelecteLoginActivity;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 免密码开始登录
 */
public class AutoLoginActivity extends Activity implements OnClickListener {
	
	private int m_time  = 5 ;
	private Button  m_btn = null ;
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
	//声明一个SharedPreferences对象和一个Editor对象
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	}

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
		
		setContentView(R.layout.auto_login_manager);
		
		
		m_activity = this ;

		//创建sp存储
		preferences = getSharedPreferences("Autoogin",MODE_PRIVATE);
		editor= preferences.edit();





		m_btn = (Button)findViewById(R.id.login_game_bt);
	//	m_userName = (TextView)findViewById(R.id.userName);
		other_login=(TextView) findViewById(R.id.other_login); //其他方式登录
		autoLogin_drop= (ImageButton) findViewById(R.id.drop_down);//
		account_et=(EditText) findViewById(R.id.auto_account_et); //输入的账号
		initUser();	
		String userName = null ;
		Intent intent = getIntent();
		if(intent.hasExtra("userName")){
			userName = intent.getStringExtra("userName");
		}
		if(intent.hasExtra("dbHas")){
			m_dbHas = intent.getBooleanExtra("dbHas",false);
		}
		if(intent.hasExtra("youke")){
			m_youke = intent.getBooleanExtra("youke",false);
		}
		m_userNames = userName ;
		

		findViewById(R.id.login_game_bt).setOnClickListener(this);
		findViewById(R.id.auto_login_back).setOnClickListener(this);
		other_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    Intent	inten=new Intent(m_activity, SelecteLoginActivity.class); //跳转到首页选择三种方式
			    startActivity(inten);
			    if (m_activity!=null) {
					m_activity.finish();
					m_activity=null;
					
				}
			}
		});
		
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
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		KnLog.log("执行按钮操作响应+++");
		int id = v.getId();
		Intent intent = null;

			if(id==R.id.login_game_bt){
			//	直接登录
			KnLog.log("直接登录++");
			if(!Util.isNetWorkAvailable(getApplicationContext())){
				Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
				return ;
			}
			
			LoadingDialog.show(m_activity, "登录中...", true);
//			if(m_youke){
//				KnLog.log("游客直接登录++");
//				HttpService.visitorReg(m_activity,handler);
//			}else{
				KnLog.log("直接登录++");
				name=account_et.getText().toString();
				m_passwords = DBHelper.getInstance().findPwdByUsername(name);

				//账号登录
				HttpService.doLogin(getApplicationContext(), handler, name,m_passwords);

//			}
			
		}else if (id==R.id.auto_login_back) {

				if (m_activity!=null){
					m_activity.finish();
					m_activity = null ;
				}


		}

	}
	
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

						//查询是否第一次登录
						String first = preferences.getString("login",null);

						//Object first= SpUtil.get(m_activity,"String","");

						KnLog.log("//查询是否第一次登录"+first);

						if(first==null){


							//存入数据
							editor.putString("login","1");
							editor.commit();

						//	SpUtil.put(m_activity,"String","1");
							KnLog.log("//存入数据第一次登录");

							finish();
							m_activity=null;

						}else {


                            //查询账号是否绑定手机号
							HttpService.queryBindAccont(m_activity.getApplicationContext(), handler, name);


						}


					}else{


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

							//		m_activity.startActivity(intent);
							m_activity.finish();
							m_activity = null ;

						}


					}
				}




				break;
			case ResultCode.QUERY_ACCOUNT_BIND_FAIL: //没有绑定手机号
				/*intent = new Intent(m_activity.getApplicationContext(),AccountManagerActivity.class);
				intent.putExtra("userName",m_userNames);
				if( null == intent ){
					return ;
				}*/


				if(msg.obj!=null) {
					if (GameSDK.getInstance().getmLoginListener() != null) {
						GameSDK.getInstance().getmLoginListener().onSuccess(msg.obj.toString());


						if (null == m_activity) {

						} else {
							LayoutInflater inflater = LayoutInflater.from(m_activity);
							View v = inflater.inflate(R.layout.bind_mobile_dialog_ts, null); //绑定手机
							LinearLayout layout = (LinearLayout) v.findViewById(R.id.visit_dialog);
							final AlertDialog dia = new AlertDialog.Builder(m_activity).create();
							Button bind = (Button) v.findViewById(R.id.visit_bind_account); //下次再说
							Button cont = (Button) v.findViewById(R.id.visit_continue);//立刻绑定
							TextView ts = (TextView) v.findViewById(R.id.ts);
							String phone = ts.getText().toString(); //占位符
							String et = account_et.getText().toString().trim(); //账号

							ts.setText(phone.replace("1", et));

							KnLog.log("账号提示：" + phone + " 需要替换的et=" + et + " 替换后的=" + phone.replace("1", et));


							// bind.setText("绑定手机");
							dia.show();
							dia.setContentView(v);
							cont.setOnClickListener(new OnClickListener() {


								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub


									DBHelper.getInstance().insertOrUpdateUser(name, m_passwords);
									Intent intent = new Intent(m_activity, BindCellActivity.class);
									intent.putExtra("userName", name);
									startActivity(intent);


									if (null == m_activity) {

									} else {
										dia.dismiss();

									}

								}
							});

							bind.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub

									//	DBHelper.getInstance().insertOrUpdateUser( name , m_passwords );
									if (null == m_activity) {

									} else {
										dia.dismiss();
										m_activity.finish();
										m_activity = null;
									}


								}
							});


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
	
	private Handler m_handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(10000==msg.what){
				String text =" 登 录 游 戏 "+"("+m_time+")";
				m_btn.setText(text);
			}
			else if(10001==msg.what){
				if(!Util.isNetWorkAvailable(getApplicationContext())){
					Util.ShowTips(getApplicationContext(),getResources().getString(R.string.tips_34).toString());
					return ;
				}
				
				if(m_youke){
					HttpService.visitorReg(m_activity,handler);
				}else{
					HttpService.doLogin(getApplicationContext(), handler, m_userNames,m_passwords);
				}
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
                  convertView=layoutInflater.inflate(R.layout.list_item, null);
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
