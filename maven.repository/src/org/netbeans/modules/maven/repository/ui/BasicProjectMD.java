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

package org.netbeans.modules.maven.repository.ui;

import java.awt.Image;
import java.io.Serializable;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.modules.maven.indexer.api.ui.ArtifactViewer;
import org.netbeans.modules.maven.indexer.spi.ui.ArtifactViewerPanelProvider;
import static org.netbeans.modules.maven.repository.ui.Bundle.*;
import org.netbeans.modules.maven.spi.IconResources;
import org.netbeans.modules.maven.spi.nodes.NodeUtils;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 *
 * @author mkleint
 */
public class BasicProjectMD implements MultiViewDescription, Serializable {

    private Lookup lookup;

    BasicProjectMD(Lookup lkp) {
        lookup = lkp;
    }


    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Messages("TAB_Project=Project")
    @Override
    public String getDisplayName() {
        return TAB_Project();
    }

    @Override
    public Image getIcon() {
        return ImageUtilities.loadImage(IconResources.ICON_DEPENDENCY_JAR, true);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public String preferredID() {
        return ArtifactViewer.HINT_PROJECT;
    }

    @Override
    public MultiViewElement createElement() {
        return new ProjectInfoPanel(lookup);
    }

    @org.openide.util.lookup.ServiceProvider(service=ArtifactViewerPanelProvider.class, position=200)
    public static class Factory implements ArtifactViewerPanelProvider {

        @Override
        public MultiViewDescription createPanel(Lookup content) {
            return new BasicProjectMD(content);
        }
    }

}
