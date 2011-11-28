package com.otdshco.restore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.otdshco.common.MyAdapter;
import com.otdshco.presto.R;
import com.otdshco.tools.Logger;
import com.otdshco.tools.Utilities;
import com.otdshco.common.Common;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;

public class Restore extends
		ListActivity implements
					OnClickListener
{
	private static final String			LOG_MAIN							="Restore";
	private String						THREAD_WORKING						=Common.THREAD_WORKING;
	private String						DONE_WORKING						=Common.DONE_WORKING;
	private String						SETTINGS_ID							=Common.SETTINGS_ID;
	private String						ACTION_VALUE						=Common.ACTION_VALUE;
	private final String				TEXT_KEY_3							=Common.TEXT_KEY_3;
	private final String				ITEM_ID								=Common.ITEM_ID;
	private final String				IMG_KEY								=Common.IMG_KEY;
	private final String				SELECTED_ITEM_KEY					=Common.SELECTED_ITEM_KEY;
	private final static String			TEXT_KEY_1							=Common.TEXT_KEY_1;
	private final static String			TEXT_KEY_2							=Common.TEXT_KEY_2;
	private int							SETTINGS_ACTIVITY					=Common.SETTINGS_ACTIVITY;
	private final int					CREATE_DIALOG						=Common.CREATE_DIALOG;
	private final int					ACTION_VALUE_REFRESH_LIST_ADAPTER	=Common.ACTION_VALUE_REFRESH_LIST_ADAPTER;
	private final int					ACTION_VALUE_ON_CLICK				=Common.ACTION_VALUE_ON_CLICK;
	private String						translate_TOTAL_APPS				=Common.translate_TOTAL_APPS;
	private String						translate_SELECTED_APPS				=Common.translate_SELECTED_APPS;
	private String						translate_INSTALLING_MESSAGE		=Common.translate_INSTALLING_MESSAGE;
	private String						translate_LOADING_TITLE				=Common.translate_LOADING_TITLE;
	private String						translate_LOADING_MESSAGE			=Common.translate_LOADING_MESSAGE;
	private String						translate_DONE_STATUS				=Common.translate_DONE_STATUS;
	private String						translate_RESTORE_BTN				=Common.translate_RESTORE_BTN;
	private String						translate_SETTINGS_BTN				=Common.translate_SETTINGS_BTN;
	private String						translate_CURRENT_WORKING			=Common.translate_CURRENT_WORKING;
	private String						translate_WAIT_MSG					=Common.translate_WAIT_MSG;
	private String						translate_IO_ERROR					=Common.translate_IO_ERROR;
	private String						translate_NO_FILE_SELECTION			=Common.translate_NO_FILE_SELECTION;
	private String						translate_BACKUP_DIR				=Common.translate_BACKUP_DIR;
	private String						translate_BACKUP_DIR_VALUE			=Common.translate_BACKUP_DIR_VALUE;
	private String						RESTORE_CHECKBOX					=Common.RESTORE_CHECKBOX;
	private String						RESTORE_LIST_TYPE					=Common.RESTORE_LIST_TYPE;
	private String						RESTORE_LIST_ORDER					=Common.RESTORE_LIST_ORDER;
	private String						RESTORE_LIST_ORDER_VALUE			=Common.RESTORE_LIST_ORDER_VALUE;
	private String						RESTORE_LIST_TYPE_VALUE				=Common.RESTORE_LIST_TYPE_VALUE;
	private String						ACTION								=String.valueOf(R.layout.restore_row);
	private int							listTotalItemsCount					=0;
	private boolean						restoreBlinking						=true;
	private boolean						RESTORE_CHECKBOX_VALUE				=false;
	private String						auxMessage;
	private String						restoreStringView;
	private Button						restoreButton;
	private Button						restoreSettingsButton;
	private TextView					restoreTextView;
	private Thread						restoreProcess;
	private SharedPreferences			sharedPreferences;
	private ArrayList<String>			applicationsArray					=new ArrayList<String>();
	private ArrayList<Integer>			selectedItems						=new ArrayList<Integer>();
	private ArrayList<Drawable>			drawableArray						=new ArrayList<Drawable>();
	private Handler						restoreHandler						=new Handler();
	private MessageReceiver				receiver							=new MessageReceiver();
	private MyAdapter					notes								=null;
	private List<Map<String,Object>>	resourceNames						=null;

	private void startUserInterfaceElements()
	{
		log("                              ");
		setContentView(R.layout.restore_main);
		restoreButton=(Button)findViewById(R.id.restore_button);
		restoreButton.setOnClickListener(this);
		restoreSettingsButton=(Button)findViewById(R.id.restore_settings_button);
		restoreSettingsButton.setOnClickListener(this);
		restoreTextView=(TextView)findViewById(R.id.text_v);
	}

	private void registerBroadcastReceiver()
	{
		log("                              ");
		IntentFilter filter=new IntentFilter(ACTION);
		registerReceiver(	receiver,
							filter);
	}

	private void unregisgerBroadcastReceiver()
	{
		log("                              ");
		unregisterReceiver(receiver);
	}

	private void executeLoadApplicationsListBackgroundTask()
	{
		log("                              ");
		new TaskLoader().execute();
	}

	private void generateApplicationsList()
	{
		log("                              ");
		getApplicationsListData();
		createApplicationsListAdapter();
	}

	private void getApplicationsListData()
	{
		log("                              ");
		resourceNames=new ArrayList<Map<String,Object>>();
		listTotalItemsCount=Utilities.generateData(	resourceNames,
													getSort(),
													drawableArray,
													applicationsArray,
													customDir(),
													this);
	}

	private void createApplicationsListAdapter()
	{
		log("                              ");
		notes=new MyAdapter(this,
							resourceNames,
							R.layout.restore_row,
							new String[]
							{
									TEXT_KEY_1,
									TEXT_KEY_2,
									TEXT_KEY_3,
									IMG_KEY,
									ITEM_ID
							},
							new int[]
							{
									R.id.text1,
									R.id.text2,
									R.id.text3,
									R.id.img
							},
							selectedItems,
							drawableArray);
	}

	public void updateStatusBarItemsCount()
	{
		log("                              ");
		printMessage(translate_TOTAL_APPS+
						getListTotalItemsCount()+
						" ] - [ "+
						translate_SELECTED_APPS+
						selectedItems.size());
	}

	private RestoreThread getRestoreProcess()
	{
		log("                              ");
		return (RestoreThread)restoreProcess;
	}

	private Runnable	restoreUpdateTask	=new Runnable()
												{
													public void run()
													{
														if((restoreProcess!=null))
														{
															if(!isRoot())
															{
																getRestoreProcess().update();
															}
															auxMessage=translate_INSTALLING_MESSAGE+
																		getRestoreProcess().get()+
																		"...";
															if((getRestoreProcess().isWorking().equalsIgnoreCase(THREAD_WORKING)))
															{
																if(restoreBlinking)
																{
																	setMessage(auxMessage);
																	restoreBlinking=false;
																}
																else
																{
																	setMessage("");
																	restoreBlinking=true;
																}
															}
															if((getRestoreProcess().isWorking().equalsIgnoreCase(DONE_WORKING)))
															{
																setMessage(translate_DONE_STATUS);
																restoreProcess.stop();
																restoreProcess=null;
																if(restoreButton!=null)
																{
																	restoreButton.setText(translate_RESTORE_BTN);
																	restoreButton.setClickable(true);
																	restoreSettingsButton.setText(translate_SETTINGS_BTN);
																	restoreSettingsButton.setClickable(true);
																	removeUninstalledApplicationsFromSelectedItemsList();
																	refreshListAdapterUsingInvalidateAndNotifyDataSetChanged();
																}
																setMessage(translate_DONE_STATUS);
															}
														}
														restoreHandler.postDelayed(	this,
																					750);
													}
												};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		log("                              ");
		startUserInterfaceElements();
		registerBroadcastReceiver();
		executeLoadApplicationsListBackgroundTask();
	}

	@Override
	protected void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		log("                              ");
		selectedItems.addAll(state.getIntegerArrayList(SELECTED_ITEM_KEY));
		setMessage(state.getString(SETTINGS_ID));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		log("                              ");
		outState.putIntegerArrayList(	SELECTED_ITEM_KEY,
										selectedItems);
		outState.putString(	SETTINGS_ID,
							restoreStringView);
	}

	public void onClick(View target)
	{
		log("                              ");
		Button restoreButton=(Button)target.findViewById(R.id.restore_button);
		Button settingsButton=(Button)target.findViewById(R.id.restore_settings_button);
		if(restoreButton!=null)
		{
			ArrayList<String> apkList=new ArrayList<String>();
			// Collections.sort(selectedItems);
			// for(Integer cur : selectedItems)
			// TODO Reverse Order
			for(int i=selectedItems.size()-1; i>=0; i--)
			{
				// applicationsArray.get(cur.intValue());
				// apkList.add(applicationsArray.get(cur.intValue()));
				// applicationsArray.get(selectedItems.get(i));
				apkList.add(applicationsArray.get(selectedItems.get(i)));
			}
			if((!(apkList.isEmpty()))||
				((apkList.size()>0)))
			{
				try
				{
					restoreProcess=new RestoreThread(	isRoot(),
														apkList,
														this);
				}
				catch(IOException ioe)
				{
					setMessage(translate_IO_ERROR);
				}
				restoreProcess.start();
				restoreButton.setText(translate_CURRENT_WORKING);
				restoreButton.setClickable(false);
				restoreSettingsButton.setText(translate_WAIT_MSG);
				restoreSettingsButton.setClickable(false);
			}
			else
			{
				printMessage(translate_NO_FILE_SELECTION);
			}
		}
		if(settingsButton!=null)
		{
			startActivityForResult(	new Intent(	this,
												RestoreSettings.class),
									SETTINGS_ACTIVITY);
		}
	}

	@Override
	public void onActivityResult(	int requestCode,
									int resultCode,
									Intent data)
	{
		log("                              ");
		Utilities.sortList(	resourceNames,
							getSort());
		refreshListAdapterUsingInvalidateAndNotifyDataSetChanged();
	}

	private void refreshSetListAdapterNotes()
	{
		log("                              ");
		if(notes!=null)
		{
			log("                              [setListAdapter]");
			setListAdapter(notes);
		}
		updateStatusBarItemsCount();
	}

	private void refreshListAdapterUsingInvalidateAndNotifyDataSetChanged()
	{
		log("                              ");
		if(notes!=null)
		{
			log("                              [notifyDataSetChanged]");
			// this.getListView().invalidate();
			// notes.notifyDataSetInvalidated();
			notes.notifyDataSetChanged();
		}
		updateStatusBarItemsCount();
	}

	private void removeUninstalledApplicationsFromSelectedItemsList()
	{
		log("                              ");
		selectedItems.clear();
	}

	private int getListTotalItemsCount()
	{
		log("                              ");
		return listTotalItemsCount;
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		log("                              ");
		restoreHandler.removeCallbacks(restoreUpdateTask);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		log("                              ");
		restoreHandler.removeCallbacks(restoreUpdateTask);
		restoreHandler.postDelayed(	restoreUpdateTask,
									800);
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();
		log("                              ");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		log("                              ");
		if(restoreHandler!=null)
		{
			restoreHandler.removeCallbacks(restoreUpdateTask);
			restoreHandler=null;
		}
		if(restoreProcess!=null)
		{
			getRestoreProcess().exit();
			restoreProcess=null;
		}
		unregisgerBroadcastReceiver();
	}

	private String customDir()
	{
		log("                              [customDir]");
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		return sharedPreferences.getString(	translate_BACKUP_DIR,
											translate_BACKUP_DIR_VALUE);
	}

	private boolean isRoot()
	{
		log("                              ");
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		return sharedPreferences.getBoolean(RESTORE_CHECKBOX,
											RESTORE_CHECKBOX_VALUE);
	}

	public int getSort()
	{
		log("                              ");
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String types=sharedPreferences.getString(	RESTORE_LIST_TYPE,
													RESTORE_LIST_TYPE_VALUE);
		String orders=sharedPreferences.getString(	RESTORE_LIST_ORDER,
													RESTORE_LIST_ORDER_VALUE);
		return Utilities.getOrder(	types,
									orders);
	}

	public void printMessage(String message)
	{
		log("                              ");
		restoreTextView.setText("[ "+
								message+
								" ]");
		restoreStringView="[ "+
							message+
							" ]";
	}

	private void setMessage(String message)
	{
		log("                              ");
		restoreTextView.setText(message);
		restoreStringView=message;
	}

	private void sendBroadcastCommandToShowList()
	{
		log("                              ");
		Intent intent=new Intent();
		intent.setAction(ACTION);
		intent.putExtra(ACTION_VALUE,
						ACTION_VALUE_REFRESH_LIST_ADAPTER);
		sendBroadcast(intent);
	}

	private void log(String logMessage)
	{
		if(logMessage.startsWith(" ")||
			logMessage.startsWith("!"))
		{
			String clazz=Thread.currentThread()
								.getStackTrace()[3].getClassName();
			String metho=Thread.currentThread()
								.getStackTrace()[3].getMethodName();
			logMessage=logMessage+
						" ["+
						clazz.substring(clazz.lastIndexOf(".")+1)+
						"."+
						metho+
						"]";
		}
		Logger.log(	LOG_MAIN,
					logMessage,
					Logger.MAIN_SOFTWARE);
	}

	public class MessageReceiver extends
			BroadcastReceiver
	{
		@Override
		public void onReceive(	Context context,
								Intent intent)
		{
			if(intent.getAction()
						.equals(ACTION))
			{
				log("MessageReceiver               [onReceive]");
				switch(intent.getExtras()
								.getInt(ACTION_VALUE))
				{
					case ACTION_VALUE_REFRESH_LIST_ADAPTER:
						log("MessageReceiver               [refreshSetListAdapterNotes]");
						refreshSetListAdapterNotes();
					break;
					case ACTION_VALUE_ON_CLICK:
						log("MessageReceiver               [updateStatusBarItemsCount]");
						updateStatusBarItemsCount();
					break;
				}
			}
		}
	}

	private ProgressDialog createDialog()
	{
		log("                              ");
		ProgressDialog builder=new ProgressDialog(this);
		builder.setIndeterminate(true);
		builder.setCancelable(true);
		builder.setTitle(translate_LOADING_TITLE);
		builder.setMessage(translate_LOADING_MESSAGE);
		return builder;
	}

	protected Dialog onCreateDialog(int id)
	{
		log("                              ");
		switch(id)
		{
			case CREATE_DIALOG:
				return createDialog();
			default:
		}
		return null;
	}

	private class TaskLoader extends
			AsyncTask<Object,Object,Object>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			log("TaskLoader                    [onPreExecute]");
			showDialog(CREATE_DIALOG);
		}

		@Override
		protected Object doInBackground(Object... arg0)
		{
			log("TaskLoader                    [doInBackground]");
			generateApplicationsList();
			sendBroadcastCommandToShowList();
			return arg0;
		}

		@Override
		protected void onPostExecute(Object result)
		{
			super.onPostExecute(result);
			log("TaskLoader                    [onPostExecute]");
			log("TaskLoader                    [dismissDialog] IN");
			dismissDialog(CREATE_DIALOG);
			log("TaskLoader                    [dismissDialog] OUT");
		}
	}
}
