package fi.sokira.metropolialukkari.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Resource implements Parcelable {

	private String type;
	private String code;
	private String name;
	
	public Resource() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	protected Resource(Parcel in) {
        type = in.readString();
        code = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(code);
        dest.writeString(name);
    }

    public static final Parcelable.Creator<Resource> CREATOR = new Parcelable.Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };
}
