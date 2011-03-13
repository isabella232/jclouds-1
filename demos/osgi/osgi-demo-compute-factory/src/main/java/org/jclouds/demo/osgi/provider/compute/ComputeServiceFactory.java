package org.jclouds.demo.osgi.provider.compute;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import com.google.common.collect.ImmutableSet;

public class ComputeServiceFactory implements ManagedServiceFactory {
	
	private final Map<String, ServiceRegistration> registrations =
		new ConcurrentHashMap<String, ServiceRegistration>();
	
	private final BundleContext bundleContext;

	public ComputeServiceFactory(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	@Override
	public String getName() {
		return "Compute Service Factory";
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void updated(String pid, Dictionary properties)
			throws ConfigurationException {
		System.out.println("Updating configuration properties for ComputeServiceFactory " + pid);
		ServiceRegistration newRegistration = null;
		try {
			if (properties != null) {
				String provider = (String) properties.get("provider");
				String identity = (String) properties.get("identity");
				String credential =	(String) properties.get("credential");
				ComputeServiceContext context = new ComputeServiceContextFactory()
				.createContext(provider, identity, credential,
						ImmutableSet.of(new Log4JLoggingModule(),
								new JschSshClientModule()));
				ComputeService client = context.getComputeService();
				newRegistration = bundleContext.registerService(
						ComputeService.class.getName(), client, properties);
			}
		} finally {
			ServiceRegistration oldRegistration = (newRegistration == null)
						? registrations.remove(pid)
						: registrations.put(pid, newRegistration);
			if (oldRegistration != null) {
				System.out.println("Unregistering ComputeService " + pid);
				oldRegistration.unregister();
			}
		}
		
	}

	@Override
	public void deleted(String pid) {
		System.out.println("ComputeServiceFactory deleted (" + pid + ")");
		ServiceRegistration oldRegistration = registrations.remove(pid);
		if (oldRegistration != null) {
			oldRegistration.unregister();
		}
	}
}
