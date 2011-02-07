/*
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
package org.jclouds.demo.osgi.provider.mock;

import java.util.Dictionary;
import java.util.Hashtable;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * This OSGi Bundle Activator registers a mock BlobStore in the OSGi Service Registry.
 * 
 * @author David Bosschaert
 */
public class Activator implements BundleActivator {
	private ServiceRegistration reg;
    private BlobStoreContext context;

    public void start(BundleContext bundleContext) throws Exception {
        String type = "transient";
        String identity = "dummy";
        String credential = "dummy";
        context = new BlobStoreContextFactory().createContext(type, identity, credential);
        BlobStore blobStore = context.getBlobStore();
        
        Dictionary<String, Object> svcProps = new Hashtable<String, Object>();
        // Register the type as a service property so that the consumer can filter based on this.
        svcProps.put("type", type);
        reg = bundleContext.registerService(BlobStore.class.getName(), blobStore, svcProps);
	}
	
	public void stop(BundleContext bundleContext) throws Exception {
	    reg.unregister();
	    context.close();
	}
}
