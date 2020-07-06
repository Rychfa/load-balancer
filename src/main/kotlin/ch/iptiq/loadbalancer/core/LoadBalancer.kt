package ch.iptiq.loadbalancer.core

import ch.iptiq.loadbalancer.core.capacity.ClusterCapacityLimitCalculator
import ch.iptiq.loadbalancer.core.heartbeat.HeartBeatChecker
import ch.iptiq.loadbalancer.core.scheduler.ProviderScheduler
import ch.iptiq.loadbalancer.model.Provider
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.fixedRateTimer

class LoadBalancer(
    numberOfProviders: Int,
    capacity: Int,
    private val scheduler: ProviderScheduler,
    private val heartBeatChecker: HeartBeatChecker,
    private val clusterCapacityCalculator : ClusterCapacityLimitCalculator,
    simulateServer: Boolean
) {

    // This variable is used to keep the load balancer thread for 6 secs (to mimic a server)
    private val LOADBALANCER_LIFE_SPAN: Long = 6000 // 6 seconds in milliseconds
    private val MAX_PROVIDERS: Int = 10
    private val EVERY_2_SECONDS: Long = 2000 // 2 seconds in milliseconds
    private val startTime: Long
    var providers = mutableListOf<Provider>()
        private set
    private var requestCount = AtomicInteger(0)

    constructor(
        numberOfProviders: Int,
        scheduler: ProviderScheduler,
        heartBeatChecker: HeartBeatChecker,
        clusterCapacityCalculator : ClusterCapacityLimitCalculator
    ) : this(numberOfProviders, 1, scheduler, heartBeatChecker, clusterCapacityCalculator, false)

    constructor(
        numberOfProviders: Int,
        capacity: Int,
        scheduler: ProviderScheduler,
        heartBeatChecker: HeartBeatChecker,
        clusterCapacityCalculator : ClusterCapacityLimitCalculator
    ) : this(numberOfProviders, capacity, scheduler, heartBeatChecker, clusterCapacityCalculator, false)

    init {
        val effectiveNumberOfProviders = numberOfProviders.coerceAtLeast(1).coerceAtMost(MAX_PROVIDERS)

        for (i in 0 until effectiveNumberOfProviders) {
            providers.add(Provider(capacity))
        }

        startTime = System.currentTimeMillis()

        if (simulateServer) {
            run()
        }
    }

    fun get(): UUID {
        val currentTotalCapacity = clusterCapacityCalculator.calculate(providers)
        if (requestCount.incrementAndGet() > currentTotalCapacity) {
            throw IllegalStateException("Request load is bigger than cluster capacity")
        }
        val response = scheduler.get(providers)
        requestCount.decrementAndGet()
        return response

    }

    fun check() {
        fixedRateTimer("heartBeatChecker", false, 0L, EVERY_2_SECONDS) {
            heartBeatChecker.check(providers)
        }
    }

    fun run() {
        var diff: Long = System.currentTimeMillis() - startTime

        check()

        while (diff < LOADBALANCER_LIFE_SPAN) {
            diff = System.currentTimeMillis() - startTime
        }
    }

    fun getProvider(queryProviderUUID: UUID): Provider {
        val provider = providers.stream()
            .filter { x -> x.get() == queryProviderUUID }
            .findFirst()

        if (!provider.isPresent) {
            throw IllegalArgumentException("No provider registered with UUID $queryProviderUUID")
        }

        return provider.get()
    }
}