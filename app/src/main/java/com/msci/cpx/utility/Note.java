package com.msci.cpx.utility;

public class Note {

	public Note(long note_id, String note_Title, String content, long created_time, long modified_time) {
		mNoteID = note_id;
		mNoteTitle = note_Title;
		mNoteContent = content;
		mCreatedTime = created_time;
		mLastModifiedTime = modified_time;		
	}

	public Note() {
		mNoteID = -1;
		mNoteTitle = null;
		mNoteContent = null;
		mCreatedTime = -1;
		mLastModifiedTime = -1;
	}
	
	public long mNoteID;
	public String mNoteTitle;
	public String mNoteContent;
	public long mCreatedTime;
	public long mLastModifiedTime;
}
