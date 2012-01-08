/** Copyright 2011 Maximilian Fellner, Gernot Solic, Florian Sumann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package at.tugraz.kmi.energy2live.model.implementation;

import java.text.SimpleDateFormat;
import java.util.Date;

import at.tugraz.kmi.energy2live.model.E2LActivity;

import com.j256.ormlite.field.DatabaseField;

public class E2LActivityImplementation implements E2LActivity {
	private static final long serialVersionUID = 5687427484069917585L;

	@DatabaseField(generatedId = true)
	private Integer mId;

	@DatabaseField
	private String mName;

	@DatabaseField
	private Date mTime;

	@DatabaseField
	private long mDuration;

	private final SimpleDateFormat sdfCompact = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public E2LActivityImplementation() {

	}

	public E2LActivityImplementation(String name) {
		mName = name;
	}

	public void setId(Integer id) {
		mId = id;
	}

	public Integer getId() {
		return mId;
	}

	@Override
	public void setName(String name) {
		mName = name;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getDateTime() {
		return sdfCompact.format(mTime.getTime());
	}

	public Date getTime() {
		return mTime;
	}

	public void setTime(Date time) {
		mTime = time;
	}

	public void setDuration(long duration) {
		if (duration < 0)
			throw new RuntimeException("negative duration");
		mDuration = duration;
	}

	public long getDuration() {
		return mDuration;
	}

	public boolean hasEmptyFields() {
		if (mName == null || mName.length() == 0 || mTime == null || mDuration == 0)
			return true;
		return false;
	}
}
