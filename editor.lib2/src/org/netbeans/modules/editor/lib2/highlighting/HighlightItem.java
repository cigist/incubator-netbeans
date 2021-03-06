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

package org.netbeans.modules.editor.lib2.highlighting;

import javax.swing.text.AttributeSet;

/**
 * Immutable single item for highlighting without a specified start (it's derived from end of previous item).
 *
 * @author Miloslav Metelka
 */
public class HighlightItem {
    
    /** End offset of the highlight. Start offset is derived from end of previous item. */
    private final int endOffset; // 8 + 4 = 12 bytes

    /** Attributes of highlight. Null for no highlight in the particular area. */
    private final AttributeSet attrs; // 12 + 4 = 16 bytes

    public HighlightItem(int endOffset, AttributeSet attrs) {
        this.endOffset = endOffset;
        this.attrs = attrs;
    }
  
    /**
     * End offset of the highlighting item.
     * 
     * @return end offset of the highlight
     */
    public int getEndOffset() {
        return endOffset;
    }
    
    public int getEndSplitOffset() {
        return 0;
    }

    /**
     * Attributes of the highlight.
     *
     * @return attributes or null if this area has no highlighting.
     */
    public AttributeSet getAttributes() {
        return attrs;
    }

    @Override
    public String toString() {
        return "<?," + endOffset + ">: " + attrs; // NOI18N
    }

    public String toString(int startOffset) {
        return "<" + startOffset + "," + endOffset + ">: " + attrs; // NOI18N
    }

}
