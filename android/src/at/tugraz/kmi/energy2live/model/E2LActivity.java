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
import java.util.ArrayList;
import java.util.Date;

import at.tugraz.kmi.energy2live.model.implementation.E2LActivityLocationImplementation;

public interface E2LActivity extends Serializable {
	public String getName();

	public void setName(String name);

	public Date getTime();

	public void setTime(Date time);

	public long getDuration();

	public void setDuration(long duration);

	public ArrayList<E2LActivityLocationImplementation> getLocations();

	public void setLocations(ArrayList<E2LActivityLocationImplementation> locations);
}
