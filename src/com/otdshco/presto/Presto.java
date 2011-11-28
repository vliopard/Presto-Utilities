package com.otdshco.presto;
import com.otdshco.backup.Backup;
import com.otdshco.restore.Restore;
import com.otdshco.uninstall.Uninstall;
import com.otdshco.bootlogo.BootLogoChanger;
import com.otdshco.presto.R;
import com.otdshco.tools.Logger;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Presto extends
		Activity implements
				OnClickListener
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		log("Starting Presto");
		setContentView(R.layout.presto_main);
		Button backupButton=(Button)findViewById(R.id.backup_main);
		backupButton.setOnClickListener(this);
		Button restoreButton=(Button)findViewById(R.id.restore_main);
		restoreButton.setOnClickListener(this);
		Button bootlogoButton=(Button)findViewById(R.id.boot_logo_main);
		bootlogoButton.setOnClickListener(this);
		Button uninstallButton=(Button)findViewById(R.id.uninstall_main);
		uninstallButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		Button restoreButton=(Button)v.findViewById(R.id.restore_main);
		if (restoreButton!=null)
		{
			Intent restoreActivity=new Intent(	this,
												Restore.class);
			startActivity(restoreActivity);
		}
		Button backupButton=(Button)v.findViewById(R.id.backup_main);
		if (backupButton!=null)
		{
			Intent backupActivity=new Intent(	this,
												Backup.class);
			startActivity(backupActivity);
		}
		Button bootlogoButton=(Button)v.findViewById(R.id.boot_logo_main);
		if (bootlogoButton!=null)
		{
			Intent backupActivity=new Intent(	this,
												BootLogoChanger.class);
			startActivity(backupActivity);
		}
		Button uninstallButton=(Button)v.findViewById(R.id.uninstall_main);
		if (uninstallButton!=null)
		{
			Intent backupActivity=new Intent(	this,
												Uninstall.class);
			startActivity(backupActivity);
		}
	}

	private void log(String logMessage)
	{
		Logger.log(	"Presto",
					logMessage);
	}
}
