package fi.sokira.metropolialukkari.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MpoliaStudentGroup implements Parcelable {
	
	private String code;
	
	public MpoliaStudentGroup() {
	}
	
	public MpoliaStudentGroup( Parcel source) {
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
	
	public static final Parcelable.Creator<MpoliaStudentGroup> CREATOR 
			= new Creator<MpoliaStudentGroup>() {
	
		@Override
		public MpoliaStudentGroup[] newArray(int size) {
			return new MpoliaStudentGroup[size];
		}
		
		@Override
		public MpoliaStudentGroup createFromParcel(Parcel source) {
			return new MpoliaStudentGroup(source);
		}
	};
}
