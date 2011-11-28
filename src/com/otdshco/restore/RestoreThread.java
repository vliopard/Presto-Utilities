package com.otdshco.restore;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.otdshco.tools.Logger;
import com.otdshco.tools.PackageProperties;
import com.otdshco.tools.Su;

public class RestoreThread extends
		Thread
{
	private static final String	LOG_MAIN			="RestoreThread";
	private String				messageBuffer		="";
	private String				running				="";
	private boolean				massiveCopyEnabled	=false;
	private Su					su;
	private ArrayList<String>	apkList;
	private Activity			mainBackup;

	public RestoreThread(	boolean mce,
							ArrayList<String> apkFiles,
							Activity activityBackup) throws IOException
	{
		apkList=apkFiles;
		mainBackup=activityBackup;
		massiveCopyEnabled=mce;
	}

	private void set(String message)
	{
		messageBuffer=message;
		log(messageBuffer);
	}

	public String get()
	{
		return messageBuffer;
	}

	public void update()
	{
		try
		{
			String tmpBuffer=su.getMessage();
			if (tmpBuffer.trim()!="")
			{
				set(tmpBuffer.trim());
			}
		}
		catch(NullPointerException npe)
		{
			log("Restore Update NPE: "+
				npe);
		}
	}

	public String isWorking()
	{
		return running;
	}

	public void run()
	{
		running="WORKING";
		try
		{
			su=new Su(massiveCopyEnabled);
			doRestore(massiveCopyEnabled);
			su._exit();
		}
		catch(IOException ioe)
		{
			set("run(IOException)");
		}
		catch(InterruptedException ie)
		{
			set("run(IntException)");
		}
		running="DONE";
	}

	private String getPackName(String pack)
	{
		PackageProperties pp=new PackageProperties(	pack,
													mainBackup);
		return pp.getName();
	}

	private void doRestore(boolean mce)	throws IOException,
										InterruptedException
	{
		if (mce)
		{
			for(String apk : apkList)
			{
				String apkname=getPackName(apk);
				set(apkname);
				if (su._install(apk)==0)
				{
					set(apkname+
						" OK");
				}
				else
				{
					set(apkname+
						" FAIL");
				}
			}
		}
		else
		{
			for(String apk : apkList)
			{
				String apkname=getPackName(apk);
				set(apkname);
				Intent intent=new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(	Uri.fromFile(new File(apk)),
										"application/vnd.android.package-archive");
				// Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=com.package.name"));
				mainBackup.startActivity(intent);
			}
		}
	}

	public void exit()
	{
		try
		{
			su.stopWork();
			su._exit();
		}
		catch(InterruptedException ie)
		{
			log("EXIT Interrupted Exception");
		}
		catch(IOException ioe)
		{
			log("EXIT Input Output Exception");
		}
		catch(NullPointerException npe)
		{
			log("EXIT Null Pointer Exception");
		}
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
					Logger.MAIN_THREAD);
	}
}
