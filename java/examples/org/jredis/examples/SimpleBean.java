/*
 *   Copyright 2009 Joubin Houshyar
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *    
 *   http://www.apache.org/licenses/LICENSE-2.0
 *    
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jredis.examples;

import java.io.Serializable;

public class SimpleBean implements Serializable {
	/**  */
	private static final long	serialVersionUID	= 0xA0be11a;
	
	public final long getCreated_on() {return created_on;}
	public final void setCreated_on(long created_on) {this.created_on = created_on;}
	public final String getName() {return name;}
	public final void setName(String name) {this.name = name;}
	public final byte[] getData() { return data;}
	public final void setData(byte[] data) { this.data = data;}
	private long   created_on;
	private String name;
	private byte[] data;
	public SimpleBean() {created_on = System.currentTimeMillis();}
	public SimpleBean(String string) { this(); name = string;}
	@Override public String toString() { return "[" + getClass().getSimpleName() + " | name: " + getName() + " created on: " + getCreated_on() + "]"; }
	@Override public boolean equals (Object o) {
		boolean res = false;
		try {
			SimpleBean isItMe = (SimpleBean) o;
			res = isItMe.getName().equals(name) && isItMe.getCreated_on()==this.created_on;
		}
		catch (ClassCastException e) {
			return false;
		}
		return res;
	}
}