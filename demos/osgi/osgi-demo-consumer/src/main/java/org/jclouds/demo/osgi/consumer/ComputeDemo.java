package org.jclouds.demo.osgi.consumer;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.predicates.NodePredicates.TERMINATED;
import static org.jclouds.compute.predicates.NodePredicates.all;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;

public class ComputeDemo {
    private final AtomicReference<ComputeService> computeServiceRef =
    	new AtomicReference<ComputeService>();
    
    private Thread thread;

    public void activate() {
    	System.out.println("Starting compute demo...");
    	thread = new Thread(new LoopingDemo());
    	thread.start();
    }
    
    public void deactivate() {
    	System.out.println("Stopping compute demo...");
    	thread.interrupt();
    }

    public void setComputeService(ComputeService cs) {
    	System.out.println("Setting computeService");
        computeServiceRef.set(cs);
    }

    public void unsetComputeService(ComputeService cs) {
    	System.out.println("Unsetting computeService");
        computeServiceRef.compareAndSet(cs, null);
    }
    
	class LoopingDemo implements Runnable {
		@Override
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					Thread.sleep(5000);
					System.out.println("Refreshing compute list");
					ComputeService computeService = computeServiceRef.get();
					if (computeService != null) {
						Iterable<? extends NodeMetadata> nodes = filter(
								computeService.listNodesDetailsMatching(all()),
								not(TERMINATED));
						for (NodeMetadata node : nodes) {
							System.out.println(String.format(
								"id %s, group %s, ip %s",
								node.getId(), node.getGroup(),
								node.getPublicAddresses()));
						}
					}
				}
			} catch (InterruptedException e) {
				System.out.println("ComputeDemo Thread interrupted");
			}
		}
	}
}
