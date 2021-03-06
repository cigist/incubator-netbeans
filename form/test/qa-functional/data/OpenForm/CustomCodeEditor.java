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

package org.netbeans.modules.form.editors;

import javax.swing.text.Document;

import org.openide.nodes.Node;
import org.openide.loaders.DataObject;

/** Customizer for "code properties" used by JavaCodeGenerator.
 *
 * @author  vzboril
 */

public class CustomCodeEditor extends javax.swing.JPanel {
    /** Creates new form CustomCodeEditor */
    public CustomCodeEditor(Node.Property property, DataObject dataObject) {
        initComponents();

        codeEditorPane.setContentType("text/x-java");  // NOI18N
        try {
            codeEditorPane.setText((String) property.getValue()); 
        }
        catch (Exception ex) { // ignore - should not happen
            ex.printStackTrace();
        }

        java.util.ResourceBundle bundle =
            org.openide.util.NbBundle.getBundle(CustomCodeEditor.class);

        jLabel1.setText(bundle.getString("CustomCodeEditor.label1")); // NOI18N
        jLabel1.setDisplayedMnemonic(
            bundle.getString("CustomCodeEditor.label1.mnemonic").charAt(0)); // NOI18N
        jLabel1.setLabelFor(codeEditorPane);
        codeEditorPane.getDocument().putProperty(Document.StreamDescriptionProperty, dataObject);
        codeEditorPane.setPreferredSize(new java.awt.Dimension(440, 200));
        codeEditorPane.requestFocus();
        codeEditorPane.getCaret().setVisible(codeEditorPane.hasFocus());
        
        codeEditorPane.getAccessibleContext().setAccessibleDescription(
            bundle.getString("ACSD_CustomCodeEditor.label1")); // NOI18N
        getAccessibleContext().setAccessibleDescription(
            bundle.getString("ACSD_CustomCodeEditor")); // NOI18N
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        codeEditorPane = new javax.swing.JEditorPane();
        jLabel1 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        codeEditorPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                codeEditorPaneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                codeEditorPaneFocusLost(evt);
            }
        });

        jScrollPane1.setViewportView(codeEditorPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 0, 11);
        add(jScrollPane1, gridBagConstraints);

        jLabel1.setText("jLabel1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 11);
        add(jLabel1, gridBagConstraints);

    }//GEN-END:initComponents

    private void codeEditorPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codeEditorPaneFocusLost
        // Add your handling code here:
        codeEditorPane.getCaret().setVisible(codeEditorPane.hasFocus());
    }//GEN-LAST:event_codeEditorPaneFocusLost

    private void codeEditorPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codeEditorPaneFocusGained
        // Add your handling code here:
        codeEditorPane.getCaret().setVisible(codeEditorPane.hasFocus());
    }//GEN-LAST:event_codeEditorPaneFocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // Add your handling code here:
        codeEditorPane.requestFocus();
        codeEditorPane.getCaret().setVisible(true); // true is HARDCODED here due to BUG in MAC OS X
    }//GEN-LAST:event_formFocusGained

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        // Add your handling code here:
        codeEditorPane.requestFocus();
        codeEditorPane.getCaret().setVisible(codeEditorPane.hasFocus());
    }//GEN-LAST:event_formComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane codeEditorPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    
    public Object getPropertyValue() throws IllegalStateException {
        return codeEditorPane.getText();
    }
}
