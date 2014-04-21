package fi.sokira.metropolialukkari.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Realization implements Parcelable {

	private String code;
	private String name;
	private List<StudentGroup> studentGroups;
	private Date startDate;
	private Date endDate;
	
	public Realization() {
	}
	
	public Realization( Parcel in) {
		this.code = in.readString();
		this.name = in.readString();
		this.studentGroups = new ArrayList<StudentGroup>();
		in.readTypedList( studentGroups, StudentGroup.CREATOR);
		DateFormat sdf = DateFormat.getDateTimeInstance();
		try {
			this.startDate = sdf.parse( in.readString());
			this.endDate = sdf.parse( in.readString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<StudentGroup> getStudentGroups() {
		return studentGroups;
	}

	public void setStudentGroups(List<StudentGroup> studentGroups) {
		this.studentGroups = studentGroups;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString( code);
		dest.writeString( name);
		dest.writeTypedList( studentGroups);
		dest.writeString( startDate.toString());
		dest.writeString( endDate.toString());
	}
	
	public static final Parcelable.Creator<Realization> CREATOR 
			= new Creator<Realization>() {
		
		@Override
		public Realization[] newArray(int size) {
			return new Realization [size];
		}
		
		@Override
		public Realization createFromParcel(Parcel source) {
			return new Realization( source);
		}
	};
}
