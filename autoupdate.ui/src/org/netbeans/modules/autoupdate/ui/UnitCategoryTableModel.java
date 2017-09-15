/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2008 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.autoupdate.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Jiri Rechtacek, Radek Matous
 */
public abstract class UnitCategoryTableModel extends AbstractTableModel {
    private static final String EXPAND_STATE = "ExpandState";//NOI18N
    private List<Unit> unitData = Collections.emptyList ();
    private List<Unit> featuretData = Collections.emptyList ();    
    private List<Unit> standAloneModules = Collections.emptyList ();    
    
    private List<UpdateUnitListener> listeners = new ArrayList<UpdateUnitListener> ();
    private String filter = "";//NOI18N
    private Comparator<Unit> unitCmp;
    
    //null == NOT_EXPANDABLE, true == EXPANDED, false == COLLAPSED
    private Boolean isExpanded = null; 
        
    public static enum Type {
        INSTALLED,UPDATE,AVAILABLE,LOCAL
    }
            
    /** Creates a new instance of CategoryTableModel */
    public UnitCategoryTableModel () {
    }
    
    List<Unit> getUnits () {
        return unitData;
    }

    boolean supportsTwoViews() {
        return false;
    }

    static Map<String, Boolean> captureState(List<Unit> units) {
        Map<String,Boolean> retval = new HashMap<String, Boolean>();
        for (Unit unit : units) {
            retval.put(unit.updateUnit.getCodeName(), unit.isMarked());
        }        
        return retval;
    }
    
    static void  restoreState(List<Unit> newUnits, Map<String, Boolean> capturedState, boolean isMarkedAsDefault) {
        for (Unit unit : newUnits) {
            Boolean isChecked = capturedState.get(unit.updateUnit.getCodeName());
            if (isChecked != null) {
                try {
                    if (isChecked.booleanValue() && !unit.isMarked() && unit.canBeMarked()) {
                        unit.setMarked(true);
                    } else if (!isChecked.booleanValue() && unit.isMarked() && unit.canBeMarked()) {
                        unit.setMarked(false);
                    }
                } catch (IllegalArgumentException ex) {
                    Unit.log.log(Level.FINE, "Cannot change the unit state", ex);
                }
            } else if (isMarkedAsDefault && !unit.isMarked() && unit.canBeMarked()) {
                unit.setMarked(true);
            }            
        }           
    }

    public static boolean isMarkedAsDefault(Type type) {
        return (type.equals(Type.LOCAL) || type.equals(Type.UPDATE)) ? true : false;
    }
    
    protected final boolean isMarkedAsDefault() {
        return isMarkedAsDefault(getType());
    }
    
    public abstract Object getValueAt (int row, int col);
    @Override
    public abstract Class getColumnClass (int c);
    public abstract Type getType ();
    public abstract boolean isSortAllowed (Object columnIdentifier);
    public abstract int getDownloadSize ();
    public abstract boolean needsRestart ();
    protected abstract Comparator<Unit> getComparator (final Object columnIdentifier, final boolean sortAscending);
    public abstract void setUnits (List<UpdateUnit> units);
    public String getTabTooltipText() {
        return null;
    }
    public abstract String getTabTitle();
    public final String getDecoratedTabTitle() {
        int count = getItemCount ();
        int rawCount = getRawItemCount ();
        String countInfo = (count == rawCount) ? String.valueOf (rawCount) :
            NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_Tabs_CountFormat", count, rawCount);
        String newName = NbBundle.getMessage (PluginManagerUI.class, "PluginManagerUI_Tabs_NameFormat", getTabTitle(), countInfo);
        return (rawCount == 0) ? getTabTitle() : newName;        
    }
        
    public boolean canBePrimaryTab() {
        return true;
    }
    public abstract int getTabIndex();
    public boolean isTabEnabled() {
        return true;
    }
    public String getToolTipText (int row, int col) {
        String retval = null;
        if (col == 0) {
            retval = getTooltipForCheckBox(row);
        } else if (col == 1) {
            retval = (String)getValueAt (row, 1);
        }
        return retval;
    }
    public int getMinWidth (JTableHeader header, int col) {
        return header.getHeaderRect (col).width;
    }
    public abstract int getPreferredWidth (JTableHeader header, int col);
    
    protected Comparator<Unit> getDefaultComparator() {
        return new Comparator<Unit>() {
            public int compare(Unit o1, Unit o2) {
                return Unit.compareCategories(o1, o2);
            }
        };
    }
    
    public final void sort (Object columnIdentifier, boolean sortAscending) {
        if (columnIdentifier == null) {
            setUnitComparator (getDefaultComparator ());
        } else {
            setUnitComparator (getComparator (columnIdentifier, sortAscending));
        }
        fireTableDataChanged ();
    }

    private String getTooltipForCheckBox(int row) {
        String key0 = null;
        switch (getType()) {
            case INSTALLED:
                key0 = "UnitTab_TooltipCheckBox_INSTALLED"; //NOI18N
                break;
            case UPDATE:
                key0 = "UnitTab_TooltipCheckBox_UPDATE"; //NOI18N
                break;
            case AVAILABLE:
                key0 = "UnitTab_TooltipCheckBox_AVAILABLE"; //NOI18N
                break;
            case LOCAL:
                key0 = "UnitTab_TooltipCheckBox_LOCAL"; //NOI18N
                break;
        }
        return (key0 != null) ? NbBundle.getMessage (UnitCategoryTableModel.class, key0, (String)getValueAt (row, 1)) : null;
    }
    
    private final void setData(List<UnitCategory> data,  Comparator<Unit> unitCmp) {
        this.unitCmp = unitCmp != null ? unitCmp : getDefaultComparator();
        featuretData = null;
        if (data != null) {
            this.unitData = Collections.emptyList();
            this.unitData = new ArrayList<Unit>();
            for (UnitCategory unitCategory : data) {
                this.unitData.addAll(unitCategory.getUnits());
            }
            standAloneModules = new ArrayList<Unit> ();
            for (Unit u : unitData) {
                if (UpdateManager.TYPE.STANDALONE_MODULE.equals(u.updateUnit.getType())) {
                    standAloneModules.add(u);
                }
            }
            computeExtensionState();
        } else {
            assert unitData != null;
        }
        if (unitCmp != null) {
            Collections.sort(unitData,unitCmp);
        }
        this.fireTableDataChanged();
    }
    
    
    private void computeExtensionState() {
        boolean exp = isExpandableType(getType()) && !Utilities.modulesOnly() && !getVisibleUnits(getFeatureList(), getFilter(), false).isEmpty() && !getVisibleUnits(standAloneModules, getFilter(), false).isEmpty();
        if (exp) {
            isExpanded = NbPreferences.forModule(UnitCategoryTableModel.class).getBoolean(EXPAND_STATE, false);
        } else {
            isExpanded = null;
        }
    }
    
    public void setUnitComparator (Comparator<Unit> comparator) {
        setData (null, comparator);
    }
        
    public final void setData (List<UnitCategory> data) {
        setData (data, unitCmp);
    }
    
    public void setFilter (final String filter, final Runnable runAfterwards) {
        SwingUtilities.invokeLater (new Runnable () {
            public void run () {
                synchronized(UnitCategoryTableModel.class) {
                    UnitCategoryTableModel.this.filter = filter.toLowerCase ();
                }                
                computeExtensionState();
                fireFilterChange();
                if (runAfterwards != null) {
                    runAfterwards.run();
                }
            }
        });
    }
    
    public String getFilter () {
        synchronized(UnitCategoryTableModel.class) {
            return this.filter == null ? "" : this.filter;
        }
    }
        
    public void addUpdateUnitListener (UpdateUnitListener l) {
        listeners.add (l);
    }
    
    public void removeUpdateUnitListener (UpdateUnitListener l) {
        listeners.remove (l);
    }
    
    void fireUpdataUnitChange () {
        assert listeners != null : "UpdateUnitListener found.";
        for (UpdateUnitListener l : listeners) {
            l.updateUnitsChanged ();
        }
    }
    
    void fireButtonsChange () {
        assert listeners != null : "UpdateUnitListener found.";
        for (UpdateUnitListener l : listeners) {
            l.buttonsChanged ();
        }
    }

    void fireFilterChange () {
        assert listeners != null : "UpdateUnitListener found.";
        for (UpdateUnitListener l : listeners) {
            l.filterChanged();
        }
    }
    
    List<Unit> getVisibleUnits () {
        return getVisibleUnits(getUnits(), getFilter(), true);
    }

    private List<Unit> getVisibleUnits (List<Unit> units, String filter, boolean filterAlsoStandardModules) {
        List<Unit> retval = new ArrayList<Unit>();
        for (Unit unit : units) {
            if (filterAlsoStandardModules) {
                if (unit.isVisible(filter) && (!isExpandable()  || isExpanded() || UpdateManager.TYPE.FEATURE.equals(unit.updateUnit.getType()))) {
                    retval.add(unit);
                }                
            } else {
                if (unit.isVisible(filter)) {
                    retval.add(unit);
                }
            }
        }
        return retval;
    }
    
    
    public int getRowCount () {
        int retval = getVisibleUnits ().size ();
        return (isExpansionControlPresent()) ? (retval + 1) : retval;
    }
    
    public int getRawItemCount () {
        return unitData.size ();
    }
    
    public int getItemCount () {        
        return getVisibleUnits ().size ();
    }
    
    
    public Collection<Unit> getMarkedUnits() {
        List<Unit> markedUnits = new ArrayList<Unit> ();
        List<Unit> units = getUnits();

        for (Unit u : units) {
            if (u.isMarked()) {
                markedUnits.add(u);
            }
        }
        return markedUnits;
    }
            
    public Unit getUnitAtRow (int row) {
        if (row < 0 ) {
            return null;
        }
        List <Unit> list = getVisibleUnits();
        return list.size () <= row ? null : list.get (row);
    }
    
    public boolean isExpansionControlAtRow (int row) {
        return ((row + 1) == getRowCount()) && isExpansionControlPresent();
    }
    
    public String getExpansionControlText() {
        assert isExpansionControlPresent();
        String bundleKey = isExpanded() ? "Less_Command_Text" : "More_Command_Text";//NOI18N
        return NbBundle.getMessage(UnitCategoryTableModel.class, bundleKey, getVisibleUnits(getStandAloneModules(), getFilter(), false).size());
    }
    
    public boolean isExpandable () {        
        return isExpanded != null;
    }
    
    public boolean isExpansionControlPresent() {
        return isExpandable () && !getFeatureList().isEmpty() && !getVisibleUnits(getStandAloneModules(), getFilter(), false).isEmpty();
    }

    public void setExpanded (Boolean expanded) {        
        this.isExpanded = expanded;
        featuretData = null;
        if (expanded != null) {
            NbPreferences.forModule(UnitCategoryTableModel.class).putBoolean(EXPAND_STATE, expanded);
        }
    }
    
    public boolean isExpanded () {        
        boolean retval = isExpandable();
        if (retval) {
            retval = isExpanded != null && isExpanded;
        }
        return retval;
    }
    
    public boolean isCollapsed () {
        boolean retval = isExpandable();
        if (retval) {
            retval = isExpanded != null && !isExpanded;
        }
        return retval;
    }
    
    private List<Unit> getFeatureList() {
        if (featuretData == null) {
            featuretData = new ArrayList<Unit> (unitData);
            featuretData.removeAll(getStandAloneModules());
            if (unitCmp != null) {
                Collections.sort(featuretData,unitCmp);
            }            
        }
        return featuretData;
    }
    
    List<Unit> getStandAloneModules() {
        List<Unit> retval = standAloneModules;
        if (retval == null) {
            retval = Collections.emptyList();
        }        
        return retval;
    }
            
    public static boolean isExpandableType (Type type) {
        return type.equals(UnitCategoryTableModel.Type.AVAILABLE) || 
                type.equals(UnitCategoryTableModel.Type.UPDATE);
    }
            
    @Override
    public boolean isCellEditable (int row, int col) {
        if (isExpansionControlAtRow(row)) {
            return false;
        }
        RequestProcessor.Task t = PluginManagerUI.getRunningTask ();
        return (t == null || t.isFinished ()) && col == 0 && Boolean.class.equals (getColumnClass (col));
    }
    
}