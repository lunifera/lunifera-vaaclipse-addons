package org.lunifera.vaaclipse.addons.common.event.impl;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.internal.events.EventBroker;
import org.lunifera.vaaclipse.addons.common.event.EventTopicNormalizer;

@SuppressWarnings({ "restriction" })
public class SeparatedEventBrokerFactory extends ContextFunction {
	
	public SeparatedEventBrokerFactory()
	{
		//System.out.println("separated event broker factory start");
	}
	
	@Override
	public Object compute(IEclipseContext context) {
		SeparatedEventBroker broker = context.getLocal(SeparatedEventBroker.class);
		if (broker == null) {
			EventTopicNormalizer normalizer = ContextInjectionFactory.make(EventTopicNormalizer.class, context);
			context.set(EventTopicNormalizer.class, normalizer);
            broker = ContextInjectionFactory.make(SeparatedEventBroker.class, context);
            context.set(SeparatedEventBroker.class, broker);
            context.set(EventBroker.class, broker);
            context.set(IEventBroker.class, broker);
		}
		return broker;
	}
}
