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

package org.netbeans.modules.xml.wsdl.model.extensions.soap;

import java.util.Collection;

/**
 *
 * @author Nam Nguyen
 */
public interface SOAPMessageBase extends SOAPComponent {
    public static final String NAMESPACE_PROPERTY = "namespace";
    public static final String USE_PROPERTY = "use";
    public static final String ENCODING_STYLE_PROPERTY = "encodingStyle";
    
    void setNamespace(String namespaceURI);
    String getNamespace();
    
    public enum Use {
        LITERAL("literal"), ENCODED("encoded");
        Use(String tag) {
            this.tag = tag;
        }
        public String toString() { return tag; }
        private String tag;
    }
    
    void setUse(Use use);
    Use getUse();   
    
    Collection<String> getEncodingStyles();
    void addEncodingStyle(String encodingStyle);
    void removeEncodingStyle(String encodingStyle);
}
