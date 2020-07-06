package ch.iptiq.loadbalancer.core

import ch.iptiq.loadbalancer.core.capacity.ClusterCapacityLimitCalculator
import ch.iptiq.loadbalancer.core.heartbeat.HeartBeatChecker
import ch.iptiq.loadbalancer.core.providerselector.ProviderSelector
import ch.iptiq.loadbalancer.core.scheduler.RandomProviderScheduler
import ch.iptiq.loadbalancer.core.scheduler.RoundRobinProviderScheduler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException
import java.util.*

internal class LoadBalancerTest {

    private val providerSelector = ProviderSelector()
    private val randomScheduler = RandomProviderScheduler()
    private val roundRobinScheduler = RoundRobinProviderScheduler()
    private val clusterCapacityLimitCalculator = ClusterCapacityLimitCalculator()
    private val heartBeatChecker = HeartBeatChecker(providerSelector)

    @Test
    fun shouldCreateOneProvider() {
        var loadBalancer = LoadBalancer(-1, randomScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        assert(loadBalancer.providers.size == 1)

        loadBalancer = LoadBalancer(0, randomScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        assert(loadBalancer.providers.size == 1)

        loadBalancer = LoadBalancer(1, randomScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        assert(loadBalancer.providers.size == 1)
    }

    @Test
    fun shouldCreateMax10Providers() {
        var loadBalancer = LoadBalancer(10, randomScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        assert(loadBalancer.providers.size == 10)

        loadBalancer = LoadBalancer(12, randomScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        assert(loadBalancer.providers.size == 10)
    }

    @Test
    fun shouldChooseTheOnlyOneAvailableProvider() {
        val loadBalancer = LoadBalancer(1, randomScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        assert(loadBalancer.get() == loadBalancer.providers[0].get())
    }

    @Test
    fun shouldRandomlyChooseProvider() {
        val loadBalancer = LoadBalancer(4, randomScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        val randomProviderUUID = loadBalancer.get()
        val provider = loadBalancer.getProvider(randomProviderUUID)
        assert(provider.get() == randomProviderUUID)
    }

    @Test
    fun shouldChooseProviderUsingRoundRobinSchedule() {
        val loadBalancer = LoadBalancer(3, roundRobinScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        val provider1 = loadBalancer.getProvider(loadBalancer.get())
        val provider2 = loadBalancer.getProvider(loadBalancer.get())
        val provider3 = loadBalancer.getProvider(loadBalancer.get())
        val provider4 = loadBalancer.getProvider(loadBalancer.get())
        val provider5 = loadBalancer.getProvider(loadBalancer.get())
        val provider6 = loadBalancer.getProvider(loadBalancer.get())
        assert(provider1 == loadBalancer.providers[0])
        assert(provider2 == loadBalancer.providers[1])
        assert(provider3 == loadBalancer.providers[2])
        assert(provider4 == loadBalancer.providers[0])
        assert(provider5 == loadBalancer.providers[1])
        assert(provider6 == loadBalancer.providers[2])
    }

    @Test
    fun shouldGetProviderByUUID() {
        val loadBalancer = LoadBalancer(4, roundRobinScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        val provider = loadBalancer.providers[2]

        val foundProvider = loadBalancer.getProvider(provider.get())
        assert(foundProvider == provider)
    }

    @Test
    fun shouldThrowExceptionWhenNoProviderWithUUID() {
        val loadBalancer = LoadBalancer(4, roundRobinScheduler, heartBeatChecker, clusterCapacityLimitCalculator)
        val queryUUID = UUID.randomUUID()
        assertThrows<IllegalArgumentException> { loadBalancer.getProvider(queryUUID) }
    }

    @Test
    fun shouldGetResultFromLoadBalancer() {
        val loadBalancer = LoadBalancer(1, roundRobinScheduler, heartBeatChecker, clusterCapacityLimitCalculator)

        val deferred = (1..1).map { n ->
            GlobalScope.async {
                loadBalancer.get()
            }
        }

        runBlocking {
            deferred.map { it.await() }
        }
    }

    @Test
    fun shouldGetResultFromLoadBalancer2() {
        val loadBalancer = LoadBalancer(3, 2, roundRobinScheduler, heartBeatChecker, clusterCapacityLimitCalculator)

        val deferred = (1..3).map { n ->
            GlobalScope.async {
                loadBalancer.get()
            }
        }

        runBlocking {
            deferred.map { it.await() }
        }
    }

    @Test
    fun shouldNotGetResultFromLoadBalancerDueToOverload() {
        val loadBalancer = LoadBalancer(1, roundRobinScheduler, heartBeatChecker, clusterCapacityLimitCalculator)

        assertThrows<IllegalStateException> {
            val deferred = (1..3).map { n ->
                GlobalScope.async {
                    loadBalancer.get()
                }
            }

            runBlocking {
                deferred.map { it.await() }
            }
        }
    }
}