package ch.iptiq.loadbalancer.core.heartbeat

import ch.iptiq.loadbalancer.core.providerselector.ProviderSelector
import ch.iptiq.loadbalancer.model.Provider
import ch.iptiq.loadbalancer.model.ProviderStatus.*
import org.junit.jupiter.api.Test

internal class HeartBeatCheckerTest {

    private val providerSelector = ProviderSelector()
    private val heartBeatChecker =
        HeartBeatChecker(providerSelector)

    val provider1 = Provider()
    val provider2 = Provider()
    val provider3 = Provider()

    val providers = listOf(provider1, provider2, provider3)

    @Test
    fun checkNonHealthyProviderIsExcluded() {

        for (provider in providers) {
            assert(provider.health == HEALTHY)
            assert(provider.status == ACTIVE)
        }

        // WHEN ONE PROVIDER IS NOT HEALTHY ANYMORE
        provider1.health = NOT_HEALTHY
        heartBeatChecker.check(providers)

        // THEN THIS ONE IS NOT ACTIVE AND OTHERS ARE NOT AFFECTED
        assert(provider1.health == NOT_HEALTHY)
        assert(provider1.status == NOT_ACTIVE)
        assert(provider2.health == HEALTHY)
        assert(provider2.status == ACTIVE)
        assert(provider3.health == HEALTHY)
        assert(provider3.status == ACTIVE)
    }

    @Test
    fun checkHealthyProviderIsIncludedBack() {
        val provider1 = Provider()
        val provider2 = Provider()
        val provider3 = Provider()

        val providers = listOf(provider1, provider2, provider3)

        for (provider in providers) {
            assert(provider.health == HEALTHY)
            assert(provider.status == ACTIVE)
        }

        // WHEN ONE PROVIDER IS NOT HEALTHY ANYMORE
        provider1.health = NOT_HEALTHY
        heartBeatChecker.check(providers)

        // THEN THIS ONE IS NOT ACTIVE AND OTHERS ARE NOT AFFECTED
        assert(provider1.health == NOT_HEALTHY)
        assert(provider1.status == NOT_ACTIVE)
        assert(provider2.health == HEALTHY)
        assert(provider2.status == ACTIVE)
        assert(provider3.health == HEALTHY)
        assert(provider3.status == ACTIVE)


        // WHEN PROVIDER IS BACK TO HEALTHY
        provider1.health = HEALTHY

        // THEN THIS PROVIDER IS BACK TO ACTIVE AFTER 2 MORE CHECKS AND OTHERS ARE NOT AFFECTED
        heartBeatChecker.check(providers)
        heartBeatChecker.check(providers)

        assert(provider1.health == HEALTHY)
        assert(provider1.status == ACTIVE)
        assert(provider2.health == HEALTHY)
        assert(provider2.status == ACTIVE)
        assert(provider3.health == HEALTHY)
        assert(provider3.status == ACTIVE)
    }
}