package com.ctrip.xpipe.redis.console.config.impl;

import com.ctrip.xpipe.api.codec.GenericTypeReference;
import com.ctrip.xpipe.api.config.ConfigChangeListener;
import com.ctrip.xpipe.cluster.ClusterType;
import com.ctrip.xpipe.codec.JsonCodec;
import com.ctrip.xpipe.redis.checker.healthcheck.actions.interaction.DcClusterDelayMarkDown;
import com.ctrip.xpipe.redis.console.config.ConsoleConfig;
import com.ctrip.xpipe.redis.console.config.model.BeaconOrgRoute;
import com.ctrip.xpipe.redis.console.util.HickwallMetricInfo;
import com.ctrip.xpipe.redis.core.config.AbstractCoreConfig;
import com.ctrip.xpipe.redis.core.meta.QuorumConfig;
import com.ctrip.xpipe.redis.core.route.RouteChooseStrategyFactory;
import com.ctrip.xpipe.tuple.Pair;
import com.ctrip.xpipe.utils.StringUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;

/**
 * @author shyin
 *         <p>
 *         Oct 19, 2016
 */
public class DefaultConsoleConfig extends AbstractCoreConfig implements ConsoleConfig {

    public static final String KEY_SERVER_MODE = "console.server.mode";
    public static final String KEY_DATASOURCE = "datasource";
    public static final String KEY_CONSOLE_NOTIFY_RETRY_TIMES = "console.notify.retry.times";
    public static final String KEY_CONSOLE_NOTIFY_THREADS = "console.notify.threads";
    public static final String KEY_CONSOLE_NOTIFY_RETRY_INTERVAL = "console.notify.retry.interval";
    public static final String KEY_METASERVERS = "metaservers";
    public static final String KEY_USER_ACCESS_WHITE_LIST = "user.access.white.list";
    public static final String KEY_HICKWALL_CLUSTER_METRIC_FORMAT = "console.hickwall.cluster.metric.format";
    public static final String KEY_HICKWALL_METRIC_INFO = "console.hickwall.metric.info";
    public static final String KEY_CACHE_REFERSH_INTERVAL = "console.cache.refresh.interval";
    public static final String KEY_ALL_CONSOLES = "console.all.addresses";

    private static final String KEY_CONFIG_DEFAULT_RESTORE_HOUR = "console.config.default.restore.hour";

    private static final String KEY_REBALANCE_SENTINEL_INTERVAL = "rebalance.sentinel.interval.second";

    private static final String KEY_REBALANCE_SENTINEL_MAX_NUM_ONCE = "rebalance.sentinel.max.num.once";

    public static final String KEY_SOCKET_STATS_ANALYZERS = "console.socket.stats.analyzers";

    public static final String KEY_CLUSTER_SHARD_FOR_MIGRATE_SYS_CHECK = "console.cluster.shard.for.migrate.sys.check";

    private static final String KEY_PROXY_INFO_CHECK_INTERVAL = "console.proxy.info.collector.check.interval";

    private static final String KEY_OUTTER_CLIENT_CHECK_INTERVAL = "console.outter.client.check.interval";

    private static final String KEY_OUTER_CLIENT_SYNC_INTERVAL = "console.outer.client.sync.interval";

    private static final String KEY_OUTER_CLIENT_TOKEN = "console.outer.client.token";

    private static final String KEY_VARIABLES_CHECK_DATASOURCE = "console.health.variables.datasource";

    private static final String KEY_OWN_CLUSTER_TYPES = "console.cluster.types";

    private static final String KEY_NOTIFY_CLUSTER_TYPES = "console.notify.cluster.types";

    private static final String KEY_OUTER_CLUSTER_TYPES = "console.outer.cluster.types";

    private static final String KEY_FILTER_OUTER_CLUSTERS = "console.filter.outer.clusters";

    private static final String KEY_NO_HEALTH_CHECK_MINUTES = "no.health.check.minutes";

    private static final String KEY_CROSS_DC_LEADER_LEASE_NAME = "console.cross.dc.leader.lease.name";

    private static final String KEY_SENTINEL_REDUNDANT_REDIS_SENSITIVE = "console.health.sentinel.monitor.redundant.sensitive";

    private static final String KEY_PARALLEL_CONSOLE_DOMAIN = "console.parallel.domain";

    private static final String KEY_BEACON_DEFAULT_HOST = "beacon.host.default";

    private static final String KEY_BEACON_HOST_BY_ORG = "beacon.host.org";

    public static final String KEY_BEACON_ORG_ROUTE = "beacon.org.routes";

    private static final String KEY_CLUSTER_DIVIDED_PARTS = "console.cluster.divide.parts";

    private static final String KEY_CHECKER_ACK_TIMEOUT_MILLI = "checker.ack.timeout.milli";

    private static final String KEY_MIGRATION_TIMEOUT_MILLI = "migration.timeout.milli";

    private static final String KEY_SERVLET_METHOD_TIMEOUT_MILLI = "servlet.method.timeout.milli";

    private static final String KEY_REDIS_CONFIG_CHECK_MONITOR_OPEN = "cosnole.redis.config.check.open";

    private static final String KEY_REDIS_CONFIG_CHECK_RULES = "console.redis.config.check.rules";
    private static final String KEY_CROSS_DC_SENTINEL_MONITOR_NAME_SUFFIX = "checker.cross.dc.sentinel.monitor.name.suffix";

    private static final String KEY_SENTINEL_MASTER_CONFIG = "checker.sentinel.master.config";

    private static final String KEY_SENTINEL_BIND_TIMEOUT_MILLI = "checker.sentinel.bind.timeout.milli";

    private static final String KEY_BIND_OUTER_CLUSTER_SHARD_SENTINEL = "checker.bind.outer.cluster.shard.sentinel";

    private static final String KEY_BI_MIGRATION_CLUSTERS = "migration.bi.support.clusters";
    private static final String KEY_MIGRATION_UNSUPPORTED_CLUSTERS = "migration.unsupported.clusters";
    private static final String KEY_BEACON_SUPPORT_ZONE = "beacon.zone";
    private static final String KEY_BI_DIRECTION_MIGRATION_DC_PRIORITY = "bi.direction.migration.dc.priority";

    private static final String KEY_ROUTE_CHOOSE_STRATEGY_TYPE = "route.choose.strategy.type";

    private static final String KEY_DCS_RELATIONS = "dcs.relations";

    private static final String KEY_MAX_REMOVED_DCS_CNT = "max.removed.dcs.count";
    private static final String KEY_MAX_REMOVED_CLUSTERS_PERCENT = "max.removed.clusters.percent";
    private static final String KEY_MONITOR_UNREGISTER_PROTECT_COUNT = "monitor.unregister.protect.count";

    private static final String KEY_MIGRATION_PROCESS_REPORT_INTERVAL_MILLI = "migration.process.report.interval.milli";
    private static final String KEY_MIGRATION_PROCESS_REPORT_OPEN = "migration.process.report.open";
    private static final String KEY_MIGRATION_PROCESS_REPORT_URL = "migration.process.report.url";

    private String defaultRouteChooseStrategyType = RouteChooseStrategyFactory.RouteStrategyType.CRC32_HASH.name();

    private Map<String, List<ConfigChangeListener>> listeners = Maps.newConcurrentMap();

    @Override
    public String getServerMode() {
        return getProperty(KEY_SERVER_MODE, "CONSOLE_CHECKER");
    }

    @Override
    public int getAlertSystemRecoverMinute() {
        return getIntProperty(KEY_ALERT_MESSAGE_RECOVER_TIME, 5);
    }

    @Override
    public int getAlertSystemSuspendMinute() {
        return getIntProperty(KEY_ALERT_MESSAGE_SUSPEND_TIME, 30);
    }

    @Override
    public String getDBAEmails() {
        return getProperty(KEY_DBA_EMAILS, "DBA@email.com");
    }

    @Override
    public String getRedisAlertSenderEmail() {
        return getProperty(KEY_REDIS_ALERT_SENDER_EMAIL, "");
    }

    @Override
    public String getXpipeRuntimeEnvironment() {
        return getProperty(KEY_XPIPE_RUNTIME_ENVIRONMENT, "");
    }

    @Override
    public String getXPipeAdminEmails() {
        return getProperty(KEY_XPIPE_ADMIN_EMAILS, "XPipeAdmin@email.com");
    }

    @Override
    public String getReplDisklessMinRedisVersion() {
        return getProperty(KEY_REDIS_REPL_DISKLESS_MINIMUM_VERSION, "2.8.22");
    }

    @Override
    public String getXRedisMinimumRequestVersion() {
        return getProperty(KEY_XREDIS_REQUEST_MINI_VERSION, "0.0.3");
    }

    @Override
    public String getDatasource() {
        return getProperty(KEY_DATASOURCE, "fxxpipe");
    }

    @Override
    public int getConsoleNotifyRetryTimes() {
        return getIntProperty(KEY_CONSOLE_NOTIFY_RETRY_TIMES, 3);
    }

    @Override
    public int getConsoleNotifyRetryInterval() {
        return getIntProperty(KEY_CONSOLE_NOTIFY_RETRY_INTERVAL, 100);
    }

    @Override
    public Map<String,String> getMetaservers() {
        String property = getProperty(KEY_METASERVERS, "{}");
        return JsonCodec.INSTANCE.decode(property, Map.class);
    }

    @Override
    public int getConsoleNotifyThreads() {
        return getIntProperty(KEY_CONSOLE_NOTIFY_THREADS, 20);
    }

    @Override
    public Set<String> getConsoleUserAccessWhiteList() {
        String whiteList = getProperty(KEY_USER_ACCESS_WHITE_LIST, "*");
        return new HashSet<>(Arrays.asList(whiteList.split(",")));
    }

    @Override
    public int getRedisReplicationHealthCheckInterval() {
        return getIntProperty(KEY_REDIS_REPLICATION_HEALTH_CHECK_INTERVAL, 2000);
    }

    @Override
    public int getClusterHealthCheckInterval() {
        return getIntProperty(KEY_CLUSTER_HEALTH_CHECK_INTERVAL, 300000);
    }

    private String hickwallInfo;

    private HickwallMetricInfo info;

    @Override
    public Map<String,String> getHickwallClusterMetricFormat() {
        String property = getProperty(KEY_HICKWALL_CLUSTER_METRIC_FORMAT, "{}");
        return JsonCodec.INSTANCE.decode(property, Map.class);
    }

    @Override
    public HickwallMetricInfo getHickwallMetricInfo() {
        String localInfo = getProperty(KEY_HICKWALL_METRIC_INFO, "{\"domain\": \"http://hickwall.qa.nt.ctripcorp.com/grafanav2/d/UR32kfjWz/fx-xpipe?fullscreen&orgId=1&from=now-1h&to=now\", \"delayPanelId\": 2, \"crossDcDelayPanelId\": 14, \"proxyPingPanelId\": 4, \"proxyTrafficPanelId\": 6, \"proxyCollectionPanelId\": 8, \"outComingTrafficToPeerPanelId\": \"16\", \"inComingTrafficFromPeerPanelId\": \"18\",\"peerSyncFullPanelId\": \"20\",\"peerSyncPartialPanelId\": \"22\"}");
        if(StringUtil.isEmpty(hickwallInfo) || !localInfo.equals(hickwallInfo)) {
            hickwallInfo = localInfo;
            info = JsonCodec.INSTANCE.decode(hickwallInfo, HickwallMetricInfo.class);
        }
        return info;
    }

    @Override
    public int getHealthyDelayMilli() {
        return getIntProperty(KEY_HEALTHY_DELAY, 2000);
    }

    @Override
    public long getHealthMarkCompensateIntervalMill() {
        return getLongProperty(KEY_HEALTHY_MARK_COMPENSATE_INTERVAL_MILLI, 2 * 60 * 1000L);
    }

    @Override
    public int getHealthMarkCompensateThreads() {
        return getIntProperty(KEY_HEALTHY_MARK_COMPENSATE_THREADS, 20);
    }

    @Override
    public int getHealthyDelayMilliThroughProxy() {
        return getIntProperty(KEY_HEALTHY_DELAY_THROUGH_PROXY, 30 * 1000);
    }

    @Override
    public int getInstanceLongDelayMilli() {
        return getIntProperty(KEY_INSTANCE_LONG_DELAY_MILLI, 3 * 60 * 1000);
    }

    @Override
    public int getDownAfterCheckNums() {
        return getIntProperty(KEY_DOWN_AFTER_CHECK_NUMS, 5);
    }

    @Override
    public int getDownAfterCheckNumsThroughProxy() {
        return getIntProperty(KEY_DOWN_AFTER_CHECK_NUMS_THROUGH_PROXY, 10);
    }

    @Override
    public int getCacheRefreshInterval() {
        return getIntProperty(KEY_CACHE_REFERSH_INTERVAL, 1000);
    }

    @Override
    public Set<String> getAlertWhileList() {
        String whitelist = getProperty(KEY_ALERT_WHITE_LIST, "");

        return getSplitStringSet(whitelist);

    }

    @Override
    public String getAllConsoles() {
        return getProperty(KEY_ALL_CONSOLES, "127.0.0.1:8080");
    }

    @Override
    public int getQuorum() {
        return getIntProperty(KEY_QUORUM, 1);
    }

    @Override
    public int getRedisConfCheckIntervalMilli() {

        return getIntProperty(
                KEY_REDIS_CONF_CHECK_INTERVAL,
                Integer.parseInt(System.getProperty(KEY_REDIS_CONF_CHECK_INTERVAL, "300000"))
        );
    }

    @Override
    public int getSentinelCheckIntervalMilli() {
        return getIntProperty(KEY_SENTINEL_CHECK_INTERVAL, 300000);
    }

    @Override
    public String getConsoleDomain() {
        return getProperty(KEY_DOMAIN, "127.0.0.1");
    }

    @Override
    public QuorumConfig getDefaultSentinelQuorumConfig() {

        String config = getProperty(KEY_SENTINEL_QUORUM, "{}");
        return JsonCodec.INSTANCE.decode(config, QuorumConfig.class);
    }

    @Override
    public int getConfigDefaultRestoreHours() {
        return getIntProperty(KEY_CONFIG_DEFAULT_RESTORE_HOUR, 10);
    }

    @Override
    public int getRebalanceSentinelInterval() {
        return getIntProperty(KEY_REBALANCE_SENTINEL_INTERVAL, 120);
    }

    @Override
    public int getRebalanceSentinelMaxNumOnce() {
        return getIntProperty(KEY_REBALANCE_SENTINEL_MAX_NUM_ONCE, 15);
    }

    @Override
    public int getNoAlarmMinutesForClusterUpdate() {
        return getIntProperty(KEY_NO_ALARM_MUNITE_FOR_CLUSTER_UPDATE, 15);
    }

    @Override
    public int getHealthCheckSuspendMinutes() {
        return getIntProperty(KEY_NO_HEALTH_CHECK_MINUTES, 40);
    }

    @Override
    public Set<String> getIgnoredHealthCheckDc() {
        return getSplitStringSet(getProperty(KEY_IGNORED_DC_FOR_HEALTH_CHECK, ""));
    }

    private int getDefaultMarkDownDelaySecond() {
        return getIntProperty(KEY_DEFAULT_MARK_DOWN_DELAY_SEC, 60 * 60);
    }

    @Override
    public Set<DcClusterDelayMarkDown> getDelayedMarkDownDcClusters() {
        Set<DcClusterDelayMarkDown> result = Sets.newHashSet();
        Set<String> dcClusters = getSplitStringSet(getProperty(KEY_DC_CLUSTER_WONT_MARK_DOWN, ""));
        for(String dcCluster : dcClusters) {
            String[] pair = StringUtil.splitRemoveEmpty("\\s*:\\s*", dcCluster);
            DcClusterDelayMarkDown instance = new DcClusterDelayMarkDown().setDcId(pair[0]).setClusterId(pair[1]);
            int delaySec = pair.length > 2 ? Integer.parseInt(pair[2]) : getDefaultMarkDownDelaySecond();
            result.add(instance.setDelaySecond(delaySec));
        }
        return result;
    }

    @Override
    public int getPingDownAfterMilli() {
        return getIntProperty(KEY_PING_DOWN_AFTER_MILLI, 12 * 1000);
    }

    @Override
    public int getPingDownAfterMilliThroughProxy() {
        return getIntProperty(KEY_PING_DOWN_AFTER_MILLI_THROUGH_PROXY, 30 * 1000);
    }

    @Override
    public void onChange(String key, String oldValue, String newValue) {
        super.onChange(key, oldValue, newValue);
        if(!listeners.containsKey(key)) {
            return;
        }
        for(ConfigChangeListener listener : listeners.get(key)) {
            listener.onChange(key, oldValue, newValue);
        }
    }

    @Override
    public void register(List<String> keys, ConfigChangeListener configListener) {
        for(String key : keys) {
            listeners.putIfAbsent(key, new LinkedList<>());
            listeners.get(key).add(configListener);
        }
    }

    @Override
    public Map<String, String> getSocketStatsAnalyzingKeys() {
        String property = getProperty(KEY_SOCKET_STATS_ANALYZERS, "{}");
        return JsonCodec.INSTANCE.decode(property, Map.class);
    }

    @Override
    public Pair<String, String> getClusterShardForMigrationSysCheck() {
        String clusterShard = getProperty(KEY_CLUSTER_SHARD_FOR_MIGRATE_SYS_CHECK, "cluster1, shard1");
        String[] strs = StringUtil.splitRemoveEmpty("\\s*,\\s*", clusterShard);
        return Pair.from(strs[0], strs[1]);
    }

    @Override
    public int getProxyInfoCollectInterval() {
        return getIntProperty(KEY_PROXY_INFO_CHECK_INTERVAL, 30 * 1000);
    }

    @Override
    public int getOutterClientCheckInterval() {
        return getIntProperty(KEY_OUTTER_CLIENT_CHECK_INTERVAL, 120 * 1000);
    }

    @Override
    public int getOuterClientSyncInterval() {
        return getIntProperty(KEY_OUTER_CLIENT_SYNC_INTERVAL, 10 * 1000);
    }

    @Override
    public String getOuterClientToken() {
        return getProperty(KEY_OUTER_CLIENT_TOKEN, "");
    }

    @Override
    public Map<String, String> getConsoleDomains() {
        String property = getProperty(KEY_CONSOLE_DOMAINS, "{}");
        return JsonCodec.INSTANCE.decode(property, Map.class);
    }

    @Override
    public boolean isSentinelRateLimitOpen() {
        return getBooleanProperty(KEY_SENTINEL_RATE_LIMIT_OPEN, false);
    }

    @Override
    public int getSentinelRateLimitSize() {
        return getIntProperty(KEY_SENTINEL_RATE_LIMIT_SIZE, 3);
    }

    @Override
    public Set<String> getVariablesCheckDataSources() {
        String dataSources = getProperty(KEY_VARIABLES_CHECK_DATASOURCE, "");

        return getSplitStringSet(dataSources);
    }

    @Override
    public Set<String> getOwnClusterType() {
        String clusterTypes = getProperty(KEY_OWN_CLUSTER_TYPES, ClusterType.ONE_WAY.toString());

        return getSplitStringSet(clusterTypes);
    }

    @Override
    public Set<String> shouldNotifyClusterTypes() {
        String clusterTypes = getProperty(KEY_NOTIFY_CLUSTER_TYPES, ClusterType.ONE_WAY.toString()+","+ClusterType.BI_DIRECTION.toString());

        return getSplitStringSet(clusterTypes);
    }

    @Override
    public Set<String> getOuterClusterTypes() {
        String clusterTypes = getProperty(KEY_OUTER_CLUSTER_TYPES, "");

        return getSplitStringSet(clusterTypes);
    }

    @Override
    public String filterOuterClusters() {
        return getProperty(KEY_FILTER_OUTER_CLUSTERS, "");
    }

    boolean shouldSentinelCheckOuterClientClusters() {
        return getBooleanProperty(KEY_SHOULD_SENTINEL_CHECK_OUTER_TYPES, false);
    }

    Set<String> sentinelCheckOuterClientClusters() {
        return getSplitStringSet(getProperty(KEY_SENTINEL_CHECK_OUTER_CLUSTERS, "").toLowerCase());
    }

    //	check if outer client clusters support sentinel health check or not
    @Override
    public boolean supportSentinelHealthCheck(ClusterType clusterType, String clusterName) {
        return clusterType.supportHealthCheck() || shouldSentinelCheckOuterClientClusters() || sentinelCheckOuterClientClusters().contains(clusterName.toLowerCase());
    }

    @Override
    public String sentinelCheckDowngradeStrategy() {
        return getProperty(KEY_SENTINEL_CHECK_DOWNGRADE_STRATEGY, "lessThanHalf");
    }

    @Override
    public String getCrossDcLeaderLeaseName() {
        return getProperty(KEY_CROSS_DC_LEADER_LEASE_NAME, "CROSS_DC_LEADER");
    }

    @Override
    public boolean isSensitiveForRedundantRedis() {
        return getBooleanProperty(KEY_SENTINEL_REDUNDANT_REDIS_SENSITIVE, false);
    }

    @Override
    public String getParallelConsoleDomain() {
        return getProperty(KEY_PARALLEL_CONSOLE_DOMAIN, "");
    }

    @Override
    public int getStableLossAfterRounds() {
        return getIntProperty(KEY_CHECKER_STABLE_LOSS_AFTER_ROUNDS, 10);
    }

    @Override
    public int getStableRecoverAfterRounds() {
        return getIntProperty(KEY_CHECKER_STABLE_RECOVER_AFTER_ROUNDS, 30 * 30);
    }

    @Override
    public int getStableResetAfterRounds() {
        return getIntProperty(KEY_CHECKER_STABLE_RESET_AFTER_ROUNDS, 30);
    }

    @Override
    public float getSiteStableThreshold() {
        return getFloatProperty(KEY_CHECKER_STABLE_THRESHOLD, 0.8f);
    }

    @Override
    public Boolean getSiteStable() {
        return getBooleanProperty(KEY_CHECKER_SITE_STABLE, null);
    }

    @Override
    public float getSiteUnstableThreshold() {
        return getFloatProperty(KEY_CHECKER_UNSTABLE_THRESHOLD, 0.8f);
    }

    @Override
    public String getDefaultBeaconHost() {
        return getProperty(KEY_BEACON_DEFAULT_HOST);
    }

    @Override
    public Map<Long, String> getBeaconHosts() {
        String property = getProperty(KEY_BEACON_HOST_BY_ORG, "{}");
        return JsonCodec.INSTANCE.decode(property, new GenericTypeReference<Map<Long, String>>() {});
    }

    @Override
    public List<BeaconOrgRoute> getBeaconOrgRoutes() {
        String property = getProperty(KEY_BEACON_ORG_ROUTE, "[]");
        return JsonCodec.INSTANCE.decode(property, new GenericTypeReference<List<BeaconOrgRoute>>() {});
    }

    @Override
    public int getClusterDividedParts() {
        return getIntProperty(KEY_CLUSTER_DIVIDED_PARTS, 1);
    }

    @Override
    public int getClustersPartIndex() {
        return getIntProperty(KEY_CLUSTERS_PART_INDEX, 0);
    }

    @Override
    public int getCheckerAckIntervalMilli() {
        return getIntProperty(KEY_CHECKER_ACK_INTERVAL, 10000);
    }

    @Override
    public int getCheckerAckTimeoutMilli() {
        return getIntProperty(KEY_CHECKER_ACK_TIMEOUT_MILLI, 60000);
    }

    @Override
    public int getCheckerReportIntervalMilli() {
        return getIntProperty(KEY_CHECKER_REPORT_INTERVAL, 10000);
    }

    @Override
    public int getCheckerMetaRefreshIntervalMilli() {
        return getIntProperty(KEY_CHECKER_META_REFRESH_INTERVAL, 30000);
    }

    @Override
    public String getConsoleAddress() {
        return getProperty(KEY_CONSOLE_ADDRESS, "http://localhost:8080");
    }

    @Override
    public Set<String> getAllCheckerAddress() {
        return getSplitStringSet(getProperty(KEY_CHECKER_ADDRESS_ALL, "127.0.0.1:8080"));
    }

    @Override
    public long getConfigCacheTimeoutMilli() {
        return getLongProperty(KEY_CONFIG_CACHE_TIMEOUT_MILLI, 1000L);
    }

    @Override
    public int getProxyCheckUpRetryTimes() {
        return getIntProperty(KEY_PROXY_CHECK_UP_RETRY_TIMES, 10);
    }

    @Override
    public int getProxyCheckDownRetryTimes() {
        return getIntProperty(KEY_PROXY_CHECK_DOWN_RETRY_TIMES, 1);
    }
    
    @Override
    public long getMigrationTimeoutMilli() {
        return getLongProperty(KEY_MIGRATION_TIMEOUT_MILLI, 15000L);
    }

    @Override
    public long getServletMethodTimeoutMilli() {
        return getLongProperty(KEY_SERVLET_METHOD_TIMEOUT_MILLI, 10000L);
    }

    public boolean isRedisConfigCheckMonitorOpen() {
        return getBooleanProperty(KEY_REDIS_CONFIG_CHECK_MONITOR_OPEN, false);
    }

    @Override
    public String getRedisConfigCheckRules() {
        return getProperty(KEY_REDIS_CONFIG_CHECK_RULES);
    }

    @Override
    public String getBiDirectionMigrationDcPriority() {
        return getProperty(KEY_BI_DIRECTION_MIGRATION_DC_PRIORITY, "SHARB,SHAXY");
    }

    public String crossDcSentinelMonitorNameSuffix() {
        return getProperty(KEY_CROSS_DC_SENTINEL_MONITOR_NAME_SUFFIX, "CROSS_DC");
    }

    @Override
    public boolean shouldBindOuterClusterShardAndSentinel() {
        return getBooleanProperty(KEY_BIND_OUTER_CLUSTER_SHARD_SENTINEL, false);
    }

    @Override
    public int sentinelBindTimeoutMilli() {
        return getIntProperty(KEY_SENTINEL_BIND_TIMEOUT_MILLI, 30000);
    }

    @Override
    public int getNonCoreCheckIntervalMilli() {
        return getIntProperty(KEY_NON_CORE_CHECK_INTERVAL, 3 * 60 * 60 * 1000);
    }

    @Override
    public Map<String, String> sentinelMasterConfig() {
        String property = getProperty(KEY_SENTINEL_MASTER_CONFIG, "{}");
        return JsonCodec.INSTANCE.decode(property, Map.class);
    }

    @Override
    public long subscribeTimeoutMilli() {
        return getLongProperty(KEY_SUBSCRIBE_TIMEOUT_MILLI, 5000L);
    }

    @Override
    public String getDcsRelations() {
        return getProperty(KEY_DCS_RELATIONS, "{}");
    }

    @Override
    public Set<String> getClustersSupportBiMigration() {
        String raw = getProperty(KEY_BI_MIGRATION_CLUSTERS, "");

        return getSplitStringSet(raw);
    }

    @Override
    public String getBeaconSupportZone() {
        return getProperty(KEY_BEACON_SUPPORT_ZONE, "");
    }

    @Override
    public String getChooseRouteStrategyType() { return getProperty(KEY_ROUTE_CHOOSE_STRATEGY_TYPE, defaultRouteChooseStrategyType);}

    @Override
    public String getClusterExcludedRegex() {
        return getProperty(KEY_ALERT_CLUSTER_EXCLUDED_REGEX, "");
    }

    @Override
    public int maxRemovedDcsCnt() {
        return getIntProperty(KEY_MAX_REMOVED_DCS_CNT, 1);
    }

    @Override
    public int maxRemovedClustersPercent() {
        return getIntProperty(KEY_MAX_REMOVED_CLUSTERS_PERCENT, 50);
    }

    @Override
    public Set<String> getMigrationUnsupportedClusters() {
        String raw = getProperty(KEY_MIGRATION_UNSUPPORTED_CLUSTERS, "").toLowerCase();
        return getSplitStringSet(raw);
    }

    @Override
    public int monitorUnregisterProtectCount() {
        return getIntProperty(KEY_MONITOR_UNREGISTER_PROTECT_COUNT, 10);
    }

    @Override
    public boolean isMigrationProcessReportOpen() {
        return getBooleanProperty(KEY_MIGRATION_PROCESS_REPORT_OPEN, false);
    }

    @Override
    public String getKeyMigrationProcessReportUrl() {
        return getProperty(KEY_MIGRATION_PROCESS_REPORT_URL, "127.0.0.1:8080");
    }

    @Override
    public long getMigrationProcessReportIntervalMill() {
        return getLongProperty(KEY_MIGRATION_PROCESS_REPORT_INTERVAL_MILLI, 10000L);
    }
}
