package org.jclouds.demo.osgi.provider.compute;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Hashtable<String, Object> properties = new Hashtable<String, Object>();
		properties.put(Constants.SERVICE_PID, ComputeServiceFactory.class.getName());
		ComputeServiceFactory computeFactory = new ComputeServiceFactory(context);
		context.registerService(ManagedServiceFactory.class.getName(),
				computeFactory, properties);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
