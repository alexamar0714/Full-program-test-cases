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
package org.nbandroid.netbeans.gradle.v2.sdk.manager;

import com.android.repository.api.UpdatablePackage;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import org.nbandroid.netbeans.gradle.v2.sdk.AndroidVersionNode;

/**
 * Node representing single Android platform package
 *
 * @author arsi
 */
public class SdkManagerPackageNode implements TreeNode {

    private final AndroidVersionNode versionDecorator;
    private final UpdatablePackage pkg;

    public SdkManagerPackageNode(AndroidVersionNode versionDecorator, UpdatablePackage pkg) {
        this.versionDecorator = versionDecorator;
        this.pkg = pkg;
    }

    /**
     * Get android package
     *
     * @return UpdatablePackage
     */
    public UpdatablePackage getPackage() {
        return pkg;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TreeNode getParent() {
        return versionDecorator;
    }

    @Override
    public int getIndex(TreeNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getAllowsChildren() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public Enumeration children() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return pkg.getRepresentative().getDisplayName();
    }

}
