package com.financialcontrol.utils;


public class AppConsts {

	
	public enum EntryActivityMode{
		ALL_ENTRIES(0),
		FILTER(1);
		
		private final int value;
		
		private EntryActivityMode(final int NewValue)
		{
			value = NewValue;
		}
		
		public int GetMode() { return value; }
	}
		
	
}
