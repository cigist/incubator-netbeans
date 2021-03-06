/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.jellytools.actions;

import java.io.IOException;
import junit.framework.Test;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.SourcePackagesNode;
import org.netbeans.jellytools.properties.PropertySheetOperator;

/** Test of SortByCategoryAction calss.
 *
 * @author Jiri Skrivanek
 */
public class SortByCategoryActionTest extends JellyTestCase {

    /** constructor required by JUnit
     * @param testName method name to be used as testcase
     */
    public SortByCategoryActionTest(String testName) {
        super(testName);
    }

    /** method used for explicit testsuite definition
     */
    public static Test suite() {
        return createModuleTest(SortByCategoryActionTest.class);
    }

    @Override
    protected void setUp() throws IOException {
        openDataProjects("SampleProject");
    }

    /** Test performPopup */
    public void testPerformPopup() {
        Node node = new Node(new SourcePackagesNode("SampleProject"), "sample1|SampleClass1.java"); // NOI18N
        new PropertiesAction().perform(node);
        PropertySheetOperator pso = new PropertySheetOperator("SampleClass1.java"); // NOI18N
        // set by name sorting
        pso.sortByName();
        int oldCount = pso.tblSheet().getRowCount();
        new SortByCategoryAction().perform(pso);
        int newCount = pso.tblSheet().getRowCount();
        pso.close();
        // if sorted by name there are no categories displayed in property sheet
        assertTrue("Sort by name failed.", oldCount < newCount); // NOI18N
    }
}
