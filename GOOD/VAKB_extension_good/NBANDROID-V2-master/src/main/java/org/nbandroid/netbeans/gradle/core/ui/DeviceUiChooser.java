/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nbandroid.netbeans.gradle.core.ui;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IDevice.DeviceState;
import com.android.sdklib.internal.avd.AvdInfo;
import com.android.sdklib.internal.avd.AvdManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.nbandroid.netbeans.gradle.avd.AvdSelector.LaunchData;
import org.nbandroid.netbeans.gradle.ui.customizer.LaunchDeviceListener;
import org.openide.util.NbBundle;

/**
 *
 * @author radim
 */
class DeviceUiChooser extends javax.swing.JPanel {
    // TODO use not-running AVDs
    // TODO filter by target platform

    private final AvdManager avdManager;
    private final AvdInfo[] infos;
    private final IDevice[] devices;
    private List<LaunchDeviceListener> listeners = new CopyOnWriteArrayList<LaunchDeviceListener>();
    private AvdUISelector avdUISelector;
    private Runnable dblClickCallback;

    /**
     * Creates new form DeviceUiChooser
     */
    public DeviceUiChooser(AvdManager avdManager, IDevice[] devices) {
        this.avdManager = avdManager;
        this.infos = avdManager.getValidAvds();
        this.devices = devices;
        initComponents();
        avdUISelector = new AvdUISelector();
        jScrollPane2.setViewportView(avdUISelector);
        avdUISelector.setAvdInfos(infos);
        avdUISelector.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DeviceUiChooser.this.updateState();
            }
        });
        setupDevicesTable();
        if (devices.length > 0) {
            jRadioDevice.setSelected(true);
            devicesTable.getSelectionModel().setSelectionInterval(0, 0);
        } else {
            jRadioAVD.setSelected(true);
        }
        devicesTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    int row = devicesTable.rowAtPoint(p);
//          int column = devicesTable.columnAtPoint(p);
                    if (dblClickCallback != null && row != -1 && jRadioDevice.isSelected()) {
                        dblClickCallback.run();
                    }
                }
            }
        });
    }

    private void setupDevicesTable() {
        DefaultTableModel tableModel = (DefaultTableModel) devicesTable.getModel();
        tableModel.setRowCount(0);
        for (IDevice device : devices) {
            String name;
            String target;
            if (device.isEmulator()) {
                name = device.getAvdName();
                AvdInfo info = avdManager.getAvd(device.getAvdName(), true /*validAvdOnly*/);
                target = info == null ? "?" : device.getAvdName();
            } else {
                name = "N/A";
                String deviceBuild = device.getProperty(IDevice.PROP_BUILD_VERSION);
                target = deviceBuild == null ? "unknown" : deviceBuild;
            }
            String state;
            if (DeviceState.BOOTLOADER.equals(device.getState())) {
                state = "bootloader";
            } else if (DeviceState.OFFLINE.equals(device.getState())) {
                state = "offline";
            } else if (DeviceState.ONLINE.equals(device.getState())) {
                state = "online";
            } else {
                state = "unknown";
            }

            tableModel.addRow(new Object[]{
                // TODO nulls?
                device.getSerialNumber(), name, target,
                Boolean.valueOf("1".equals(device.getProperty(IDevice.PROP_DEBUGGABLE))),
                state
            });
        }
        devicesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                DeviceUiChooser.this.updateState();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new ButtonGroup();
        jRadioDevice = new JRadioButton();
        jScrollPane1 = new JScrollPane();
        devicesTable = new JTable();
        jRadioAVD = new JRadioButton();
        jScrollPane2 = new JScrollPane();

        buttonGroup1.add(jRadioDevice);
        jRadioDevice.setText(NbBundle.getMessage(DeviceUiChooser.class, "DeviceUiChooser.jRadioDevice.text")); // NOI18N
        jRadioDevice.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioDeviceActionPerformed(evt);
            }
        });

        devicesTable.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Serial Number/Name", "AVD Name", "Target", "Debug", "State"
            }
        ) {
            Class[] types = new Class [] {
                String.class, String.class, String.class, Boolean.class, String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(devicesTable);
        if (devicesTable.getColumnModel().getColumnCount() > 0) {
            devicesTable.getColumnModel().getColumn(0).setHeaderValue(NbBundle.getMessage(DeviceUiChooser.class, "DeviceUiChooser.devicesTable.columnModel.title0")); // NOI18N
            devicesTable.getColumnModel().getColumn(1).setHeaderValue(NbBundle.getMessage(DeviceUiChooser.class, "DeviceUiChooser.devicesTable.columnModel.title1")); // NOI18N
            devicesTable.getColumnModel().getColumn(2).setHeaderValue(NbBundle.getMessage(DeviceUiChooser.class, "DeviceUiChooser.devicesTable.columnModel.title2")); // NOI18N
            devicesTable.getColumnModel().getColumn(3).setHeaderValue(NbBundle.getMessage(DeviceUiChooser.class, "DeviceUiChooser.devicesTable.columnModel.title3")); // NOI18N
            devicesTable.getColumnModel().getColumn(4).setHeaderValue(NbBundle.getMessage(DeviceUiChooser.class, "DeviceUiChooser.devicesTable.columnModel.title4")); // NOI18N
        }

        buttonGroup1.add(jRadioAVD);
        jRadioAVD.setSelected(true);
        jRadioAVD.setText(NbBundle.getMessage(DeviceUiChooser.class, "DeviceUiChooser.jRadioAVD.text")); // NOI18N
        jRadioAVD.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jRadioAVDActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jRadioDevice)
                            .addComponent(jRadioAVD))
                        .addGap(0, 237, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioDevice)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jRadioAVD)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void jRadioAVDActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jRadioAVDActionPerformed
    updateState();
}//GEN-LAST:event_jRadioAVDActionPerformed

private void jRadioDeviceActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jRadioDeviceActionPerformed
    updateState();
}//GEN-LAST:event_jRadioDeviceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ButtonGroup buttonGroup1;
    private JTable devicesTable;
    private JRadioButton jRadioAVD;
    private JRadioButton jRadioDevice;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    LaunchData getLaunchData() {
        if (jRadioAVD.isSelected()) {
            AvdInfo info = avdUISelector.getAvdInfo();
            return info == null ? null : new LaunchData(info, null);
        } else {
            int row = devicesTable.getSelectedRow();
            if (row >= 0) {
                return new LaunchData(null, devices[row]); // TODO look for AvdInfo too?
            }
        }
        return null;
    }

    void addLaunchDataListener(LaunchDeviceListener launchDeviceListener) {
        listeners.add(launchDeviceListener);
        updateState();
    }

    private void updateState() {
        LaunchData ld = getLaunchData();
        for (LaunchDeviceListener lsnr : listeners) {
            lsnr.lauchDeviceChanged(ld);
        }
    }

    void addSelectCallback(Runnable dblClickCallback) {
        this.dblClickCallback = dblClickCallback;
    }
}
