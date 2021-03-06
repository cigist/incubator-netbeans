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

package org.netbeans.modules.mylyn.util;

import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 *
 * @author Ondrej Vrabec
 */
public final class NbTaskDataState {
    private final ITaskDataWorkingCopy state;

    NbTaskDataState (ITaskDataWorkingCopy state) {
        this.state = state;
    }

    public TaskData getLastReadData () {
        return state.getLastReadData();
    }

    public TaskData getLocalData () {
        return state.getLocalData();
    }

    public TaskData getRepositoryData () {
        return state.getRepositoryData();
    }
    
    public TaskData getLocalEdits () {
        return state.getEditsData();
    }
    
}
