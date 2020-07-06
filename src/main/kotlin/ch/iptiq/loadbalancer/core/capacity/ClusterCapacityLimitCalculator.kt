package ch.iptiq.loadbalancer.core.capacity

import ch.iptiq.loadbalancer.model.Provider
import ch.iptiq.loadbalancer.model.ProviderStatus

class ClusterCapacityLimitCalculator {

    fun calculate(providers: List<Provider>): Int {
        return providers.stream()
            .filter { provider -> provider.status == ProviderStatus.ACTIVE }
            .map { provider -> provider.capacity }
            .reduce { sum, capacity -> sum + capacity }
            .get()
    }
}