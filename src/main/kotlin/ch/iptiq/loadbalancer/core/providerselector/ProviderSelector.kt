package ch.iptiq.loadbalancer.core.providerselector

import ch.iptiq.loadbalancer.model.Provider
import ch.iptiq.loadbalancer.model.ProviderStatus

class ProviderSelector {

    fun excludeProvider(queryProvider: Provider) {
        queryProvider.status = ProviderStatus.NOT_ACTIVE
    }

    fun includeProvider(queryProvider: Provider) {
        queryProvider.status = ProviderStatus.ACTIVE
    }
}