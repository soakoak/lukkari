package fi.sokira.metropolialukkari.models;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class ResultItem implements Parcelable {

	@Override
	public abstract int describeContents();
	
	@Override
	public abstract void writeToParcel(Parcel dest, int flags);	
	
}
