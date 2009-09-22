/**
 *  Copyright 2003-2009 Terracotta, Inc.
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

package net.sf.ehcache.management.sampled;

/**
 * An MBean for {@link Cache} exposing sampled cache usage statistics
 * 
 * <p />
 * 
 * @author <a href="mailto:asanoujam@terracottatech.com">Abhishek Sanoujam</a>
 * @since 1.7
 */
public interface SampledCacheMBean {

    /**
     * Removes all cached items.
     */
    void removeAll();

    /**
     * Flushes all cache items from memory to the disk store, and from the
     * DiskStore to disk.
     */
    void flush();

    /**
     * Gets the status attribute of the Cache.
     * 
     * @return The status value from the Status enum class
     */
    String getStatus();

    /**
     * Gets the cache name.
     */
    String getName();

    /**
     * Gets the most recent sample for cache hit count
     * 
     * @return
     */
    public long getCacheHitMostRecentSample();

    /**
     * Get most recent value for in-memory cache hit
     * 
     * @return
     */
    public long getCacheHitInMemoryMostRecentSample();

    /**
     * Get most recent value for on-disk cache hit
     * 
     * @return
     */
    public long getCacheHitOnDiskMostRecentSample();

    /**
     * Get most recent value for cache miss
     * 
     * @return
     */
    public long getCacheMissMostRecentSample();

    /**
     * Get most recent value for cache miss as result of the element getting
     * expired
     * 
     * @return
     */
    public long getCacheMissExpiredMostRecentSample();

    /**
     * Get most recent value for cache miss as result of the element not found
     * in cache
     * 
     * @return
     */
    public long getCacheMissNotFoundMostRecentSample();

    /**
     * Get most recent value element evicted from cache
     * 
     * @return
     */
    public long getCacheElementEvictedMostRecentSample();

    /**
     * Get most recent value element removed from cache
     * 
     * @return
     */
    public long getCacheElementRemovedMostRecentSample();

    /**
     * Get most recent value element expired from cache
     * 
     * @return
     */
    public long getCacheElementExpiredMostRecentSample();

    /**
     * Get most recent value element puts in the cache
     * 
     * @return
     */
    public long getCacheElementPutMostRecentSample();

    /**
     * Get most recent value element updates , i.e. put() on elements with
     * already existing keys in the cache
     * 
     * @return
     */
    public long getCacheElementUpdatedMostRecentSample();

    /**
     * Get most recent value for average time taken for get() operation in the
     * cache
     * 
     * @return
     */
    public long getAverageGetTimeMostRecentSample();

    /**
     * Get value for statisticsAccuracy
     * 
     * @return one of {@link Statistics#STATISTICS_ACCURACY_BEST_EFFORT},
     *         {@link Statistics#STATISTICS_ACCURACY_GUARANTEED},
     *         {@link Statistics#STATISTICS_ACCURACY_NONE}
     */
    public int getStatisticsAccuracy();

    /**
     * Get Description for statisticsAccuracy
     */
    public String getStatisticsAccuracyDescription();

    /**
     * Returns true if statistics collection is enabled for cache, otherwise
     * false
     * 
     * @return
     */
    public boolean isStatisticsEnabled();

    /**
     * Returns true if sampled statistics collection is enabled for cache,
     * otherwise
     * false
     * 
     * @return
     */
    public boolean isSampledStatisticsEnabled();

    /**
     * Is the cache configured with Terracotta clustering?
     */
    public boolean isTerracottaClustered();

    /**
     * Enables statistics collection
     */
    public void enableStatistics();

    /**
     * Disables statistics collection. Also disables sampled statistics if it is
     * enabled.
     */
    public void disableStatistics();

    /**
     * Enables statistics collection. As it requires that normal statistics
     * collection to be enabled, it enables it if its not already
     */
    public void enableSampledStatistics();

    /**
     * Disables statistics collection
     */
    public void disableSampledStatistics();

}
