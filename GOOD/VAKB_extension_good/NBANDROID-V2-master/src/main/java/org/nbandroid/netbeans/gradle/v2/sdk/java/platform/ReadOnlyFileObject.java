/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.nbandroid.netbeans.gradle.v2.sdk.java.platform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.WeakHashMap;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileSystem;
import org.openide.util.Lookup;

/**
 * Hack to make android platform source folder read-only
 *
 * @see org.nbandroid.netbeans.gradle.v2.sdk.java.platform.ReadOnlyURLMapper
 * @author arsi
 */
public class ReadOnlyFileObject extends FileObject {

    private final FileObject original;
    private static final Map<FileObject, FileObject> cache = new WeakHashMap<>();

    public static final FileObject findOrCreate(FileObject orig) {
        if (orig == null) {
            return null;
        }
        if (orig instanceof ReadOnlyFileObject) {
            return orig;
        }
        FileObject roFo = cache.get(orig);
        if (roFo == null) {
            roFo = new ReadOnlyFileObject(orig);
            cache.put(orig, roFo);
        }
        return roFo;
    }

    public ReadOnlyFileObject(FileObject original) {
        this.original = original;
    }

    @Override
    public String getName() {
        return original.getName();
    }

    @Override
    public String getExt() {
        return original.getExt();
    }

    @Override
    public void rename(FileLock lock, String name, String ext) throws IOException {
        original.rename(lock, name, ext);
    }

    @Override
    public FileSystem getFileSystem() throws FileStateInvalidException {
        return original.getFileSystem();
    }

    @Override
    public FileObject getParent() {
        return findOrCreate(original.getParent());
    }

    @Override
    public boolean isFolder() {
        return original.isFolder();
    }

    @Override
    public Date lastModified() {
        return original.lastModified();
    }

    @Override
    public boolean isRoot() {
        return original.isRoot();
    }

    @Override
    public boolean isData() {
        return original.isData();
    }

    @Override
    public boolean isValid() {
        return original.isValid();
    }

    @Override
    public void delete(FileLock lock) throws IOException {
        throw new IOException();
    }

    @Override
    public Object getAttribute(String attrName) {
        return original.getAttribute(attrName);
    }

    @Override
    public void setAttribute(String attrName, Object value) throws IOException {
        original.setAttribute(attrName, value);
    }

    @Override
    public Enumeration<String> getAttributes() {
        return original.getAttributes();
    }

    @Override
    public void addFileChangeListener(FileChangeListener fcl) {
        original.addFileChangeListener(fcl);
    }

    @Override
    public void removeFileChangeListener(FileChangeListener fcl) {
        original.removeFileChangeListener(fcl);
    }

    @Override
    public long getSize() {
        return original.getSize();
    }

    @Override
    public InputStream getInputStream() throws FileNotFoundException {
        return original.getInputStream();
    }

    @Override
    public OutputStream getOutputStream(FileLock lock) throws IOException {
        return original.getOutputStream(lock);
    }

    @Override
    public FileLock lock() throws IOException {
        return original.lock();
    }

    @Override
    public void setImportant(boolean b) {
        original.setImportant(b);
    }

    @Override
    public FileObject[] getChildren() {
        FileObject[] children = original.getChildren();
        List<FileObject> tmp = new ArrayList<>();
        for (FileObject chld : children) {
            tmp.add(findOrCreate(chld));
        }
        return tmp.toArray(new FileObject[tmp.size()]);
    }

    @Override
    public FileObject getFileObject(String name, String ext) {
        return findOrCreate(original.getFileObject(name, ext));
    }

    @Override
    public FileObject createFolder(String name) throws IOException {
        throw new IOException();
    }

    @Override
    public FileObject createData(String name, String ext) throws IOException {
        throw new IOException();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public FileObject getCanonicalFileObject() throws IOException {
        return findOrCreate(original.getCanonicalFileObject());
    }

    @Override
    public String readSymbolicLinkPath() throws IOException {
        return original.readSymbolicLinkPath();
    }

    @Override
    public FileObject readSymbolicLink() throws IOException {
        return original.readSymbolicLink();
    }

    @Override
    public boolean isSymbolicLink() throws IOException {
        return original.isSymbolicLink();
    }

    @Override
    public boolean isVirtual() {
        return original.isVirtual();
    }

    @Override
    public void refresh() {
        original.refresh();
    }

    @Override
    public void refresh(boolean expected) {
        original.refresh(expected);
    }

    @Override
    public boolean canRead() {
        return original.canRead();
    }

    @Override
    public OutputStream createAndOpen(String name) throws IOException {
        return original.createAndOpen(name);
    }

    @Override
    public FileObject createData(String name) throws IOException {
        throw new IOException();
    }

    @Override
    public FileObject getFileObject(String relativePath) {
        return findOrCreate(original.getFileObject(relativePath));
    }

    @Override
    public Enumeration<? extends FileObject> getData(boolean rec) {
        Enumeration<? extends FileObject> datas = original.getData(rec);
        Vector<FileObject> tmp = new Vector<>();
        while (datas.hasMoreElements()) {
            FileObject nextElement = datas.nextElement();
            tmp.add(findOrCreate(nextElement));
        }
        return tmp.elements();
    }

    @Override
    public Enumeration<? extends FileObject> getFolders(boolean rec) {
        Enumeration<? extends FileObject> datas = original.getFolders(rec);
        Vector<FileObject> tmp = new Vector<>();
        while (datas.hasMoreElements()) {
            FileObject nextElement = datas.nextElement();
            tmp.add(findOrCreate(nextElement));
        }
        return tmp.elements();
    }

    @Override
    public Enumeration<? extends FileObject> getChildren(boolean rec) {
        Enumeration<? extends FileObject> datas = original.getChildren(rec);
        Vector<FileObject> tmp = new Vector<>();
        while (datas.hasMoreElements()) {
            FileObject nextElement = datas.nextElement();
            tmp.add(findOrCreate(nextElement));
        }
        return tmp.elements();
    }

    @Override
    public boolean isLocked() {
        return original.isLocked();
    }

    @Override
    public List<String> asLines(String encoding) throws IOException {
        return original.asLines(encoding);
    }

    @Override
    public List<String> asLines() throws IOException {
        return original.asLines();
    }

    @Override
    public String asText() throws IOException {
        return original.asText();
    }

    @Override
    public String asText(String encoding) throws IOException {
        return original.asText(encoding);
    }

    @Override
    public byte[] asBytes() throws IOException {
        return original.asBytes();
    }

    @Override
    public String getMIMEType(String... withinMIMETypes) {
        return original.getMIMEType(withinMIMETypes);
    }

    @Override
    public String getMIMEType() {
        return original.getMIMEType();
    }

    @Override
    public void removeRecursiveListener(FileChangeListener fcl) {
        original.removeRecursiveListener(fcl);
    }

    @Override
    public void addRecursiveListener(FileChangeListener fcl) {
        original.addRecursiveListener(fcl);
    }

    @Override
    public Lookup getLookup() {
        return original.getLookup();
    }

    @Override
    public boolean existsExt(String ext) {
        return original.existsExt(ext);
    }

    @Override
    public String getNameExt() {
        return original.getNameExt();
    }

    @Override
    public String getPackageName(char separatorChar) {
        return original.getPackageName(separatorChar);
    }

    @Override
    public String getPackageNameExt(char separatorChar, char extSepChar) {
        return original.getPackageNameExt(separatorChar, extSepChar);
    }

    @Override
    public String getPath() {
        return original.getPath();
    }

    @Override
    public String toString() {
        return original.toString();
    }

    @Override
    public FileObject move(FileLock lock, FileObject target, String name, String ext) throws IOException {
        throw new IOException();
    }

    @Override
    public FileObject copy(FileObject target, String name, String ext) throws IOException {
        throw new IOException();
    }

    @Override
    public int hashCode() {
        return original.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReadOnlyFileObject) {
            final ReadOnlyFileObject other = (ReadOnlyFileObject) obj;
            if (Objects.equals(this.original, other.original)) {
                return true;
            }
        }
        return false;
    }

}
