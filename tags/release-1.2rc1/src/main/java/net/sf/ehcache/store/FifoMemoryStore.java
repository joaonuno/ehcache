/**
 *  Copyright 2003-2006 Greg Luck
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.sf.ehcache.store;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.CacheException;
import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * First-In-First-Out (FIFO) implementation of MemoryStore.
 *
 * @author <a href="mailto:ssuravarapu@users.sourceforge.net">Surya Suravarapu</a>
 * @version $Id$
 */
public class FifoMemoryStore extends MemoryStore {
    private static final Log LOG = LogFactory.getLog(FifoMemoryStore.class.getName());

    private final static int SEQUENCED_HASH_MAP = 1;

    private final static int LINKED_HASH_MAP = 2;

    /**
     * One of the above declared static collection types
     */
    private int collectionType;

    /**
     * Constructor for the FifoMemoryStore object.
     * <p/>
     * First tries to use {@link java.util.LinkedHashMap}. If not found uses
     * Jakarta Commons collections.
     */
    public FifoMemoryStore(Cache cache, DiskStore diskStore) {
        super(cache, diskStore);

        // Use LinkedHashMap for JDK 1.4
        try {
            Class.forName("java.util.LinkedHashMap");
            map = new LinkedHashMap();
            collectionType = LINKED_HASH_MAP;
        } catch (ClassNotFoundException e) {
            // If not JDK 1.4 use the commons collections
            try {
                Class.forName("org.apache.commons.collections.SequencedHashMap");
                map = new SequencedHashMap();
                collectionType = SEQUENCED_HASH_MAP;
            } catch (ClassNotFoundException ee) {
                LOG.error(ee.getMessage());
            }
        }
    }

    /**
     * Allow specialised actions over adding the element to the map
     *
     * @param element
     */
    protected void doPut(Element element) throws CacheException {
        if (isFull()) {
            removeFirstElement();
        }
    }


    /**
     * Returns the first eligible element that can be taken out of the cache
     * based on the FIFO policy
     */
    public synchronized Element getFirstElement() {
        if (map.size() == 0) {
            return null;
        }

        Element element = null;
        Serializable key = null;

        if (collectionType == LINKED_HASH_MAP) {
            Set keySet = map.keySet();
            Iterator itr = keySet.iterator();
            // The first element is the candidate to remove
            if (itr.hasNext()) {
                key = (Serializable) itr.next();
                element = (Element) map.get(key);
            }
        } else if (collectionType == SEQUENCED_HASH_MAP) {
            key = (Serializable) ((SequencedHashMap) map).getFirstKey();
            element = (Element) map.get(key);
        }

        return element;
    }

    /**
     * Remove the first element that is eligible to removed from the store
     * based on the FIFO policy
     */
    private void removeFirstElement() throws CacheException {
        Element element = getFirstElement();

        if (cache.isExpired(element)) {
            remove(element.getKey());
            notifyExpiry(element);
            return;
        }
        remove(element.getKey());
        evict(element);
    }
}
