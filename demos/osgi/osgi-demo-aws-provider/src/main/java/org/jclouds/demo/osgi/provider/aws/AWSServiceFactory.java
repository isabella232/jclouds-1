package org.jclouds.demo.osgi.provider.aws;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.rest.RestContext;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

public class AWSServiceFactory implements ManagedServiceFactory {

	// configuration properties
	private static final String PROVIDER_PROPERTY = "provider";
	private static final String IDENTITY_PROPERTY = "identity";
	private static final String CREDENTIAL_PROPERTY = "credential";
	
	// map of factory created service instances 
	private final Map<String, ServiceRegistration[]> registrations = new ConcurrentHashMap<String, ServiceRegistration[]>();
	
	private final BundleContext bundleContext;

	public AWSServiceFactory(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}
	
	@Override
	public String getName() {
		return "AWS BlobStore Service Factory";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updated(String pid, Dictionary properties)
			throws ConfigurationException {
		System.out.println("Updating configuration properties for AWSServiceFactory " + pid);
		ServiceRegistration newRegistration = null;
		if (properties != null) {
			String provider = checkAndGet(properties, PROVIDER_PROPERTY);
			String identity = checkAndGet(properties, IDENTITY_PROPERTY);
			String credential = checkAndGet(properties, CREDENTIAL_PROPERTY);
			BlobStoreContext context =
				new BlobStoreContextFactory().createContext(provider, identity, credential);
			BlobStore blobStore = context.getBlobStore();
			Dictionary<String, Object> svcProps = new Hashtable<String, Object>();
			// Register the type as a service property so that the consumer can
			// filter based on this.
			svcProps.put("type", provider);
			newRegistration = bundleContext.registerService(
					BlobStore.class.getName(), blobStore, svcProps);
			// Register the provider specific api
			RestContext<S3Client, S3AsyncClient> providerContext = context
					.getProviderSpecificContext();
			ServiceRegistration providerRegistration = bundleContext
					.registerService(S3Client.class.getName(), providerContext.getApi(), properties);
			registrations.put(pid, new ServiceRegistration[] { newRegistration,
					providerRegistration });
		}
		if (newRegistration == null) {
			deleted(pid);
		}
	}

	@Override
	public void deleted(String pid) {
		System.out.println("AWSServiceFactory deleted (" + pid + ")");
		ServiceRegistration[] registeredServices = registrations.remove(pid);
		if (registeredServices != null) {
			for (ServiceRegistration registeredService : registeredServices) {
				registeredService.unregister();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private String checkAndGet(Dictionary properties, String key) throws ConfigurationException {
		String value = (String) properties.get(key);
		if (value == null) {
			throw new ConfigurationException(key, "Property missing");
		}
		return value;
	}

}
