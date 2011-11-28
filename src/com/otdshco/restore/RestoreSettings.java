package com.otdshco.restore;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.otdshco.presto.R;

public class RestoreSettings extends
		PreferenceActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.restore_sample_preferences);
	}
}
