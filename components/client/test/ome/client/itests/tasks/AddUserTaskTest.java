/*
 * ome.client.itests.tasks.AddUserTaskTest
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package ome.client.itests.tasks;

import org.testng.annotations.*;

import java.util.Properties;
import java.util.UUID;

import ome.model.meta.ExperimenterGroup;
import ome.system.Login;
import ome.system.ServiceFactory;
import ome.util.tasks.Run;
import ome.util.tasks.admin.AddUserTask;

import junit.framework.TestCase;

public class AddUserTaskTest extends AbstractAdminTaskTest {

	@Test
	public void testSimple() throws Exception {
		String group = makeGroup();
		Properties p = new Properties();
		p.setProperty("omename",UUID.randomUUID().toString());
		p.setProperty("firstname", "task");
		p.setProperty("lastname", "test");
		p.setProperty("group", group);
		new AddUserTask(root,p).run();
	}
	
	@Test
	public void testViaCommandLine() throws Exception {
		String group = makeGroup();
		Run.main(join(rootString, 
				new String[]{
				"task=admin.AddUserTask",
				"firstname=task",
				"lastname=test",
				"group="+group,
				"omename="+UUID.randomUUID().toString()}));
	}
	
	private String makeGroup() {
		String uuid = UUID.randomUUID().toString();
		ExperimenterGroup group = new ExperimenterGroup();
		group.setName(uuid);
		root.getAdminService().createGroup(group);
		return uuid;
	}

}
