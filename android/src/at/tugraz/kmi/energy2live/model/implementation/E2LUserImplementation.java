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

import java.util.List;

import at.tugraz.kmi.energy2live.model.E2LUser;

import com.j256.ormlite.field.DatabaseField;

public class E2LUserImplementation implements E2LUser {
	private static final long serialVersionUID = -1669538222952789588L;

	@DatabaseField(generatedId = true)
	private Integer mId;

	@DatabaseField
	private String mName;

	@DatabaseField
	private String mPassword;

	@DatabaseField
	private String mFullName;

	@DatabaseField
	private String mEmail;

	@DatabaseField
	private String mHomePage;

	@DatabaseField
	private int mPrivacy;

	@DatabaseField
	private List<E2LActivityImplementation> mActivities;

	public E2LUserImplementation() {

	}

	// TODO add constructor with email address
	public E2LUserImplementation(String name, String password) {
		mName = name;
		mPassword = password;
	}

	public Integer getId() {
		return mId;
	}

	public void setId(Integer id) {
		mId = id;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public void setName(String name) {
		mName = name;
	}

	@Override
	public String getPassword() {
		return mPassword;
	}

	@Override
	public void setPassword(String password) {
		mPassword = password;
	}

	@Override
	public String getFullName() {
		return mFullName;
	}

	@Override
	public void setFullName(String fullname) {
		mFullName = fullname;
	}

	@Override
	public String getEmail() {
		return mEmail;
	}

	@Override
	public void setEmail(String email) {
		mEmail = email;
	}

	@Override
	public String getHomePage() {
		return mHomePage;
	}

	@Override
	public void setHomePage(String homepage) {
		mHomePage = homepage;
	}

	@Override
	public int getPrivacy() {
		return mPrivacy;
	}

	@Override
	public void setPrivacy(int privacy) {
		mPrivacy = privacy;
	}

	@Override
	public List<E2LActivityImplementation> getActivities() {
		return mActivities;
	}

	@Override
	public void setActivities(List<E2LActivityImplementation> activities) {
		mActivities = activities;
	}

}
