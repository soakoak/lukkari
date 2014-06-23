package fi.sokira.metropolialukkari.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MpoliaReservation extends MpoliaResultItem {

	private String subject;
	private Date startDate;
	private Date endDate;
	private List<MpoliaResource> resources;
	
	public MpoliaReservation() {
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public List<MpoliaResource> getResources() {
		return resources;
	}

	public void setResources(List<MpoliaResource> resources) {
		this.resources = resources;
	}
	
    protected MpoliaReservation(Parcel in) {
        subject = in.readString();
        long tmpStartDate = in.readLong();
        startDate = tmpStartDate != -1 ? new Date(tmpStartDate) : null;
        long tmpEndDate = in.readLong();
        endDate = tmpEndDate != -1 ? new Date(tmpEndDate) : null;
        if (in.readByte() == 0x01) {
            resources = new ArrayList<MpoliaResource>();
            in.readList(resources, MpoliaResource.class.getClassLoader());
        } else {
            resources = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subject);
        dest.writeLong(startDate != null ? startDate.getTime() : -1L);
        dest.writeLong(endDate != null ? endDate.getTime() : -1L);
        if (resources == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(resources);
        }
    }

    public static final Parcelable.Creator<MpoliaReservation> CREATOR = new Parcelable.Creator<MpoliaReservation>() {
        @Override
        public MpoliaReservation createFromParcel(Parcel in) {
            return new MpoliaReservation(in);
        }

        @Override
        public MpoliaReservation[] newArray(int size) {
            return new MpoliaReservation[size];
        }
    };
}
