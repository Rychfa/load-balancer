package ch.iptiq.loadbalancer.core.providerselector

import ch.iptiq.loadbalancer.model.Provider
import ch.iptiq.loadbalancer.model.ProviderStatus.ACTIVE
import ch.iptiq.loadbalancer.model.ProviderStatus.NOT_ACTIVE
import org.junit.jupiter.api.Test

internal class ProviderSelectorTest {

    val providerSelector = ProviderSelector()

    @Test
    fun excludeProvider() {
        val provider = Provider()

        assert(provider.status == ACTIVE)
        providerSelector.excludeProvider(provider)
        assert(provider.status == NOT_ACTIVE)
    }

    @Test
    fun includeProvider() {
        val provider = Provider()
        providerSelector.excludeProvider(provider)
        assert(provider.status == NOT_ACTIVE)
        providerSelector.includeProvider(provider)
        assert(provider.status == ACTIVE)
    }
}