/*
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.demo.osgi.consumer;

import java.io.InputStream;
import java.util.Map;

import javax.swing.JOptionPane;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;

/**
 * The main component for the OSGi Demo
 *
 * @author David Bosschaert
 *   based on a demo from 
 * @author Carlos Fernandes
 * @author Adrian Cole
 */
public class Component {
    private BlobStore blobStore;

    public void activate() {        
        try {
            // Create Container
            String containerName = JOptionPane.showInputDialog("Please Specify Container Name");
            blobStore.createContainerInLocation(null, containerName);
            
            // Add Blob
            Blob blob = blobStore.newBlob("test");
            blob.setPayload("testdata");
            blobStore.putBlob(containerName, blob);
            
            // List Container
            for (StorageMetadata resourceMd : blobStore.list()) {
                if (resourceMd.getType() == StorageType.CONTAINER
                        || resourceMd.getType() == StorageType.FOLDER) {
                    // Use Map API
                    Map<String, InputStream> containerMap = blobStore.getContext().createInputStreamMap(resourceMd.getName());
                    System.out.printf("  %s: %s entries%n", resourceMd.getName(), containerMap.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deactivate() {
        // can do cleanup here
    }

    public void setBlobStore(BlobStore bs) {
        blobStore = bs;
    }

    public void unsetBlobStore(BlobStore bs) {
        blobStore = null;
    }
}
