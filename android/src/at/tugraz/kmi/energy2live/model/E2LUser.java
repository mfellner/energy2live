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

package at.tugraz.kmi.energy2live.model;

import java.io.Serializable;
import java.util.List;

import at.tugraz.kmi.energy2live.model.implementation.E2LActivityImplementation;

public interface E2LUser extends Serializable {
	String getName();

	void setName(String name);

	String getPassword();

	void setPassword(String password);

	String getFullName();

	void setFullName(String fullname);

	String getEmail();

	void setEmail(String email);

	String getHomePage();

	void setHomePage(String homepage);

	int getPrivacy();

	void setPrivacy(int privacy);

	List<E2LActivityImplementation> getActivities();

	void setActivities(List<E2LActivityImplementation> activities);
}
