/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 - 2004 Greg Luck.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by Greg Luck
 *       (http://sourceforge.net/users/gregluck) and contributors.
 *       See http://sourceforge.net/project/memberlist.php?group_id=93232
 *       for a list of contributors"
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "EHCache" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For written
 *    permission, please contact Greg Luck (gregluck at users.sourceforge.net).
 *
 * 5. Products derived from this software may not be called "EHCache"
 *    nor may "EHCache" appear in their names without prior written
 *    permission of Greg Luck.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL GREG LUCK OR OTHER
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by contributors
 * individuals on behalf of the EHCache project.  For more
 * information on EHCache, please see <http://ehcache.sourceforge.net/>.
 *
 */


package net.sf.ehcache;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.store.DiskStore;

/**
 * Tests for CacheManager
 *
 * @author Greg Luck
 * @version $Id: CacheManagerTest.java,v 1.3 2006/03/25 04:40:40 gregluck Exp $
 */
public class CacheManagerTest extends TestCase {
    private static final Log LOG = LogFactory.getLog(CacheManagerTest.class.getName());

    /**
     * the CacheManager Singleton instance
     */
    protected CacheManager singletonManager;

    /**
     * a CacheManager which is created as an instance
     */
    protected CacheManager instanceManager;

    /**
     * Shutdown managers and check for thread leak.
     */
    protected void tearDown() throws Exception {
        if (singletonManager != null) {
            singletonManager.shutdown();
        }
        if (instanceManager != null) {
            instanceManager.shutdown();
        }
    }


    /**
     * Tests that the CacheManager was successfully created
     */
    public void testCreateCacheManager() throws CacheException {
        singletonManager = CacheManager.create();
        assertNotNull(singletonManager);
        assertTrue(singletonManager.getCacheNames().length == 8);
    }

    /**
     * Tests that the CacheManager was successfully created
     */
    public void testCreateCacheManagerFromFile() throws CacheException {
        singletonManager = CacheManager.create(AbstractCacheTest.SRC_CONFIG_DIR + "ehcache.xml");
        assertNotNull(singletonManager);
        assertEquals(5, singletonManager.getCacheNames().length);
    }

    /**
     * Tests that the CacheManager was successfully created from a Configuration
     */
    public void testCreateCacheManagerFromConfiguration() throws CacheException {
        File file = new File(AbstractCacheTest.SRC_CONFIG_DIR + "ehcache.xml");
        Configuration configuration = ConfigurationFactory.parseConfiguration(file);
        CacheManager manager = new CacheManager(configuration);
        assertNotNull(manager);
        assertEquals(5, manager.getCacheNames().length);
        manager.shutdown();
    }

    /**
     * Tests that the CacheManager was successfully created
     */
    public void testCreateCacheManagerFromInputStream() throws Exception {
        InputStream fis = new FileInputStream(new File(AbstractCacheTest.SRC_CONFIG_DIR + "ehcache.xml").getAbsolutePath());
        try {
            singletonManager = CacheManager.create(fis);
        } finally {
            fis.close();
        }
        assertNotNull(singletonManager);
        assertEquals(5, singletonManager.getCacheNames().length);
    }

    /**
     * Tests that creating a second cache manager with the same disk path will fail.
     */
    public void testCreateTwoCacheManagersWithSamePath() throws CacheException {
        URL secondCacheConfiguration = this.getClass().getResource("/ehcache-2.xml");

        singletonManager = CacheManager.create(secondCacheConfiguration);
        try {
            instanceManager = new CacheManager(secondCacheConfiguration);
            fail();
        } catch (CacheException e) {
            //expected
        }
    }

    /**
     * Tests that two CacheManagers were successfully created
     */
    public void testTwoCacheManagers() throws CacheException {
        Element element1 = new Element(1 + "", new Date());
        Element element2 = new Element(2 + "", new Date());

        CacheManager.getInstance().getCache("sampleCache1").put(element1);

        //Check can start second one with a different disk path
        URL secondCacheConfiguration = this.getClass().getResource("/ehcache-2.xml");
        instanceManager = new CacheManager(secondCacheConfiguration);
        instanceManager.getCache("sampleCache1").put(element2);

        assertEquals(element1, CacheManager.getInstance().getCache("sampleCache1").get(1 + ""));
        assertEquals(element2, instanceManager.getCache("sampleCache1").get(2 + ""));

        //shutting down instance should leave singleton unaffected
        instanceManager.shutdown();
        assertEquals(element1, CacheManager.getInstance().getCache("sampleCache1").get(1 + ""));

        //Try shutting and recreating a new instance cache manager
        instanceManager = new CacheManager(secondCacheConfiguration);
        instanceManager.getCache("sampleCache1").put(element2);
        CacheManager.getInstance().shutdown();
        assertEquals(element2, instanceManager.getCache("sampleCache1").get(2 + ""));

        //Try shutting and recreating the singleton cache manager
        CacheManager.getInstance().getCache("sampleCache1").put(element2);
        assertNull(CacheManager.getInstance().getCache("sampleCache1").get(1 + ""));
        assertEquals(element2, CacheManager.getInstance().getCache("sampleCache1").get(2 + ""));
    }

    /**
     * Create and destory cache managers and see what happens with threads.
     * Each Cache creates at least two threads. These should all be killed when the Cache
     * disposes. Doing that 800 times as in that test gives the reassurance.
     */
    public void testForCacheManagerThreadLeak() throws CacheException, InterruptedException {
        //Check can start second one with a different disk path
        int startingThreadCount = countThreads();

        URL secondCacheConfiguration = this.getClass().getResource("/ehcache-2.xml");
        for (int i = 0; i < 100; i++) {
            instanceManager = new CacheManager(secondCacheConfiguration);
            instanceManager.shutdown();
        }
        //Give the spools a chance to exit
        Thread.sleep(300);
        int endingThreadCount = countThreads();
        //Allow a bit of variation.
        assertTrue(endingThreadCount < startingThreadCount + 2);

    }

    /**
     * It should be possible to create a new CacheManager instance with the same disk configuration,
     * provided the first was shutdown. Note that any persistent disk stores will be available to the second cache manager.
     */
    public void testInstanceCreateShutdownCreate() throws CacheException {
        singletonManager = CacheManager.create();

        URL secondCacheConfiguration = this.getClass().getResource("/ehcache-2.xml");
        instanceManager = new CacheManager(secondCacheConfiguration);
        instanceManager.shutdown();

        //shutting down instance should leave singleton ok
        assertTrue(singletonManager.getCacheNames().length == 8);


        instanceManager = new CacheManager(secondCacheConfiguration);
        assertNotNull(instanceManager);
        assertTrue(instanceManager.getCacheNames().length == 8);


    }

    /**
     * Tests programmatic creation of CacheManager with a programmatic Configuration.
     * todo uncomment
     *
     * @throws CacheException
     */
    public void xTestCreateCacheManagersProgrammatically() throws CacheException {
//        DefaultCacheConfiguration defaultCache = new DefaultCacheConfiguration();
//        defaultCache.setEternal(false);
//
//        Configuration configuration =
//                new Configuration(manager, configurationBean);
//        assertNotNull(configuration);
//
//        instanceManager = new CacheManager(configuration);
//        assertNotNull(instanceManager);
//        assertEquals(0, instanceManager.getCacheNames().length);

    }

    /**
     * Checks we can get a cache
     */
    public void testGetCache() throws CacheException {
        instanceManager = CacheManager.create();
        Cache cache = instanceManager.getCache("sampleCache1");
        assertNotNull(cache);
    }

    /**
     * Tests shutdown after shutdown.
     */
    public void testShutdownAfterShutdown() throws CacheException {
        instanceManager = CacheManager.create();
        assertEquals(Status.STATUS_ALIVE, instanceManager.getStatus());
        instanceManager.shutdown();
        assertEquals(Status.STATUS_SHUTDOWN, instanceManager.getStatus());
        instanceManager.shutdown();
        assertEquals(Status.STATUS_SHUTDOWN, instanceManager.getStatus());
    }

    /**
     * Tests create, shutdown, create
     */
    public void testCreateShutdownCreate() throws CacheException {
        singletonManager = CacheManager.create();
        assertEquals(Status.STATUS_ALIVE, singletonManager.getStatus());
        singletonManager.shutdown();

        //check we can recreate the CacheManager on demand.
        singletonManager = CacheManager.create();
        assertNotNull(singletonManager);
        assertTrue(singletonManager.getCacheNames().length == 8);
        assertEquals(Status.STATUS_ALIVE, singletonManager.getStatus());

        singletonManager.shutdown();
        assertEquals(Status.STATUS_SHUTDOWN, singletonManager.getStatus());
    }

    /**
     * Tests removing a cache
     */
    public void testRemoveCache() throws CacheException {
        singletonManager = CacheManager.create();
        Cache cache = singletonManager.getCache("sampleCache1");
        assertNotNull(cache);
        singletonManager.removeCache("sampleCache1");
        cache = singletonManager.getCache("sampleCache1");
        assertNull(cache);
    }

    /**
     * Tests adding a new cache with default config
     */
    public void testAddCache() throws CacheException {
        singletonManager = CacheManager.create();
        singletonManager.addCache("test");
        Cache cache = singletonManager.getCache("test");
        assertNotNull(cache);
        assertEquals("test", cache.getName());
        String[] cacheNames = singletonManager.getCacheNames();
        boolean match = false;
        for (int i = 0; i < cacheNames.length; i++) {
            String cacheName = cacheNames[i];
            if (cacheName.equals("test")) {
                match = true;
            }
        }
        assertTrue(match);
    }

    /**
     * Bug 1457268. Instance of RegisteredEventListeners shared between caches created from default cache.
     * The issue also results in sharing of all references.
     * This test makes sure each cache has its own.
     */
    public void testCachesCreatedFromDefaultDoNotShareListenerReferences() {
        singletonManager = CacheManager.create();
        singletonManager.addCache("newfromdefault1");
        Cache cache1 = singletonManager.getCache("newfromdefault1");
        singletonManager.addCache("newfromdefault2");
        Cache cache2 = singletonManager.getCache("newfromdefault2");

        RegisteredEventListeners listeners1 = cache1.getCacheEventNotificationService();
        RegisteredEventListeners listeners2 = cache2.getCacheEventNotificationService();
        assertTrue(listeners1 != listeners2);

        DiskStore diskStore1 = cache1.getDiskStore();
        DiskStore diskStore2 = cache2.getDiskStore();
        assertTrue(diskStore1 != diskStore2);

    }

    /**
     * Test using a cache which has been removed and replaced.
     */
    public void testStaleCacheReference() throws CacheException {
        singletonManager = CacheManager.create();
        singletonManager.addCache("test");
        Cache cache = singletonManager.getCache("test");
        assertNotNull(cache);
        cache.put(new Element("key1", "value1"));

        assertEquals("value1", cache.get("key1").getValue());
        singletonManager.removeCache("test");
        singletonManager.addCache("test");

        try {
            cache.get("key1");
            fail();
        } catch (IllegalStateException e) {
            assertEquals("The test Cache is not alive.", e.getMessage());
        }
    }

    /**
     * Tests that we can run 69 caches, most with disk stores, with no ill effects
     * <p/>
     * Check that this is fast.
     */
    public void testCreateCacheManagerWithManyCaches() throws CacheException, InterruptedException {
        singletonManager = CacheManager.create(AbstractCacheTest.TEST_CONFIG_DIR + "ehcache-big.xml");
        assertNotNull(singletonManager);
        assertEquals(69, singletonManager.getCacheNames().length);

        String[] names = singletonManager.getCacheNames();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Cache cache = singletonManager.getCache(name);
            for (int j = 0; i < 100; i++) {
                cache.put(new Element(new Integer(j), "value"));
            }
        }
        StopWatch stopWatch = new StopWatch();
        for (int repeats = 0; repeats < 5000; repeats++) {
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                Cache cache = singletonManager.getCache(name);
                for (int j = 0; i < 100; i++) {
                    Element element = cache.get(name + j);
                    if ((element == null)) {
                        cache.put(new Element(new Integer(j), "value"));
                    }
                }
            }
        }
        long elapsedTime = stopWatch.getElapsedTime();
        LOG.info("Time taken was: " + elapsedTime);
        assertTrue(elapsedTime < 5000);
    }

    private int countThreads() {


        /**
         * A class for visiting threads
         */
        class ThreadVisitor {

            private int threadCount;


            // This method recursively visits all thread groups under `group'.
            private void visit(ThreadGroup group, int level) {
                // Get threads in `group'
                int numThreads = group.activeCount();
                Thread[] threads = new Thread[numThreads * 2];
                numThreads = group.enumerate(threads, false);

                // Enumerate each thread in `group'
                for (int i = 0; i < numThreads; i++) {
                    // Get thread
                    Thread thread = threads[i];
                    threadCount++;
                    LOG.info(thread);
                }

                // Get thread subgroups of `group'
                int numGroups = group.activeGroupCount();
                ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
                numGroups = group.enumerate(groups, false);

                // Recursively visit each subgroup
                for (int i = 0; i < numGroups; i++) {
                    visit(groups[i], level + 1);
                }
            }

        }


        // Find the root thread group
        ThreadGroup root = Thread.currentThread().getThreadGroup().getParent();
        while (root.getParent() != null) {
            root = root.getParent();
        }


        // Visit each thread group
        ThreadVisitor visitor = new ThreadVisitor();
        visitor.visit(root, 0);
        return visitor.threadCount;


    }


}
