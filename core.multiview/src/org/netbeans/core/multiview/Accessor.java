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

package org.netbeans.core.multiview;

import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;

/**
 * @author  mkleint
 */
public abstract class Accessor {

    protected static Accessor DEFAULT = null;

    static {
        // invokes static initializer of Item.class
        // that will assign value to the DEFAULT field above
        Class c = MultiViewPerspective.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    public abstract MultiViewPerspective createPerspective(MultiViewDescription desc);
    
//    public abstract MultiViewPerspectiveComponent createPersComponent(MultiViewElement elem);
    
    public abstract MultiViewHandler createHandler(MultiViewHandlerDelegate delegate);
    
//    public abstract MultiViewElement extractElement(MultiViewPerspectiveComponent comp);
    
    public abstract MultiViewDescription extractDescription(MultiViewPerspective perspective);
    
}
