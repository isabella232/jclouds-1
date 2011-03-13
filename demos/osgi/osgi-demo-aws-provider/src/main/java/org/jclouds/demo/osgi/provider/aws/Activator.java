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
package org.jclouds.demo.osgi.provider.aws;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.blobstore.BlobStoreContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * This OSGi Bundle Activator registers a AWS S3 BlobStore in the OSGi Service Registry.
 * 
 * @author David Bosschaert
 */
public class Activator implements BundleActivator {
	
	private BundleContext bundleContext;

    public void start(BundleContext bundleContext) throws Exception {
    	this.bundleContext = bundleContext;
    	Dictionary<String, Object> svcProps = new Hashtable<String, Object>();
    	svcProps.put(Constants.SERVICE_PID, "org.jclouds.demo.osgi.provider.aws.BlobStoreServiceFactory");
    	AWSServiceFactory serviceFactory = new AWSServiceFactory(bundleContext);
    	bundleContext.registerService(ManagedServiceFactory.class.getName(), serviceFactory, svcProps);
    }
    
    public void stop(BundleContext bundleContext) throws Exception {
    }

}
