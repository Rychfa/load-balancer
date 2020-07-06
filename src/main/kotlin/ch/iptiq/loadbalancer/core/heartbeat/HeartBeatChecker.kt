package ch.iptiq.loadbalancer.core.heartbeat

import ch.iptiq.loadbalancer.core.providerselector.ProviderSelector
import ch.iptiq.loadbalancer.model.Provider
import ch.iptiq.loadbalancer.model.ProviderStatus.NOT_ACTIVE

class HeartBeatChecker(providerSelector: ProviderSelector) {

    private val providerSelector: ProviderSelector = providerSelector

    fun check(providers: Collection<Provider>) {
        for (provider in providers) {
            if (!provider.check()) {
                providerSelector.excludeProvider(provider)
//                provider.status = NOT_ACTIVE
            } else {
                if (provider.status == NOT_ACTIVE) {
                    provider.recoveryCounter++
                    if (provider.recoveryCounter == 2) {
                        providerSelector.includeProvider(provider)
//                provider.status = NOT_ACTIVE
//                        provider.status = ACTIVE
                        provider.recoveryCounter = 0
                    }
                }
            }
        }
    }

}