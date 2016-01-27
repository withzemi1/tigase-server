package tigase.eventbus;

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tigase.xml.Element;

public class EventBusImplementationTest {

	private EventBusImplementation eventBus;

	@Before
	public void setUp() throws Exception {
		eventBus = new EventBusImplementation();
		eventBus.setExecutor(new Executor() {
			@Override
			public void execute(Runnable command) {
				command.run();
			}
		});
	}

	@Test
	public void test1() {
		final String[] value = new String[5];

		eventBus.addListener(Event1.class, new EventListener<Event1>() {

			@Override
			public void onEvent(Event1 event) {
				value[1] = "l";
			}
		});
		eventBus.addListener(Event12.class, new EventListener<Event12>() {

			@Override
			public void onEvent(Event12 event) {
				value[2] = "l";
			}
		});
		eventBus.addListener(Event2.class, new EventListener<Event2>() {

			@Override
			public void onEvent(Event2 event) {
				value[3] = "l";
			}
		});

		Event1 event = new Event1();
		eventBus.fire(event);

		Assert.assertNull(value[0]);
		Assert.assertEquals("l", value[1]);
		Assert.assertNull(value[2]);
		Assert.assertNull(value[3]);
		Assert.assertNull(value[4]);
	}

	@Test
	public void testAddListener() {
		final Object resp[] = new Object[] { null, null, null, null, null };

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNull(resp[0]);
		Assert.assertNull(resp[1]);
		Assert.assertNull(resp[2]);
		Assert.assertNull(resp[3]);
		Assert.assertNull(resp[4]);

		eventBus.addListener(Event1.class, e -> resp[0] = e);
		eventBus.addListener(Event12.class, e -> resp[1] = e);
		eventBus.addListener(Event1.class.getPackage().getName(), Event1.class.getSimpleName(), e -> resp[2] = e);
		eventBus.addListener(Event12.class.getPackage().getName(), Event12.class.getSimpleName(), e -> resp[3] = e);
		eventBus.addHandler(new AbstractListenerHandler(null, null, new Object()) {
			@Override
			public void dispatch(Object event, Object source, boolean remotelyGeneratedEvent) {
				resp[4] = event;
			}

			@Override
			public Type getRequiredEventType() {
				return Type.asIs;
			}

		});

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNotNull(resp[0]);
		Assert.assertNotNull(resp[1]);
		Assert.assertNotNull(resp[2]);
		Assert.assertNotNull(resp[3]);
		Assert.assertNotNull(resp[4]);
	}

	@Test
	public void testFire() throws Exception {
		Object resp[] = new Object[] { null, null, null, null, null };
		eventBus.addListener(Event1.class, e -> resp[0] = e);
		eventBus.addListener(Event12.class, e -> resp[1] = e);
		eventBus.addListener(Event1.class.getPackage().getName(), Event1.class.getSimpleName(), e -> resp[2] = e);
		eventBus.addListener(Event12.class.getPackage().getName(), Event12.class.getSimpleName(), e -> resp[3] = e);
		eventBus.addHandler(new AbstractListenerHandler(null, null, new Object()) {
			@Override
			public void dispatch(Object event, Object source, boolean remotelyGeneratedEvent) {
				resp[4] = event;
			}

			@Override
			public Type getRequiredEventType() {
				return Type.asIs;
			}

		});

		eventBus.fire(new Event1());
		Assert.assertTrue(resp[0] instanceof Event1);
		Assert.assertNull(resp[1]);
		Assert.assertTrue(resp[2] instanceof Element);
		Assert.assertNull(resp[3]);
		Assert.assertTrue(resp[4] instanceof Event1);

		Arrays.fill(resp, null);

		eventBus.fire(new Event12());
		Assert.assertTrue(resp[0] instanceof Event12);
		Assert.assertTrue(resp[1] instanceof Event12);
		Assert.assertTrue(resp[2] instanceof Element);
		Assert.assertTrue(resp[3] instanceof Element);
		Assert.assertTrue(resp[4] instanceof Event12);

		Arrays.fill(resp, null);

		eventBus.fire(new Element("tigase.eventbus.Event1"));
		Assert.assertNull(resp[0]);
		Assert.assertNull(resp[1]);
		Assert.assertTrue(resp[2] instanceof Element);
		Assert.assertNull(resp[3]);
		Assert.assertTrue(resp[4] instanceof Element);

		Arrays.fill(resp, null);

		eventBus.fire(new Element("tigase.eventbus.Event12"));
		Assert.assertNull(resp[0]);
		Assert.assertNull(resp[1]);
		Assert.assertNull(resp[2]);
		Assert.assertTrue(resp[3] instanceof Element);
		Assert.assertTrue(resp[4] instanceof Element);
	}

	@Test
	public void testGetListenersForEvent_Element() throws Exception {
		eventBus.addListener(Event1.class, new EventListener<Event1>() {
			@Override
			public void onEvent(Event1 e) {

			}
		});
		eventBus.addListener(Event12.class, new EventListener<Event12>() {
			@Override
			public void onEvent(Event12 e) {

			}
		});
		eventBus.addListener(Event1.class.getPackage().getName(), Event1.class.getSimpleName(), new EventListener<Element>() {
			@Override
			public void onEvent(Element e) {

			}
		});
		eventBus.addListener(Event1.class.getPackage().getName(), null, new EventListener<Element>() {
			@Override
			public void onEvent(Element e) {

			}
		});
		eventBus.addListener(null, null, new EventListener<Element>() {
			@Override
			public void onEvent(Element e) {

			}
		});

		Assert.assertEquals(4, eventBus.getListenersForEvent(Event1.class).size());
		Assert.assertEquals(4,
				eventBus.getListenersForEvent(Event1.class.getPackage().getName(), Event1.class.getSimpleName()).size());
		Assert.assertEquals(5, eventBus.getListenersForEvent(Event12.class).size());

	}

	@Test
	public void testGetListenersForEvent_Object() throws Exception {
		eventBus.addListener(Event1.class, new EventListener<Event1>() {
			@Override
			public void onEvent(Event1 e) {

			}
		});
		eventBus.addListener(Event2.class, new EventListener<Event2>() {
			@Override
			public void onEvent(Event2 e) {

			}
		});
		eventBus.addListener(Event2.class, new EventListener<Event2>() {
			@Override
			public void onEvent(Event2 e) {

			}
		});
		eventBus.addListener(Event12.class, new EventListener<Event12>() {
			@Override
			public void onEvent(Event12 e) {

			}
		});
		eventBus.addListener(Event12.class, new EventListener<Event12>() {
			@Override
			public void onEvent(Event12 e) {

			}
		});

		Assert.assertEquals(1, eventBus.getListenersForEvent(Event1.class).size());
		Assert.assertEquals(2, eventBus.getListenersForEvent(Event2.class).size());
		Assert.assertEquals(3, eventBus.getListenersForEvent(Event12.class).size());

		Assert.assertEquals(0, eventBus.getListenersForEvent(String.class).size());

		Assert.assertEquals(1,
				eventBus.getListenersForEvent(Event1.class.getPackage().getName(), Event1.class.getSimpleName()).size());
		Assert.assertEquals(2,
				eventBus.getListenersForEvent(Event2.class.getPackage().getName(), Event12.class.getSimpleName()).size());
		Assert.assertEquals(2,
				eventBus.getListenersForEvent(Event12.class.getPackage().getName(), Event12.class.getSimpleName()).size());
	}

	@Test
	public void testRegisterAll() throws Exception {
		final Consumer c = new Consumer();
		eventBus.registerAll(c);

		eventBus.fire(new Event12(), this);
		Assert.assertNotNull(c.resp[0]);
		Assert.assertNotNull(c.resp[1]);
		Assert.assertNotNull(c.resp[2]);

		Arrays.fill(c.resp, null);

		eventBus.unregisterAll(c);

		eventBus.fire(new Event12());

		Assert.assertNull(c.resp[0]);
		Assert.assertNull(c.resp[1]);
		Assert.assertNull(c.resp[2]);
	}

	@Test
	public void testRemoveListener() {
		final Object resp[] = new Object[] { null, null, null, null, null };

		EventListener<Event1> l0 = e -> resp[0] = e;
		eventBus.addListener(Event1.class, l0);

		EventListener<Event12> l1 = e -> resp[1] = e;
		eventBus.addListener(Event12.class, l1);

		EventListener<Element> l2 = e -> resp[2] = e;
		eventBus.addListener(Event1.class.getPackage().getName(), Event1.class.getSimpleName(), l2);

		EventListener<Element> l3 = e -> resp[3] = e;
		eventBus.addListener(Event12.class.getPackage().getName(), Event12.class.getSimpleName(), l3);

		AbstractListenerHandler l4 = new AbstractListenerHandler(null, null, new Object()) {
			@Override
			public void dispatch(Object event, Object source, boolean remotelyGeneratedEvent) {
				resp[4] = event;
			}

			@Override
			public Type getRequiredEventType() {
				return Type.asIs;
			}

		};
		eventBus.addHandler(l4);

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNotNull(resp[0]);
		Assert.assertNotNull(resp[1]);
		Assert.assertNotNull(resp[2]);
		Assert.assertNotNull(resp[3]);
		Assert.assertNotNull(resp[4]);

		eventBus.removeListener(l0);

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNull(resp[0]);
		Assert.assertNotNull(resp[1]);
		Assert.assertNotNull(resp[2]);
		Assert.assertNotNull(resp[3]);
		Assert.assertNotNull(resp[4]);

		eventBus.removeListener(l1);

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNull(resp[0]);
		Assert.assertNull(resp[1]);
		Assert.assertNotNull(resp[2]);
		Assert.assertNotNull(resp[3]);
		Assert.assertNotNull(resp[4]);

		eventBus.removeListener(l2);

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNull(resp[0]);
		Assert.assertNull(resp[1]);
		Assert.assertNull(resp[2]);
		Assert.assertNotNull(resp[3]);
		Assert.assertNotNull(resp[4]);

		eventBus.removeListener(l3);

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNull(resp[0]);
		Assert.assertNull(resp[1]);
		Assert.assertNull(resp[2]);
		Assert.assertNull(resp[3]);
		Assert.assertNotNull(resp[4]);

		eventBus.removeListenerHandler(l4);

		Arrays.fill(resp, null);
		eventBus.fire(new Event12());
		Assert.assertNull(resp[0]);
		Assert.assertNull(resp[1]);
		Assert.assertNull(resp[2]);
		Assert.assertNull(resp[3]);
		Assert.assertNull(resp[4]);
	}

	public static class Consumer {

		private final Object resp[] = new Object[] { null, null, null };

		@HandleEvent
		public void event0(Event1 e) {
			resp[0] = e;
		}

		@HandleEvent
		public void event1(Event12 e) {
			resp[1] = e;
		}

		@HandleEvent
		public void event2(Event12 e, Object source) {
			resp[2] = e;
			Assert.assertTrue(source instanceof EventBusImplementationTest);
		}

	}
}