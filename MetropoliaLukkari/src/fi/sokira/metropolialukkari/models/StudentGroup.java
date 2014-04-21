package fi.sokira.metropolialukkari.models;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentGroup implements Parcelable {
	
	private String code;
	
	public StudentGroup() {
	}
	
	public StudentGroup( Parcel source) {
		this.code = source.readString();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString( code);
	}
	
	public static final Parcelable.Creator<StudentGroup> CREATOR 
			= new Creator<StudentGroup>() {
	
		@Override
		public StudentGroup[] newArray(int size) {
			return new StudentGroup[size];
		}
		
		@Override
		public StudentGroup createFromParcel(Parcel source) {
			return new StudentGroup(source);
		}
	};
}
